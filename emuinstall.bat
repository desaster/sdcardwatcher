@echo off

set SDKPATH="C:\stuff\android\android-sdk-windows"
set ADB="%SDKPATH%\platform-tools\adb.exe"

set PACKAGE="com.desaster.sdcardwatcher"
set ACTIVITY="SDCardWatcherActivity"

%ADB% install "%1"
%ADB% shell "am start -n %PACKAGE%/.%ACTIVITY%"

pause
