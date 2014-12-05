package com.voja.AndroidUtils;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class Model {
	
	public SQLiteHelper connection;
	public Context context;
	
	public ArrayList<String> createSQLs  = new ArrayList<String>();
	public ArrayList<String> upgradeSQLs = new ArrayList<String>();
	
	public int dbVersion;
	
	public Model(Context c) {
		context = c;
		
		try {
			PackageManager  manager     = context.getPackageManager();
			String          packageName = context.getPackageName();
			ApplicationInfo appInfo     = context.getApplicationInfo();
			PackageInfo packageInfo     = manager.getPackageInfo(packageName, 0);
			dbVersion = packageInfo.versionCode;
			
			String dbName = appInfo.packageName.replaceAll("/[^a-z0-9]/i", "_");
			connection = new SQLiteHelper(dbName, dbVersion, context);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public SQLiteHelper getConnection() {
		return connection;
	}
	
	public SQLiteDatabase getDatabase() {
		return connection.db;
	}
	
	public SQLiteStatement prepare(String sql) {
		return connection.prepareStatement(sql);
	}
	
	public int execute(String sql) {
		return getConnection().execute(sql);
	}
	
	public Context getContext() {
		return this.context;
	}
	
	public String escapeString(String s) {
		return DatabaseUtils.sqlEscapeString(s);
	}
}
