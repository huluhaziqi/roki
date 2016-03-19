
cd src/main
javah -d jni -classpath ../../build/intermediates/classes/debug com.legent.utils.serialport.SerialPort

ndk-build