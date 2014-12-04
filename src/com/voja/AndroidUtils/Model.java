package com.voja.AndroidUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.michenux.android.db.utils.SqlParser;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
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
		
		loadStructure();
		
		for (String sql : createSQLs) {
			android.util.Log.v("create", sql);
		}
		for (String sql : upgradeSQLs) {
			android.util.Log.v("upgrade", sql);
		}
		
		ApplicationInfo appInfo 	= context.getApplicationInfo();
		PackageManager  manager  	= context.getPackageManager();
		String 			packageName = context.getPackageName();
		try {
			android.util.Log.v("package", packageName);
			PackageInfo packageInfo = manager.getPackageInfo(packageName, 0);
			android.util.Log.v("package-name", packageName);
			android.util.Log.v("package-version-code", ""+packageInfo.versionCode);
			android.util.Log.v("package-version-name", packageInfo.versionName);
			dbVersion = packageInfo.versionCode;
			
			String dbName = appInfo.packageName.replaceAll("/[^a-z0-9]/i", "_");
			connection = new SQLiteHelper(dbName, dbVersion, context, createSQLs, upgradeSQLs);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void loadStructure() {
		try {
			loadSQL(createSQLs,  String.format("database.%d.create.sql", dbVersion));
			loadSQL(upgradeSQLs, String.format("database.%d.upgrade.sql", dbVersion));
		} catch(IOException e) {
			android.util.Log.e(context.getPackageName(), "Error loading database structure. " + e.getMessage());
		}
	}
	
	private void loadSQL(ArrayList<String> list, String filename) throws IOException {
		AssetManager manager = context.getAssets();
		InputStream in = manager.open(filename);
		
		for (String sql : SqlParser.parseSqlFile(in)) {
			list.add(sql);
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
