package com.SBUMAD.jottogame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Game extends Activity {
	public static final String TAG = "JottoGame";
	public static String lastGuess = "";
	private ArrayList<String> guesses = new ArrayList<String>();;
	private String answer = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		//createAZDialog();
		//loadData("EN_FiveLetterWordsList.txt");
		loadData();
		/*
		ArrayList<String> results = db.getAllWords();
		for(int i=0;i<results.size();i++)
		{
			Log.i(TAG, results.get(i));
		}
		*/
		//newGameListener();
		//addGuessListener();
		//newGame();
	}
	
	private void loadData() {
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		try{
			db.createDataBase();
		}catch (IOException e){
			throw new Error("Unable able to create Database");
		}
		try{
			db.openDataBase();
		}catch (SQLException e){
			throw e;
		}
	}

	private void addGuessListener() {
		Button btnG = (Button) findViewById(R.id.Guess);
		btnG.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				EditText et01 = (EditText) findViewById(R.id.EditText01);
				String input = et01.getText().toString();
				//make sure it is a meaningful and valid guess
				if (input != null && !input.isEmpty() && input.length() == 5){
					lastGuess = input;
				}
			}
			
		});
		this.guesses.add(lastGuess);
		
	}
	/*
	private void newGameListener(){
		Button btnNG = (Button) findViewById(R.id.Giveup);
		btnNG.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				newGame();
			}
		});
	}
	
	private void newGame() {
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		int limit = db.getNumberOfWords();
		int randomNum = (int)(Math.random()*limit); 
		answer = db.getWordAtIndex(randomNum);
		guesses = new ArrayList<String>();
	}

	public void loadData(String inFile) {
		Log.i(TAG,"STARTING TO LOAD DATA");
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		db.loadTextFile(inFile,this);	
		//db.loadTextFile2(inFile,this);	
		db.close();
		Log.i(TAG,"DONE LOADING DATA");
	}

	private void createAZDialog(){
		Button az = (Button) findViewById(R.id.AZ);
		az.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				int length = (int)'Z' - (int)'A';
				String[] items = new String[length+1];
				int j = 0;
				for(char i='A';i<='Z';i++,j++){ items[j] = ""+i;}
				AlertDialog.Builder builder = new AlertDialog.Builder(arg0.getContext());
				builder.setTitle("Letter Filter A-Z");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int index) {
						//TODO change letters colors
						
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				
			}
			
		});
	}
	*/
}
