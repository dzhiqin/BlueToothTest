package com.example.bluetoothtest;

import com.example.bluetoothutil.LogUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button startServerBtn;
	private Button startClientBtn;
	private ButtonClickListener btnClickListener=new ButtonClickListener();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LogUtil.v("DEBUG", "MainActivity_onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		startServerBtn=(Button)findViewById(R.id.startServerBtn);
		startClientBtn=(Button)findViewById(R.id.startClientBtn);
		startServerBtn.setOnClickListener(btnClickListener);
		startClientBtn.setOnClickListener(btnClickListener);
	}

	class ButtonClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO 自动生成的方法存根
			switch(v.getId())
			{
			case R.id.startServerBtn:
				//打开服务器
				LogUtil.v("DEBUG", "MainActivity_startServerBtn_onClick");
				Intent serverIntent=new Intent(MainActivity.this,ServerActivity.class);
				serverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(serverIntent);
				break;
			case R.id.startClientBtn:
				//打开客户端
				LogUtil.v("DEBUG", "MainActivity_startClientBtn_onClick");
				Intent clientIntent=new Intent(MainActivity.this,ClientActivity.class);
				clientIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(clientIntent);
				break;
			}
		}
		
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
