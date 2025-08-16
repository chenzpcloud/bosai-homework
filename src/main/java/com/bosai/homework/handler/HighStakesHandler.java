package com.bosai.homework.handler;

import com.bosai.homework.Constant;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HighStakesHandler 投注排名处理器
 */
public class HighStakesHandler extends BaseHandler implements HttpHandler {
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
                buildResponse(httpExchange, 400, "Invalid path format");
                return;
            }

            try {
                int betOfferId = Integer.parseInt(parts[1]);
                ConcurrentHashMap<Integer, AtomicInteger> offerStakes = Constant.stakesData.get(betOfferId);
                if (offerStakes == null || offerStakes.isEmpty()) {
                    buildResponse(httpExchange, 200, "");
                    return;
                }

                // 优先队列，用于存储前20名
                PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(
                        Constant.TOP_STAKE,
                        Comparator.comparingInt(Map.Entry::getValue)
                );

                // 判断队列数，如果小于20，则加入队列，否则判断当前值是否小于队列中的最小值，
                // 如果是则替换队列中的最小值
                for (Map.Entry<Integer, AtomicInteger> entry : offerStakes.entrySet()) {
                    int customerId = entry.getKey();
                    int stake = entry.getValue().get();
                    if (minHeap.size() < Constant.TOP_STAKE) {
                        minHeap.offer(new AbstractMap.SimpleEntry<>(customerId, stake));
                    } else if (minHeap.peek().getValue() < stake) {
                        minHeap.poll();
                        minHeap.offer(new AbstractMap.SimpleEntry<>(customerId, stake));
                    }
                }

                // 构建返回结果集
                List<String> results = new ArrayList<>();
                while (!minHeap.isEmpty()) {
                    Map.Entry<Integer, Integer> entry = minHeap.poll();
                    results.add(entry.getKey() + "=" + entry.getValue());
                }
                // 小根队列 结果集反转
                Collections.reverse(results);
                buildResponse(httpExchange, 200, String.join(",", results));
                System.out.println(String.format("HighStakesHandler  Results:%d", results));
            } catch (NumberFormatException e) {
                buildResponse(httpExchange, 400, "Invalid bet offer ID format");
            }
        } catch (Exception e) {
            buildResponse(httpExchange, 500, "Internal Server Error");
        }
    }
}
