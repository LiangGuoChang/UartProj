package com.jimi.uartproj;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jimi.port.Constant;
import com.jimi.port.PortService;
import com.jimi.port.SerialPort;
import com.jimi.uartproj.util.CheckPermissionUtils;
import com.jimi.uartproj.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = "MainActivity";

    private MyDataReceiver receiver;
    private ArrayList<String> titleList = new ArrayList<String>();
//    private ListView lv_log;
    private List<String> logList = new ArrayList<String>();
//    private ArrayAdapter<String> adapter;
    private MyEditTextDel edt_commond;
    private LogAdapter logAdapter;
    private MyEditTextDel edt_baudrate;
    private TextView tv_log_msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initPermission();

        receiver = new MyDataReceiver();//广播接受者实例
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.gmuart.data");
        intentFilter.addAction("com.jimi.log");
        registerReceiver(receiver, intentFilter);
    }

    /**
     * 初始化权限事件
     */
    private void initPermission() {
        //检查权限
        String[] permissions = CheckPermissionUtils.checkPermission(this);
        if (permissions.length == 0) {
            //权限都申请了
            //是否登录
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, permissions, 100);
        }
    }

    private void initView(){
        findViewById(R.id.btn_start_service).setOnClickListener(this);
        findViewById(R.id.btn_send_command).setOnClickListener(this);
        edt_baudrate = findViewById(R.id.edt_baudrate);
        edt_commond = (MyEditTextDel) findViewById(R.id.edt_send_command);
        tv_log_msg = findViewById(R.id.tv_log_msg);
//        lv_log = findViewById(R.id.lv_log);
//        logList.add(new SerialPort(getApplicationContext()).stringFromJNI());
//        logAdapter = new LogAdapter(getApplicationContext(),logList);

//        lv_log.setAdapter(logAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start_service:


                Log.d(TAG, "onClick: btn_start_service");
                /*
                Intent intent = new Intent();
                intent.setAction("android.intent.action.BOOT_COMPLETED");
                sendBroadcast(intent);
                */
                int buadrate = 9600;
                if (!TextUtils.isEmpty(edt_baudrate.getText().toString().trim())){
                    buadrate = Integer.parseInt(edt_baudrate.getText().toString().trim());
                }
                Intent uart = new Intent(this, PortService.class);
                uart.setAction(Constant.SV_OPEN_SERIALPORT);
                uart.putExtra("baudrate",buadrate);
                uart.putExtra("bits",8);
                uart.putExtra("event",'N');
                uart.putExtra("stop",1);
                uart.putExtra("flags",0);
                uart.putExtra("readBufferSize",2000);
                startService(uart);
                // uart.setClassName("com.gmuart.port", "com.gmuart.port.PortService");
//                uart.setPackage("com.jimi.port");
//                this.startService(uart);
                break;

            case R.id.btn_send_command:
                if (!TextUtils.isEmpty(edt_commond.getText().toString().trim())){
                    String commondStr = edt_commond.getText().toString().trim();
                    byte[] commond = commondStr.getBytes();
                    Intent inten = new Intent(Constant.WRITE_UART_CTRL_DATA);
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("ctrl_cmd",commond);
                    inten.putExtras(bundle);
                    this.sendBroadcast(inten);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }


    class MyDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (("android.intent.action.gmuart.data").equalsIgnoreCase(intent.getAction())){
                //串口数据
//                logList.add("data->");
//                byte[] data = intent.getByteArrayExtra("uart_data");
//                logList.add(Arrays.toString(data));
                String strData = intent.getStringExtra("uart_data");
                tv_log_msg.setText(strData);
//                logList.add(strData);
//                logAdapter.notifyDataSetChanged();
//                lv_log.setSelectionAfterHeaderView();
//                Toast.makeText(context,"广播数据:\n"+intent.getByteExtra("uart_data", Byte.parseByte(String.valueOf(0))),Toast.LENGTH_LONG).show();
            }else if ("com.jimi.log".equalsIgnoreCase(intent.getAction())){
                //日志
//                logList.add("log->");
//                logList.add(intent.getStringExtra("log"));
//                logAdapter.notifyDataSetChanged();
//                lv_log.setSelectionAfterHeaderView();
            }
        }
    }


}
