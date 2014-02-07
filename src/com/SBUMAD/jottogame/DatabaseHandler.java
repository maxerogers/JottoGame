package com.SBUMAD.jottogame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler  extends SQLiteOpenHelper{
	private static String TAG = "DatabaseHandler";
	private static int DB_VERSION = 1;
	private static String DB_NAME = "jottogame";
	private static String DB_PATH = "";
	private SQLiteDatabase myDataBase;
	private final Context myContext;
	
	public DatabaseHandler(Context context){
		super(context, DB_NAME, null, DB_VERSION);
		if(android.os.Build.VERSION.SDK_INT >= 17)
		{
			DB_PATH = context.getApplicationInfo().dataDir+"/databases";
		}else{
			DB_PATH = "data/data/"+context.getPackageName()+"/databases";
		}
		myContext = context;
	}
	
	public void createDataBase() throws IOException{
		//If database does not exist, copy it form the assets
		boolean dataBaseExists = checkDataBase();
		if(!dataBaseExists)
		{
			this.getReadableDatabase();
			this.close();
			try{
				//copy the database from assests
				copyDataBase();
				Log.e(TAG, "Create Database");
			}catch(IOException e){
				throw new Error("Error Copying Database");
			}
		}
	}
	
	public boolean checkDataBase(){
		File dbFile = new File(DB_PATH + DB_NAME);
		Log.v(TAG, dbFile+"  "+dbFile.exists());
		return dbFile.exists();
	}
	
	public void copyDataBase() throws IOException{
		InputStream input = myContext.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream output = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while((length = input.read(buffer)) > 0)
		{
			output.write(buffer, 0 , length);
		}
		output.flush();
		output.close();
		input.close();
	}
	
	public boolean openDataBase() throws SQLException{
		String path = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(
				path, null, SQLiteDatabase.CREATE_IF_NECESSARY);
		return myDataBase != null;
		
	}
	@Override
	public synchronized void close()
	{
		if(myDataBase != null){
			myDataBase.close();
		}
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
