/*
 * Copyright (C) 2008 ZXing authors
 * 
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


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Collection;

import com.jwetherell.QRComm575.camera.CameraManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.lang.String;
import java.util.DuplicateFormatFlagsException;

/**
 * This class handles all the messaging which comprises the state machine for
 * capture.
 * 
 *
 */
/*
    DecoderActivityHandler() constructor copies the values of the activity used, cameraManager and gets the decoding format
    from the passing activity. It initializes the camera and also starts a decodeThread.handleMessage() is called when a
    message is sent to the DecodeActivityHandler. The switch statement handles the type of message received.auto_focus: Requests the cameraManager for AutoFocus.
    restart_preview: Starts the camera display and starts the preview.decode_succeeded: Gets the data from message and decodes
    the string that was encoded. If in receive mode, checks if Initializer is not set, waits for the Start QRCode to mark the
    beginning of communication and initializer is set when Start is received. Then it displays the Start QRCode and again
    requests for the next frame. When a new frame is received, if count is not received, it throws an exception screen, after the count is received it decodes the data from QRCodes and stores them in finalResult string. If in the send mode, it calls displaymethod() to get the next qrcode to be displayed.
    decode_failed: If decode fails, it requests another frame to decode. return_scan_result: Returns the result, not handled in our case.

     */
public final class DecoderActivityHandler extends Handler{

    public static final String TAG = DecoderActivityHandler.class.getSimpleName();

    public final IDecoderActivity activity;
    public final DecodeThread decodeThread;
    public final CameraManager cameraManager;
    public State state;
    public String Start="Start";            //Variable holds "Start"
    public int Count;                       //Variable holds number of characters the receiver is expecting
    public String STATE;                    //This variable holds the state.
    public int Initializer;                 //This variable is used to Initialize the receiver
    public String TempChar;                 //This variable holds the message deocded from the QR
    public String PermChar="";              //This variable holds the total message
    public String DummyChar;
    public CharSequence Temper;
    public String DupResol;                 //This variable is used to resolve duplicates.This is second character of message received by receiver
    public int DupResolver;                 //This variable is used to resolve duplicates.
    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";
    private enum State {
        PREVIEW, SUCCESS, DONE
    }


    DecoderActivityHandler(IDecoderActivity activity, Collection<BarcodeFormat> decodeFormats, String characterSet,
            CameraManager cameraManager) {
        this.activity = activity;
        decodeThread = new DecodeThread(activity, decodeFormats, characterSet, new ViewfinderResultPointCallback(
                activity.getViewfinder()));
        decodeThread.start();
        state = State.SUCCESS;
        Initializer=0;
        // Start ourselves capturing previews and decoding.
        this.cameraManager = cameraManager;
        cameraManager.startPreview();
        restartPreviewAndDecode();
    }
    /*
    After scanning a QR sucessfully handle Message is called.If their is a message in the QR code then message.what
    contains R.id.decode_succeeded
     */

    @Override
    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.auto_focus:
                // Log.d(TAG, "Got auto-focus message");
                // When one auto focus pass finishes, start another. This is the
                // closest thing to
                // continuous AF. It does seem to hunt a bit, but I'm not sure
                // what else to do.
                if (state == State.PREVIEW) cameraManager.requestAutoFocus(this, R.id.auto_focus);
                break;
            case R.id.restart_preview:
                Log.d(TAG, "Got restart preview message");
                restartPreviewAndDecode();
                break;
            case R.id.decode_succeeded:
                Log.d(TAG, "Got decode succeeded message");
               // System.out.println("Got decode succeeded message");
                state = State.SUCCESS;
                Bundle bundle = message.getData();
                Bitmap barcode = bundle == null ? null : (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
                Temper = activity.handleDecode((Result) message.obj, barcode, bundle);
                /*
                After successful scan the message got from the scan is store in Temper.
                 */
                TempChar = Temper.toString();

                /*
                The sender and receiver both uses Decoder Activity handler.When this handler is called we should know
                whether it is a encoder or a decoder.First CaptureType1() function is called to to know the mode of operation
                and when CaptureType is 1 and received message contains the starting character "S".It means the message is from
                the sender.
                 */
                if((activity.CaptureType1()==1)&&("S".equals(TempChar.substring(0, 1)))) {
                    DupResol=TempChar.substring(1,2);           //The DupResol is extracted from the message
                    TempChar=TempChar.substring(2);             //Actual Message sent from sender

                    if ((Initializer == 0)) {                   //Initialize the decoder.Getting the first message from the sender
                        //Log.d("String received", TempChar);
                        //System.out.println("Here OR");


                        if (TempChar.equals(Start)) {           //The receiver expects the first message from sender to be "Start"
                            Log.d("Went In", TempChar);
                            STATE = TempChar;
                            Initializer = 1;                    //Setting Initializer means "Start" message is successfully received
                            DupResolver=0;                      //Initialize the duplicate resolver
                            state = State.PREVIEW;
                            activity.changeImage("R"+DupResol+TempChar);//Displaying the received message to the sender(Feedback)
                            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);//Requesting a new QR
                        } else
                        {
                            //System.out.println("Doing BAd");
                            state = State.PREVIEW;                  //Requesting new QR
                            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                        }


                    } else {
                            /*Receiver keeps on scanning until it sees new QR from the sender.The condition below checks whether
                        the received QR is same or not.If it is same then take  a new capture
                        */
                        if ((DupResol+TempChar).equals(Integer.toString(DupResolver)+STATE)) {

                            //System.out.println("Doing BAd 7");
                            state = State.PREVIEW;

                            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                        } else {


                            //Alternating the DupResolver to handle duplicates
                            if(DupResolver==0)

                            {
                                DupResolver=1;
                            }
                            else
                            {
                                DupResolver=0;
                            }
                            if (Initializer == 1) {
                               /*
                               The second message the receiver get total number of characters of the data.
                                */
                                STATE = TempChar;
                                System.out.println("SG"+TempChar);
                                //The function is numeric checks whether the data received is a numeric or not
                                if (isNumeric(TempChar) == true) {
                                    Count = Integer.parseInt(TempChar);
                                    System.out.println(Count);
                                    Initializer = 2;                //Setting Initializer means start taking actual data
                                    activity.changeImage("R"+DupResol+TempChar); //Giving feedback to the sender
                                    state = State.PREVIEW;
                                    cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);//Restart the scanner
                                } else {
                                    //((Activity) activity).finish();
                                    //If second message from receiver is not numeric display Error message in Result activity
                                    Intent intent = new Intent((Activity) activity, ResultActivity.class);

                                    intent.putExtra(EXTRA_MESSAGE, "Count of the sentences missing. Please start again.");
                                    System.out.println("SHUT DOWN");

                                    ((Activity) activity).startActivity(intent);
                                    quitSynchronously();
                                }
                            } else {
                               /*When Initializer is set to 2 .The receiver takes the actual message from the sender and
                               reconstructs the string

                                */
                                if (Initializer == 2) {

                                    STATE = TempChar;
                                    if(TempChar!=null) {
                                        PermChar = PermChar + TempChar;         //Reconstruction of string
                                    }
                                    Count = Count - TempChar.length();          //Count holds number of characters still left
                                    activity.changeImage("R"+DupResol+TempChar);//Feedback by receiver to sender
                                    if (Count > 0) {

                                        state = State.PREVIEW;                  //Restart the scanner
                                        cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                                    } else {
                                        //Resetting the variables
                                        DummyChar = PermChar;
                                        PermChar = "";
                                        Initializer = 0;
                                        System.out.println("Shutting Down");
                                        activity.getResult(DummyChar);          //Calling get result function in Capture Activity to display result
                                        quitSynchronously();


                                    }

                                }
                            }

                        }
                    }
                }
                else
                {
                    /*
                    When Device is in sender mode .The sender scans for the feedback from receiver.
                    The sender checks whether the first character is R.Conforming the message is from the sender
                     */
                    if((activity.CaptureType1()==2)&&(TempChar.substring(0,1).equals("R")))
                    {
                        TempChar = Temper.toString();
                        TempChar=TempChar.substring(1);             //Removing the first character
                        activity.DisplayMethod(TempChar);           //Calling DisplayMethod in Capture Activity to check whether the message received is equal to state
                        state = State.PREVIEW;
                        cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);//Restarting the scanner
                    }
                    else
                    {
                        quitSynchronously();

                    }

                }
                break;

            case R.id.decode_failed:
                // We're decoding as fast as possible, so when one decode fails,
                // start another.
               // System.out.println("Got decode decode_failed");
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                break;
            case R.id.return_scan_result:
                Log.d(TAG, "Got return scan result message");
                if (activity instanceof Activity) {
                    ((Activity) activity).setResult(Activity.RESULT_OK, (Intent) message.obj);
                    ((Activity) activity).finish();
                } else {
                    Log.e(TAG, "Scan result message, activity is not Activity. Doing nothing.");
                }
                break;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            // Wait at most half a second; should be enough time, and onPause()
            // will timeout quickly
            decodeThread.join(500L);
        } catch (InterruptedException e) {
            // continue
        }

        // Be absolutely sure we don't send any queued up messages
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            cameraManager.requestAutoFocus(this, R.id.auto_focus);
            activity.getViewfinder().drawViewfinder();
        }
    }
    public static boolean isNumeric(String str)
    {
        try
        {
            double d = Double.parseDouble(str);
        }
        catch(NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }
}
