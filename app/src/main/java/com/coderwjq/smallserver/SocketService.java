package com.coderwjq.smallserver;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketService extends Service {
    private static final String TAG = "SocketService";

    private ServerConfig mServerConfig;
    private ExecutorService mThreadPool;
    private ServerSocket mServerSocket;
    private SocketControlBinder mSocketControlBinder;
    private Set<IResourceUriHandler> mHandlers;

    public SocketService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSocketControlBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mServerConfig = new ServerConfig();
        mServerConfig.setPort(8088);
        mServerConfig.setMaxListener(50);

        mThreadPool = Executors.newCachedThreadPool();

        mSocketControlBinder = new SocketControlBinder();

        mHandlers = new HashSet<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public class SocketControlBinder extends Binder {
        private boolean mIsEnable = false;

        public boolean isEnable() {
            return mIsEnable;
        }

        /**
         * 异步结束
         */
        public void stopAsync() {
            Log.d(TAG, "stopAsync() called");

            if (!mIsEnable) {
                return;
            }

            mIsEnable = false;

            mThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        mServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mServerSocket = null;
                }
            });
        }

        /**
         * 异步开启
         */
        public void startAsync() {
            Log.d(TAG, "startAsync() called");

            mIsEnable = true;

            mThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    doProSync();
                }
            });
        }

        private void doProSync() {
            try {
                InetSocketAddress socketAddress = new InetSocketAddress(mServerConfig.getPort());
                mServerSocket = new ServerSocket();
                mServerSocket.bind(socketAddress);

                while (mIsEnable) {
                    // 阻塞式方法
                    final Socket remotePeer = mServerSocket.accept();
                    mThreadPool.submit(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "remotePeer:" + remotePeer.getRemoteSocketAddress().toString());
                            onAcceptRemotePeer(remotePeer);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * 接收到数据后的处理过程
         *
         * @param remotePeer
         */
        private void onAcceptRemotePeer(Socket remotePeer) {
            HttpContext httpContext = new HttpContext();

            try {
                httpContext.setSocket(remotePeer);
                InputStream is = remotePeer.getInputStream();
                String headerLine = null;
                String readLine = StreamUtils.readLine(is);

                httpContext.setType(readLine.split(" ")[0]);
                String resourceUri = readLine.split(" ")[1];
                String httpVersion = readLine.split(" ")[2];

                Log.e(TAG, "type: " + readLine.split(" ")[0]);
                Log.e(TAG, "uri: " + resourceUri);
                Log.e(TAG, "version: " + httpVersion);

                while ((headerLine = StreamUtils.readLine(is)) != null) {

                    // 处理空行
                    if (headerLine.equals("\r\n")) break;

                    String[] pairs = headerLine.split(": ");

                    if (pairs.length > 1) {
                        httpContext.addRequestHeader(pairs[0], pairs[1]);
                        Log.d(TAG, pairs[0] + ":" + pairs[1]);
                    }
                }

                // route
                for (IResourceUriHandler handler : mHandlers) {
                    if (!handler.accept(resourceUri)) {
                        continue;
                    }

                    handler.postHandle(resourceUri, httpContext);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    remotePeer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void registResourceHandler(IResourceUriHandler handler) {
            mHandlers.add(handler);
        }

        public void unRegistResourceHandler() {
            mHandlers.clear();
        }
    }
}
