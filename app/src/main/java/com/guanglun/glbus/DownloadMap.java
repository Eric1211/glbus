package com.guanglun.glbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class DownloadMap extends AppCompatActivity {

    private static final String TAG = "DownloadMap";
    private static final int DEFAULT_POOL_SIZE = 1;
    private static final int GET_LENGTH_SUCCESS = 1;
    //下载路径
    private String downloadPath = Environment.getExternalStorageDirectory() +
            File.separator + "download";

    //    private String mUrl = "http://ftp.neu.edu.cn/mirrors/eclipse/technology/epp/downloads/release/juno/SR2/eclipse-java-juno-SR2-linux-gtk-x86_64.tar.gz";
 //   private String mUrl = "http://p.gdown.baidu.com/c4cb746699b92c9b6565cc65aa2e086552651f73c5d0e634a51f028e32af6abf3d68079eeb75401c76c9bb301e5fb71c144a704cb1a2f527a2e8ca3d6fe561dc5eaf6538e5b3ab0699308d13fe0b711a817c88b0f85a01a248df82824ace3cd7f2832c7c19173236";
    private  String mUrl = "https://codeload.github.com/liuling07/MultiTaskAndThreadDownload/zip/master";
    private ProgressBar mProgressBar;
    private TextView mPercentTV;
    SharedPreferences mSharedPreferences = null;
    long mFileLength = 0;
    Long mCurrentLength = 0L;

    /**
     * 下载的AsyncTask
     */
    private class DownloadAsyncTask extends AsyncTask<String, Integer, Long> {
        private static final String TAG = "DownloadAsyncTask";
        private long beginPosition = 0;
        private long endPosition = 0;
        private String currentThreadIndex;
        private long current = 0;



        public DownloadAsyncTask(long beginPosition, long endPosition) {
            this.beginPosition = beginPosition;
            this.endPosition = endPosition;
        }

        @Override
        protected Long doInBackground(String... params) {
            Log.i(TAG, "downloading");
            String urlt = params[0];
            currentThreadIndex = urlt + params[1];

            if(mUrl == null) {
                return null;
            }


            InputStream in = null;
            RandomAccessFile fos = null;
            OutputStream output = null;
            BufferedReader  reader = null;
            HttpURLConnection connection =  null;
            try {
                //创建存储文件夹
                File dir = new File(downloadPath);
                if(!dir.exists()) {
                    dir.mkdir();
                }
                //本地文件
                File file = new File(downloadPath + File.separator + mUrl.substring(mUrl.lastIndexOf("/") + 1));
                //获取之前下载保存的信息，从之前结束的位置继续下载
                //这里加了判断file.exists()，判断是否被用户删除了，如果文件没有下载完，但是已经被用户删除了，则重新下载
                long downedPosition = mSharedPreferences.getLong(currentThreadIndex, 0);
                if(file.exists() && downedPosition != 0){

                    beginPosition = beginPosition + downedPosition;
                    current = downedPosition;
                    synchronized (mCurrentLength) {
                        mCurrentLength += downedPosition;
                    }
                    //创建文件输出流
                    output = new FileOutputStream(file);
                    //获取下载输入流
                    URL url = new URL(mUrl);
                    //得到connection对象。
                     connection = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);

                    //此时获取的是字节流
                     in = connection.getInputStream();
                    //对获取到的输入流进行读取
                      reader = new BufferedReader(new InputStreamReader(in)); //将字节流转化成字符流
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine())!= null) {
                        response.append(line);
                    }



                    //写入本地
                    file.createNewFile();
                    byte buffer [] = new byte[1024];
                    int inputSize = -1;
                    //获取文件总大小，用于计算进度
                    long total = response.length();
                    int count = 0; //已下载大小
                    while((inputSize = in.read(buffer)) != -1) {
                        output.write(buffer, 0, inputSize);
                        count += inputSize;
                        //更新进度
                        this.publishProgress((int) ((count / (float) total) * 100));
                        //一旦任务被取消则退出循环，否则一直执行，直到结束
                        if(isCancelled()) {
                            output.flush();
                            return null;
                        }
                    }
                    output.flush();
                } else {
                    long readedSize = file.length(); //文件大小，即已下载大小
                    //设置下载的数据位置XX字节到XX字节

                    URL url = new URL(mUrl);
                    //得到connection对象。
                   connection = (HttpURLConnection) url.openConnection();
                    //设置请求方式
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setChunkedStreamingMode(0);   //上传大文件的时候，很容易就OOM了，原因是数据默认是全读入buffer的，可以设置setChunkedStreamingMode设置块大小
                    in = connection.getInputStream();
                    //对获取到的输入流进行读取
                    reader = new BufferedReader(new InputStreamReader(in)); //将字节流转化成字符流
                    long total = 0;
                    try {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine())!= null) {  //
                            response.append(line);
                        }

                         total = response.length();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }


                    //创建文件输出流
                    fos = new RandomAccessFile(file, "rw");
                    //从文件的size以后的位置开始写入，其实也不用，直接往后写就可以。有时候多线程下载需要用
                    fos.seek(readedSize);
                    //这里用RandomAccessFile和FileOutputStream都可以，只是使用FileOutputStream的时候要传入第二哥参数true,表示从后面填充
//                    output = new FileOutputStream(file, true);

                    byte buffer [] = new byte[1024];
                    int inputSize = -1;
                    int count = (int)readedSize;
                    while((inputSize = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, inputSize);
//                        output.write(buffer, 0, inputSize);
                        count += inputSize;
                        this.publishProgress((int) ((count / (float) total) * 100));
                        if(isCancelled()) {
//                            output.flush();
                            return null;
                        }
                    }
//                    output.flush();
                }
            } catch (MalformedURLException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } finally{
                try{
                    if(in != null) {
                        in.close();
                    }
                    if(output != null) {
                        output.close();
                    }
                    if(fos != null) {
                        fos.close();
                    }

                    if(reader != null) {
                        try {
                             reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    if(connection != null) {
                        connection.disconnect();
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            Log.i(TAG, "download begin ");
            Toast.makeText(DownloadMap.this, "开始下载", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            Log.i(TAG, "downloading  " + values[0]);
            //更新界面进度条
            updateProgress();
        }

        @Override
        protected void onPostExecute(Long aLong) {
            Log.i(TAG, "download success " + aLong);
            Toast.makeText(DownloadMap.this, "下载结束", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aLong);
        }
    }



    private InnerHandler mHandler = new InnerHandler();

    //创建线程池
    private Executor mExecutor = Executors.newCachedThreadPool();
    private List<DownloadAsyncTask> mTaskList = new ArrayList<DownloadAsyncTask>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_map);

        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mPercentTV = (TextView) findViewById(R.id.percent_tv);
        mSharedPreferences = getSharedPreferences("download", Context.MODE_PRIVATE);
        //开始下载
        findViewById(R.id.begin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        //创建存储文件夹
                        File dir = new File(downloadPath);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        try {
                            URL url = new URL(mUrl);
                            //得到connection对象。
                            URLConnection urlConnection = url.openConnection();
                            InputStream is = urlConnection.getInputStream();
                            //获取文件总长度
                            mFileLength = urlConnection.getContentLength();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Message.obtain(mHandler, GET_LENGTH_SUCCESS).sendToTarget();
                    }
                }.start();
            }
        });

        //暂停下载
        findViewById(R.id.end).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DownloadAsyncTask task : mTaskList) {
                    if (task != null && task.getStatus() == AsyncTask.Status.RUNNING ) {
                        task.cancel(true);
                    }
                }
                mTaskList.clear();
            }
        });

    }

    /**
     * 开始下载
     * 根据待下载文件大小计算每个线程下载位置，并创建AsyncTask
     */
    private void beginDownload() {
        mCurrentLength = 0L;
        mPercentTV.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(0);
        Integer start = new Integer(2);
        long blockLength = mFileLength / DEFAULT_POOL_SIZE;
        for (int i = 0; i < DEFAULT_POOL_SIZE; i++) {
            long beginPosition = i * blockLength;//每条线程下载的开始位置
            long endPosition = (i + 1) * blockLength;//每条线程下载的结束位置
            if (i == (DEFAULT_POOL_SIZE - 1)) {
                endPosition = mFileLength;//如果整个文件的大小不为线程个数的整数倍，则最后一个线程的结束位置即为文件的总长度
            }


            DownloadAsyncTask task = new DownloadAsyncTask(beginPosition, endPosition);
            mTaskList.add(task);
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUrl, String.valueOf(i));
        }
    }

    /**
     * 更新进度条
     */
    synchronized public void updateProgress() {
        int percent = (int) Math.ceil((float)mCurrentLength / (float)mFileLength * 100);
//        Log.i(TAG, "downloading  " + mCurrentLength + "," + mFileLength + "," + percent);
        if(percent > mProgressBar.getProgress()) {
            mProgressBar.setProgress(percent);
            mPercentTV.setText("下载进度：" + percent + "%");
            if (mProgressBar.getProgress() == mProgressBar.getMax()) {
                Toast.makeText(DownloadMap.this, "下载结束", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        for(DownloadAsyncTask task: mTaskList) {
            if(task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
                task.cancel(true);
            }
            mTaskList.clear();
        }
        super.onDestroy();
    }

    private class InnerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_LENGTH_SUCCESS :
                    beginDownload();
                    break;
            }
            super.handleMessage(msg);
        }
    }



}
