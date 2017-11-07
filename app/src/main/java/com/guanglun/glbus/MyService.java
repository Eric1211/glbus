package com.guanglun.glbus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class MyService extends Service {
    public MyService() {
    }
    public static final String TAG = "MyService";
    private NotificationManager manager;
    private MyBinder mBinder = new MyBinder();



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate() executed");
        Log.d("MyService", "MyService thread id is " + Thread.currentThread().getId());
        Intent intent = new Intent(MyService.this,MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,0);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        //设置通知到来时的一些选项

        mBuilder.setTicker("notify_activity");
        //通知消息下拉是显示的文本内容
        mBuilder.setContentText("你收到了一个红包，速度进入APP领取。一个很大的红包哦");
        //通知栏消息下拉时显示的标题
        mBuilder.setContentTitle("测试通知栏通知");
        //接收到通知时，按手机的默认设置进行处理，声音，震动，灯
        mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
        //通知栏显示图标
        mBuilder.setSmallIcon(R.drawable.notification_template_icon_bg);
        mBuilder.setContentIntent(pendingIntent);

        Notification notification = mBuilder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        manager.notify(1,notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand() executed");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "new Thread start");
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() executed");

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    class MyBinder extends Binder {
        public void startDownload() {
            Log.d("MyBinder", "startDownload() executed");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // 执行具体的下载任务
                }
            }).start();
        }

    }

}
