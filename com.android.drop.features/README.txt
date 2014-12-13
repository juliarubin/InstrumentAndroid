run InstrumentAndroid from com.android.drop.features with parameters:
-android-jars "D:\data\soot\android-platforms" -process-dir "D:\data\soot\apks\com.devuni.flashlight.apk"

possible to add: -allow-phantom-refs
-Xmx1024M

D:\eclipse-android\sdk\platform-tools\adb kill-server
D:\eclipse-android\sdk\platform-tools\adb start-server
D:\eclipse-android\sdk\platform-tools\adb -s emulator-5554 emu kill
D:\eclipse-android\sdk\platform-tools\adb devices

if more than one device: adb -s emulator-5554

cd C:\Users\mjulia\.android
D:\data\soot\apks\sootOutput\run.bat com.sydneyapps.remotecontrol instrumented
