package com.glassky.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.user.Note;
import com.util.DataOperate;
import com.util.Load;

import java.io.File;
import java.util.List;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;
public class ScanActivity extends ActionBarActivity implements Load.LoadCompare{
    private String photo = "1.png";
    private String name,shelf_life,num,remark;
    private String objectId;
    private EditText editText_name,editText_shelf_life,editText_num,editText_remark;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan);
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_shelf_life = (EditText) findViewById(R.id.editText_shelf_life);
        editText_num = (EditText) findViewById(R.id.editText_num);
        editText_remark = (EditText) findViewById(R.id.editText_remark);

        //打开扫描界面扫描条形码或二维码
        Intent openCameraIntent = new Intent(ScanActivity.this,CaptureActivity.class);
        startActivityForResult(openCameraIntent, 0);
    }


    //处理CaptureActivity里面返回的结果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理扫描结果（在界面上显示）
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            //获取到扫描数据
            String result = bundle.getString("result");
            String[] strarray=result.split("[,]");
            for (int i = 0; i < strarray.length; i++){
                if(i == 0){
                    name = strarray[i];
                    editText_name.setText(name);
                }else if(i == 1){
                    shelf_life = strarray[i];
                    editText_shelf_life.setText(shelf_life);
                }else if(i == 2){
                    num = strarray[i];
                    editText_num.setText(num);
                }else if(i == 3){
                    remark = strarray[i];
                    editText_remark.setText(remark);
                }else if(i == 4){
                    if(strarray[i] != null) {
                        photo = strarray[i];
                    }
                }
            }
        }
    }
    //删除食品
    private void deletefood(String strId) {
        Note note = new Note();
        DataOperate.deleteDate(ScanActivity.this,note,strId);
        DataOperate.setOnSuccessAndFalied(new DataOperate.SuccessAndFalied() {
            @Override
            public void success(){
                finish();
            }

            @Override
            public void failed() {
                Toast.makeText(ScanActivity.this, "删除失败，你可以试试长按选项手动删除...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ScanActivity.this, NoteListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void on_Click(View view){
        addfood();
    }

    public void del(View view){
        query("Note",name);
    }
    private void query(String tableName ,String name) {
        DataOperate.querySql(tableName,name);
        DataOperate.setOnListFood(new DataOperate.ListFood() {
            @Override
            public void listFood(List list1) {
                List<Note> list = (List<Note>) list1;
                if (list != null && list.size() > 0) {
                    for (int i = 0;i<list.size();i++){
                        Note note = list.get(i);
                        objectId = note.getObjectId();
                        deletefood(objectId);
                    }
                } else {
                    Log.i("smile", "查询成功，无数据返回");
                }
            }
        });
    }

    public void addfood() {
        //得到图片文件路径
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + photo;
        final File file = new File(path);
        if (!file.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Load.load_Photo(ScanActivity.this, photo);
                }
            }).start();
        } else {
            foodAdd(path);
        }

    }

    private void foodAdd(String path) {
        //图片的上传
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Note note = new Note();
                    String numm = editText_num.getText().toString();
                    int num = 0;
                    if (!numm.isEmpty()) {
                        num = Integer.parseInt(numm);
                    }
                    String remark = editText_remark.getText().toString();
                    if (!TextUtils.isEmpty(editText_name.getText().toString())) {
                        note.setFoodname(editText_name.getText().toString());
                        note.setShelf_life(editText_shelf_life.getText().toString());
                        note.setNum(num);
                        note.setRemark(remark);
                        note.setIcon(bmobFile);
                        DataOperate.addData(ScanActivity.this,note);
                        DataOperate.setOnSuccessAndFalied(new DataOperate.SuccessAndFalied() {
                            @Override
                            public void success() {
                                finish();
                            }

                            @Override
                            public void failed() {
                                Toast.makeText(ScanActivity.this, "添加数据失败,请重新添加", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                    } else {
                        Toast.makeText(ScanActivity.this, "图片上传失败..." + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    @Override
    public void strPath(String path) {
        foodAdd(path);
    }
}
