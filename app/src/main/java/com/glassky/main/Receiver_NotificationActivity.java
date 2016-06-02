package com.glassky.main;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.widget.TextView;

public class Receiver_NotificationActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receiver__notification);

        //接收你发送的消息
        String msg = getIntent().getStringExtra("msg");
        TextView tv = (TextView) findViewById(R.id.msg);
        tv.setText(msg);

        //打开界面后取消指定ID的通知
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //nm.cancel(MainActivity.NID_1);
    }
}
