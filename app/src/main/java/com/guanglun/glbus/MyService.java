package com.guanglun.glbus;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;


// https://blog.csdn.net/forlong401/article/details/13772733
public class MyService extends Service {
    public MyService() {
    }



    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d("onCreate() executed");
        LogUtil.d("process ID is " + android.os.Process.myPid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.d("onStartCommand() executed");
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.d("new Thread start");
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.d("onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    class MyBinder extends Binder {
        public void startDownload() {
            LogUtil.d("startDownload() executed");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 执行具体的下载任务
                }
            }).start();
        }
    }

    IMyAidlInterface.Stub mBinder = new IMyAidlInterface.Stub()
    {
        @Override
        public String toUpperCase(String str) throws RemoteException {
            if (str != null) {
                LogUtil.d("toUpperCase called");
                return str.toUpperCase();
            }
            return null;
        }

        @Override
        public int plus(int a, int b) throws RemoteException {
            LogUtil.d("plus called a= "+ a + ", b = " + b);
            return a + b;
        }


        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
                               double aDouble, String aString) {
            // do nothing.
        }

    };

}
