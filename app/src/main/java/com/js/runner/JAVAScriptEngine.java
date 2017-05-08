package com.js.runner;

import android.util.Log;
import com.chat.room.MainActivity;
import com.chat.room.R;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class JAVAScriptEngine
{
	private Context cx=null;
	private ScriptableObject scope=null;
	private JSHostFunction host;
	public static android.content.Context ctx;
	
	public JAVAScriptEngine() {
		ctx=MainActivity.ctx;
		try {
		this.cx=Context.enter();
		cx.setOptimizationLevel(-1);
		host=new JSHostFunction(ctx.getApplicationContext().getString(R.string.app_name));
		this.scope=cx.initStandardObjects(host, false);
		scope.defineFunctionProperties(host.getHostFunction(), JSHostFunction.class, ScriptableObject.DONTENUM);
		} catch (Exception e) {Log.e(MainActivity.TAG, e.toString());}
	}
	
	public Object runJS(final String content) {
		try {
			/*new Thread(new Runnable() {
					@Override
					public void run() {*/
						Object obj=cx.evaluateString(scope, content, host.getName(), 1, null);
						return obj;/*这里调用了一个库
						一系列初始化后调用函数就会返回一个Object
						这里原本是在子线程执行的
						但考虑到子线程的返回有些麻烦
						所以还是改成了主线程*/
					/*}
				}).run();*/
		} catch (Exception e) {
			UI.showMessage(ctx, "错误！", e.getMessage().toString());
			Log.e(MainActivity.TAG, e.toString());
		}
		return 0;
	}
	
	public void flush() {
		cx.exit();
	}
	
	public Scriptable getScope() {
		return this.scope;
	}
}
