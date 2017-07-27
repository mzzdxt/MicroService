package com.coderwjq.smallserver;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 * @Created by coderwjq on 2017/7/27 10:36.
 * @Desc
 */

public class UploadImageHandler implements IResourceUriHandler {
    private static final String TAG = "UploadImageHandler";
    private String mAcceptPrefix = "/image/";
    private Activity mContext;

    public UploadImageHandler(Activity context) {
        mContext = context;
    }

    @Override
    public boolean accept(String uri) {
        return uri.startsWith(mAcceptPrefix);
    }

    @Override
    public void postHandle(String uri, HttpContext httpContext) throws IOException {
        // handle request
        long contentLength = Long.parseLong(httpContext.getRequestHeaderValue("content-length").trim());

        File imageFile = new File(Environment.getExternalStorageDirectory(), "tempImage.jpg");

        Log.e(TAG, "imageFile.getPath(): " + imageFile.getPath());

        if (imageFile.exists()) {
            imageFile.delete();
        }

        byte[] buffer = new byte[10240];
        int read;

        long leftLenght = contentLength;

        FileOutputStream fos = new FileOutputStream(imageFile.getPath());
        InputStream inputStream = httpContext.getSocket().getInputStream();

        while (leftLenght > 0 && (read = inputStream.read(buffer)) > 0) {
            fos.write(buffer, 0, read);
            leftLenght -= read;
        }

        fos.close();

        // make response
        Log.d(TAG, "make response");
        OutputStream outputStream = httpContext.getSocket().getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.println("HTTP/1.1 200 OK");
        printWriter.println();

        onImageLoaded(imageFile.getPath());
    }

    public void onImageLoaded(String path) {

    }
}
