package com.coderwjq.smallserver;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.blankj.utilcode.utils.ToastUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ToggleButton mTbSocket;
    private SocketService.SocketControlBinder mSocketBinder;
    private SocketServiceConnection mConnection;
    private boolean mConnectResult = false;
    private ImageView mIvUploadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handlePermission();

        mTbSocket = (ToggleButton) findViewById(R.id.tb_socket);
        mTbSocket.setTextOn("listening...");
        mTbSocket.setTextOff("touch connect");
        mTbSocket.setText("touch connect");
        mTbSocket.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (!mConnectResult) {
                    ToastUtils.showShortToast(MainActivity.this, "service not connected");
                    return;
                } else {
                    if (isChecked) {
                        mSocketBinder.startAsync();
                    } else {
                        mSocketBinder.stopAsync();
                    }
                }

            }
        });

        mIvUploadImage = (ImageView) findViewById(R.id.iv_upload_image);

        startSocketService();
    }

    private void handlePermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean granted) throws Exception {
                        Log.d(TAG, "sdcard:" + Environment.getExternalStorageDirectory());
                        ToastUtils.showShortToast(MainActivity.this, granted + "");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopSocketService();
    }

    private void stopSocketService() {
        unbindService(mConnection);
    }

    private void startSocketService() {
        Intent intent = new Intent();
        intent.setClass(this, SocketService.class);

        mConnection = new SocketServiceConnection();
        bindService(intent, mConnection, BIND_AUTO_CREATE);
        startService(intent);
    }

    private final class SocketServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            ToastUtils.showShortToast(MainActivity.this, "service connect success");
            mTbSocket.setEnabled(true);
            mConnectResult = true;
            mSocketBinder = (SocketService.SocketControlBinder) iBinder;

            mSocketBinder.registResourceHandler(new ResourceInAssetsHandler(MainActivity.this));
            mSocketBinder.registResourceHandler(new UploadImageHandler(MainActivity.this) {

                @Override
                public void onImageLoaded(final String path) {
                    Log.d(TAG, "onImageLoaded: " + path);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            mIvUploadImage.setImageBitmap(bitmap);
                        }
                    });
                }

            });
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            ToastUtils.showShortToast(MainActivity.this, "service disconnect success");
            mTbSocket.setEnabled(false);
            mConnectResult = false;

            mSocketBinder.unRegistResourceHandler();
        }
    }
}
