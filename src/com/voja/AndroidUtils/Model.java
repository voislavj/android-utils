package com.voja.AndroidUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class Model {
	
	SQLiteHelper connection;
	Context context;
	
	ArrayList<String> createSQLs  = new ArrayList<String>();
	ArrayList<String> upgradeSQLs = new ArrayList<String>();
	
	public Model(Context c) {
		context = c;
		
		loadStructure();
		
		ApplicationInfo appInfo = context.getApplicationInfo();
		int dbVersion;
        try {
	        dbVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
	        dbVersion = 1;
        }
        String dbName = appInfo.packageName.replaceAll("/[^a-z0-9]/i", "_");
		connection = new SQLiteHelper(dbName, dbVersion, context, createSQLs, upgradeSQLs);
		connection.open();
	}
	
	public void loadStructure() {
		String sql 	 = "";
		try {
			InputStream in = context.getAssets().open("database.sql");
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			String line;
			while((line = br.readLine()) != null) {
				sql += line;
			}
			
			String rgx = "create.*?;|(update.*?;)|(insert.*?;)";
			Pattern pattern = Pattern.compile(rgx, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			Matcher matcher = pattern.matcher(sql);
			while (matcher.find()) {
				createSQLs.add(matcher.group());
			}
			
			rgx = "alter.*?;";
			pattern = Pattern.compile(rgx, Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			matcher = pattern.matcher(sql);
			while (matcher.find()) {
				upgradeSQLs.add(matcher.group());
			}
			
			
		} catch(IOException e) {
			android.util.Log.e(context.getPackageName(), "Error loading database structure. " + e.getMessage());
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
