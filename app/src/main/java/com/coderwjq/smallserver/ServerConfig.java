package com.coderwjq.smallserver;

/**
 * @Created by coderwjq on 2017/7/25 18:02.
 * @Desc
 */

public final class ServerConfig {

    private int port;
    private int maxListener;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxListener() {
        return maxListener;
    }

    public void setMaxListener(int maxListener) {
        this.maxListener = maxListener;
    }
}
