package com.chat.room;

import android.app.Activity;
import android.os.Bundle;
import java.net.*;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.LayoutInflater;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public class MainActivity extends BaseActivity 
{
	public final static String sys="系统";
	public static String user="用户";
	public final static String askName="::name";
	public final static int TYPE_ADDTEXT=1;
	
	public final static String welcome="欢迎使用局域网聊天室！";
	
	private int port=0;
	private InetAddress iadr=null;
	private MulticastSocket socket=null;
	private boolean confi=false;
	public static String nameofroom;
	public static boolean isOwner=false;
	private String ip;
	private EditText edit;
	private Button send;
	private boolean pressBack=false;
	private TextView result;
	/*private Handler han=new Handler() {
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what) {
				case TYPE_ADDTEXT:
					result.setText(result.getText().toString()+((String) msg.obj));
					break;
			}
		}
	};*/
	
	private void print(String who, String text) {
		String con=new StringBuilder("[").append(who).append("]").append(text).append("\n").toString();
		/*Message msg=new Message();
		msg.what=TYPE_ADDTEXT;
		msg.obj=con;
		han.sendMessage(msg);*/
		result.setText(result.getText()+con);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		edit=(EditText) findViewById(R.id.chat_edit);
		send=(Button) findViewById(R.id.chat_send);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (confi) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							final String con=edit.getText().toString();
							byte[] data=(user+":"+con).getBytes();
							DatagramPacket packet=new DatagramPacket(data, data.length, iadr, port);
							try {
								socket.send(packet);
								MainActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										edit.setText("");
									}
								});
							} catch (Exception e) {
								err(e);
							}
						}
					}).start();
				} else {
					alert("还没有连接成功，请等待！");
				}
			}
		});
		result=(TextView) findViewById(R.id.chat_result);
		print(sys, welcome);
		addChatRoom();
    }
	
	private void addChatRoom() {
		Intent in=new Intent(MainActivity.this, EnterChatRoom.class);
		startActivityForResult(in, 1);
	}
	
	private void alert(String text) {
		Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
	}
	
	private void err(Exception e) {
		print(sys, "程序出现错误！");
		print(sys, "错误详情:\n"+e.toString());
	}
	
	private void connect() {
		print(sys, "正在加入聊天室……");
		try {
			iadr=InetAddress.getByName(ip);
			socket=new MulticastSocket(port);
			socket.setTimeToLive(1);
			socket.joinGroup(iadr);
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						byte[] data=new byte[2048];
						DatagramPacket packet=new DatagramPacket(data, data.length, iadr, port);
						try {
							socket.receive(packet);
							final String msg=new String(packet.getData(), 0, packet.getLength());
							if (isOwner&&msg.startsWith(askName)) {
								sendMsg(":"+askName+nameofroom);
							} else if (msg.startsWith(":"+askName)) {
								MainActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										setTitle(msg.substring(askName.length()+1));
									}
								});
							} else if (!msg.endsWith(askName)) {
								StringBuilder n=new StringBuilder();
								int q;
								for (q=0;q<msg.length();q++) {
									if (msg.charAt(q) != ':') n.append(msg.charAt(q));
										else break;
								}
								q++;
								final String nam=n.toString();
								final String p=msg.substring(q);
								MainActivity.this.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										print(nam, p);
									}
								});
							}
						} catch (Exception e) {
							err(e);
						}
					}
				}
			}).start();
			if (isOwner) {
				sendMsg(gets(sys, user+"创建了房间"));
				setTitle(nameofroom);
			} else {
				sendMsg(gets(sys, user+"加入了房间"));
				sendMsg(askName);
			}
			confi=true;
		} catch (Exception e) {
			err(e);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == 1&&resultCode == RESULT_OK) {
			ip=data.getStringExtra("ip");
			port=data.getIntExtra("port", 0);
			print(sys, "聊天室设置成功！");
			print(sys, "IP:"+ip+" 端口:"+port);
			connect();
		}
	}
	
	public void sendMsg(final String text) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				byte[] data=text.getBytes();
				DatagramPacket packet=new DatagramPacket(data, data.length, iadr, port);
				try {
					socket.send(packet);
				} catch (Exception e) {
					err(e);
				}
			}
		}).start();
	}
	
	private String gets(String who, String text) {
		return new StringBuilder(who).append(":").append(text).toString();
	}

	@Override
	public void onBackPressed()
	{
		if (pressBack) {
			sendMsg(gets(sys, user+"离开了房间"));
			exit();
		} else {
			pressBack=true;
			alert("再按一次退出");
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					pressBack=false;
				}
			}, 1000);
		}
	}
}
