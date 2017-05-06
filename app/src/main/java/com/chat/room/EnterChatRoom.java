package com.chat.room;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.chat.room.MainActivity;
import com.chat.room.R;
import android.app.Dialog;

public class EnterChatRoom extends BaseActivity
{
	public final static int RES_RIGHT=0;
	public final static int RES_L4=1;
	public final static int RES_OUT=2;
	public final static int RES_SPECIAL=3;
	public final static int RES_TEXT=4;
	
	private EditText ip1;
	private EditText ip2;
	private EditText ip3;
	private EditText po;
	private EditText name;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enter_room);
		ip1=(EditText) findViewById(R.id.enter_room_ip_1);
		ip2=(EditText) findViewById(R.id.enter_room_ip_2);
		ip3=(EditText) findViewById(R.id.enter_room_ip_3);
		po=(EditText) findViewById(R.id.enter_roon_port);
		name=(EditText) findViewById(R.id.enter_room_name);
		Button bu=(Button) findViewById(R.id.enter_room_done);
		bu.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					switch (isIpRight(getIP())) {
						case RES_L4:
							alert("IP不能小于四个位！");
							break;
						case RES_OUT:
							alert("每个IP位不能小于1或大于255！");
							break;
						case RES_SPECIAL:
							alert("聊天室IP首位必须为224！");
							break;
						case RES_TEXT:
							alert("IP地址包含非法字符！");
							break;
						case RES_RIGHT:
							String n=name.getText().toString();
							if (n.contains(":")) {
								alert("用户名包含非法字符！");
								break;
							}
							if (getIP().equals("224.111.111.111")&&Integer.parseInt(po.getText().toString()) == 8888) {
								alert("对不起，请不要使用广播IP！");
								break;
							}
							MainActivity.user=n;
							Intent in=new Intent();
							in.putExtra("ip", getIP());
							in.putExtra("port", Integer.parseInt(po.getText().toString()));
							setResult(RESULT_OK, in);
							finish();
							break;
					}
				}
			});
		Button buy=(Button) findViewById(R.id.enter_room_create);
		buy.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					switch (isIpRight(getIP())) {
						case RES_L4:
							alert("IP不能小于四个位！");
							break;
						case RES_OUT:
							alert("每个IP位不能小于1或大于255！");
							break;
						case RES_SPECIAL:
							alert("聊天室IP首位必须为224！");
							break;
						case RES_TEXT:
							alert("IP地址包含非法字符！");
							break;
						case RES_RIGHT:
							final String n=name.getText().toString();
							if (n.contains(":")) {
								alert("用户名包含非法字符！");
								break;
							}
							if (getIP().equals("224.111.111.111")&&Integer.parseInt(po.getText().toString()) == 8888) {
								alert("对不起，请不要使用广播IP！");
								break;
							}
							AlertDialog.Builder b=new AlertDialog.Builder(EnterChatRoom.this);
							b.setTitle("设置房间名称");
							final EditText ed=new EditText(EnterChatRoom.this);
							ed.setHint("名字");
							b.setView(ed);
							b.setCancelable(false);
							b.setPositiveButton("确定", new Dialog.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int position) {
									MainActivity.user=n;
									Intent in=new Intent();
									MainActivity.isOwner=true;
									MainActivity.nameofroom=ed.getText().toString();
									in.putExtra("ip", getIP());
									in.putExtra("port", Integer.parseInt(po.getText().toString()));
									setResult(RESULT_OK, in);
									finish();
								}
							});
							b.show();
							break;
					}
				}
			});
	}
	

	private int isIpRight(String ipp) {
		String[] q=ipp.split("[.]");
		if (q.length != 4) return RES_L4;
		int[] w=new int[4];
		try {
		for (int i=0;i<4;i++) {
			w[i]=Integer.parseInt(q[i]);
			if (w[i] <= 0||w[i] >= 256) return RES_OUT;
		}
		} catch (Exception e) {
			return RES_TEXT;
		}
		if (w[0] != 224) return RES_SPECIAL;
		return RES_RIGHT;
	}
	
	private void alert(String text) {
		Toast.makeText(EnterChatRoom.this, text, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onBackPressed()
	{
		
		exit();
	}
	
	private String getIP() {
		return "224."+ip1.getText().toString()+"."+ip2.getText().toString()+"."+ip3.getText().toString();
	}
}
