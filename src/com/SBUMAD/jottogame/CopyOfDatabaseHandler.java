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

public class CopyOfDatabaseHandler  extends SQLiteOpenHelper{
	//members
	private static final int DATABASE_VERSION = 1;
	private static String DB_PATH = "/data/data/com.sbumad.jottogame/databases/";
	private static final String DATABASE_NAME = "jottogame";
	// Labels table name
	private static final String TABLE_WORDS = "words";
	// labels column names
	private static final String KEY_ID = "id";
	private static final String KEY_VALUE = "value";
	private SQLiteDatabase dataBase;
	private final Context dbContext;
	//methods
	public CopyOfDatabaseHandler(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		dbContext = context;
	}
	
	public void createDataBase() throws IOException{
		boolean dataBaseExist = checkDataBase();
		Log.d(Game.TAG, "Create DB in Helper. Data exists? "+dataBaseExist);
		if(!dataBaseExist){
			Log.d(Game.TAG, "get Writable in DatabaseHelper");
			this.getWritableDatabase();
			try{
				Log.d(Game.TAG, "copy Database");
				copyDataBase();
			}catch(IOException e){
				Log.d(Game.TAG, "copy not successful");
				throw new Error("ErrorCopyingDataBase");
			}
		}
	}
	private void copyDataBase() throws IOException{
		Log.d(Game.TAG,"copy");
		InputStream input = dbContext.getResources().getAssets().open(DATABASE_NAME);
		String outFileName = DB_PATH + DATABASE_NAME;
		Log.d(Game.TAG, "Output: "+outFileName);
		File createOutFile = new File(outFileName);
		if(!createOutFile.exists()){
			createOutFile.mkdir();
		}
		OutputStream output = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while((length = input.read(buffer))>0)
		{
			output.write(buffer, 0, length);
		}
		output.flush();
		output.close();
		input.close();
	}
	private boolean checkDataBase(){
		SQLiteDatabase checkDataBase = null;
		try{
			String path = DB_PATH + DATABASE_NAME;
			checkDataBase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		}catch(SQLiteException e){
			Log.e(Game.TAG,"DatabaseNotFound "+e.toString());
		}
		if(checkDataBase != null){
			checkDataBase.close();
		}
		return checkDataBase != null;
	}
	public boolean openDataBase() throws SQLException{
		String path = DB_PATH + DATABASE_NAME;
		dataBase = SQLiteDatabase.openDatabase(path, null,  SQLiteDatabase.NO_LOCALIZED_COLLATORS);
		return dataBase != null;
	}
	@Override
	public synchronized void close(){
		if(dataBase != null){	dataBase.close(); }
		super.close();
	}
	@Override
	public void onCreate(SQLiteDatabase db){
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.v(Game.TAG, "UpgradingDatabase, This will drop current database and will recreate it");	
	}
	
	public void insertWord(String word){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_VALUE, word);
		db.insert(TABLE_WORDS, null, values);
		db.close();
	}
	public ArrayList<String> getAllWords(){
		ArrayList<String> words = new ArrayList<String>();
		String selectQuery = "SELECT * FROM "+TABLE_WORDS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		//looping through all rows and adding it to list
		if(cursor.moveToFirst()){
			do{
				words.add(cursor.getString(1));
			} while (cursor.moveToNext());
		}
		//close connections
		cursor.close();
		db.close();
		
		//return words
		return words;
	}
	
	public int getNumberOfWords(){
		String query ="SELECT * FROM "+TABLE_WORDS+" ORDER BY column DESC LIMIT 1;";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		int result = -1;
		//looping through all rows and adding it to list
		if(cursor.moveToFirst()){
			result = Integer.parseInt(cursor.getString(0));
		}
		return result;
	}
	public String getWordAtIndex(int randomNum) {
		String result = "";
		String query = "SELECT * FROM "+TABLE_WORDS+" WHERE "+KEY_ID+" = "+randomNum;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst()){
			result = cursor.getString(0);
		}
		return result;
	}
	public void loadTextFile(String inFile, Activity act) {
		SQLiteDatabase db = this.getWritableDatabase();
		try {
		    BufferedReader reader = new BufferedReader(
		        new InputStreamReader(act.getAssets().open(inFile), "UTF-8")); 
		    // do reading, usually loop until end of file reading 
		    String tempLine = reader.readLine();
		    while (tempLine != null) {
		    	tempLine.trim();
		    	ContentValues values = new ContentValues();
		    	values.put(KEY_VALUE, tempLine);
				db.insert(TABLE_WORDS, null, values);
		    	tempLine = reader.readLine(); 
		    }
		    reader.close();
		} catch (IOException e) {
		    //log the exception
		}
		db.close();
	}
}
