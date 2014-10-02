package com.voja.AndroidUtils;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {
	
	SQLiteDatabase db;
	Context context;
	
	ArrayList<String> sqls;

	public SQLiteHelper(String dbName, int dbVersion, Context c, ArrayList<String> createSQLs) {
		super(c, dbName, null, dbVersion);
		
		context = c;
		sqls = createSQLs;
		
		open();
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		for (int i=0; i<sqls.size(); i++) {
			android.util.Log.v("sql", sqls.get(i));
			database.execSQL(sqls.get(i));
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}
	
	public void open() {
		db = getWritableDatabase();
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
