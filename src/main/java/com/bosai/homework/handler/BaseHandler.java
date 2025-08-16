package com.bosai.homework.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

/**
 * BaseHandler  跟路劲处理器
 */
public class BaseHandler implements HttpHandler {


    /**
     * 构建请求响应
     *
     * @param httpExchange
     * @param code
     * @param body
     */
    public void buildResponse(HttpExchange httpExchange, int code, String body) {
        System.out.println(String.format("Response Code: %s, Body: %s", code, body));
        try {
            byte[] response = body.getBytes();
            httpExchange.sendResponseHeaders(code, response.length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response);
            }
        } catch (IOException e) {

        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        //url 路径分发
        String path = httpExchange.getRequestURI().getPath();
        System.out.println(String.format("Request Path: %s", path));
        if (path.matches("/\\d+/session")) {
            new SessionHandler().handle(httpExchange);
        } else if (path.matches("/\\d+/stake")) {
            new StakeHandler().handle(httpExchange);
        } else if (path.matches("/\\d+/highstakes")) {
            new HighStakesHandler().handle(httpExchange);
        } else {
            buildResponse(httpExchange, 404, "Not Found");
        }

    }
}
