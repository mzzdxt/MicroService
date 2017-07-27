package com.coderwjq.smallserver;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * @Created by coderwjq on 2017/7/25 19:59.
 * @Desc
 */

public final class HttpContext {
    private Socket socket;
    private Map<String, String> header = new HashMap<>();
    private String type;

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getRequestHeaderValue(String key) {
        return header.get(key);
    }

    public void addRequestHeader(String key, String value) {
        header.put(key, value);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
