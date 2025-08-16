package com.bosai.homework.handler;

import com.bosai.homework.Constant;
import com.bosai.homework.SessionInfo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Random;


/**
 * SessionHandler  处理会话信息
 */
public class SessionHandler extends BaseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("SessionHandler  handle  Method");
        try {
            if (!"GET".equals(httpExchange.getRequestMethod())) {
                buildResponse(httpExchange, 405, "Please  Use the GET method");
                return;
            }
            String path = httpExchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 2) {
                buildResponse(httpExchange, 400, "The parameter is illegal");
                return;
            }

            try {
                // 获取用户ID
                int customerId = Integer.parseInt(parts[1]);
                System.out.println(String.format("Customer ID: %s", customerId));
                //CAS 原子操作更新新值
                SessionInfo session = Constant.userSessionMap.compute(customerId, (k, v) ->
                        (v != null && v.isExpired()) ? v : new SessionInfo(customerId, buildSessionKey())
                );
                System.out.println(String.format("Session ID: %s", session.getSessionId()));
                buildResponse(httpExchange, 200, session.getSessionId());
            } catch (NumberFormatException e) {
                buildResponse(httpExchange, 400, "Customer ID is invalid");
            }
        } catch (Exception e) {
            buildResponse(httpExchange, 500, "UnKnow Exception");
        }

    }


    private  String buildSessionKey() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
