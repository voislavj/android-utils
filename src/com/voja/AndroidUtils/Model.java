package com.voja.AndroidUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

public class Model {
	
	SQLiteHelper connection;
	Context context;
	
	ArrayList<String> sqls = new ArrayList<String>();
	
	public Model(Context c) {
		context = c;
		
		sqls = loadStructure();
		connection = new SQLiteHelper(context, sqls);
		connection.open();
	}
	
	public ArrayList<String> loadStructure() {
		ArrayList<String> sqls = new ArrayList<String>();
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
				sqls.add(matcher.group());
			}
		} catch(IOException e) {
			android.util.Log.e(context.getPackageName(), "Error loading database structure. " + e.getMessage());
		}
		return sqls;
	}
	
	public SQLiteHelper getConnection() {
		return connection;
	}
	
	public int execute(String sql) {
		return getConnection().execute(sql);
	}
	
	public Context getContext() {
		return this.context;
	}
}
