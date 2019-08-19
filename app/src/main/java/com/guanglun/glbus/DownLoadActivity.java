package com.guanglun.glbus;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadActivity extends AppCompatActivity {

    public static final int DOWNLOAD_MESSAGE_CODE = 10001;
    private static final int DOWNLOAD_FAILURE_CODE = 10002;
    public static final String APP_URL = "http://www.imooc.com/mobile/mukewang.apk";
    private static final String TAG = "DownloadActivity";
    private int contentLength;
    private Handler mHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_down_load);
        /**
         * 主线程
         * 点击按钮
         * 开启子线程下载
         * 主线程更新进度条
         */
        findViewById(R.id.startDownload2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //子线程执行网络请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        download(APP_URL);
                    }
                }).start();

            }
        });

        findViewById(R.id.stopDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //子线程执行网络请求
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        download(APP_URL);
                    }
                }).stop();

            }
        });


        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressbar);
        final TextView errorText = (TextView) findViewById(R.id.fail_txt);
        final TextView processText = (TextView) findViewById(R.id.percent_tv2);
        mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case DOWNLOAD_MESSAGE_CODE:
                        //更新进度
                        processText.setText(msg.arg1 + "/" + contentLength);
                        progressBar.setProgress((Integer) msg.obj);
                        break;
                    case DOWNLOAD_FAILURE_CODE:
                        errorText.setText("download Failed!");
                }
            }
        };
    }

    private void download(String appUrl) {
        try {
            URL url = new URL(appUrl);
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();
            //获取文件总长度
            contentLength = urlConnection.getContentLength();
            //建立目录
            String downloadFolderName = Environment.getExternalStorageDirectory()
                    + File.separator + "imooc" + File.separator;
            File file = new File(downloadFolderName);
            if(!file.exists()){
                file.mkdir();
                Log.e(TAG, "no file");
            }
            String fileName = downloadFolderName + "imooc.apk";
            //建立apk文件
            File apkFile = new File(fileName);
            if(apkFile.exists()){
                apkFile.delete();
            }
            //记录下载进度
            int downloadSize = 0;
            byte[] bytes = new byte[1024];
            int length = 0;
            OutputStream os = new FileOutputStream(fileName);
            while((length = is.read(bytes)) != -1){
                os.write(bytes, 0, length);
                downloadSize += length;
                /**
                 * 更新 UI
                 */
                Message message = Message.obtain();
                //通过message向UI线程发送进度条进度
                message.obj = downloadSize * 100 / contentLength;
                message.arg1 = downloadSize;
                message.what = DOWNLOAD_MESSAGE_CODE;
                mHandler.sendMessage(message);
            }
            is.close();
            os.close();
            Log.i(TAG, "success");
        } catch (MalformedURLException e) {
            notifyDownloadFailed();
            e.printStackTrace();
        } catch (IOException e) {
            notifyDownloadFailed();
            e.printStackTrace();
        }
    }

    private void notifyDownloadFailed() {
        Message message = Message.obtain();
        message.what = DOWNLOAD_FAILURE_CODE;
        mHandler.sendMessage(message);
    }
}