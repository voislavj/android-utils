package com.voja.AndroidUtils;

import java.io.IOException;
import java.util.ArrayList;
import org.michenux.android.db.utils.SqlParser;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {
	public SQLiteDatabase db;
	public Context context;
	public ArrayList<String> createSQLs, upgradeSQLs;
	
	private AssetManager assManager;
	private int dbVersion;
	
	public SQLiteHelper(String dbName, int dbVersion, Context context) {
		super(context, dbName, null, dbVersion);
		
		this.context    = context;
		this.assManager = context.getAssets();
		this.dbVersion  = dbVersion;
		
		open();
	}
	
	@Override
	public void onCreate(SQLiteDatabase database) {
		db = database;
		try {
			String filename = String.format("db/%d.create.sql", dbVersion);
			execFile(filename);
		} catch (IOException ioe) {
			android.util.Log.e("db-create", ioe.getMessage(), ioe);
		} catch (SQLException dbe) {
			android.util.Log.e("db-create", dbe.getMessage(), dbe);
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		db = database;
		try {
			int fileVersion=0;
			for(String file : assManager.list("db")) {
				if (file.endsWith(".upgrade.sql")) {
					fileVersion = Integer.parseInt(file.replaceAll(".upgrade.sql$", ""));
					if (fileVersion > oldVersion && fileVersion <= newVersion) {
						execFile("db/"+file);
					}
				}
			}
		} catch(IOException ioe) {
			android.util.Log.e("db-upgrade", ioe.getMessage(), ioe);
		} catch (SQLException dbe) {
			android.util.Log.e("db-create", dbe.getMessage(), dbe);
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
	
	private void execFile(String filename) throws IOException, SQLException {
		for(String sql : SqlParser.parseSqlFile(filename, assManager)) {
			db.execSQL(sql);
		}
	}
}
