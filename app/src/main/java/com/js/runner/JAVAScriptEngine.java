package com.js.runner;

import android.util.Log;
import com.chat.room.MainActivity;
import com.chat.room.R;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import android.os.Looper;

public class JAVAScriptEngine
{
	private static Context cx=null;
	private static ScriptableObject scope=null;
	private static JSHostFunction host;
	public static android.content.Context ctx;
	
	public static void init() {
		ctx=MainActivity.ctx;
		try {
			host=new JSHostFunction("233");
		cx=Context.enter();
		cx.setOptimizationLevel(-1);
		host=new JSHostFunction(ctx.getApplicationContext().getString(R.string.app_name));
		scope=cx.initStandardObjects(host, false);
		scope.defineFunctionProperties(host.getHostFunction(), JSHostFunction.class, ScriptableObject.DONTENUM);
		} catch (Exception e) {Log.e(MainActivity.TAG, e.toString());}
	}
	
	public static void runJS(final String content) {
		try {
			new Thread(new Runnable() {
					@Override
					public void run() {
						Looper.prepare();
						init();
						cx.evaluateString(scope, content, host.getName(), 1, null);
						flush();
				}
				}).start();
		} catch (Exception e) {
			//UI.showMessage(ctx, "错误！", e.getMessage().toString());
			Log.e(MainActivity.TAG, e.toString());
		}
	}
	
	public static void flush() {
		cx.exit();
	}
	
	public Scriptable getScope() {
		return this.scope;
	}
	
	public Context getContext() {
		return this.cx;
	}
}
