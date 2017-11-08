package com.guanglun.glbus;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.app.FragmentManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private TextView mTextMessage;
    public  Intent mBusIntent;
    public Intent mLoginIntent;
    public Intent mSettingIntent;
    public  Intent mNotificationIntent;
    public  Intent mFavoriteIntent;

    private busFragment frag2;

    FragmentManager manager = getFragmentManager();
    Fragment frag3 = manager.findFragmentById(3);

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_bus:
                    mTextMessage.setText(R.string.title_bus);
                    LogUtil.d("点击HOME");
                    return true;
                case R.id.navigation_transfer:
                    LogUtil.d("点击dashboard tag");
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
                case  R.id.navigation_favorite:
                    mFavoriteIntent = new Intent();
                    ComponentName component4 = new ComponentName(MainActivity.this, favoriteActivity.class);
                    mFavoriteIntent.setComponent(component4);
                    startActivity(mFavoriteIntent);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LogUtil.d("MainActivity process id is " + android.os.Process.myPid());

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        processExtraData();
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

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        setIntent(intent);//must store the new intent unless getIntent() will return the old one

        processExtraData();

    }

    private void processExtraData(){

        Intent intent = getIntent();
        //use the data received here
    }
}
