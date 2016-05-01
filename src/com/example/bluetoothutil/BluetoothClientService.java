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
 * 蓝牙模块客户端主控制Service
 */
public class BluetoothClientService extends Service {

	//搜索到的远程设备集合
	private List<BluetoothDevice> discoveredDevices=new ArrayList<BluetoothDevice>();
	//蓝牙适配器
	private final BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
	//蓝牙通讯线程
	private BluetoothCommunThread communThread;
	//控制信息广播的接收器
	private BroadcastReceiver controlReceiver=new BroadcastReceiver(){
		
		public void onReceive(Context context,Intent intent){
			LogUtil.v("DEBUG", "BluetoothToolsClientService_controlReceiver_onReceive");
			String action=intent.getAction();
			if(BluetoothTools.ACTION_START_DISCOVERY.equals(action)){
				//开始搜索
				LogUtil.v("DEBUG", "BluetoothToolsClientService_controlReceiver_onReceive_startDiscovery");
				discoveredDevices.clear();bluetoothAdapter.enable();
				bluetoothAdapter.startDiscovery();
			}else if(BluetoothTools.ACTION_SELECTED_DEVICE.equals(action)){
				//选择了连接的服务器设备
				LogUtil.v("DEBUG", "BluetoothToolsClientService_controlReceiver_onReceive_selectedDevice");
				BluetoothDevice device=(BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
				//开启设备连接线程
				new BluetoothClientConnThread(handler,device).start();
			}else if(BluetoothTools.ACTION_STOP_SERVICE.equals(action)){
				//停止后台服务
				LogUtil.v("DEBUG", "BluetoothToolsClientService_controlReceiver_onReceive_stopService");
				if(communThread!=null){
					communThread.isRun=false;
				}
				stopSelf();
			}else if(BluetoothTools.ACTION_DATA_TO_SERVICE.equals(action)){
				//获取数据
				LogUtil.v("DEBUG", "BluetoothToolsClientService_controlReceiver_onReceive_data2Service");
				Object data=intent.getSerializableExtra(BluetoothTools.DATA);
				if(communThread!=null)
				{
					communThread.writeObject(data);
				}
			}
		}
	};
	//蓝牙搜索广播的接收器
	private BroadcastReceiver discoveryReceiver=new BroadcastReceiver(){
	
		
		@SuppressLint("HandlerLeak")
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.v("DEBUG", "BluetoothToolsClientService_discoveryReceiver_onReceive");
			// TODO 自动生成的方法存根
			String action=intent.getAction();
			if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.endsWith(action)){
				//开始搜索
				LogUtil.v("DEBUG", "BluetoothToolsClientService_discoveryReceiver_onReceive_discoveryStarted");
			}else if (BluetoothDevice.ACTION_FOUND.equals(action)){
				//发现远程蓝牙设备
				//获取设备
				LogUtil.v("DEBUG", "BluetoothToolsClientService_discoveryReceiver_onReceive_actionFound");
				BluetoothDevice bluetoothDevice=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				discoveredDevices.add(bluetoothDevice);
				
				//发送发现设备广播
				Intent deviceListIntent=new Intent(BluetoothTools.ACTION_FOUND_DEVICE);
				deviceListIntent.putExtra(BluetoothTools.DEVICE, bluetoothDevice);
				sendBroadcast(deviceListIntent);
			}else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
				//搜索结束
				LogUtil.v("DEBUG", "BluetoothToolsClientService_discoveryReceiver_onReceive_discoveryFinished");
				if(discoveredDevices.isEmpty()){
					//若未找到设备则发动未发现广播
					Intent foundIntent=new Intent(BluetoothTools.ACTION_NOT_FOUND_SERVER);
					sendBroadcast(foundIntent);
				}
			}
		}
	};
	
	//接收其他线程消息的handler
	Handler handler=new Handler(){
		
		public void handleMessage(Message msg){
			LogUtil.v("DEBUG", "BluetoothClientService_handler_handleMessage");
			//处理消息
			switch(msg.what)
			{
			case BluetoothTools.MESSAGE_CONNECT_ERROR:
				//连接错误
				//发送连接错误广播
				LogUtil.v("DEBUG", "BluetoothToolsClientService_handler_connectError");
				Intent errorIntent=new Intent(BluetoothTools.ACTION_CONNECT_ERROR);
				sendBroadcast(errorIntent);
				break;
				
			case BluetoothTools.MESSAGE_CONNECT_SUCCESS:
				//连接成功，开启通讯线程
				LogUtil.v("DEBUG", "BluetoothToolsClientService_handler_connectSuccess");
				communThread=new BluetoothCommunThread(handler,(BluetoothSocket)msg.obj);
				communThread.start();
				
				//发送连接成功广播
				Intent succIntent=new Intent(BluetoothTools.ACTION_CONNECT_SUCCESS);
				sendBroadcast(succIntent);
				break;
			case BluetoothTools.MESSAGE_READ_OBJECT:
				//读取对象，发送数据广播
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
	 * 获取通讯线程（非 Javadoc）
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
		// TODO 自动生成的方法存根
		return null;
	}
	/*
	 * Service创建时的回调函数
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
	 * Service销毁时的回调函数
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
