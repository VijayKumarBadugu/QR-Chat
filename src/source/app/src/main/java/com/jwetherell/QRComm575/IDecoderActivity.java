package com.jwetherell.QRComm575;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;

import com.jwetherell.QRComm575.camera.CameraManager;
import com.google.zxing.Result;

public interface IDecoderActivity {

    public ViewfinderView getViewfinder();

    public Handler getHandler();

    public CameraManager getCameraManager();

    public CharSequence handleDecode(Result rawResult, Bitmap barcode, Bundle bundle);

    public void getResult(String string);

    public void changeImage(String string);

    public boolean DisplayMethod(String Hi);

    public void Initialize(String Hi);
    public int CaptureType1();


}
