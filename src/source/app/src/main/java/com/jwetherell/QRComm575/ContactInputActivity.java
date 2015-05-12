package com.jwetherell.QRComm575;
/*
This activity is called to take inputs from the user for the contact information and send the contact
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.jwetherell.QRComm575.R;

public class ContactInputActivity extends Activity {
    public static Bundle contactbundle = new Bundle();
    public final static String EXTRA_MESSAGE = "com.ncsu.ece575.VisualComm.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_input);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contact_input, menu);
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
    This function is called when Display QR button is pressed.The user can enter the contact information in the
    relevant fields and will be able to send the contact as a QR.
     */
    public void displayQR(View view) {
        int i = 0;
        EditText editText = (EditText) findViewById(R.id.editText);
        EditText name = (EditText) findViewById(R.id.name);
        EditText number = (EditText) findViewById(R.id.phoneno);
        EditText postal = (EditText) findViewById(R.id.paddress);
        EditText email = (EditText) findViewById(R.id.emailaddr);
        String ename = name.getText().toString();               //Get the contact name string entered by user
        String enumber = number.getText().toString();           //Get the number string entered by user
        String epostal = postal.getText().toString();           //Get the postal address string entered by user
        String eemail = email.getText().toString();             //Get the email id entered by user
        Intent intent = new Intent(this, QRDisplay.class);
        intent.putExtra(EXTRA_MESSAGE, "Contact");
        contactbundle.putString(ContactsContract.Intents.Insert.NAME, ename);       //adding contact name to bundle
        contactbundle.putString(ContactsContract.Intents.Insert.PHONE, enumber);    //adding contact number to bundle
        contactbundle.putString(ContactsContract.Intents.Insert.POSTAL, epostal);   //adding contact address to bundle
        contactbundle.putString(ContactsContract.Intents.Insert.EMAIL, eemail);     //adding contact email to bundle
        startActivity(intent);
    }
}
