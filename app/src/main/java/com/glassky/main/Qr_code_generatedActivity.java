package com.glassky.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.Constant;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Hashtable;

//生成二维码
public class Qr_code_generatedActivity extends ActionBarActivity implements View.OnClickListener{

    private String photo = "1.jpg";

    private EditText editText_name,editText_shelf_life,editText_num,editText_remark;

    private Button button_selectPhoto,button_generate;
    private ImageView codeImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_generated);

        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_shelf_life = (EditText) findViewById(R.id.editText_shelf_life);
        editText_num = (EditText) findViewById(R.id.editText_num);
        editText_remark = (EditText) findViewById(R.id.editText_remark);

        codeImg = (ImageView) findViewById(R.id.imageView);

        button_selectPhoto = (Button) findViewById(R.id.button_selectphoto);
        button_generate = (Button) findViewById(R.id.button_generate);

        button_selectPhoto.setOnClickListener(this);
        button_generate.setOnClickListener(this);
    }

    //菜单被选中时
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.scan_menu:
                Intent intent = new Intent(Qr_code_generatedActivity.this,ScanActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.scan_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.button_selectphoto:
                selectPhoto();
                break;
            case R.id.button_generate:
                //生成二维码按钮
                button_generate();
                break;
        }
    }

    private void button_generate() {
        String name = editText_name.getText().toString();
        String shelf_life = editText_shelf_life.getText().toString();
        String num = editText_num.getText().toString();
        String remark = editText_remark.getText().toString();
        String text = name+","+shelf_life+","+num+","+remark+","+photo;
        if(null!=text && !text.equals("")){
            try {
                // 实例化二维码对象
                QRCodeWriter writer = new QRCodeWriter();
                // 用一个map保存编码类型
                Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
                // 保持字符集为“utf－8”
                hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
                /**
                 * 第一个参数：输入的文本
                 * 第二个参数：条形码样式－》二维码
                 * 第三个参数：宽度
                 * 第四个参数：高度
                 * 第五个参数：map保存编码类型
                 */

                BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 300, 300, hints);
                // 将像素保存在数组里
                int[] pixels = new int[300 * 300];
                for (int y = 0; y < 300; y++) {
                    for (int x = 0; x < 300; x++) {
                        if (bitMatrix.get(x, y)) {// 二维码黑点
                            pixels[y * 300 + x] = 0xff000000;
                        } else {// 二维码背景白色
                            pixels[y * 300 + x] = 0xffffffff;
                        }

                    }
                }
                // 生成位图
                Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
                /**
                 * 第一个参数：填充位图的像素数组
                 * 第二个参数：第一个颜色跳过几个像素读取
                 * 第三个参数：像素的幅度
                 * 第四个参数：起点x坐标
                 * 第五个参数：起点y坐标
                 * 第六个参数：宽
                 * 第七个参数：高
                 */
                bitmap.setPixels(pixels, 0, 300, 0, 0, 300, 300);

                //设置图像
                codeImg.setImageBitmap(bitmap);
                saveBitmap(bitmap);

            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
    }

    //保存图片
    String rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath();
    public void saveBitmap(Bitmap bm) {
        File f = new File(rootPath+File.separator+System.currentTimeMillis()+".jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(this,"图片以保存在picture文件夹下",Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void selectPhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Qr_code_generatedActivity.this);
        builder.setIcon(R.drawable.apple);
        builder.setTitle("请选择相对应的照片：");
        builder.setItems(Constant.photos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < Constant.namePhoto.length; i++) {
                    photo = Constant.namePhoto[which];
                }
            }
        });
        builder.create().show();
    }
}
