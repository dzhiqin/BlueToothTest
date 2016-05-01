package com.example.bluetoothtest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.bluetoothutil.BluetoothClientService;
import com.example.bluetoothutil.BluetoothTools;
import com.example.bluetoothutil.LogUtil;
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
	//�㲥������
	private BroadcastReceiver broadcastReceiver=new BroadcastReceiver(){
		
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.v("DEBUG", "ClientActivity_BroadcastRecever_onReceive");
			String action=intent.getAction();
			if(BluetoothTools.ACTION_NOT_FOUND_SERVER.equals(action)){
				LogUtil.v("DEBUG", "ClientActivity_BroadcastRecever_onReceive_notFoundServer");
				serversText.append("not found device\r\n");
			}else if(BluetoothTools.ACTION_FOUND_DEVICE.equals(action)){
				LogUtil.v("DEBUG", "ClientActivity_BroadcastRecever_onReceive_foundDevice");
				BluetoothDevice device=(BluetoothDevice)intent.getExtras().get(BluetoothTools.DEVICE);
				deviceList.add(device);
				serversText.append(device.getName()+"\r\n");
			}else if(BluetoothTools.ACTION_CONNECT_SUCCESS.equals(action)){
				serversText.append("���ӳɹ�\r\n");
				sendBtn.setEnabled(true);
			}else if(BluetoothTools.ACTION_DATA_TO_GAME.equals(action)){
				//��������
				LogUtil.v("DEBUG", "ClientActivity_BroadcastRecever_onReceive_dataToGame");
				TransmitBean data=(TransmitBean)intent.getExtras().getSerializable(BluetoothTools.DATA);
				String msg="from remote"+new Date().toLocaleString()+":\r\n"+data.getMsg()+"\r\n";
				chatEditText.append(msg);
			}
		}
		
	};
	public void onStart(){
		//����б�
		LogUtil.v("DEBUG", "ClientActivity_onStart");
		deviceList.clear();
		//������̨service
		Intent startService=new Intent(ClientActivity.this,BluetoothClientService.class);
		startService(startService);
		
		//ע��broadcastRecever
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(BluetoothTools.ACTION_NOT_FOUND_SERVER);
		intentFilter.addAction(BluetoothTools.ACTION_FOUND_DEVICE);
		intentFilter.addAction(BluetoothTools.ACTION_DATA_TO_GAME);
		intentFilter.addAction(BluetoothTools.ACTION_CONNECT_SUCCESS);
		registerReceiver(broadcastReceiver,intentFilter);
		
		super.onStart();
	}
	protected void onCreate(Bundle savedInstanceState){
		LogUtil.v("DEBUG", "ClientActivity_onCreate");
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
				// ������Ϣ
				LogUtil.v("DEBUG", "ClientActivity_sendBtn_onClick");
				if("".equals(sendEditText.getText().toString().trim())){
					Toast.makeText(ClientActivity.this,"���벻��Ϊ��", Toast.LENGTH_SHORT).show();
				}else{
					//������Ϣ
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
				//��ʼ����
				LogUtil.v("DEBUG", "ClientActivity_startSearchBtn_onClick");
				Intent startSearchIntent=new Intent(BluetoothTools.ACTION_START_DISCOVERY);
				sendBroadcast(startSearchIntent);
			}
			
		});
		selectDeviceBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// ѡ���һ���豸
				LogUtil.v("DEBUG", "ClientActivity_selectDeviceBtn_onClick");
				Intent selectDeviceIntent=new Intent(BluetoothTools.ACTION_SELECTED_DEVICE);
				selectDeviceIntent.putExtra(BluetoothTools.DEVICE,deviceList.get(0));
				sendBroadcast(selectDeviceIntent);
				
			}
			
		});
	}
	protected void onStop(){
		//�رպ�̨service
		LogUtil.v("DEBUG", "ClientActivity_onStop");
		Intent startService=new Intent(BluetoothTools.ACTION_STOP_SERVICE);
		sendBroadcast(startService);
		unregisterReceiver(broadcastReceiver);
		super.onStop();
	}
}