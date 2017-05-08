package com.js.runner;

import android.content.Context;
import android.app.AlertDialog;

public class UI
{
	public static AlertDialog.Builder showMessage(Context con, String title, String msg) {
		AlertDialog.Builder b=new AlertDialog.Builder(con);
		b.setTitle(title);
		b.setMessage(msg);
		b.setPositiveButton("确定", null);
		return b;
	}
}
