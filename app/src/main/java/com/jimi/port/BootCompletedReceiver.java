package com.jimi.port;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {

    private String TAG = "BootCompletedReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Log.d("Deng Liangyou", "recevie boot completed ... ");
        Log.v(TAG, "onReceive: action"+intent.getAction());
/*		
		Intent mBootIntent = new Intent(context, MainActivity.class);
		mBootIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mBootIntent);		
*/


// TODO: 2018/7/27
        /*
        Intent uart = new Intent();
        // uart.setClassName("com.gmuart.port", "com.gmuart.port.PortService");
        uart.setAction(Constant.SV_OPEN_SERIALPORT);
        uart.setPackage("com.gmuart.port");
        context.startService(uart);
*/

    }

} 
