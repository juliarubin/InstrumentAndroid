call D:\data\leakd\android_tools\dex2jar-0.0.9.15\dex2jar.bat D:\data\soot\apks\%1.apk

call D:\data\leakd\android_tools\jd-gui\jd-gui.exe D:\data\soot\apks\%1_dex2jar.jar

del D:\data\soot\apks\%1_dex2jar.jar

call D:\data\leakd\android_tools\apktool-install-windows-r05-ibot\apktool.bat d D:\data\soot\apks\%1.apk D:\data\soot\apks\decompiled\%1_xmls