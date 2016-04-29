package com.example.bluetoothtest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.bluetoothutil.BluetoothClientService;
import com.example.bluetoothutil.BluetoothTools;
import com.example.bluetoothutil.TransmitBean;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ClientActivity extends Activity {

	private TextView serversText;
	private EditText chatEditText;
	private EditText sendEditText;
	private Button sendBtn;
	private Button startSearchBtn;
	private Button selectDeviceBtn;
	private List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
	//广播接收器
	private BroadcastReceiver broadcastReceiver=new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action=intent.getAction();
			if(BluetoothTools.ACTION_NOT_FOUND_SERVER.equals(action)){
				serversText.append("not found device\r\n");
			}else if(BluetoothTools.ACTION_FOUND_DEVICE.equals(action)){
				BluetoothDevice device=(BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
				deviceList.add(device);
				serversText.append(device.getName()+"\r\n");
			}else if(BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action)){
				serversText.append("连接成功\r\n");
				sendBtn.setEnabled(true);
			}else if(BluetoothTools.ACTION_DATA_TO_GAME.equals(action)){
				//接收数据
				TransmitBean data=(TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
				String msg="from remote"+new Date().toLocaleString()+":\r\n"+data.getMsg()+"\r\n";
				chatEditText.append(msg);
			}
		}
		
	};
	public void onStart(){
		//清空列表
		deviceList.clear();
		//开启后台service
		Intent startService=new Intent(ClientActivity.this,BluetoothClientService.class);
		startService(startService);
		
		//注册broadcastRecever
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(BluetoothTools.ACTION_NOT_FOUND_SERVER);
		intentFilter.addAction(BluetoothTools.ACTION_FOUND_DEVICE);
		intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
		registerReceiver(broadcastReceiver,intentFilter);
		
		super.onStart();
	}
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.client);
		
		serversText=(TextView)findViewById(R.id.clientServersText);
		chatEditText=(EditText)findViewById(R.id.clientChatEditText);
		sendEditText=(EditText)findViewById(R.id.clientSendEditText);
		sendBtn=(Button)findViewById(R.id.clientSendMsg);
		startSearchBtn=(Button)findViewById(R.id.startSearch);
		selectDeviceBtn=(Button)findViewById(R.id.selectDevice);
		sendBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// 发送消息
				if("".equals(sendEditText.getText().toString().trim())){
					Toast.makeText(ClientActivity.this,"输入不能为空", Toast.LENGTH_SHORT).show();
				}else{
					//发送消息
					TransmitBean data=new TransmitBean();
					data.setMsg(sendEditText.getText().toString());
					Intent sendDataIntent=new Intent(BluetoothTools.ACTION_DATA_TO_SERVICE);
					sendDataIntent.putExtra(BluetoothTools.DATA,data);
					sendBroadcast(sendDataIntent);
				}
				
			}
			
		});
		
		startSearchBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				//开始搜索
				Intent startSearchIntent=new Intent(BluetoothTools.ACTION_START_DISCOVERY);
				sendBroadcast(startSearchIntent);
			}
			
		});
		selectDeviceBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// 选择第一个设备
				Intent selectDeviceIntent=new Intent(BluetoothTools.ACTION_SELECTED_DEVICE);
				selectDeviceIntent.putExtra(BluetoothTools.DEVICE,deviceList.get(0));
				sendBroadcast(selectDeviceIntent);
				
			}
			
		});
	}
	protected void onStop(){
		//关闭后台service
		Intent startService=new Intent(BluetoothTools.ACTION_STOP_SERVICE);
		sendBroadcast(startService);
		unregisterReceiver(broadcastReceiver);
		super.onStop();
	}
}
