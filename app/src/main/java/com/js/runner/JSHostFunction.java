package com.js.runner;

import org.mozilla.javascript.Function;
import android.widget.Toast;
import org.mozilla.javascript.ImporterTopLevel;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Arrays;
import android.graphics.Bitmap;
import android.util.Base64;
import android.graphics.BitmapFactory;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.app.Activity;
import android.widget.EditText;
import com.chat.room.MainActivity;

public class JSHostFunction extends ImporterTopLevel
{
	private String name;
	public String[] fc={};
	private Function handle;
	
	public void setHandler(Function fc) {
		this.handle=fc;
	}
	
	public Function getHandler() {
		return this.handle;
	}
	
	public JSHostFunction(String n) {
		this.name=n;
		initFunction();
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String n) {
		this.name=n;
	}
	
	public void initFunction() {
		String[] con={"print","exit"};
		fc=new String[]{};
		add(con);
	}
	
	private void add(String[] he) {
		String[] hee=fc;
		fc=new String[hee.length+he.length];
		System.arraycopy(hee, 0, fc, 0, hee.length);
		System.arraycopy(he, 0, fc, 0, he.length);
	}
	
	public String[] getHostFunction() {
		return fc;
	}
	
	/*
	hehe
	hehe
	hehe
	hehe
	hehe
	hehe
	hehe
	hehe
	hehe
	hehe*/
	
	public static void print(Object what) {
		Toast.makeText(MainActivity.ctx, what.toString(), Toast.LENGTH_SHORT).show();
	}
	
	/*public AlertDialog.Builder alert(String title, String msg) {
		return UI.showMessage(MainActivity.ctx, title, msg);
	}*/
	
	public void exit() {
		((Activity) MainActivity.ctx).finish();
	}
}
