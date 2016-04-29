package com.example.bluetoothutil;

import java.io.IOException;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

/*
 * 蓝牙客户端连接线程
 */
public class BluetoothClientConnThread extends Thread{

	private Handler serviceHandler;
	private BluetoothDevice serverDevice;
	private BluetoothSocket socket;
	/*
	 * constractor
	 */
	public BluetoothClientConnThread(Handler handler,BluetoothDevice serverDevice){
		this.serviceHandler=handler;
		this.serverDevice=serverDevice;
	}
	public void run(){
		LogUtil.v("DEBUG", "BluetoothClientConnThread_run");
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		try{
			socket=serverDevice.createRfcommSocketToServiceRecord(BluetoothTools.PRIVATE_UUID);
			BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
			socket.connect();
		}catch(Exception ex){
			try{
				socket.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			//发送连接失败消息
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			return;
		}
		//发送连接成功信息
		Message msg=serviceHandler.obtainMessage();
		msg.what=BluetoothTools.MESSAGE_CONNECT_SUCCESS;
		msg.obj=socket;
		msg.sendToTarget();
	}
}
