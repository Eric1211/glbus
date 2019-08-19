package com.guanglun.glbus;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
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
    private Button sendBroadcast;
    private Button asyncDownBtn;
    private MyService.MyBinder myBinder;
    private IMyAidlInterface myAIDLService;
    //   private ownBroadcastReceiver mFinishReceiver = new ownBroadcastReceiver();


    // create service connection
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtil.d("onServiceDisconnected");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtil.d("onServiceConnected");
            myAIDLService = IMyAidlInterface.Stub.asInterface(service);
            try {
                int result = myAIDLService.plus(3, 5);
                String upperStr = myAIDLService.toUpperCase("hello world");
                LogUtil.d("result is " + result);
                LogUtil.d("upperStr is " + upperStr);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
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
        sendBroadcast = (Button) findViewById(R.id.broadcast);
        asyncDownBtn = (Button) findViewById(R.id.startDownLoad);

        startService.setOnClickListener((View.OnClickListener)this);
        stopService.setOnClickListener((View.OnClickListener)this);
        bindService.setOnClickListener((View.OnClickListener)this);
        unbindService.setOnClickListener((View.OnClickListener)this);
        sendBroadcast.setOnClickListener((View.OnClickListener)this);
        asyncDownBtn.setOnClickListener((View.OnClickListener)this);

        LogUtil.d("debugScreenActivity process ID is " + android.os.Process.myPid());
        processExtraData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_service:
                LogUtil.d("click Start Service button");

                Intent startIntent = new Intent(debugScreenActivity.this,com.guanglun.glbus.MyService.class);
                startService(startIntent);

                break;
            case R.id.stop_service:
              LogUtil.d("click Stop Service button");
                Intent stopIntent = new Intent(debugScreenActivity.this,com.guanglun.glbus.MyService.class);
                stopService(stopIntent);
                break;
            case R.id.bind_service:
                Intent bindIntent = new Intent(debugScreenActivity.this,com.guanglun.glbus.MyService.class);
                bindService(bindIntent, connection, BIND_AUTO_CREATE);
                break;
            case R.id.unbind_service:
                LogUtil.d("click Unbind Service button");
                unbindService(connection);
                break;
            case R.id.startDownLoad:
                Intent tmpIntent =new Intent();
                ComponentName component = new ComponentName(debugScreenActivity.this, DownloadActivity.class);
                tmpIntent.setComponent(component);
                startActivity(tmpIntent);
                break;
            case R.id.broadcast:
                LogUtil.d("send broadcast");

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
