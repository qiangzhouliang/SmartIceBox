package com.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.glassky.main.R;
import com.glassky.main.Receiver_NotificationActivity;

/**
 * Created by Q on 2016-05-30.
 */
public class SendNotification {
    //发送一个普通的通知
    public static void sendNotification1(Context context,String str,NotificationManager notificationManager,int i){

        //v4支持包的用法
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        //设置相关的属性
        builder.setSmallIcon(R.drawable.shopper_icon);//设置小图标
        builder.setContentTitle("你有一条新消息");
        builder.setContentText(str);//发送的信息
        //builder.setOngoing(true);//设置为常驻通知
        builder.setAutoCancel(true);//自动清除
        builder.setDefaults(Notification.DEFAULT_ALL);//设置响铃跟震动
        builder.setNumber(10);//发送了多少条
        builder.setTicker("新消息");

        //定义一个意图，当点击通知时，要打开一个界面（Activity）
        Intent intent = new Intent(context, Receiver_NotificationActivity.class);
        intent.putExtra("msg",str);//发送的消息

        //参数(上下文，请求编码（没用），意图，创建PendingIntent的方式)
//        PendingIntent.FLAG_CANCEL_CURRENT :取消当前PI，创建新的
//        PendingIntent.FLAG_ONE_SHOT ：只是用一次
//        PendingIntent.FLAG_NO_CREATE ：如果有就使用，没有不创建
//        PendingIntent.FLAG_UPDATE_CURRENT ：如果有，更新Intent，没有就创建
        PendingIntent pi = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);//通知的事件

        //创建一个通知对象
        Notification n = builder.build();

        //发送通知(获取系统的通知管理器)
        //NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationManager nm = notificationManager;
        nm.notify(i,n);//发送通知
    }
}
