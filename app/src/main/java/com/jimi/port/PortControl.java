package com.jimi.port;

import android.content.Context;
import android.content.Intent;

import com.jimi.port.SerialPort.SerialPortCallBack;
import com.jimi.uartproj.util.Log;

import java.util.Arrays;
import java.util.Formatter;

public class PortControl {

    private static final String TAG = "gmuartportcontrol";

    private Context mContext;
    private SerialPort mSerialPort;
    private SerialPortCallBack mSerialPortCallBack;
    private static final String MSG_CUSTOM_DATA = "android.intent.action.gmuart.data";

    public PortControl(Context ctx) {
        mContext = ctx;
        log("PortControl init....");
        mSerialPortCallBack = new SerialPortCallBack() {

            public void onDataReceived(byte[] buffer, int size) {
                log("onDataReceived size " + size);
                // TODO Auto-generated method stub
                notifyCommon(MSG_CUSTOM_DATA, buffer, size);
            }

            @Override
            public void onDataReceived(String buffer, int length) {
                log("onDataReceived length ->"+ length);
                log("onDataReceived string ->"+ buffer);
            }

        };
        mSerialPort = new SerialPort(mContext);
    }

    public void freePortControl() {

        if (mSerialPort != null) {
            mSerialPort.ClosePort();
            mSerialPort = null;
        }

        mSerialPortCallBack = null;
        mContext = null;

        log("PortControl free");
    }

    public SerialPortCallBack getCallBack() {
        return mSerialPortCallBack;
    }

    public SerialPort getPort() {
        return mSerialPort;
    }

    private void notifyCommon(String action, byte[] data, int size) {
        log("notifyCommon byte[] data size ->"+ size);
        log("notifyCommon byte[] data ->"+ Arrays.toString(Arrays.copyOfRange(data, 0, size)));
//        log("notifyCommon string ->"+AtoString(data));
        String strData = AtoString(data);
        Intent intent = new Intent(action);
//        intent.putExtra("uart_data", data);
//        intent.putExtra("data_length", size);
        intent.putExtra("uart_data", strData);
//        intent.putExtra("data_length", strData.length());
        mContext.sendBroadcast(intent);
    }

    private static String AtoString(byte[] datas){
        StringBuilder temp= new StringBuilder();
        for (byte data : datas) {

            String str = Character.toString((char) data);
            temp.append(str);

        }
        return temp.toString();
    }

    private static String getLatLong(){
        StringBuilder temp = new StringBuilder();
        return temp.toString();
    }

    public String bytesToHexString(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder(length * 2);
        Formatter formatter = new Formatter(sb);
        for (int i = 0; i < length; i++) {
            formatter.format("%02X ", bytes[i]);
        }
        return sb.toString();
    }

	/*private void log(String msg) {
        Log.d(TAG, this.toString() + "=>" + msg);
	}*/

    private void log(String msg) {
        Log.d(TAG, this.toString() + "=>" + msg);
        Intent log = new Intent("com.jimi.log");
        log.putExtra("log", msg);
        mContext.sendBroadcast(log);

    }

    private void logE(String msg) {
        Log.e(TAG, this.toString() + "=>" + msg);
    }
}
