package com.nodrev.bgfx.examples;

public class BgfxAndroidActivity extends android.app.NativeActivity
{
    static
    {
        System.loadLibrary("c++_shared");
        System.loadLibrary("examplesRelease");
    }
}
