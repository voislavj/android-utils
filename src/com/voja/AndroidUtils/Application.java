package com.voja.AndroidUtils;

import android.content.Context;
import android.widget.Toast;

public class Application {

	public static Context appContext;
	
	public static void setContext(Context context) {
		appContext = context;
	}
	private static Context getContext() throws RuntimeException {
		if (appContext.equals(null)) {
			throw new RuntimeException("Application Context is not set");
		}
		
		return appContext;
	}
	
	public static void alert(String message) {
		alert(message, Toast.LENGTH_LONG);
	}
	public static void alert(String message, int duration) {
		Toast.makeText(getContext(), message, duration).show();
	}
}
