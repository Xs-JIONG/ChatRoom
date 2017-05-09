package com.js.runner;

import android.content.Context;
import android.app.AlertDialog;
import com.chat.room.MainActivity;
import android.app.Activity;

public class UI
{
	public static AlertDialog.Builder showMessagea(Context con, String title, String msg) {
		AlertDialog.Builder b=new AlertDialog.Builder(con);
		b.setTitle(title);
		b.setMessage(msg);
		b.setPositiveButton("确定", null);
		return b;
	}
	
	public static void showMessage(final Context con, final String title, final String msg) {
		((Activity) MainActivity.ctx).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder b=showMessagea(con, title, msg);
				b.show();
			}
		});
	}
}
