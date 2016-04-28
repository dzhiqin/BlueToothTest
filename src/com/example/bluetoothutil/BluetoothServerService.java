package com.example.bluetoothutil;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class BluetoothServerService extends Service {

	//����������
	private final BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	private BluetoothCommunThread communThread;
	//������Ϣ�㲥������
	private BroadcastReceiver controlReceiver=new BroadcastReceiver(){
		public void onRecive(Context context,Intent intent){
			String action =intent.getAction();
			if(BluetoothTools.ACTION_STOP_SERVICE.equals(action)){
				//ֹͣ��̨����
				if(communThread!=null){
					communThread.isRun=false;
				}
				stopSelf();
			}else if(BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)){
				//��������
				Object data=intent.getSerializableExtra(BluetoothTools.DATA);
				if(communThread!=null){
					communThread.writeObject(data);
				}
			}
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO �Զ����ɵķ������
			
		}
	};
	@Override
	public IBinder onBind(Intent intent) {
		// TODO �Զ����ɵķ������
		return null;
	}

}
