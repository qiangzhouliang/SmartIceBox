package com.glassky.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.EditText;

import com.user.Note;
import com.util.DataOperate;

public class NoteDetailActivity extends Activity {
    private EditText editText_name,editText_shelf_life,editText_num,editText_remark;
    private String objectId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);
        editText_name = (EditText) findViewById(R.id.editText_name);
        editText_shelf_life = (EditText) findViewById(R.id.editText_shelf_life);
        editText_num = (EditText) findViewById(R.id.editText_num);
        editText_remark = (EditText) findViewById(R.id.editText_remark);

        objectId = getIntent().getStringExtra("objectId");
        editText_name.setText(getIntent().getStringExtra("name"));
        editText_shelf_life.setText(getIntent().getStringExtra("shelf_life"));
        editText_num.setText(getIntent().getStringExtra("num"));
        editText_remark.setText(getIntent().getStringExtra("remark"));

    }

    //返回键自动保存
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Note note = new Note();
            String name = editText_name.getText().toString();
            String shelf_life = editText_shelf_life.getText().toString();
            String numm = editText_num.getText().toString();
            int num = 0;
            if(!numm.isEmpty()) {
                num = Integer.parseInt(numm);
            }
            String remark = editText_remark.getText().toString();
            if(!TextUtils.isEmpty(name)){
                note.setFoodname(name);
                note.setShelf_life(shelf_life);
                note.setNum(num);
                note.setRemark(remark);
                DataOperate.updateData(NoteDetailActivity.this,note,objectId);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
