LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := app
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\util.c \
	D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\arm64-v8a\libmsc.so \
	D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\armeabi\libmsc.so \
	D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\armeabi-v7a\libmsc.so \
	D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\mips\libmsc.so \
	D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\mips64\libmsc.so \
	D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\x86\libmsc.so \
	D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\x86_64\libmsc.so \

LOCAL_C_INCLUDES += D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jni
LOCAL_C_INCLUDES += D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs
LOCAL_C_INCLUDES += D:\AndroidStudioProjects\QRCodeDemo-master2\app\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
