/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_jimi_port_SerialPort */

#ifndef _Included_com_jimi_port_SerialPort
#define _Included_com_jimi_port_SerialPort
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_serialport_apis_SerialPort
 * Method:    stringFromJNI
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_jimi_port_SerialPort_stringFromJNI
  (JNIEnv *, jobject);

/*
 * Class:     com_serialport_apis_SerialPort
 * Method:    open
 * Signature: (IICII)I
 */
JNIEXPORT jint JNICALL Java_com_jimi_port_SerialPort_open
  (JNIEnv *, jclass, jint, jint, jchar, jint, jint);

/*
 * Class:     com_serialport_apis_SerialPort
 * Method:    write
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_com_jimi_port_SerialPort_write
  (JNIEnv *, jclass, jint, jbyteArray, jint);

/*
 * Class:     com_serialport_apis_SerialPort
 * Method:    read
 * Signature: (I[BI)I
 */
JNIEXPORT jint JNICALL Java_com_jimi_port_SerialPort_read
  (JNIEnv *, jclass, jint, jbyteArray, jint);

/*
 * Class:     com_serialport_apis_SerialPort
 * Method:    close
 * Signature: (I)V
 */
JNIEXPORT jint JNICALL Java_com_jimi_port_SerialPort_close
  (JNIEnv *, jobject, jint);

#ifdef __cplusplus
}
#endif
#endif