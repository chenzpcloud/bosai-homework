package com.bosai.homework;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Constant 常量 类
 */
public interface Constant {


    /**
     * session 过期时间
     */
    long SESSION_EXPIRY_TIME = 600_000;


    /**
     * 用户和Session 映射关系
     */
    ConcurrentHashMap<Integer, SessionInfo> userSessionMap = new ConcurrentHashMap<>();

    /**
     * 用户和投注映射关系
     */
    ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, AtomicInteger>> stakesData =
            new ConcurrentHashMap<>();


    /**
     * 前20的投注数
     */
    int TOP_STAKE = 20;


    /**
     * 服务端口
     */
    int PORT = 8001;


}
