package com.jwetherell.QRComm575;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;
/*
This is the main activity which displays the home page of the app. It has four buttons,
1)Receive Data  ----> Clicking this button the device acts as a receiver and receives text data.
2)Send Data     ----> Clicking this button the device acts as a sender to send text data
3)Send Picture  ----> Clicking this button the device acts as a sender and sends image
4)Send Contact  ----> Clicking this button the device acts as a sender and sends contact.

 */
public class MainActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.ncsu.ece575.VisualComm.MESSAGE";

    public String msg = " In Main Activity: ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button Recvbutton = (Button) findViewById(R.id.button);
        Button Sendbutton = (Button) findViewById(R.id.button2);
        Button sendPicture= (Button) findViewById(R.id.button3);
        Button sendContact = (Button) findViewById(R.id.button4);
    }

    /*
    Function is called when Send Picture button is clicked. Starts a new activity SendPictureActivity.
     */


    public void sendPicture(View view)
    {
        Intent intent = new Intent(this, SendPictureActivity.class);
        startActivity(intent);

    }

    /*
    Function is called when receive data button is clicked. Starts a new activity CaptureActivity.
     */

    public void callCaptureQR(View view) {

        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);

        Log.d(msg, "Came here Call Capture");


    }

    /*
    Function is called when Send data button is clicked. Starts a new activity displayEncodeMainActivity
     */
    public void callEncodeQR(View view) {

        Intent intent = new Intent(this, EncodeMainActivity.class);
        startActivity(intent);

        Log.d(msg, "Came here Call EncodeMain");


    }
     /*
    Function is called when Send Contact button is clicked. Starts a new activity ContactInputActivity
     */

    public void sendContact(View view) {
        Intent intent = new Intent(this, ContactInputActivity.class);
        startActivity(intent);

        Log.d(msg, "Came here Call ContactInputActivity");
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}


