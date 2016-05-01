package com.example.bluetoothutil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
/*
 * ����ģ��ͻ���������Service
 */
public class BluetoothClientService extends Service {

	//��������Զ���豸����
	private List<BluetoothDevice> discoveredDevices=new ArrayList<BluetoothDevice>();
	//����������
	private final BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	//����ͨѶ�߳�
	private BluetoothCommunThread communThread;
	//������Ϣ�㲥�Ľ�����
	private BroadcastReceiver controlReceiver=new BroadcastReceiver(){
		
		public void onReceive(Context context,Intent intent){
			LogUtil.v("DEBUG", "BluetoothToolsClientService_controlReceiver_onReceive");
			String action=intent.getAction();
			if(BluetoothTools.ACTION_START_DISCOVERY.equals(action)){
				//��ʼ����
				LogUtil.v("DEBUG", "BluetoothToolsClientService_controlReceiver_onReceive_startDiscovery");
				discoveredDevices.clear();bluetoothAdapter.enable();
				bluetoothAdapter.startDiscovery();
			}else if(BluetoothTools.ACTION_SELECTED_DEVICE.equals(action)){
				//ѡ�������ӵķ������豸
				LogUtil.v("DEBUG", "BluetoothToolsClientService_controlReceiver_onReceive_selectedDevice");
				BluetoothDevice device=(BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
				//�����豸�����߳�
				new BluetoothClientConnThread(handler,device).start();
			}else if(BluetoothTools.ACTION_STOP_SERVICE.equals(action)){
				//ֹͣ��̨����
				LogUtil.v("DEBUG", "BluetoothToolsClientService_controlReceiver_onReceive_stopService");
				if(communThread!=null){
					communThread.isRun=false;
				}
				stopSelf();
			}else if(BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)){
				//��ȡ����
				LogUtil.v("DEBUG", "BluetoothToolsClientService_controlReceiver_onReceive_data2Service");
				Object data=intent.getSerializableExtra(BluetoothTools.DATA);
				if(communThread!=null)
				{
					communThread.writeObject(data);
				}
			}
		}
	};
	//���������㲥�Ľ�����
	private BroadcastReceiver discoveryReceiver=new BroadcastReceiver(){
	
		
		@SuppressLint("HandlerLeak")
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.v("DEBUG", "BluetoothToolsClientService_discoveryReceiver_onReceive");
			// TODO �Զ����ɵķ������
			String action=intent.getAction();
			if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.endsWith(action)){
				//��ʼ����
				LogUtil.v("DEBUG", "BluetoothToolsClientService_discoveryReceiver_onReceive_discoveryStarted");
			}else if (BluetoothDevice.ACTION_FOUND.equals(action)){
				//����Զ�������豸
				//��ȡ�豸
				LogUtil.v("DEBUG", "BluetoothToolsClientService_discoveryReceiver_onReceive_actionFound");
				BluetoothDevice bluetoothDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				discoveredDevices.add(bluetoothDevice);
				
				//���ͷ����豸�㲥
				Intent deviceListIntent=new Intent(BluetoothTools.ACTION_FOUND_DEVICE);
				deviceListIntent.putExtra(BluetoothTools.DEVICE, bluetoothDevice);
				sendBroadcast(deviceListIntent);
			}else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
				//��������
				LogUtil.v("DEBUG", "BluetoothToolsClientService_discoveryReceiver_onReceive_discoveryFinished");
				if(discoveredDevices.isEmpty()){
					//��δ�ҵ��豸�򷢶�δ���ֹ㲥
					Intent foundIntent=new Intent(BluetoothTools.ACTION_NOT_FOUND_SERVER);
					sendBroadcast(foundIntent);
				}
			}
		}
	};
	
	//���������߳���Ϣ��handler
	Handler handler=new Handler(){
		
		public void handleMessage(Message msg){
			LogUtil.v("DEBUG", "BluetoothClientService_handler_handleMessage");
			//������Ϣ
			switch(msg.what)
			{
			case BluetoothTools.MESSAGE_CONNECT_ERROR:
				//���Ӵ���
				//�������Ӵ���㲥
				LogUtil.v("DEBUG", "BluetoothToolsClientService_handler_connectError");
				Intent errorIntent=new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
				sendBroadcast(errorIntent);
				break;
				
			case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
				//���ӳɹ�������ͨѶ�߳�
				LogUtil.v("DEBUG", "BluetoothToolsClientService_handler_connectSuccess");
				communThread=new BluetoothCommunThread(handler,(BluetoothSocket)msg.obj);
				communThread.start();
				
				//�������ӳɹ��㲥
				Intent succIntent=new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
				sendBroadcast(succIntent);
				break;
			case BluetoothTools.MESSAGE_READ_OBJECT:
				//��ȡ���󣬷������ݹ㲥
				LogUtil.v("DEBUG", "BluetoothToolsClientService_handler_readObject");
				Intent dataIntent=new Intent(BluetoothTools.ACTION_DATA_TO_GAME);
				dataIntent.putExtra(BluetoothTools.DATA, (Serializable)msg.obj);
				sendBroadcast(dataIntent);
				break;
			default:
				break;
			
			}
			super.handleMessage(msg);
			
			
		}
	};
	/*
	 * ��ȡͨѶ�̣߳��� Javadoc��
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	public BluetoothCommunThread getBluetoothCommunThread(){
		return communThread;
	}
	
	@SuppressWarnings("deprecation")
	public void onStart(Intent intent ,int startId){LogUtil.v("DEBUG", "BluetoothClientService_onStart");
		super.onStart(intent, startId);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO �Զ����ɵķ������
		return null;
	}
	/*
	 * Service����ʱ�Ļص�����
	 */
	public void onCreate(){
		LogUtil.v("DEBUG", "BluetoothClientService_onCreate");
		IntentFilter discoveryFilter=new IntentFilter();
		discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND);
		
		IntentFilter controlFilter=new IntentFilter();
		controlFilter.addAction(BluetoothTools.ACTION_START_DISCOVERY);
		controlFilter.addAction(BluetoothTools.ACTION_SELECTED_DEVICE);
		controlFilter.addAction(BluetoothTools.ACTION_STOP_SERVICE);
		controlFilter.addAction(BluetoothTools.ACTION_DATA_TO_SERVICE);
		
		registerReceiver(discoveryReceiver,discoveryFilter);
		registerReceiver(controlReceiver,controlFilter);
		super.onCreate();
	}
	
	/*
	 * Service����ʱ�Ļص�����
	 */
	public void onDestroy(){
		if(communThread!=null){
			communThread.isRun=false;
		}
		unregisterReceiver(discoveryReceiver);
		super.onDestroy();
		LogUtil.v("DEBUG", "BluetoothToolsClientService_onDestroy");
	}

}
