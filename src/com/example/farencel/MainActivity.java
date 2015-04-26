package com.example.farencel;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	Button convert;
	EditText input;
	TextView output;
	RadioGroup startTemp, endTemp;
	RadioButton clicked, fa, ce, ke;
	InputMethodManager imm;
	Button button;
	String previous, inputs;
	String results, outputs;
	String testRegex;
	SharedPreferences save, osave, load, oload;
	int defaultText;
	ListView ip;
	ListView op;
	ArrayList<String> iprev;
	ArrayList<String> oprev;
	ArrayAdapter<String> iAdapter;
	ArrayAdapter<String> oAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		input = (EditText) findViewById(R.id.inputText); //Input EditText in InputFragment xml
		input.setImeOptions(EditorInfo.IME_ACTION_DONE); //Replaces keyboard next button with done button
		testRegex = "^\\W*\\d*\\" + "." + "?\\d{0,1}$"; //Used to compare input for no more than one decimal place
		output = (TextView) findViewById(R.id.outputText); //Output TextView in OutputFragment xml
		startTemp = (RadioGroup) findViewById(R.id.radioGroup1); //Input RadioGroup in main xml
		endTemp = (RadioGroup) findViewById(R.id.radioGroup2); //Output RadioGroup in main xml
		ip = (ListView) findViewById(R.id.listView1); //Input History ListView in InputFragment xml
		op = (ListView) findViewById(R.id.listView2); //Output History ListView in OutputFragment xml
		iprev = new ArrayList<String>(); //ArrayList to populate ListView with input history
		oprev = new ArrayList<String>(); //ArrayList to populate ListView with output history
		iAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, iprev ); //Adapter to communicate between input ArrayList and ListView
		oAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, oprev ); //Adapter to communicate between output ArrayList and ListView
		save = getSharedPreferences(previous, 0); //Saves previous input values to device
		osave = getSharedPreferences(results, 0); //Saves previous output values to device
		load = getSharedPreferences(previous, 0); //Loads previous inputs
		oload = getSharedPreferences(results, 0); //Loads previous outputs
		ip.setAdapter(iAdapter); //attach adapter to input ListView
		op.setAdapter(oAdapter); //attach adapter to output ListView
	    previous = load.getString("saved_prev", ""); //loads previous input string from SharedPref to string for ArrayList population
	    results = oload.getString("saved_res", ""); //loads previous output string from SharedPref to string for ArrayList population
	    Collections.addAll(iprev, previous.split(",")); //loads previous inputs into ArrayList using comma to determine separation
		Collections.addAll(oprev, results.split(",")); //loads previous outputs into ArrayList using comma to determine separation
	    
		startTemp.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId is the RadioButton selected
				fa = (RadioButton) findViewById(R.id.radioButton5);
				ce = (RadioButton) findViewById(R.id.radioButton6);
				ke = (RadioButton) findViewById(R.id.radioButton7);
				/*
				 * If radioButton in 'from' group is selected, prevents the same button 
				 * from being selected in 'to' group, disables button and grays out text.
				 * Also auto selects a different conversion in 'to' group to prevent same button from being selected.
				 */
				if (checkedId == R.id.radioButton1) {
					endTemp.check(R.id.radioButton6);
					fa.setClickable(false);
					ce.setClickable(true);
					ke.setClickable(true);
					fa.setTextColor(Color.parseColor("#737373"));
					ce.setTextColor(Color.parseColor("#000000"));
					ke.setTextColor(Color.parseColor("#000000"));
				}
				if (checkedId == R.id.radioButton2) {
					endTemp.check(R.id.radioButton7);
					ce.setClickable(false);
					fa.setClickable(true);
					ke.setClickable(true);
					fa.setTextColor(Color.parseColor("#000000"));
					ce.setTextColor(Color.parseColor("#737373"));
					ke.setTextColor(Color.parseColor("#000000"));
				}
				if (checkedId == R.id.radioButton3) {
					endTemp.check(R.id.radioButton5);
					ke.setClickable(false);
					fa.setClickable(true);
					ce.setClickable(true);
					fa.setTextColor(Color.parseColor("#000000"));
					ce.setTextColor(Color.parseColor("#000000"));
					ke.setTextColor(Color.parseColor("#737373"));
				}
				
			}
		});
	}
	
	
	
	public void convert(View v) {
		if(!(input.getText().toString().matches(testRegex))){ //If input has more than one digit after decimal gives error
			Toast.makeText(getApplicationContext(), " Enter a value with at most one decimal place", Toast.LENGTH_SHORT).show();
		} 
		else if (input.getText().length() != 0) { // Prevents errors from null input
			
			NumberFormat oneDigit = new DecimalFormat("########.#"); //Formats one decimal place and ignores decimal for whole numbers
			double entered = Double.parseDouble(input.getText().toString()); //Converts input string to double for conversion
			double temp; //Output variable
			
			/*
			 * Comments for Fahr to Cel apply for all conversions
			 */
			
			//Fahr to Cel
			if ((startTemp.getCheckedRadioButtonId() == R.id.radioButton1) && (endTemp.getCheckedRadioButtonId() == R.id.radioButton6)){
				temp = Double.valueOf(oneDigit.format((entered - 32)/(1.8))); //Conversion formula
				String stringdouble= oneDigit.format(entered) + " °F     =     " + Double.toString(temp) + " °C";
				output.setText(stringdouble);
				if(previous.isEmpty()){ //if string of previous values is empty add values as first value
					iprev.remove(0); //removes blank space from top of listView
					oprev.remove(0); //removes blank space from top of listView
					iAdapter.notifyDataSetChanged(); //updates ListView with blank space removed
					oAdapter.notifyDataSetChanged(); //updates ListView with blank space removed
					previous = oneDigit.format(entered) + "°F"; //adds new value to input history string
					results = oneDigit.format(temp) + "°C"; //adds new value to output history string
				}
				else{
					previous = previous + "," + oneDigit.format(entered) + "°F"; //adds new values to input history with comma if not the first value
					results = results + "," + oneDigit.format(temp) + "°C"; //adds new values to output history with comma if not the first value
				}
				inputs = oneDigit.format(entered) + "°F"; //input with degree sign
				outputs = oneDigit.format(temp) + "°C"; //output with degree sign
				iprev.add(inputs); //adds new values to input ArrayList
				oprev.add(outputs); //adds new values to output ArrayList
				iAdapter.notifyDataSetChanged(); //dynamically updates listView when conversion is complete
				oAdapter.notifyDataSetChanged(); //dynamically updates listView when conversion is complete				
			}
			//Cel to Fahr
			if ((startTemp.getCheckedRadioButtonId() == R.id.radioButton2) && (endTemp.getCheckedRadioButtonId() == R.id.radioButton5)){
				temp = Double.valueOf(oneDigit.format(9 * (entered / 5) + 32));
				String stringdouble = oneDigit.format(entered) + " °C     =     " + Double.toString(temp) + " °F";
				output.setText(stringdouble);
				if(previous.isEmpty()){
					iprev.remove(0);
					oprev.remove(0);
					iAdapter.notifyDataSetChanged();
					oAdapter.notifyDataSetChanged();
					previous = oneDigit.format(entered) + "°C";
					results = oneDigit.format(temp) + "°F";
				}
				else{
					previous = previous + "," + oneDigit.format(entered) + "°C";
					results = results + "," + oneDigit.format(temp) + "°F";
				}
				inputs = oneDigit.format(entered) + "°C";
				outputs = oneDigit.format(temp) + "°F";
				iprev.add(inputs);
				oprev.add(outputs);
				iAdapter.notifyDataSetChanged();
				oAdapter.notifyDataSetChanged();
				
			}
			//Fahr to Kel
			if ((startTemp.getCheckedRadioButtonId() == R.id.radioButton1) && (endTemp.getCheckedRadioButtonId() == R.id.radioButton7)){
				temp = Double.valueOf(oneDigit.format(((entered - 32)/(1.8)) + 273.15));
				String stringdouble = oneDigit.format(entered) + " °F     =     " + Double.toString(temp) + " K";
				output.setText(stringdouble);
				if(previous.isEmpty()){
					iprev.remove(0);
					oprev.remove(0);
					iAdapter.notifyDataSetChanged();
					oAdapter.notifyDataSetChanged();
					previous = oneDigit.format(entered) + "°F";
					results = oneDigit.format(temp) + "K";
				}
				else{
					previous = previous + "," + oneDigit.format(entered) + "°F";
					results = results + "," + oneDigit.format(temp) + "K";
				}
				inputs = oneDigit.format(entered) + "°F";
				outputs = oneDigit.format(temp) + "K";
				iprev.add(inputs);
				oprev.add(outputs);
				iAdapter.notifyDataSetChanged();
				oAdapter.notifyDataSetChanged();
			}
			//Cel to Kel
			if ((startTemp.getCheckedRadioButtonId() == R.id.radioButton2) && (endTemp.getCheckedRadioButtonId() == R.id.radioButton7)){
				temp = Double.valueOf(oneDigit.format(entered + 273.15));
				String stringdouble = oneDigit.format(entered) + " °C     =     " + Double.toString(temp) + " K";
				output.setText(stringdouble);
				if(previous.isEmpty()){
					iprev.remove(0);
					oprev.remove(0);
					iAdapter.notifyDataSetChanged();
					oAdapter.notifyDataSetChanged();
					previous = oneDigit.format(entered) + "°C";
					results = oneDigit.format(temp) + "K";
				}
				else{
					previous = previous + "," + oneDigit.format(entered) + "°C";
					results = results + "," + oneDigit.format(temp) + "K";
				}
				inputs = oneDigit.format(entered) + "°C";
				outputs = oneDigit.format(temp) + "K";
				iprev.add(inputs);
				oprev.add(outputs);
				iAdapter.notifyDataSetChanged();
				oAdapter.notifyDataSetChanged();
			}
			//Kel to Fahr
			if ((startTemp.getCheckedRadioButtonId() == R.id.radioButton3) && (endTemp.getCheckedRadioButtonId() == R.id.radioButton5)){
				temp = Double.valueOf(oneDigit.format(((entered - 273.15)*1.8) + 32));
				String stringdouble = oneDigit.format(entered) + " K     =     " + Double.toString(temp) + " °F";
				output.setText(stringdouble);
				if(previous.isEmpty()){
					iprev.remove(0);
					oprev.remove(0);
					iAdapter.notifyDataSetChanged();
					oAdapter.notifyDataSetChanged();
					previous = oneDigit.format(entered) + "K";
					results = oneDigit.format(temp) + "°F";
				}
				else{
					previous = previous + "," + oneDigit.format(entered) + "K";
					results = results + "," + oneDigit.format(temp) + "°F";
				}
				inputs = oneDigit.format(entered) + "K";
				outputs = oneDigit.format(temp) + "°F";
				iprev.add(inputs);
				oprev.add(outputs);
				iAdapter.notifyDataSetChanged();
				oAdapter.notifyDataSetChanged();
			}
			//Kel to Cel
			if ((startTemp.getCheckedRadioButtonId() == R.id.radioButton3) && (endTemp.getCheckedRadioButtonId() == R.id.radioButton6)){
				temp = Double.valueOf(oneDigit.format(entered - 273.15));
				String stringdouble = oneDigit.format(entered) + " K     =     " + Double.toString(temp) + " °C";
				output.setText(stringdouble);
				if(previous.isEmpty()){
					iprev.remove(0);
					oprev.remove(0);
					iAdapter.notifyDataSetChanged();
					oAdapter.notifyDataSetChanged();
					previous = oneDigit.format(entered) + "K";
					results = oneDigit.format(temp) + "°C";
				}
				else{
					previous = previous + "," + oneDigit.format(entered) + "K";
					results = results + "," + oneDigit.format(temp) + "°C";
				}
				inputs = oneDigit.format(entered) + "K";
				outputs = oneDigit.format(temp) + "°C";
				iprev.add(inputs);
				oprev.add(outputs);
				iAdapter.notifyDataSetChanged();
				oAdapter.notifyDataSetChanged();
			}
		} 
		else {
			Toast.makeText(getApplicationContext(), " Please Enter A Value", Toast.LENGTH_SHORT).show(); //Error message for no input
		}
		input.setText(""); //Clears EditText when convert button is pressed
		save.edit().putString("saved_prev", previous).commit(); //saves string of previous inputs to SharedPreferences
	    osave.edit().putString("saved_res", results).commit(); //save string of previous outputs to SharedPreferences
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
