package com.nodrev.bgfx.examples;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

public class BgfxAndroidActivity extends android.app.NativeActivity
{
    // Load bundle shared libraries
    static
    {
        System.loadLibrary("c++_shared");
        System.loadLibrary("examplesRelease");
    }

    // Olds permission request result
    private static final int readExternalStoragePermission = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        // Call NativeActivity onCreate
        super.onCreate(savedInstanceState);

        // Check permission
        // Since Android 23, permissions must explicitly be confirmed by user.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            // We absolutly need this permission to run some of the examples, so ask it again even if it was refused before.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, readExternalStoragePermission);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case readExternalStoragePermission:
            {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                {
                    // Permission refused, show a warning!
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("Some examples needs to load data from the sdcard, those will crash if data can't be loaded!");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        }
    }
}
