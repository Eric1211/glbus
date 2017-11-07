package com.guanglun.glbus;

import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import com.guanglun.*;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView mTextMessage;

    public  Intent mBusIntent;
    public Intent mLoginIntent;
    public Intent mSettingIntent;
    private Button startService;
    private Button stopService;
    private Button bindService;
    private Button unbindService;
    private MyService.MyBinder myBinder;
    private ownBroadcastReceiver mFinishReceiver = new ownBroadcastReceiver();

   // filter.addAction("android.intent.action.MY_BROADCAST");
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                 //   mTextMessage.setText(R.string.title_home);
                    Log.d("点击HOME tag", "点击ET_phone");
                    return true;
                case R.id.navigation_dashboard:
                    Log.d("点击dashboard tag", "点击ET_phone");
                //    mTextMessage.setText(R.string.title_dashboard);
                    mLoginIntent =new Intent();
                    ComponentName component = new ComponentName(MainActivity.this, LoginActivity.class);
                    mLoginIntent.setComponent(component);
                    startActivity(mLoginIntent);
                    return true;
                case R.id.navigation_notifications:
                    mSettingIntent =new Intent();
                    ComponentName component2 = new ComponentName(MainActivity.this, SettingsActivity.class);
                    mSettingIntent.setComponent(component2);
                    startActivity(mSettingIntent);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MyService", "MainActivity thread id is " + Thread.currentThread().getId());
        startService = (Button) findViewById(R.id.start_service);
        stopService = (Button) findViewById(R.id.stop_service);
        bindService = (Button) findViewById(R.id.bind_service);
        unbindService = (Button) findViewById(R.id.unbind_service);

        startService.setOnClickListener((View.OnClickListener) this);
        stopService.setOnClickListener((View.OnClickListener) this);
        bindService.setOnClickListener(this);
        unbindService.setOnClickListener(this);


        IntentFilter filter = new IntentFilter("finish");
        registerReceiver(mFinishReceiver, filter);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

     @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
           //     Intent startIntent = new Intent(this, MyService.class);
             //   startService(startIntent);
                NewMessageNotification newMsg = new NewMessageNotification();
                newMsg.notify(this, "当前天气是 “20度”", 1);
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
                send();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        //发送广播
        sendBroadcast(new Intent("finish"));
        //一定要注销广播
        unregisterReceiver(mFinishReceiver);
    }


    private void send() {
        Intent intent = new Intent("android.intent.action.MY_BROADCAST");
        intent.putExtra("msg", "hello receiver.");
        sendBroadcast(intent);
    }


}
