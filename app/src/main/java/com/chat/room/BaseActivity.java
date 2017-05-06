package com.chat.room;

import android.app.Activity;
import android.os.Bundle;
import java.util.List;

public class BaseActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		ActivityList.add(this);
	}

	@Override
	protected void onDestroy()
	{
		ActivityList.remove(this);
		super.onDestroy();
	}
	
	public void exit() {
		ActivityList.exit();
	}
}
