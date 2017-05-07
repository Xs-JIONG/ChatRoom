package com.chat.room;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.chat.room.R;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class NetBroadcast
{
	public static String 状态="null";
	
	public final static String ip="224.111.111.111";
	public final static int port=8888;
	
	private static InetAddress iadr=null;
	private static MulticastSocket socket=null;
	
	public static void init() {
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
							String msg=new String(packet.getData(), 0, packet.getLength());
							parseMsg(msg);
						} catch (Exception e) {
							err(e);
						}
					}
				}
			}).start();
		} catch (Exception e) {
			err(e);
		}
	}

	public static void sendMsg(final String msg) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				byte[] data=msg.getBytes();
				DatagramPacket packet=new DatagramPacket(data, data.length, iadr, port);
				try {
					socket.send(packet);
				} catch (Exception e) {
					err(e);
				}
			}
		}).start();
	}
	
	private static void err(Exception e) {
		Log.e("局域网聊天室", e.toString());
	}
	
	private static void parseMsg(String msg) {
		switch (状态) {
			case MainActivity.askRen:
				if (msg.startsWith(":"+MainActivity.askRen)) {
					String[] data=msg.substring(MainActivity.askRen.length()+1).split(",");
					String u=EnterChatRoom.tempip;
					String z=EnterChatRoom.tempport;
					if (data[0].equals(EnterChatRoom.tempip.trim())&&data[1].equals(EnterChatRoom.tempport.trim())) {
						EnterChatRoom.have=true;
						状态="null";
					}
				}
				break;
		}
		if (msg.startsWith(MainActivity.askRen)) {
			String[] dat=msg.substring(MainActivity.askRen.length()).split(",");
			if (MainActivity.isOwner&&dat[0].equals(MainActivity.ip)&&dat[1].equals(MainActivity.port+"")) {
				sendMsg(":"+msg);
			}
		}
	}
	
	public static void setNow(String now) {
		状态=now;
	}
}
