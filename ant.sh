#!/bin/sh
set -e

PACKAGE="com.desaster.sdcardwatcher"
ACTIVITY="SDCardWatcherActivity"

ADB="$HOME/android/sdk/android-sdk-linux_x86/platform-tools/adb"

export JAVA_HOME="/usr/java/jdk1.7.0_02/"

if [ "$1" == "run" ]
then
    $ADB shell "am start -n $PACKAGE/.$ACTIVITY"
    exit
fi

ant $*

if [ "$2" == "install" ]
then
    $ADB shell "am start -n $PACKAGE/.$ACTIVITY"
fi
