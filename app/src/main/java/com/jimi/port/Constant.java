package com.jimi.port;

public class Constant {
	public static final String SV_OPEN_SERIALPORT = "com.open.gmuart.port.start";
	public static final String MSG_CUSTOM_DATA = "android.intent.action.gmuart.data";
	public static final String WRITE_UART_TESTDATA = "android.intent.action.gmuart.WRITE_UART_TESTDATA";
	public static final String UART_PORT_SERVICE_NAME = "com.gmuart.port.PortService";
	public static final String UART_READ_SERVICE_NAME = "com.gm.gmuart.ReadService";
	public static final String GM_UART_TESTDATA = "UART test OK";
	public static final String WRITE_UART_CTRL_DATA = "android.intent.action.gmuart.WRITE_UART_DATA";
	public static final String STOP_SERVICE = "android.intent.action.gmuart.STOP_SERVICE";

	public static final String PTZ_ACTION = "com.android.ptz.ACTION";
	public static final int PTZ_DIRECT_LEFT = 1;
	public static final int PTZ_DIRECT_RIGHT = 2;
	public static final int PTZ_DIRECT_END = 3;

}
