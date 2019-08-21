package com.guanglun.glbus;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.app.FragmentManager;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private TextView mTextMessage;
    public  Intent mBusIntent;
    public Intent mLoginIntent;
    public Intent mSettingIntent;
    public  Intent mNotificationIntent;
    public  Intent mFavoriteIntent;
    private busFragment frag2;


    private final int SDK_PERMISSION_REQUEST = 127;
    private ListView FunctionList;
    private String permissionInfo;


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
		FunctionList = (ListView) findViewById(R.id.functionList);



		List<Student> stuList=new ArrayList<>();
		for(int i=0;i<10;i++){
			Student stu=new Student();
			stu.setAge(10+i);
			stu.setName("name"+i);
			stu.setPhoto(R.mipmap.ic_launcher);
			stuList.add(stu);
		}


		MyAdapter adapter=new MyAdapter(stuList,MainActivity.this);
		FunctionList.setAdapter(adapter);

		// after andrioid m,must request Permiision on runtime
		getPersimmions();
		
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

@TargetApi(23)
	private void getPersimmions() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			ArrayList<String> permissions = new ArrayList<String>();
			/***
			 * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
			 */
			// 定位精确位置
			if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
				permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
			}
			if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
				permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
			}
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
			// 读写权限
			if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
				permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
			}
			// 读取电话状态权限
			if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
				permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
			}
			
			if (permissions.size() > 0) {
				requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
			}
		}
	}

	@TargetApi(23)
	private boolean addPermission(ArrayList<String> permissionsList, String permission) {
		if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
			if (shouldShowRequestPermissionRationale(permission)){
				return true;
			}else{
				permissionsList.add(permission);
				return false;
			}
				
		}else{
			return true;
		}
	}

	@TargetApi(23)
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		// TODO Auto-generated method stub
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		FunctionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				Class<?> TargetClass = null;
				switch (arg2) {
				case 0:
				//	TargetClass = LocationActivity.class;
					break;
				case 1:
				//	TargetClass = LocationOption.class;
					break;
				case 2:
				//	TargetClass = LocationAutoNotify.class;
					break;
				case 3:
				//	TargetClass = LocationFilter.class;
					break;
				case 4:
				//	TargetClass = NotifyActivity.class;
					break;
				case 5:
				//	TargetClass = IndoorLocationActivity.class;
					break;
				case 6:
                 //   TargetClass = IsHotWifiActivity.class;
					break;
				case 7:
                  //  TargetClass = ForegroundActivity.class;
					break;
				case 8:
				//	TargetClass = QuestActivity.class;
					break;
				default:
					break;
				}
				if (TargetClass != null) {
					Intent intent = new Intent(MainActivity.this, TargetClass);
					intent.putExtra("from", 0);
					startActivity(intent);
				}
			}
		});
	}

	private List<String> getData() {

		List<String> data = new ArrayList<String>();
		data.add("基础定位功能");
		data.add("配置定位参数");
		data.add("自定义回调示例");
		data.add("连续定位示例");
		data.add("位置消息提醒");
		data.add("室内定位功能");
		data.add("判断移动热点");
		data.add("android 8.0后台定位示例");
		data.add("常见问题说明");

		return data;
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
