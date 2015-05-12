package com.jwetherell.QRComm575;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
/*
This activity displays the message received by the receiver.The message received can be either text data or Image data
 */
public class ResultActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        //Getting the message from the Intent
        CharSequence resText = intent.getCharSequenceExtra(CaptureActivity.EXTRA_MESSAGE);
        /*Check whether first string is "Image".If it is image the image is displayed in image view by removing first
        5 characters.
         */
        if(((resText).toString()).substring(0,5).equals("Image")) {
            byte [] encodeByte= Base64.decode((resText).toString().substring(5), Base64.DEFAULT);
            Bitmap Result_image= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            ImageView imageView = (ImageView) findViewById(R.id.recimage);
            imageView.setImageBitmap(Result_image);
        }else {
            /*
            If it is a text data the text is displayed in text view by stripping first four characters of the message
             */
            TextView Result = (TextView) findViewById(R.id.textView2);
            Result.setTextColor(Color.parseColor("#000000"));
            Result.setMovementMethod(new ScrollingMovementMethod());
            Result.setText((resText).toString().substring(4));
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result, menu);
        return true;
    }
    /*
This function is called when back button is pressed.When back button is pressed the main activity is called by Intent.
 */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            //intent.putExtra(EXTRA_MESSAGE, result);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}