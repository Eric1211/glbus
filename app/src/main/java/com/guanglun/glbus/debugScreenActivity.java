package com.guanglun.glbus;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class debugScreenActivity extends AppCompatActivity implements View.OnClickListener {


    private Button startService;
    private Button stopService;
    private Button bindService;
    private Button unbindService;
    private MyService.MyBinder myBinder;
 //   private ownBroadcastReceiver mFinishReceiver = new ownBroadcastReceiver();


    // create service connection
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("service", "onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("service", "onServiceConnected");
            myBinder = (MyService.MyBinder) service;
            myBinder.startDownload();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_screen);


        startService = (Button) findViewById(R.id.start_service);
        stopService = (Button) findViewById(R.id.stop_service);
        bindService = (Button) findViewById(R.id.bind_service);
        unbindService = (Button) findViewById(R.id.unbind_service);

        startService.setOnClickListener((View.OnClickListener)this);
        stopService.setOnClickListener((View.OnClickListener)this);
        bindService.setOnClickListener((View.OnClickListener)this);
        unbindService.setOnClickListener((View.OnClickListener)this);

     //   IntentFilter filter = new IntentFilter("finish");
     //   registerReceiver(mFinishReceiver, filter);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                Log.d("MyService", "click Start Service button");
                Intent startIntent = new Intent(this, MyService.class);
                startService(startIntent);

                break;
            case R.id.stop_service:
                Log.d("MyService", "click Stop Service button");
                Intent stopIntent = new Intent(this, MyService.class);
                stopService(stopIntent);
                break;
            case R.id.bind_service:
                Intent bindIntent = new Intent(this, MyService.class);
                bindService(bindIntent, connection, BIND_AUTO_CREATE);
                break;
            case R.id.unbind_service:
                Log.d("MyService", "click Unbind Service button");
                unbindService(connection);
                break;

            case R.id.broadcast:
                Log.d("MyService", "send broadcast");

                // 测试通知栏  通知栏信息类
                //TODO 还需要测试不同机型是否能收到。
                //TODO 还需要在熄屏时也显示这个通知。
                NewMessageNotification newMsg = new NewMessageNotification();
                newMsg.notify(this, "当前天气是 “20度”", 1);
                break;
            default:
                break;
        }
    }

}
