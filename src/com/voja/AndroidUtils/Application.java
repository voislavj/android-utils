package com.voja.AndroidUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

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
	
	public static void dialog(
		String 	message,
		int		icon,
		String	title,
		Click	clickPositive,
		Click	clickNegative
		) {
		
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
		
		AlertDialog dialog = builder.create();
		dialog.show();
	}
	
	/**
	 * ALERT    ==============================================
	 **/
	public static void alert(String message) {
		Click posClick = new Click("OK", new CommandWrapper(Command.NOOP));
		Click negClick = null;
		dialog(message, android.R.drawable.ic_dialog_info, DEFAULT_MESSAGE_TITLE, posClick, negClick);
	}
	public static void alert(String message, Click onClick) {
		dialog(message, android.R.drawable.ic_dialog_info, DEFAULT_MESSAGE_TITLE, onClick, null);
	}
	public static void alert(
			String 	message,
			int 	icon,
			String 	title,
			Click 	clickPositive,
			Click	clickNegative
			) {
		
		dialog(message, icon, title, clickPositive, clickNegative);
	}
	
	/**
	 * CONFIRM    ==============================================
	 **/
	public static void confirm(String message, Click yesClick) {
		Click noClick = new Click("Cancel", new CommandWrapper(Command.NOOP));
		dialog(message, -1, DEFAULT_MESSAGE_TITLE, yesClick, noClick);
	}
	public static void confirm(String message, Click yesClick, Click noClick) {
		dialog(message, -1, DEFAULT_MESSAGE_TITLE, yesClick, noClick);
	}
	public static void confirm(String message, Click yesClick, Click noClick, int icon) {
		dialog(message, icon, DEFAULT_MESSAGE_TITLE, yesClick, noClick);	
	}
	public static void confirm(String message, Click yesClick, Click noClick, int icon, String title) {
		dialog(message, icon, title, yesClick, noClick);
	}
}
