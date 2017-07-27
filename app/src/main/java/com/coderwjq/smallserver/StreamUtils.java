package com.coderwjq.smallserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Created by coderwjq on 2017/7/25 20:07.
 * @Desc
 */

public final class StreamUtils {

    public static String readLine(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();

        int a = 0, b = 0;

        while (b != -1 && !(a == '\r' && b == '\n')) {
            a = b;
            b = is.read();
            sb.append((char) b);
        }

        if (sb.length() == 0) {
            return null;
        }

        return sb.toString();
    }

    public static byte[] readRawFromStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[10240];

        int read;

        while ((read = inputStream.read(buffer)) > 0){
            outputStream.write(buffer, 0, read);
        }

        return outputStream.toByteArray();
    }
}
