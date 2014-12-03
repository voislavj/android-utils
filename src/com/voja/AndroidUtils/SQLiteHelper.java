package com.voja.AndroidUtils;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {
	
	SQLiteDatabase db;
	Context context;
	
	ArrayList<String> createSQLs, upgradeSQLs;

	public SQLiteHelper(String dbName, int dbVersion, Context c, ArrayList<String> createSQLs, ArrayList<String> upgradeSQLs) {
		super(c, dbName, null, dbVersion);
		
		context = c;
		this.createSQLs = createSQLs;
		this.upgradeSQLs = upgradeSQLs;
		
		open();
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		for (int i=0; i<createSQLs.size(); i++) {
			database.execSQL(createSQLs.get(i));
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		for (int i=0; i<upgradeSQLs.size(); i++) {
			try {
				database.execSQL(upgradeSQLs.get(i));
			} catch (SQLiteException e) {
				android.util.Log.v("sql-error", upgradeSQLs.get(i));
				android.util.Log.v("sql-error-message", e.getMessage());
			}
		}
	}
	
	public void open() {
		db = getWritableDatabase();
	}
	
	public void close() {
		if (db != null) db.close();
	}
	
	public SQLiteStatement prepareStatement(String sql) {
		if (db == null) {
			open();
		}
		
		return db.compileStatement(sql);
	}
	
	public int execute(String sql) {
		int affected = 0;
		
		db.execSQL(sql);
		Cursor c = query("SELECT changes()");
		if (c.moveToFirst()) {
			affected = c.getInt(0);
		} else {
			android.util.Log.e("voja", "empty cursor");
		}
		
		return affected;
	}
	
	public Cursor query(String sql) {
		return db.rawQuery(sql, null);
	}
	public Cursor query(String sql, String[] selArgs) {
		return db.rawQuery(sql, selArgs);
	}
}
