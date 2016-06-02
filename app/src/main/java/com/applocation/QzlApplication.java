package com.applocation;

import android.app.Application;
import android.content.Context;

import com.lidroid.xutils.DbUtils;

import cn.bmob.v3.Bmob;

/**
 * Created by Q on 2016-05-18.
 */
public class QzlApplication extends Application {
    public static DbUtils dbUtils;
    public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();
        dbUtils = DbUtils.create(getApplicationContext(),"ListFood");
        context = getApplicationContext();
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, "7111b1b0ca4263017a2765f3cdeacfd2");
    }
}
