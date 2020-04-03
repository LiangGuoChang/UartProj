package com.jimi.port;

import android.content.Context;
import android.content.Intent;

import com.jimi.uartproj.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;

/**
 * 类名: ${name}
 * 创建人: Liang GuoChang
 * 创建时间: ${date} ${time}
 * 描述:
 * 版本号:
 * 修改记录:
 */

public class SerialPort {

    static {
        System.loadLibrary("gmuartport");
    }

    private static final String TAG = "gmuartSerialPort";

    private int mFd = 0;
    private boolean bOpenPort = false;
    private SerialPortCallBack mCallback;
    private ReadThread mReadThread;
    private int nReadBufferSize = 0;
    private Context mContext;
    private ArrayList<String> dataList = new ArrayList<String>();

    public SerialPort(Context context) {
        this.mContext = context;
    }

    //回掉方法
    public interface SerialPortCallBack {
        void onDataReceived(byte[] buffer, int size);

        void onDataReceived(String buffer, int length);
    }

    public boolean isOpen() {
        return bOpenPort;
    }

    //打开串口的方法
    public int OpenPort(int baudrate, int bits, char event, int stop,
                        int flags, int readBufferSize, SerialPortCallBack serialPortCallBack) {
        // gm_nativeInit();

        if (isOpen())
            return 0;
        mFd = open(baudrate, bits, event, stop, flags);

        log("mFd=" + mFd);

        /*
        if (mFd <= 0) {
//            Log.d("serial", "open serial port throw IOException");
            log("serial -> open serial port throw IOException");
            return -1;
        }
        */

        nReadBufferSize = readBufferSize;
        bOpenPort = true;
        mCallback = serialPortCallBack;
        startRead();
        return 1;
    }

    //开始读
    public void startRead() {

        stopRead();

        mReadThread = new ReadThread();
        if (mReadThread != null) {
            mReadThread.start();
            log("start ReadThread");
        }
    }

    //暂停读
    public void stopRead() {
        if (mReadThread != null) {
            mReadThread.interrupt();
            log("stop ReadThread");
        }
        mReadThread = null;
    }

    //休眠
    protected void Sleep(int i) {
        // TODO Auto-generated method stub

    }

    //关闭串口
    public void ClosePort() {
        stopRead();

        if (bOpenPort) {
            close(mFd);
            log("close serial port");
        }

        bOpenPort = false;
        mFd = 0;
        mCallback = null;
    }

    //写byte数据
    public int writeData(byte[] buffer) {
        if (!isOpen()) {
            logE("Serial port is not open");
            return -1;
        }
        if (buffer == null || buffer.length == 0) {
            log("serial -> writeData buffer== null");
            return -1;
        }
        log("gmuartserial_write -> writeData buffer[]=" + bytesToHexString(buffer, buffer.length));
        int result = write(mFd, buffer, buffer.length);
        log("writeData result - >" + result);
        return result;
    }

    //写String
    public int writeData(String buffer) {
        if (!isOpen()) {
            logE("Serial port is not open");
            return -1;
        }
        if (buffer == null) {
            logE("writeData buffer== null");
            return -1;
        }

        //	byte[] buf = HexString2Bytes(buffer);
        byte[] buf = buffer.getBytes();
        int res = write(mFd, buf, buf.length);

        log("writeData buffer=" + buffer + ",res=" + res);

        return res;
    }

    private static String AtoString(byte[] datas) {
        StringBuilder temp = new StringBuilder();
        for (byte data : datas) {
            String str = Character.toString((char) data);
            temp.append(str);
        }
        return temp.toString();
    }


    private byte[] HexString2Bytes(String src) {

        byte[] tmp = src.getBytes();
        int bytes = tmp.length / 2;

        byte[] ret = new byte[bytes];

        for (int i = 0; i < bytes; i++) {
            ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
        }
        return ret;
    }

    private byte uniteBytes(byte src0, byte src1) {
        byte _b0 = Byte.decode("0x" + new String(new byte[]{src0}))
                .byteValue();
        _b0 = (byte) (_b0 << 4);
        byte _b1 = Byte.decode("0x" + new String(new byte[]{src1}))
                .byteValue();
        byte ret = (byte) (_b0 ^ _b1);
        return ret;
    }

    public String bytesToHexString(byte[] bytes, int length) {
        StringBuilder sb = new StringBuilder(length * 2);
        Formatter formatter = new Formatter(sb);
        for (int i = 0; i < length; i++) {
            formatter.format("0x%02X ", bytes[i]);
        }
        return sb.toString();
    }

    private void log(String msg) {
        Log.d(TAG, this.toString() + "=>" + msg);
        Intent log = new Intent("com.jimi.log");
        log.putExtra("log", msg);
        mContext.sendBroadcast(log);

    }

    private void logE(String msg) {
        Log.e(TAG, this.toString() + "=>" + msg);
        Intent log = new Intent("com.jimi.log");
        log.putExtra("log", msg);
        mContext.sendBroadcast(log);
    }


    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            log("ReadThread run");
            int beforeSize = 0;
            byte[] buffer = new byte[nReadBufferSize];
            String strBuffer;
            byte[] beforebuffer = new byte[nReadBufferSize];
            while (!isInterrupted()) {
                int size;
                size = read(mFd, buffer, buffer.length);
                if (size > 0) {
                    log("ReadThread size -> " + size);
//                    log("ReadThread buffer -> "+ Arrays.toString(Arrays.copyOfRange(buffer,0,size)));


                    /*
                    // TODO: 2018/7/31 将数据转换
                    strBuffer = AtoString(Arrays.copyOfRange(buffer, 0, size));
                    if (mCallback != null) {
                        // TODO: 2018/7/31 遇到回车再传递出去
//                        mCallback.onDataReceived(Arrays.copyOfRange(buffer, 0, size), Arrays.copyOfRange(buffer, 0, size).length);
                        mCallback.onDataReceived(strBuffer,strBuffer.length());
                        buffer = new byte[nReadBufferSize];
                        beforebuffer = new byte[nReadBufferSize];
                        beforeSize = 0;
                    }
                    */


                    if (buffer[size - 1] != '\n') {
                        System.arraycopy(buffer, 0, beforebuffer, beforeSize, size);
                        beforeSize = beforeSize + size;
                    } else {
                        System.arraycopy(buffer, 0, beforebuffer, beforeSize, size - 1);
                        beforeSize = beforeSize + size - 1;

                        if (mCallback != null) {
                            mCallback.onDataReceived(beforebuffer, beforeSize);
                            buffer = new byte[nReadBufferSize];
                            beforebuffer = new byte[nReadBufferSize];
                            beforeSize = 0;
                        }
                    }


                }
                Sleep(100);
            }
        }
    }

    /**
     * 以下是 c 的方法
     *
     * @return
     */
    //返回string
    public native String stringFromJNI();

    //打开串口
    private native static int open(int baudrate, int bits, char event, int stop, int flags);

    //写串口
    private native static int write(int fd, byte[] buf, int sizes);

    //读串口
    private native static int read(int fd, byte[] buf, int sizes);

    //关闭串口
    private native int close(int fd);
}
