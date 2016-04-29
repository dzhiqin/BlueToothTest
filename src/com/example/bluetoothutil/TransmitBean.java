package com.example.bluetoothutil;

import java.io.Serializable;
/*
 * 用于传输的数据类
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
