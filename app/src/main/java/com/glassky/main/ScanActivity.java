package com.glassky.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.user.Note;

import java.io.File;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class ScanActivity extends ActionBarActivity implements View.OnClickListener{

    private String photo = "1.png";
    private String name,shelf_life,num,remark;
    private String objectId;
    private EditText editText_name,editText_shelf_life,editText_num,editText_remark;

    private Button button_add,button_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scan);
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, "7111b1b0ca4263017a2765f3cdeacfd2");
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_shelf_life = (EditText) findViewById(R.id.editText_shelf_life);
        editText_num = (EditText) findViewById(R.id.editText_num);
        editText_remark = (EditText) findViewById(R.id.editText_remark);

        button_add = (Button) findViewById(R.id.button_add);
        button_delete = (Button) findViewById(R.id.button_delete);
        //打开扫描界面扫描条形码或二维码
        Intent openCameraIntent = new Intent(ScanActivity.this,CaptureActivity.class);
        startActivityForResult(openCameraIntent, 0);

        button_add.setOnClickListener(this);
        button_delete.setOnClickListener(this);

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
        note.delete(this, strId, new DeleteListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(ScanActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(ScanActivity.this, "删除失败，你可以试试长按选项手动删除..." + s, Toast.LENGTH_SHORT).show();
                //删除成功之后让跳到列表界面
                Intent intent = new Intent(ScanActivity.this, NoteListActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_add:
                addfood();
                break;
            case R.id.button_delete:
                query(name);
                break;
        }
    }

    private void query(String name) {
        String bql1="select * from Note where foodname = ?";
        new BmobQuery<Note>().doSQLQuery(ScanActivity.this, bql1, new SQLQueryListener<Note>() {

            @Override
            public void done(BmobQueryResult<Note> result, BmobException e) {
                if (e == null) {
                    List<Note> list = (List<Note>) result.getResults();
                    if (list != null && list.size() > 0) {
                        for (int i = 0;i<list.size();i++){
                            Note note = list.get(i);
                            objectId = note.getObjectId();
                            deletefood(objectId);
                        }
                    } else {
                        Log.i("smile", "查询成功，无数据返回");
                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
                }
            }
        }, name);
    }

    private void addfood() {
        //得到图片文件路径
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + photo;
        //图片的上传
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(ScanActivity.this, new UploadFileListener() {
            @Override
            public void onSuccess() {
                Note note = new Note();
                String name = editText_name.getText().toString();
                String shelf_life = editText_shelf_life.getText().toString();
                String numm = editText_num.getText().toString();
                int num = 0;
                if(!numm.isEmpty()) {
                    num = Integer.parseInt(numm);
                }
                String remark = editText_remark.getText().toString();
                if (!TextUtils.isEmpty(name)) {
                    note.setFoodname(name);
                    note.setShelf_life(shelf_life);
                    note.setNum(num);
                    note.setRemark(remark);
                    note.setIcon(bmobFile);
                    note.save(ScanActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            //保存成功
                            Toast.makeText(ScanActivity.this, "添加数据成功", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(ScanActivity.this, "添加数据失败", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                }
            }

            @Override
            public void onFailure(int i, String s) {
                //Toast.makeText(ScanActivity.this,"图片上传失败..."+s,Toast.LENGTH_SHORT).show();
                System.out.println("------ "+s);
            }
        });
    }
}
