package com.glassky.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.user._User;
import com.util.Load;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class RegActivity extends Activity implements Load.LoadCompare{

    private EditText editText_name,editText_password,editText_email,phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_password = (EditText) findViewById(R.id.editText2_password);
        editText_email = (EditText) findViewById(R.id.editText_email);
        phoneNumber = (EditText) findViewById(R.id.phone_num);
    }

    public void registerClick(View view){
        final String photo = "apple.png";
        final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + photo;
        final File file = new File(path);
        if(!file.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Load.load_Photo(RegActivity.this, photo);
                }
            }).start();
        }else {
            reg(path);
        }
    }

    @Override
    public void strPath(String path) {
        //注册方法
        reg(path);
        System.out.println("注册");
    }

    private void reg(String path) {
        final String name = editText_name.getText().toString();
        final String pass = editText_password.getText().toString();
        final String email = editText_email.getText().toString();
        final String phone = phoneNumber.getText().toString();
        //图片的上传
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    //上传成功
                    _User user = new _User();
                    user.setUsername(name);
                    user.setPassword(pass);
                    user.setEmail(email);
                    user.setIcon(bmobFile);
                    user.setMobilePhoneNumber(phone);
                    user.signUp(new SaveListener<_User>() {
                        @Override
                        public void done(_User user, BmobException e) {
                            if (e == null){
                                Toast.makeText(RegActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegActivity.this, LoginMainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(RegActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                System.out.println(e);
                            }
                        }
                    });
                }else {
                    System.out.println("文件上传失败");
                }
            }
        });
    }
}

