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
    public  Intent mNotificationIntent;
    public  Intent mFavoriteIntent;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_bus:
                    mTextMessage.setText(R.string.title_bus);
                    Log.d("点击HOME tag", "点击ET_phone");
                    return true;
                case R.id.navigation_transfer:
                    Log.d("点击dashboard tag", "点击ET_phone");
                   mTextMessage.setText(R.string.title_transfer);
                    mLoginIntent =new Intent();
                    ComponentName component = new ComponentName(MainActivity.this, LoginActivity.class);
                    mLoginIntent.setComponent(component);
                    startActivity(mLoginIntent);
                    return true;
                case R.id.navigation_notifications:
                    mNotificationIntent =new Intent();
                    ComponentName component2 = new ComponentName(MainActivity.this, SettingsActivity.class);
                    mNotificationIntent.setComponent(component2);
                    startActivity(mNotificationIntent);
                    return true;
                case R.id.navigation_setting:
                    mSettingIntent =new Intent();
                    ComponentName component3 = new ComponentName(MainActivity.this, debugScreenActivity.class);
                    mSettingIntent.setComponent(component3);
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
     //   Log.d("MyService", "MainActivity thread id is " + Thread.currentThread().getId());




        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

     @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }




}
