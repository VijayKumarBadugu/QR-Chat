package com.jwetherell.QRComm575;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
/*
This function is called to display the contact as QR.
 */

public class QRDisplay extends Activity {


    public String msg = "Android : ";

    @Override
    protected void onResume() {
        super.onResume();


        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);         //Getting message from calling Activity via Intent

        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        int width = display.getWidth();
        int height = display.getHeight();
       /* display.getSize(point);
        int width = point.x;
        int height = point.y;*/
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3/4;
        if(message.equals("Contact")) {
            System.out.println("Generating QR for Contact");
            QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(message,
                    ContactInputActivity.contactbundle,
                    Contents.Type.CONTACT,
                    BarcodeFormat.QR_CODE.toString(),
                    smallerDimension);                                              //Encode the message as QR
            try {
                Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();                     //Generating bitmap for QR
                ImageView myImage = (ImageView) findViewById(R.id.imageView);
                myImage.setImageBitmap(bitmap);                                     //Displaying the QR image
                Log.d(msg, "Came here to resume QR Display");
            } catch (WriterException e) {
                System.out.println("Contact not functioning");
                e.printStackTrace();
            }
        }
        //finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrdisplay);
        Intent intent = getIntent();
        String message = intent.getStringExtra(EncodeMainActivity.EXTRA_MESSAGE);
        Log.d(msg, "Came here to oncreate QR ");
        int i = 0;
        //EditText qrInput = (EditText) findViewById(R.id.editText);
        // String qrInputText = qrInput.getText().toString();
        // Log.v(LOG_TAG, qrInputText);

        //Find screen size
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        /*display.getSize(point);
        int width = point.x;
        int height = point.y;*/
        int width = display.getWidth();
        int height = display.getHeight();
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3/4;








        System.out.println("Going to Profile Data");
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(message,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension/10);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ImageView myImage = (ImageView) findViewById(R.id.imageView);
            myImage.setImageBitmap(bitmap);
            Log.d(msg, "Came here to Oncreate QR ");

        } catch (WriterException e) {
            e.printStackTrace();
        }



    }












    public void GenerateDelay(long delay) {

        delay = delay + System.currentTimeMillis();

        do
        {
            //nothing;
        } while(System.currentTimeMillis() < delay);


    }
}