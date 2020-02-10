package com.example.scanme;



import android.app.IntentService;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class WifiServerService extends IntentService {

    private static final String TAG = "WifiServerService";

    public interface OnProgressChangListener {

        void onProgressChanged(FileTransfer fileTransfer, int progress);

        void onTransferFinished(File file);

    }

    private ServerSocket serverSocket;

    private InputStream inputStream;

    private ObjectInputStream objectInputStream;

    private FileOutputStream fileOutputStream;

    private OnProgressChangListener progressChangListener;

    public class MyBinder extends Binder {
        public WifiServerService getService() {
            return WifiServerService.this;
        }
    }

    public WifiServerService() {
        super("WifiServerService");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        clean();
        File file = null;
        try {
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(1995));
            Socket client = serverSocket.accept();
            Log.e(TAG, "getHostAddress : " + client.getInetAddress().getHostAddress());
            inputStream = client.getInputStream();
            objectInputStream = new ObjectInputStream(inputStream);
            FileTransfer fileTransfer = (FileTransfer) objectInputStream.readObject();
            Log.e(TAG, "Documents to be received: " + fileTransfer);
            String name = new File(fileTransfer.getFilePath()).getName();
            //将文件存储至指定位置
            file = new File(Environment.getExternalStorageDirectory() + "/" + name);
            fileOutputStream = new FileOutputStream(file);
            byte[] buf = new byte[512];
            int len;
            long total = 0;
            int progress;
            while ((len = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, len);
                total += len;
                progress = (int) ((total * 100) / fileTransfer.getFileLength());
                Log.e(TAG, "Document receiving progress: " + progress);
                if (progressChangListener != null) {
                    progressChangListener.onProgressChanged(fileTransfer, progress);
                }
            }
            serverSocket.close();
            inputStream.close();
            objectInputStream.close();
            fileOutputStream.close();
            serverSocket = null;
            inputStream = null;
            objectInputStream = null;
            fileOutputStream = null;
            Log.e(TAG, "The file was successfully received. The MD5 code of the file is：" + Md5Util.getMd5(file));
        } catch (Exception e) {
            Log.e(TAG, "File reception Exception: " + e.getMessage());
        } finally {
            clean();
            if (progressChangListener != null) {
                progressChangListener.onTransferFinished(file);
            }
            //Start the service again and wait for the client to connect next time
            startService(new Intent(this, WifiServerService.class));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clean();
    }

    public void setProgressChangListener(OnProgressChangListener progressChangListener) {
        this.progressChangListener = progressChangListener;
    }

    private void clean() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
                inputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (objectInputStream != null) {
            try {
                objectInputStream.close();
                objectInputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileOutputStream != null) {
            try {
                fileOutputStream.close();
                fileOutputStream = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
