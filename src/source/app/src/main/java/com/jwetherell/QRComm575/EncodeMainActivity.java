package com.jwetherell.QRComm575;



import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.ActionBarActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.TimerTask;
import java.util.Timer;
import android.os.CountDownTimer;
/*
EncodeMainActivity activity is started when send data is clicked in the main menu.This activity gives user prompt
to enter the data to be sent.The user can enter enter text in EditTextView and click Generate Qr code
 */

public class EncodeMainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "eduteamvisuallinks.comncsu.google.httpssites.newactivityqr.MESSAGE";
    private static final int SPLASH_TIME = 10 * 1000;
    public int Noter;
    public String msg = "Android : ";
    public  String message;
    public  CountDownTimer waitTimer;
    private Timer timer = new Timer();
    public String [] words;
    public int NoterMax;
    public static int Myendcoder;
    public EditText pubedit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Myendcoder=0;
        setContentView(R.layout.activity_encode_main);
        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setTextColor(Color.parseColor("#000000"));
        editText.setMovementMethod(new ScrollingMovementMethod());
        pubedit = editText;
        Button button1 = (Button) findViewById(R.id.button);


    }
    /*
    This function is called when back button is pressed.When back button is pressed the main activity is called by Intent.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }


    //This function is called when Generate QRcode is clicked
    public void displayQR(View view) {
        int i = 0;
        Noter=0; // Do something in response to button

        EditText lceditText = (EditText) findViewById(R.id.editText);

        message = lceditText.getText().toString();                                  //Getting the message entered from the user



        Intent intent = new Intent(EncodeMainActivity.this, CaptureActivity.class); //Creating an Intent

        intent.putExtra(EXTRA_MESSAGE, "Text" + message);                           //Sending the data entered via intent
        Myendcoder=1;
        System.out.println("Came Here");
        startActivity(intent);

    }
}