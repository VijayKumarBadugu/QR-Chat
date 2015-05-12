/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jwetherell.QRComm575;

import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.lang.String;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.Result;
import com.google.zxing.ResultMetadataType;
import com.jwetherell.QRComm575.result.ResultHandler;
import com.jwetherell.QRComm575.result.ResultHandlerFactory;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/*
The main activity for both send and receive data. Functionality changes based on the mode(send or receive).This class does
the core functionality of the application. It extends DecoderActivity class.
Based on the way it is called, it works differently for send and receive modes.

 */
public class CaptureActivity extends DecoderActivity {

    public final static String EXTRA_MESSAGE = "com.ncsu.ece575.VisualComm.MESSAGE";

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final Set<ResultMetadataType> DISPLAYABLE_METADATA_TYPES = EnumSet.of(ResultMetadataType.ISSUE_NUMBER, ResultMetadataType.SUGGESTED_PRICE,
            ResultMetadataType.ERROR_CORRECTION_LEVEL, ResultMetadataType.POSSIBLE_COUNTRY);

    private TextView statusView = null; //Used to describe the display to be showing status
    private View resultView = null; //Used to describe the display when in result showing mode
    private boolean inScanMode = false; //Shows the scan mode of the application. Set when capture starts

    public String [] words =new String[10000];;; //Stores array of the data split with the delimiters.
    public int NoterMax;        //Number of QR codes exchanged.
    public CharSequence Disp;   //Stores the string of displayed QR code
    public int CaptureType;     //Mode: 1 for Receive and 2 for send
    public int Noter;           //This variable store amount of data sent by the sender
    public String STATE;        //This variable holds the data sent and feedback the device is expecting
    public int DupRes;          //This variable is defined to make the device to send duplicate data
    public long TimeState;      //This variable holds the time at which a chuck of data is sent
    public int xize;            //This variable defines chunk size
    public int currpointer;     //This variable store amount of data sent by the sender
    public String messageActual;//This variable holds the data that should be transmitted
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.capture);
        Log.v(TAG, "onCreate()");

        resultView = findViewById(R.id.result_view);
        statusView = (TextView) findViewById(R.id.status_view);
        Intent intent = getIntent();
        String message = intent.getStringExtra(EncodeMainActivity.EXTRA_MESSAGE);

        /*
            SENDER:
            Global variable Myendcoder in EncodeMainActivity class is set when the device wants to act as sender.The sender will
            set variable and call CaptureActivity.On Myendcoder set to 1.The sender is initialized.The sender First transmits
            "Start" message to receiver.The message is appended with "S" so that receiver knows the message is from sender and also
            appends DupRes.The first message sent by sender is "S0Start".The STATE is "Start"
            The sender then goes on scan mode.It takes QR captures infinitely until it sees "R0Start" sent from receiver.It
            strips the first two character and checks whether the received message is equal to the STATE.
         */


        if(EncodeMainActivity.Myendcoder==1)

        {
            //System.out.println("Initialized"+message);
            CaptureType=2;                                      //This variable holds whether the device is sender or receive
            Initialize("Hello");                                //This functions gets message to be transmitted from intent

            Noter=-1;
            STATE="Start";                                      //Initial State
            DupRes=0;                                           //DupRes Initialized
            currpointer=0;                                      //currpointer Initialized
            TimeState=System.currentTimeMillis()/1000;          //Initializing the Timer
            changeImage("S"+Integer.toString(DupRes)+"Start");  //Displaying the QR for "S0Start"
            EncodeMainActivity.Myendcoder=0;                    //Reseting the variable

        }
        else
        {
            CaptureType=1;                                      //Initializing the device to act as a receiver
        }



        inScanMode = false;
    }

    /*
    This function returns the mode of operation of the device
     */
    public int CaptureType1()
    {
        return CaptureType;
    }

    /*
    This function is initialize the variables in sender mode and get the message to be transmitted from the intent.
     */
    public void Initialize(String Hi)
    {
        Intent intent = getIntent();
        messageActual = intent.getStringExtra(EncodeMainActivity.EXTRA_MESSAGE);
        String Temp;
        int j;
        //words=message.split("\\s+");
        //words = message.split(".");
        int N;

        xize=50;                         //Initializing the Chunk size
        N=messageActual.length();


        NoterMax=N;                     //NoterMax holds number of characters in the message

    }
    /*
    This function is called when a sends scans a QR code successfully.This functions checks whether the message is sent from
    the receiver and it also hand duplicate message.The function first checks whether the state is equal to the message received.
    If their is a match then next message is sent.Else the scanner is called again to get new capture
     */
    public boolean DisplayMethod(String Hi) {
        if (((STATE.equals(Hi.substring(1))&&(Integer.toString(DupRes).equals(Hi.substring(0,1))))))
        {
            if(DupRes==0)                   //Alternating DupRes to handle duplicate messages
            {
                DupRes=1;
            }
            else
            {
                DupRes=0;
            }

            if (Noter <=NoterMax)
            {
                System.out.println("Chunk Size is "+xize);
                /*The second message from the sender is number of characters being sent.After getting "Start" from receiver
                as Noter is -1 the code jumps into block below and sends the number of characters being sent
                */

                    if (Noter == -1)

                    {

                        String str = String.valueOf(NoterMax);
                        changeImage("S"+Integer.toString(DupRes)+str);
                        Noter = Noter + 1;
                        STATE=str;
                        return true;

                    }
                    else
                    {
                        /*
                        Actual Data transfer happens in this else part.When a feed is received the time stamp is noted.If the
                        time between data sent and feedback received is more than 1 sec.Then chunk size is reduced
                         */
                        if((System.currentTimeMillis()/1000)-TimeState>1)
                        {
                            xize=xize-1;
                            if(xize<2)
                            {
                                xize=1;         //Minimum Chunk size
                            }
                            if(xize>=174)
                            {
                                xize=174;       //Maximum Chunk size
                            }
                        }
                        else
                        {
                            xize=xize+1;
                            if(xize<2)
                            {
                                xize=1;
                            }
                            if(xize>=174)
                            {
                                xize=174;
                            }

                        }
                        String TempStr;
                        /*
                        The real message is divided into substring and is sent based on chunk size and currpointer
                         */
                        if(Noter+xize>NoterMax)
                        {
                            TempStr=messageActual.substring(currpointer,NoterMax);
                        }
                        else
                        {
                            TempStr=messageActual.substring(currpointer,currpointer+xize);
                        }

                        currpointer=currpointer+xize;
                        TimeState=System.currentTimeMillis()/1000;              //Time is noted when data is sent
                        changeImage("S"+Integer.toString(DupRes)+TempStr);      //changeImage function is called to display QR
                        STATE=TempStr;

                        Noter = Noter + xize;

                        return true;
                    }


            }
            else
            {
                //Return to MainActivity after sending data successfully
                Intent intent = new Intent(CaptureActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }

        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()");
    }

/*
This function is called when back button is pressed.When back button is pressed the main activity is called by Intent.
 */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inScanMode) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
            else
                onResume();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
/*
The handleDecode function returns the decode message and also displays contact if the message type is contact
 */
    @Override
    public CharSequence handleDecode(Result rawResult, Bitmap barcode, Bundle bundle) {
        ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(this, rawResult);
        String type = resultHandler.getType().toString();
        Disp = resultHandler.getDisplayContents();
        if(type.equals("ADDRESSBOOK")) {
            System.out.println("Calling the Contact Display aactivity");
            Intent intent = new Intent(this, ResultActivity2.class);

            intent.putExtra(EXTRA_MESSAGE, Disp);
            startActivity(intent);
        }
        return Disp;
    }



    protected void showScanner() {
        inScanMode = true;
        resultView.setVisibility(View.GONE);
        statusView.setText(R.string.msg_default_status);
        statusView.setVisibility(View.VISIBLE);
        viewfinderView.setVisibility(View.VISIBLE);
    }

    // Put up our own UI for how to handle the decodBarcodeFormated contents.


//This function is used to start result activity and display the received message by the receiver
    public void getResult(String result) {
        cameraManager.stopPreview();

        Intent intent = new Intent(this, ResultActivity.class);

        intent.putExtra(EXTRA_MESSAGE, result);

        startActivity(intent);

    }
//This function displays the QR code of the string message.
    public void changeImage(String message)
    {
        System.out.println("Debuuger "+CaptureType+message);
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


        //System.out.println("Going to Profile Data");
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(message,
                null,
                Contents.Type.TEXT,
                BarcodeFormat.QR_CODE.toString(),
                smallerDimension);                                              //Encode the message as QR
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();                     //Generating bitmap for QR
            ImageView myImage = (ImageView) findViewById(R.id.image_viewer);
            myImage.setImageBitmap(bitmap);                                     //Displaying the QR image

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
}
