package com.bosai.homework;

/**
 * SessionInfo  会话信息
 */
public class SessionInfo {


    /**
     * 用户ID
     */
    private int userId;

    /**
     * SessionID
     */
    private String sessionId;

    /**
     * Session过期时间
     */
    private long sessionExpiryTime;


    public SessionInfo() {
    }

    public SessionInfo(int userId, String sessionId) {
        this.userId = userId;
        this.sessionId = sessionId;
        this.sessionExpiryTime = System.currentTimeMillis()+Constant.SESSION_EXPIRY_TIME;
    }


    /**
     * 判断Session是否过期
     *
     * @return
     */
   public boolean isExpired() {
        return System.currentTimeMillis() < sessionExpiryTime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getSessionExpiryTime() {
        return sessionExpiryTime;
    }

    public void setSessionExpiryTime(long sessionExpiryTime) {
        this.sessionExpiryTime = sessionExpiryTime;
    }
}
