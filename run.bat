cd C:\Users\mjulia\.android

del D:\data\soot\apks\sootOutput\%1_%2.apk

del D:\data\soot\apks\sootOutput\%1_%2_new.apk

move D:\data\soot\apks\sootOutput\%1.apk D:\data\soot\apks\sootOutput\%1_%2.apk


D:\eclipse-android\sdk\build-tools\19.1.0\zipalign.exe -v 4 D:\data\soot\apks\sootOutput\%1_%2.apk D:\data\soot\apks\sootOutput\%1_%2_new.apk

"C:\Program Files\Java\jdk1.7.0_25\bin\jarsigner.exe" -verbose -sigalg SHA1withRSA -digestalg SHA1 -keystore D:\data\soot\apks\key\julia-release-key.keystore D:\data\soot\apks\sootOutput\%1_%2_new.apk julia_key -storepass mjulia

"C:\Program Files\Java\jdk1.7.0_25\bin\jarsigner.exe" -verify -verbose D:\data\soot\apks\sootOutput\%1_%2_new.apk

D:\eclipse-android\sdk\platform-tools\adb -s emulator-5556 uninstall %1

D:\eclipse-android\sdk\platform-tools\adb -s emulator-5556 logcat -c

D:\eclipse-android\sdk\platform-tools\adb -s emulator-5556 install -r D:\data\soot\apks\sootOutput\%1_%2_new.apk

call D:\data\soot\apks\sootOutput\decompile.bat.lnk sootOutput\%1_%2

D:\eclipse-android\sdk\platform-tools\adb -s emulator-5556 logcat > D:\data\soot\logs\%1_%2.logcat.log