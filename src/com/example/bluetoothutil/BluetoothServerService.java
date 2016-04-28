package com.example.bluetoothutil;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothServerService extends Service {

	//蓝牙适配器
	private final BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	private BluetoothCommunThread communThread;
	//控制信息广播接收器
	private BroadcastReceiver controlReceiver=new BroadcastReceiver(){
		public void onRecive(Context context,Intent intent){
			String action =intent.getAction();
			if(BluetoothTools.ACTION_STOP_SERVICE.equals(action)){
				//停止后台服务
				if(communThread!=null){
					communThread.isRun=false;
				}
				stopSelf();
			}else if(BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)){
				//发送数据
				Object data=intent.getSerializableExtra(BluetoothTools.DATA);
				if(communThread!=null){
					communThread.writeObject(data);
				}
			}
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO 自动生成的方法存根
			
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		// TODO 自动生成的方法存根
		return null;
	}

}
