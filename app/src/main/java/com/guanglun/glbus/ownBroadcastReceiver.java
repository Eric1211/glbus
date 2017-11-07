package com.guanglun.glbus;

import android.content.ComponentName;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ownBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.

        String msg = intent.getStringExtra("msg");
        Log.i("TAG", "MyReceiver："+msg);

        //    throw new UnsupportedOperationException("Not yet implemented");
    }

}
