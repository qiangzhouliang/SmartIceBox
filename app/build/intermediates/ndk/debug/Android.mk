LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := app
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_SRC_FILES := \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\arm64-v8a\libbmob.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\arm64-v8a\libBmobStat.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\arm64-v8a\libmsc.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\armeabi\libbmob.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\armeabi\libBmobStat.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\armeabi\libmsc.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\armeabi-v7a\libbmob.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\armeabi-v7a\libBmobStat.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\armeabi-v7a\libmsc.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\mips\libbmob.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\mips\libBmobStat.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\mips\libmsc.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\mips64\libbmob.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\mips64\libBmobStat.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\mips64\libmsc.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\util.c \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\x86\libbmob.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\x86\libBmobStat.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\x86\libmsc.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\x86_64\libbmob.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\x86_64\libBmobStat.so \
	E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs\x86_64\libmsc.so \

LOCAL_C_INCLUDES += E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jni
LOCAL_C_INCLUDES += E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\main\jniLibs
LOCAL_C_INCLUDES += E:\AndroidStudioProjects\QRCodeDemo-master2\app\src\debug\jni

include $(BUILD_SHARED_LIBRARY)
