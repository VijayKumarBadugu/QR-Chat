package com.jwetherell.QRComm575;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jwetherell.QRComm575.R;

import org.w3c.dom.Text;
/*
This activity is called to display the contact sent by the sender
 */
public class ResultActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultactivity2);
        Intent intent = getIntent();
        System.out.println("Came to Contact Display activity");
        String words = intent.getCharSequenceExtra(CaptureActivity.EXTRA_MESSAGE).toString();

        String field[] = words.split("\n");


        TextView conname = (TextView) findViewById(R.id.contname);
        conname.setText("Name: " + field[0]);
        TextView conphone = (TextView) findViewById(R.id.contphone);
        conphone.setText("Phone: " + field[1]);
        TextView conaddr = (TextView) findViewById(R.id.contaddr);
        conaddr.setText("Addr: " + field[2]);
        TextView conmail = (TextView) findViewById(R.id.contemail);
        conmail.setText("Email: " + field[3]);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result_activity2, menu);
        return true;
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
    /*
    This function is called when back button is pressed.When back button is pressed the main activity is called by Intent.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }
}
