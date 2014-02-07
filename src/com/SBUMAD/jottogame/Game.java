package com.SBUMAD.jottogame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Game extends Activity {
	public static final String TAG = "JottoGame";
	public static String lastGuess = "";
	private ArrayList<String> guesses = new ArrayList<String>();;
	private String answer = "";
	private int[] abc = new int[26];
	private DatabaseHandler db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		loadData();
		newGameListener();
		addGuessListener();
		newGame();
		createAZDialog();
	}
	
	private void loadData() {
		db = new DatabaseHandler(getApplicationContext());
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
		Log.v(TAG,"Database Handler setup");
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
					//Log.i(TAG, "Check if valid guess");
					if(db.isValidGuess(input))
					{
						guesses.add(input);
						//Log.i(TAG, guesses.get(guesses.size()-1));
						TextView guessList = (TextView) findViewById(R.id.GuessList);
						String temp = guessList.getText().toString();
						guessList.setText(input+": "+inCommon(answer, input).length()+"\n"+temp);
					}else{
						//Log.i(TAG, "NOT a valid guess");
						Toast.makeText(getApplicationContext(), input+" is not a valid guess", Toast.LENGTH_SHORT).show();
					}
				}
			}

			private String inCommon(String wordA, String wordB) {
				String common = "";
				for(int i=0;i<wordA.length();i++){  
				    for(int j=0;j<wordB.length();j++){  
				        if(wordA.charAt(i)==wordB.charAt(j)){  
				            common += wordA.charAt(i);  
				            break;
				        }  
				    }  
				} 
				return common;
			}
			
		});
	}
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
		answer = db.getRandomWord();
		guesses = new ArrayList<String>();
		Log.v(TAG, answer);
		TextView guessList = (TextView) findViewById(R.id.GuessList);
		guessList.setText("");
	}
	
	private void recolorLetter(int index) {
		TextView tv = (TextView) findViewById(R.id.GuessList);
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
						if(abc[index] < 1){
							abc[index] = 1;
						}else if(abc[index] > 1){
							abc[index] = 0;
						}else{
							abc[index] = 2;
						}
						Log.i(TAG,index+":"+abc[index]);
						recolorLetter(index);
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				
			}
			
		});
	}

}
