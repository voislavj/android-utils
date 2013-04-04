package com.voja.AndroidUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class Application extends android.app.Application {
	
	public static String DEFAULT_MESSAGE_TITLE = "Message";

	private static Context appContext;
	
	public interface Command {
		public void execute();
		public static final Command NOOP = new Command(){public void execute(){}};
	}
	public static class CommandWrapper implements DialogInterface.OnClickListener {
		private Command command;
		
		public CommandWrapper() {
			this.command = Command.NOOP;
		}
		public CommandWrapper(Command command) {
			this.command = command;
		}
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			command.execute();
		}
	}
	
	public static class Click {
		private String txt = "";
		private CommandWrapper e;
		
		public Click(String text) {
			txt = text;
			e = new CommandWrapper(Command.NOOP);
		}
		public Click(String text, CommandWrapper event) {
			txt = text;
			e = event;
		}
		
		public String getText() {
			return txt;
		}
		
		public CommandWrapper getEvent() {
			return e;
		}
		
		public static Click noop(String text) {
			return new Click(text, new CommandWrapper(Command.NOOP));
		}
	};
	
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
		Click posClick = new Click("OK", new CommandWrapper(Command.NOOP));
		Click negClick = null;
		alert(message, Toast.LENGTH_LONG, -1, DEFAULT_MESSAGE_TITLE, posClick, negClick);
	}
	public static void alert(String message, Click onClick) {
		alert(message, Toast.LENGTH_LONG, -1, DEFAULT_MESSAGE_TITLE, onClick, null);
	}
	public static void alert(
			String 	message, 
			int 	duration, 
			int 	icon, 
			String title,
			Click clickPositive) {
		
		alert(message, duration, icon, title, clickPositive, null);
	}
	
	public static void alert(
			String 	message, 
			int 	duration, 
			int 	icon, 
			String title,
			Click clickPositive,
			Click clickNegative) {
		
		//Toast.makeText(getContext(), message, duration).show();
		Context context = getContext();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setCancelable(true);
		if (icon > -1) {
			builder.setIcon(icon);
		}
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setInverseBackgroundForced(true);
		
		builder.setPositiveButton(clickPositive.getText(), clickPositive.getEvent());
		if (clickNegative != null) {
			builder.setNegativeButton(clickNegative.getText(), clickNegative.getEvent());
		}
		
		AlertDialog alert = builder.create();
		alert.show();
	}
}
