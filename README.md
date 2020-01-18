[bgfx android activity](https://github.com/nodrev/bgfx-android-activity) - Android glue for bgfx
================================================================================================

A minimal Android Activity using `NativeActivity` class which allows to run [bgfx](https://github.com/bkaradzic/bgfx)'s examples onto Android platforms.
![Android emulator with helloworld example](https://github.com/nodrev/bgfx-android-activity/raw/master/app/src/main/screenshot.png)

# Prerequisites

**Remark**: Although those build instructions assume a linux platform to generate the APKs, the used tools are available for OSX and MSWindows. As a result, the specified commands should be easily adapted to work for those platforms.

## Determine minimum target

By default, `bgfx` targets android API 24 (Android Nougat 7.0). So, if you have a phone or a tablet which runs an older Android version, you'll need to target the corresponding API number.

Determine the minimal android version you wish to support, and get the corresponding API level using this page: https://source.android.com/source/build-numbers

*Example: my phone runs Android Marshmallow 6.0.1, the platform number is 23*

## Android Studio 3.2

This project uses [Android Studio](http://developer.android.com/sdk/index.html) build system, Gradle, to generate Android's APK files, so you need to download it and install it properly. Android Studio comes with Android SDK, so no need to install it separatly.

*Following commands assume it was installed to `~/android/android-studio` directory.*

## Android NDK

[Android NDK](http://developer.android.com/ndk/downloads/index.html) is required to compile bgfx for android platforms. You can install it using Android Studio's SDK Manager. 

*Following commands assume it was installed to `~/android/sdk/ndk-bundle` directory.*

## Environment variables

```shell
sudo pluma /etc/profile.d/ndk.sh
```
Add the following lines:
```shell
export ANDROID_NDK_ROOT=~/android/sdk/ndk-bundle
export ANDROID_NDK_CLANG=$ANDROID_NDK_ROOT/toolchains/llvm/prebuilt/linux-x86_64
export ANDROID_NDK_ARM=$ANDROID_NDK_ROOT/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-x86_64
export ANDROID_NDK_ARM64=$ANDROID_NDK_ROOT/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64
export ANDROID_NDK_X86=$ANDROID_NDK_ROOT/toolchains/x86-4.9/prebuilt/linux-x86_64
```
*Note: Historically the NDK supported 32-bit and 64-bit MIPS (and so does `bgfx`), but support was removed in NDK r17.*

You can also extend the `PATH` variable to be able to access Android platform tools (`adb`, `dmtracedump`, etc) from the shell:
```shell
export ANDROID_SDK_ROOT=~/android/sdk
export PATH=$PATH:$ANDROID_SDK_ROOT/platform-tools
```

You may need to reboot in order to reload those environment variables.

# Setup project

## Clone repositories

```shell
mkdir bgfx-android
cd bgfx-android
git clone https://github.com/bkaradzic/bx.git
git clone https://github.com/bkaradzic/bimg.git
git clone https://github.com/bkaradzic/bgfx.git
git clone https://github.com/nodrev/bgfx-android-activity.git
```

## Compile

First, modify the file`bgfx/makefile` and modify the `projgen` to provide the minimal supported android platform (add `--with-android=xx`):
```makefile
	$(GENIE) --with-android=23 --with-combined-examples --gcc=android-arm gmake
	$(GENIE) --with-android=23 --with-combined-examples --gcc=android-x86 gmake
```

Then, compile BGFX samples for every android abi we want to support:
```shell
cd bgfx
make projgen
make android-arm & make android-arm64 & make android-x86
```

# Build APK

## Modify application ID

Import the project in Android Studio, and edit `bgfx-android-activity/app/build.gradle`. Replace `applicationId` with your own application id. Set `compileSdkVersion` and `targetSdkVersion` to your android platform number:
```
    compileSdkVersion 26
    defaultConfig {
        applicationId 'com.nodrev.bgfx.examples'
        minSdkVersion 23 // The version we use to compile bgfx
        targetSdkVersion 26
        versionCode 100 // Application version, 3 digits, major/minor/revision
        versionName "1.0.0"
    }
```

Edit `bgfx-android-activity/app/src/main/AndroidManifest.xml`, and set `package` value to the same application id:
```
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.nodrev.bgfx.examples">
    [...]
</manifest>
```

## Modify application name

Edit `bgfx-android-activity/app/src/main/res/values/strings.xml`, and replace `app_name` value with your application name
```
<resources>
    <string name="app_name">BGFX Examples</string>
</resources>
```

## Define the library name

To define the .so file to load by the native activity, you have to edit `bgfx-android-activity/app/src/main/AndroidManifest.xml`
```
<activity android:name="android.app.NativeActivity"
    <!-- Tell NativeActivity the name of our .so (strip 'lib' and '.so') -->
    <meta-data android:name="android.app.lib_name"
               android:value="examplesRelease" />
</activity>
```

Modify the .so to load in `BgfxAndroidActivity.java` too:
```java
public class BgfxAndroidActivity extends android.app.NativeActivity
{
    static
    {
        System.loadLibrary("c++_shared");
        System.loadLibrary("examplesRelease");
    }
}
```

## Resource files

Some examples requires resource files, you will need to copy them to the Android device (physical or emulator) SDCard using `adb`:
```shell
~/android/sdk/platform-tools/adb push bgfx/examples/runtime /sdcard/bgfx/examples/runtime
```

**Remark:** This is not the official way to do for a real application, runtime files should be embedded into APK, but for bgfx examples, we go that way.

## Packaging

Launch android studio, and import the project. Select `Build` menu, and generate APK using `Make Project` entry.

*Note: If you change the build variant to release, you'll need to sign your APK before deployment, this is off this tutorial's scope*

To deploy to your target device, go to the `Run` menu and either choose `Run 'app'` or `Debug 'app'` entry.

*Note: Generated APKs goes to `bgfx-android-activity/app/build/outputs/apk` directory*

[License (BSD 2-clause)](https://github.com/nodrev/bgfx-android-activity/blob/master/LICENSE)
-----------------------------------------------------------------------

<a href="http://opensource.org/licenses/BSD-2-Clause" target="_blank">
<img align="right" src="http://opensource.org/trademarks/opensource/OSI-Approved-License-100x137.png">
</a>

	Copyright 2016-2018 Jean-François Verdon. All rights reserved.
	
	https://github.com/nodrev/bgfx-android-activity
	
	Redistribution and use in source and binary forms, with or without
	modification, are permitted provided that the following conditions are met:
	
	   1. Redistributions of source code must retain the above copyright notice,
	      this list of conditions and the following disclaimer.
	
	   2. Redistributions in binary form must reproduce the above copyright
	      notice, this list of conditions and the following disclaimer in the
	      documentation and/or other materials provided with the distribution.
	
	THIS SOFTWARE IS PROVIDED BY COPYRIGHT HOLDER ``AS IS'' AND ANY EXPRESS OR
	IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
	MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
	EVENT SHALL COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
	INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
	THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
