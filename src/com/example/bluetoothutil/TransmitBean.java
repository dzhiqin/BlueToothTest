package com.example.bluetoothutil;

import java.io.Serializable;
/*
 * ���ڴ����������
 */
public class TransmitBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8739046261434511905L;
	private String msg="";
	public void setMsg(String msg){
		this.msg=msg;
		
	}
	public String getMsg(){
		return this.msg;
	}
}
