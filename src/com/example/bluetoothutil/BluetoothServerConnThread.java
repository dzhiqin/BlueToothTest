package com.example.bluetoothutil;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

public class BluetoothServerConnThread extends Thread{
	
	private Handler serviceHandler;	//����ͬserviceͨ�ŵ�handler
	private BluetoothAdapter adapter;
	private BluetoothSocket socket;
	private BluetoothServerSocket serverSocket;
	/*
	 * ���캯��
	 */
	public BluetoothServerConnThread(Handler handler){
		LogUtil.v("DEBUG", "BluetoothServerConnThread_constructor");
		this.serviceHandler=handler;
		adapter=BluetoothAdapter.getDefaultAdapter();
	}

	public void run(){
		LogUtil.v("DEBUG", "BluetoothServerConnThread_run");
		try{
			serverSocket=adapter.listenUsingRfcommWithServiceRecord("Server", BluetoothTools.PRIVATE_UUID);
			socket=serverSocket.accept();
		}catch(Exception e){
			//��������ʧ����Ϣ
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
			//�������ӳɹ���Ϣ����Ϣ��OBJ�ֶ�Ϊ���ӵ�socket
			Message msg=serviceHandler.obtainMessage();
			msg.what=BluetoothTools.MESSAGE_CONNECT_SUCCESS;
			msg.obj=socket;
			msg.sendToTarget();
		}else{
			//��������ʧ����Ϣ
			serviceHandler.obtainMessage(BluetoothTools.MESSAGE_CONNECT_ERROR).sendToTarget();
			return;
		}
	}
}
