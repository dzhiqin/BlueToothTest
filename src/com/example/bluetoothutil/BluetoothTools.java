package com.example.bluetoothutil;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BluetoothTools {

	private static BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
	/*
	 * 本程序所使用的UUID
	 */
	public  static final UUID PRIVATE_UUID=UUID.fromString("0f3561b9-bda5-4672-84ff-ab1f98e349b6");
	/*
	 * 字符串常量，存放在Intent中的设备对象
	 */
	public static  final String DEVICE="DEVICE";
	
	public static final String SERVER_INDEX="SERVER_INDEX";
	
	public static final String DATA="DATA";
	
	/*
	 * ACTION 类型标识符，读到数据
	 */
	public static final String ACTION_READ_DATA="ACTION_READ_DATA";
	/*
	 * ACTION 类型标识符，未发现设备
	 */
	public static final String ACTION_NOT_FOUND_SERVER="ACTION_NOT_FOUND_DEVICE";
	/*
	 * ACTION 类型标识符，开始搜索设备
	 */
	public static final String ACTION_START_DISCOVERY="ACTION_START_DISCOVERY";
	/*
	 * ACTION 设备列表
	 */
	public static final String ACTION_FOUND_DEVICE="ACTION_FOUND_DEVICE";
	/*
	 * 选择的用于链接的设备
	 */
	public static final String ACTION_SELECTED_DEVICE="ACTION_SELECTED_DEVICE";
	/*
	 * ACTION 开启服务器
	 */
	public static final String ACTION_START_SERVER="ACTION_START_SERVER";
	/*
	 * ACTION关闭后台service
	 */
	public static final String ACTION_STOP_SERVICE="ACTION_STOP_SERVICE";
	/*
	 * 到service的数据
	 */
	public static final String ACTION_DATA_TO_SERVICE="ACTION_DATA_TO_SERVICE";
	/*
	 * 到游戏业务中的数据
	 */
	public static final String ACTION_DATA_TO_GAME="ACTION_DATA_TO_GAME";
	/*
	 * 连接成功
	 */
	public static final String ACTION_CONNECT_SUCCESS="ACTION_CONNECT_SUCCESS";
	/*
	 * 连接错误
	 */
	public static final String ACTION_CONNECT_ERROR="ACTION_CONNECT_ERROR";
	/*
	 * MESSAGE类型标识符，连接成功
	 */
	public static final int MESSAGE_CONNECT_SUCCESS=0x00000002;
	/*
	 * MESSAGE,连接失败
	 */
	public static final int MESSAGE_CONNECT_ERROR=0x00000003;
	/*
	 * 读取到一个对象
	 */
	public static final int MESSAGE_READ_OBJECT=0X00000004;
	/*
	 * 打开蓝牙功能
	 */
	public static void openBluetooth(){
		adapter.enable();
	}
	/*
	 * 关闭蓝牙功能
	 */
	public static void closeBluetooth(){
		adapter.disable();
	}
	/*
	 * 设置蓝牙发现功能
	 * @param duration设置蓝牙发现功能持续的描述
	 */
	 public static void openDiscovery(int duration){
		 if(duration<=0||duration>300){
			 duration=200;
		 }
		 Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		 intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
	 }
	 /*
	  * 停止蓝牙搜索
	  */
	 public static void stopDiscovery(){
		 adapter.cancelDiscovery();
	 }
}
