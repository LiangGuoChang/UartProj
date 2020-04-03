package com.jimi.port;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;

import com.jimi.uartproj.util.Log;

//import com.mediatek.gmMisc.gmMisc;

public class PortService extends Service {
    private static final String TAG = "gmuartPortService";
    private static PortControl mPortControl;
    private PowerManager.WakeLock m_wakeLockObj = null;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, final int flags, int startId) {
        log("PortService: onStartCommand");
        if (intent == null)
            return super.onStartCommand(intent, flags, startId);
        String action = intent.getAction();
        log(intent.getAction());
        if (action.equals(Constant.SV_OPEN_SERIALPORT)) {
            log("action: " + action);
            final int baudrate = intent.getIntExtra("baudrate",9600);
            final int bits = intent.getIntExtra("bits",8);
            final char event = intent.getCharExtra("event",'N');
            final int stop = intent.getIntExtra("stop",1);
            final int mFlag = intent.getIntExtra("flags",0);
            final int readBufferSize = intent.getIntExtra("readBufferSize",2000);
//            log("port: " + mPortControl.getPort());

//            if (mPortControl == null) {

                log("PortService OpenPort....");
                //gmMisc.GM_VOC_Power_ctrl(1);
                mPortControl = new PortControl(getApplicationContext());

                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        // TODO Auto-generated method stub
                        log("PortService run OpenPort.....");
                        SerialPort sp = mPortControl.getPort();
                        if (sp == null) {
                            sp = new SerialPort(getApplicationContext());
                        }
//                        sp.OpenPort(115200, 8, 'N', 1, 0,2000,mPortControl.getCallBack());
                        sp.OpenPort(baudrate, bits, event, stop, mFlag,readBufferSize,mPortControl.getCallBack());
                    }
                }, 500);

//            }
            /*else if (null != mPortControl){
                log("mPortControl != null");
            }*/

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private BroadcastReceiver mtestInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constant.WRITE_UART_TESTDATA)) {
                log("onReceive:WRITE_UART_TESTDATA");
                mPortControl.getPort().writeData(Constant.GM_UART_TESTDATA);
            } else if (action.equals(Constant.WRITE_UART_CTRL_DATA)) {
                log("onReceive:WRITE_UART_CTRL_DATA");
                Bundle mBundle = intent.getExtras();
                byte[] uartdata = mBundle.getByteArray("ctrl_cmd");
                mPortControl.getPort().writeData(uartdata);
            } else if (action.equals(Constant.PTZ_ACTION)) {
                PtzAction(intent.getIntExtra("direction", -1));
            }
        }
    };

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        log("PortService: onCreate");
        startForeground(0x1999, new Notification());

        IntentFilter testFilter = new IntentFilter();
        testFilter.addAction(Constant.WRITE_UART_TESTDATA);
        testFilter.addAction(Constant.WRITE_UART_CTRL_DATA);
        testFilter.addAction(Constant.PTZ_ACTION);
        registerReceiver(mtestInfoReceiver, testFilter);
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        stopForeground(true);
        if (mPortControl != null) {
            log("PortService onDestroy freePortControl");
            mPortControl.freePortControl();
            mPortControl = null;
        }
        //gmMisc.GM_VOC_Power_ctrl(0);
        unregisterReceiver(mtestInfoReceiver);
        super.onDestroy();
    }

    public static PortControl getSPort() {
        return mPortControl;
    }
/*

    private void log(String msg) {
        Log.d(TAG, this.toString() + "=>" + msg);
    }
*/

    private void log(String msg) {
        Log.d(TAG, this.toString() + "=>" + msg);
        Intent log = new Intent("com.jimi.log");
        log.putExtra("log",msg);
        getApplicationContext().sendBroadcast(log);

    }

    public void acquireWakeLock() {
        if (m_wakeLockObj == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            m_wakeLockObj = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE, TAG);
            m_wakeLockObj.acquire();
        }
    }

    public void releaseWakeLock() {
        if (m_wakeLockObj != null && m_wakeLockObj.isHeld()) {
            m_wakeLockObj.release();
            m_wakeLockObj = null;
        }
    }

    private void PtzAction(int direction) {
        log("PtzAction:direction=" + direction);
    }
}
