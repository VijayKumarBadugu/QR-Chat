package com.jwetherell.QRComm575;

/**
 * Created by vijay on 4/22/15.
 */
/*
This activity is called when the sender wants to send a picture.This Activity opens the phones gallery and sends then user
can choose the phone and the photo.
 */
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SendPictureActivity extends Activity {
    private static int RESULT_LOAD_IMAGE = 1;
    public Bitmap BMP;
    public final static String EXTRA_MESSAGE = "eduteamvisuallinks.comncsu.google.httpssites.newactivityqr.MESSAGE";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendpicture);
        Button buttonLoadImage = (Button) findViewById(R.id.buttonLoadPicture);
        buttonLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);//picture path contains the path to the image
            cursor.close();


            BMP=BitmapFactory.decodeFile(picturePath);              //The bitmap of the image is got here
            ByteArrayOutputStream baos=new  ByteArrayOutputStream();
            ImageView imageView = (ImageView) findViewById(R.id.imgView);

            imageView.setImageBitmap(BMP);
            System.out.println("Came Here");
            BMP.compress(Bitmap.CompressFormat.JPEG,100, baos);
            System.out.println("GOt out");
            byte [] b=baos.toByteArray();                           //The bitmap is converted to byte array
            String temp= Base64.encodeToString(b, Base64.DEFAULT);  //The byte array is converted to string
            Intent intent = new Intent(SendPictureActivity.this, CaptureActivity.class);
            intent.putExtra(EXTRA_MESSAGE, "Image"+temp);           //Message is passed to CaptureActivity by Intent."Image is appended to the string such that receiver can know the data received is of Image
            EncodeMainActivity.Myendcoder=1;                        //Setting Myencoder makes the device to act in sender mode
            System.out.println("Message length"+"Image"+temp);
            startActivity(intent);
        }
    }
}
