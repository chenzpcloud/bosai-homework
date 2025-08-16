package com.bosai.homework.handler;

import com.bosai.homework.Constant;
import com.bosai.homework.SessionInfo;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * StakeHandler  投注处理器
 */
public class StakeHandler extends BaseHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("StakeHandler  handle  Method");
        try {
            if (!"POST".equals(httpExchange.getRequestMethod())) {
                buildResponse(httpExchange, 405, "Please  Use the POST method");
                return;
            }

            String query = httpExchange.getRequestURI().getQuery();
            if (query == null || !query.contains("sessionkey=")) {
                buildResponse(httpExchange, 401, "Missing session key");
                return;
            }

            String[] params = query.split("=");
            if (params.length < 2) {
                buildResponse(httpExchange, 401, "Invalid session key format");
                return;
            }
            String sessionKey = params[1];

            // 校验Session是否合法
            boolean validSession = false;
            int customerId = -1;
            for (SessionInfo session : Constant.userSessionMap.values()) {
                if (session.getSessionId().equals(sessionKey) && session.isExpired()) {
                    validSession = true;
                    customerId = session.getUserId();
                    break;
                }
            }
            if (!validSession) {
                //Session 不合法
                buildResponse(httpExchange, 401, "Invalid or expired session");
                return;
            }
            String path = httpExchange.getRequestURI().getPath();
            //校验参数合法性
            String[] parts = path.split("/");
            if (parts.length < 2) {
                buildResponse(httpExchange, 400, "Invalid path format");
                return;
            }

            try {
                // 获取投注金额
                int betOfferId = Integer.parseInt(parts[1]);
                // Read stake value
                InputStream is = httpExchange.getRequestBody();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String body = reader.readLine();
                if (body == null || body.isEmpty()) {
                    buildResponse(httpExchange, 400, "Missing stake value");
                    return;
                }
                int stake = Integer.parseInt(body);
                // ConcurrentHashMap  原子更新最大投注值
                Constant.stakesData.computeIfAbsent(betOfferId, k -> new ConcurrentHashMap<>())
                        .compute(customerId, (k, v) -> {
                            if (v == null) {
                                return new AtomicInteger(stake);
                            } else {
                                v.updateAndGet(curr -> Math.max(curr, stake));
                                return v;
                            }
                        });

                buildResponse(httpExchange, 200, "");
                System.out.println(String.format("Stake Success CustomerId:%d BetOfferId:%d Stake:%d",  customerId,betOfferId,stake));
            } catch (Exception e) {
                buildResponse(httpExchange, 500, "Stake Failed");
            }
        } catch (Exception e) {
            buildResponse(httpExchange, 500, "UnKnown Exception");
        }

    }
}
