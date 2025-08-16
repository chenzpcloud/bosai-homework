package com.bosai.homework;

import com.bosai.homework.handler.BaseHandler;
import com.bosai.homework.handler.HighStakesHandler;
import com.bosai.homework.handler.SessionHandler;
import com.bosai.homework.handler.StakeHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Application {


    /**
     * 线程池，定时清理session映射关系
     */
    static final ScheduledExecutorService sessionCleanerScheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws IOException {
        //启动定时任务  执行session的校验用户是否过期
        sessionCleanerScheduler.scheduleAtFixedRate(() -> {
            Constant.userSessionMap.entrySet().removeIf(entry ->
                    !entry.getValue().isExpired()
            );
        }, 1, 1, TimeUnit.MINUTES);

        // 创建HttpServer
        HttpServer server = HttpServer.create(new InetSocketAddress(Constant.PORT), 0);

        // 类似dispatcher  对请求进行分发
        server.createContext("/", new BaseHandler());
        //获取Session
        server.createContext("/session", new SessionHandler());
        //进行投注
        server.createContext("/stake", new StakeHandler());
        //获取前20投注数据
        server.createContext("/highstakes", new HighStakesHandler());

        server.setExecutor(Executors.newCachedThreadPool());
        //启动Http服务
        server.start();
        System.out.println("Application  Stared  Port: " + Constant.PORT);
    }
}