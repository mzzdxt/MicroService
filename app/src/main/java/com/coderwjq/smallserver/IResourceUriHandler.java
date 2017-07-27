package com.coderwjq.smallserver;

import java.io.IOException;

/**
 * @Created by coderwjq on 2017/7/27 10:29.
 * @Desc 根据URI处理不同的请求
 */

public interface IResourceUriHandler {
    boolean accept(String uri);
    void postHandle(String uri, HttpContext httpContext) throws IOException;
}
