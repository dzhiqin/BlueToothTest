package com.example.bluetoothutil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

public class BluetoothServerConnThread extends Thread{
	
	private Handler serviceHandler;	//用于同service通信的handler
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;
	private BluetoothServerSocket serverSocket;
	/*
	 * 构造函数
	 */
	public BluetoothServerConnThread(Handler handler){
		this.serviceHandler=handler;
		adapter=BluetoothAdapter.getDefaultAdapter();
	}

	public void run(){
		try{
			serverSocket=adapter.listenUsingRfcommWithServiceRecord("Server", BluetoothTools.PRIVATE_UUID);
			socket=serverSocket.accept();
		}catch(Exception e){
			//发送连接失败消息
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			e.printStackTrace();
			return;
		}finally{
			try{
				serverSocket.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(socket!=null){
			//发送连接成功信息，消息的OBJ字段为连接的socket
			Message msg=serviceHandler.obtainMessage();
			msg.what=BluetoothTools.MESSAGE_CONNECT_SUCCESS;
			msg.obj=socket;
			msg.sendToTarget();
		}else{
			//发送连接失败消息
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			return;
		}
	}
}
