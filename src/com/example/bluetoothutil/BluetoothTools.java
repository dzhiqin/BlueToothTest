package com.example.bluetoothutil;

import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;

public class BluetoothTools {

	private static BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
	/*
	 * ��������ʹ�õ�UUID
	 */
	public  static final UUID PRIVATE_UUID=UUID.fromString("0f3561b9-bda5-4672-84ff-ab1f98e349b6");
	/*
	 * �ַ��������������Intent�е��豸����
	 */
	public static  final String DEVICE="DEVICE";
	
	public static final String SERVER_INDEX="SERVER_INDEX";
	
	public static final String DATA="DATA";
	
	/*
	 * ACTION ���ͱ�ʶ������������
	 */
	public static final String ACTION_READ_DATA="ACTION_READ_DATA";
	/*
	 * ACTION ���ͱ�ʶ����δ�����豸
	 */
	public static final String ACTION_NOT_FOUND_SERVER="ACTION_NOT_FOUND_DEVICE";
	/*
	 * ACTION ���ͱ�ʶ������ʼ�����豸
	 */
	public static final String ACTION_START_DISCOVERY="ACTION_START_DISCOVERY";
	/*
	 * ACTION �豸�б�
	 */
	public static final String ACTION_FOUND_DEVICE="ACTION_FOUND_DEVICE";
	/*
	 * ѡ����������ӵ��豸
	 */
	public static final String ACTION_SELECTED_DEVICE="ACTION_SELECTED_DEVICE";
	/*
	 * ACTION ����������
	 */
	public static final String ACTION_START_SERVER="ACTION_START_SERVER";
	/*
	 * ACTION�رպ�̨service
	 */
	public static final String ACTION_STOP_SERVICE="ACTION_STOP_SERVICE";
	/*
	 * ��service������
	 */
	public static final String ACTION_DATA_TO_SERVICE="ACTION_DATA_TO_SERVICE";
	/*
	 * ����Ϸҵ���е�����
	 */
	public static final String ACTION_DATA_TO_GAME="ACTION_DATA_TO_GAME";
	/*
	 * ���ӳɹ�
	 */
	public static final String ACTION_CONNECT_SUCCESS="ACTION_CONNECT_SUCCESS";
	/*
	 * ���Ӵ���
	 */
	public static final String ACTION_CONNECT_ERROR="ACTION_CONNECT_ERROR";
	/*
	 * MESSAGE���ͱ�ʶ�������ӳɹ�
	 */
	public static final int MESSAGE_CONNECT_SUCCESS=0x00000002;
	/*
	 * MESSAGE,����ʧ��
	 */
	public static final int MESSAGE_CONNECT_ERROR=0x00000003;
	/*
	 * ��ȡ��һ������
	 */
	public static final int MESSAGE_READ_OBJECT=0X00000004;
	/*
	 * ����������
	 */
	public static void openBluetooth(){
		adapter.enable();
	}
	/*
	 * �ر���������
	 */
	public static void closeBluetooth(){
		adapter.disable();
	}
	/*
	 * �����������ֹ���
	 * @param duration�����������ֹ��ܳ���������
	 */
	 public static void openDiscovery(int duration){
		 if(duration<=0||duration>300){
			 duration=200;
		 }
		 Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		 intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
	 }
	 /*
	  * ֹͣ��������
	  */
	 public static void stopDiscovery(){
		 adapter.cancelDiscovery();
	 }
}
