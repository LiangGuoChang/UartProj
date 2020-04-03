//
// Created by lgc on 2018/7/27.
//

#include <termios.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <string.h>
#include <jni.h>
#include <stdio.h>

#include "com_jimi_port_SerialPort.h"

//#include <utils/Log.h>
static const char *TAG = "gmuartserial_port";
#define LOGI(fmt, args...) __android_log_print(ANDROID_LOG_INFO,  TAG, fmt, ##args)
#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, TAG, fmt, ##args)
#define LOGE(fmt, args...) __android_log_print(ANDROID_LOG_ERROR, TAG, fmt, ##args)


static speed_t getBaudrate(jint baudrate) {
    switch (baudrate) {
        case 0:
            return B0;
        case 50:
            return B50;
        case 75:
            return B75;
        case 110:
            return B110;
        case 134:
            return B134;
        case 150:
            return B150;
        case 200:
            return B200;
        case 300:
            return B300;
        case 600:
            return B600;
        case 1200:
            return B1200;
        case 1800:
            return B1800;
        case 2400:
            return B2400;
        case 4800:
            return B4800;
        case 9600:
            return B9600;
        case 19200:
            return B19200;
        case 38400:
            return B38400;
        case 57600:
            return B57600;
        case 115200:
            return B115200;
        case 230400:
            return B230400;
        case 460800:
            return B460800;
        case 500000:
            return B500000;
        case 576000:
            return B576000;
        case 921600:
            return B921600;
        case 1000000:
            return B1000000;
        case 1152000:
            return B1152000;
        case 1500000:
            return B1500000;
        case 2000000:
            return B2000000;
        case 2500000:
            return B2500000;
        case 3000000:
            return B3000000;
        case 3500000:
            return B3500000;
        case 4000000:
            return B4000000;
        default:
            return -1;
    }
}

JNIEXPORT jstring JNICALL Java_com_jimi_port_SerialPort_stringFromJNI(JNIEnv *env, jobject thiz) {
    return (*env)->NewStringUTF(env, "Hello from JNI !");
}

JNIEXPORT jint Java_com_jimi_port_SerialPort_open
        (JNIEnv *env, jclass thiz, jint baudrate, jint nBits, jchar nEvent, jint nStop,
         jint flags) {
    int fd;
    speed_t speed;
    jobject mFileDescriptor;

    /* Opening device */
    {
        //"/dev/ttyMT0"  //mid01 printer
        //"/dev/ttyMT3" //cms301
        jboolean iscopy;
        // /dev/ttyS0 /dev/ttyMT3
        const char *path_utf = "/dev/ttyMT3";
//		LOGD("Opening serial port %s with flags 0x%x", path_utf, O_RDWR | flags);
        fd = open(path_utf, O_RDWR | O_NOCTTY | O_SYNC | flags);
//		LOGD("open() fdn = %d", fd);
        if (fd == -1) {
            /* Throw an exception */
//			LOGE("Cannot open port");
            /* TODO: throw an exception */
            return NULL;
        }
    }

    /* Configure device */
    {
        struct termios cfg, newtio;
//		LOGD("Configuring serial port");
        if (tcgetattr(fd, &cfg)) {
//			LOGE("tcgetattr() failed");
            close(fd);
            /* TODO: throw an exception */
            return NULL;
        }

        fcntl(fd, F_SETFL, 0);
//        tcgetattr(fd,&oldtio);
        bzero(&newtio, sizeof(newtio));

        newtio.c_cflag |= CLOCAL | CREAD;
        newtio.c_cflag &= ~CSIZE;

        switch (nBits) {
            case 7:
                newtio.c_cflag |= CS7;
                break;
            case 8:
                newtio.c_cflag |= CS8;
                break;
        }

        switch (nEvent) {
            case 'O':
                newtio.c_cflag |= PARENB;
                newtio.c_cflag |= PARODD;
                newtio.c_iflag |= (INPCK);    //| ISTRIP);
                break;
            case 'E':
                newtio.c_iflag |= (INPCK);    // | ISTRIP);
                newtio.c_cflag |= PARENB;
                newtio.c_cflag &= ~PARODD;
                break;
            case 'N':
            default:
                newtio.c_cflag &= ~(PARENB | PARODD);
                newtio.c_iflag &= ~INPCK;
                break;
        }

        switch (baudrate) {
            case 2400:
                cfsetispeed(&newtio, B2400);
                cfsetospeed(&newtio, B2400);
                break;
            case 4800:
                cfsetispeed(&newtio, B4800);
                cfsetospeed(&newtio, B4800);
                break;
            case 9600:
                cfsetispeed(&newtio, B9600);
                cfsetospeed(&newtio, B9600);
                break;
            case 19200:
                cfsetispeed(&newtio, B19200);
                cfsetospeed(&newtio, B19200);
                break;
            case 57600:
                cfsetispeed(&newtio, B57600);
                cfsetospeed(&newtio, B57600);
                break;
            case 115200:
                cfsetispeed(&newtio, B115200);
                cfsetospeed(&newtio, B115200);
                break;
            case 460800:
                cfsetispeed(&newtio, B460800);
                cfsetospeed(&newtio, B460800);
                break;
            default:
                cfsetispeed(&newtio, B9600);
                cfsetospeed(&newtio, B9600);
                break;
        }

        if (nStop == 1) {
            newtio.c_cflag &= ~CSTOPB;
        } else if (nStop == 2) {
            newtio.c_cflag |= CSTOPB;
        }
        newtio.c_cc[VTIME] = 0;
        newtio.c_cc[VMIN] = 1;

        tcflush(fd, TCIFLUSH);

        newtio.c_lflag &= ~(ICANON | ECHO | ECHOE | ISIG);
        newtio.c_iflag &= ~(INLCR | IGNCR | ICRNL | IXON | IXOFF);
        newtio.c_oflag &= ~(INLCR | IGNCR | ICRNL);
        newtio.c_oflag &= ~(ONLCR | OCRNL);

        if (tcsetattr(fd, TCSANOW, &newtio)) {
//			LOGE("tcsetattr() failed");
            //todo
			close(fd);
            /* TODO: throw an exception */
//			return NULL;
        }
    }
//		LOGD("Configuring serial port--end");

    return fd;
}

JNIEXPORT jint JNICALL Java_com_jimi_port_SerialPort_close(JNIEnv *env, jobject thiz, jint fd) {
    return close(fd);
}

static int file_read(int fd, unsigned char *buf, int size) {
    return read(fd, buf, size);
}

static int file_write(int fd, const unsigned char *buf, int size) {
    return write(fd, buf, size);
}

/*JNIEXPORT jint JNICALL Java_com_jimi_port_SerialPort_read(JNIEnv *env, jclass thiz, jint fd, jbyteArray buf, jint sizes){
	unsigned char *buf_char = (char*)((*env)->GetByteArrayElements(env,buf, NULL));
	int res= file_read(fd, buf_char,  sizes);
	(*env)->ReleaseByteArrayElements(env, buf, buf_char, 0);
	return res;
}*/

JNIEXPORT jint JNICALL
Java_com_jimi_port_SerialPort_read(JNIEnv *env, jclass thiz, jint fd, jbyteArray buf, jint sizes) {
    unsigned char *buf_char = (char *) ((*env)->GetByteArrayElements(env, buf, NULL));
    int res = file_read(fd, buf_char, sizes);
    (*env)->ReleaseByteArrayElements(env, buf, buf_char, 0);
    return res;
}

JNIEXPORT jint JNICALL
Java_com_jimi_port_SerialPort_write(JNIEnv *env, jclass thiz, jint fd, jbyteArray buf, jint sizes) {
    unsigned char *buf_char = (char *) ((*env)->GetByteArrayElements(env, buf, NULL));
    int res = file_write(fd, buf_char, sizes);
    (*env)->ReleaseByteArrayElements(env, buf, buf_char, 0);
    return res;
}