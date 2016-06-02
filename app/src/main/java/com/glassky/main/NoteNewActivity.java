package com.glassky.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.Constant;
import com.user.Food;
import com.user.Note;
import com.util.Load;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

public class NoteNewActivity extends Activity implements View.OnClickListener,Load.LoadCompare {

    private String photo = "1.png";

    private EditText editText_name, editText_shelf_life, editText_num, editText_remark;

    private Button button_selectPhoto, button_save, button_cancel;

    private ImageView imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_new);
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_shelf_life = (EditText) findViewById(R.id.editText_shelf_life);
        editText_num = (EditText) findViewById(R.id.editText_num);
        editText_remark = (EditText) findViewById(R.id.editText_remark);

        button_selectPhoto = (Button) findViewById(R.id.button_selectphoto);
        button_save = (Button) findViewById(R.id.button_save);
        button_cancel = (Button) findViewById(R.id.button_cancel);
        imageView1 = (ImageView) findViewById(R.id.imageView1);

        button_selectPhoto.setOnClickListener(this);
        button_save.setOnClickListener(this);
        button_cancel.setOnClickListener(this);

    }

    private void save_food() {
        //得到图片文件路径
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + photo;
        //图片的上传
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                Note note = new Note();
                String name = editText_name.getText().toString();
                String shelf_life = editText_shelf_life.getText().toString();
                String numm = editText_num.getText().toString();
                int num = 0;
                if (!numm.isEmpty()) {
                    num = Integer.parseInt(numm);
                }
                String remark = editText_remark.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    note.setFoodname(name);
                    note.setShelf_life(shelf_life);
                    note.setNum(num);
                    note.setRemark(remark);
                    note.setIcon(bmobFile);
                    note.save(NoteNewActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            //保存成功
                            Toast.makeText(NoteNewActivity.this, "添加数据成功", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(NoteNewActivity.this, "添加数据失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(int i, String s) {
                System.out.println("--------" + s);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_selectphoto:
                selectPhoto();
                break;
            case R.id.button_save:
                //保存食物
                save_food();
                break;
            case R.id.button_cancel:
                finish();
                break;
        }
    }


    private void selectPhoto() {

        AlertDialog.Builder builder = new AlertDialog.Builder(NoteNewActivity.this);
        builder.setIcon(R.drawable.apple);
        builder.setTitle("请选择相对应的照片：");
        builder.setItems(Constant.photos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, final int which) {
                if (Constant.namePhoto[which] != null) {
                    photo = Constant.namePhoto[which];
                    final String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + photo;
                    final File file = new File(path);
                    if (!file.exists()) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Load.load_Photo(NoteNewActivity.this, photo);
                            }
                        }).start();
                    } else {
                        Bitmap bm = BitmapFactory.decodeFile(path);
                        imageView1.setImageBitmap(bm);
                    }

                }
            }
        });
        builder.create().show();
    }

    //处理图片
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Bitmap bm = (Bitmap) msg.obj;
                    imageView1.setImageBitmap(bm);//不会变形
                    break;
            }
        }
    };

    @Override
    public void strPath(String path) {
        Bitmap bm = BitmapFactory.decodeFile(path);
        Message message = new Message();
        message.what = 1;
        message.obj = bm;
        handler.sendMessage(message);
    }
}