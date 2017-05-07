package com.chat.room;

import java.util.List;
import java.util.ArrayList;
import android.app.Activity;

public class ActivityList
{
	private static List<Activity> list=new ArrayList<Activity>();
	
	public static void add(Activity ac) {
		list.add(ac);
	}
	
	public static void remove(Activity ac) {
		list.remove(ac);
	}
	
	public static void exit() {
		for (Activity one : list) one.finish();
		System.exit(0);
	}
}
