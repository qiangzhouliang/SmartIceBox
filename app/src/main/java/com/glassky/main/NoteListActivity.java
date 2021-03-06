package com.glassky.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.user.Note;
import com.util.DataOperate;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询食品，显示界面
 */
public class NoteListActivity extends ActionBarActivity {

    private static final int DEL_ITEM = 0x1;
    //存放note
    private List<Note> notes = new ArrayList<Note>();
    private ListView listView;
    private NoteAdapter na;
    private TextView tv_note_list_foodshape;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);//activity_note_list
        tv_note_list_foodshape = (TextView) findViewById(R.id.tv_note_list_foodshape);

        initView();
        initListener();
        //上下文菜单
        registerForContextMenu(listView);
    }

    private void initListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView textView_name = (TextView) view.findViewById(R.id.textView_name);
                TextView textView_shelf_life = (TextView) view.findViewById(R.id.textView_shelf_life);
                TextView textView_num = (TextView) view.findViewById(R.id.textView_num);
                TextView textView_remark = (TextView) view.findViewById(R.id.textView_remark);

                String objectId = (String) view.getTag();//取出ID
                String name = textView_name.getText().toString();//取出内容
                String shelf_life = textView_shelf_life.getText().toString();
                String num = textView_num.getText().toString() + "";
                String remark = textView_remark.getText().toString();

                Intent intent = new Intent(NoteListActivity.this, NoteDetailActivity.class);
                intent.putExtra("objectId", objectId);
                intent.putExtra("name", name);
                intent.putExtra("shelf_life", shelf_life);
                intent.putExtra("num", num);
                intent.putExtra("remark", remark);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        listView = (ListView) findViewById(R.id.listView_note);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //从后台加载数据
        loadData();
    }

    //从后台取出数据
    private void loadData() {
        DataOperate.getFoodInfo(NoteListActivity.this);
        DataOperate.setOnListFood(new DataOperate.ListFood() {
            @Override
            public void listFood(List list) {
                //查询出结果后将食物种类显示在textView上
                tv_note_list_foodshape.setText("冰箱内有 "+list.size()+" 种食物");
                notes = list;
                na = new NoteAdapter(NoteListActivity.this,notes);
                listView.setAdapter(na);
            }
        });
    }


    //自定义Note适配器（静态内部类）
    class NoteAdapter extends BaseAdapter {

        private Context context;
        private List<Note> list;
        //传递参数
        public NoteAdapter(Context context,List<Note> list){
            this.context = context;
            this.list = list;
        }
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.layout, null);
            final Note note = list.get(i);
            String id = note.getObjectId();//拿到id
            //获取列表布局
            view = LayoutInflater.from(context).inflate(R.layout.layout_note_item, null);

            TextView name = (TextView) view.findViewById(R.id.textView_name);
            TextView num = (TextView) view.findViewById(R.id.textView_num);
            TextView shelf_life = (TextView) view.findViewById(R.id.textView_shelf_life);
            final TextView remark = (TextView) view.findViewById(R.id.textView_remark);
            //是这一个点击事件
            remark.setOnClickListener(new View.OnClickListener() {
                Boolean flag = true;
                @Override
                public void onClick(View v) {
                    if(flag){
                        flag = false;
                        remark.setSingleLine(flag);
                    }else{
                        flag = true;
                        remark.setSingleLine(flag);
                    }
                }
            });
            final ImageView imageView_icon = (ImageView) view.findViewById(R.id.imageView_iconn);
            DataOperate.getIcon(NoteListActivity.this,imageView_icon,note.getIcon().getFilename(),"",note.getIcon().getUrl());
            DataOperate.setOnPhotoPath(new DataOperate.PhotoPath() {
                @Override
                public void photoPath(final ImageView img, String str) {
                    showPhoto(img, str);
                }
            });
            name.setText(note.getFoodname());//设置显示内容
            String str = note.getNum() + "";
            num.setText(str);
            shelf_life.setText(note.getShelf_life());
            remark.setText(note.getRemark());
            view.setTag(note.getObjectId());//保存ID
            return view;
        }
    }

    private void showPhoto(final ImageView img, String str) {
        Bitmap bitmap = null;
        bitmap = BitmapFactory.decodeFile(str);
        final Bitmap finalBitmap = bitmap;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                img.setImageBitmap(finalBitmap);
            }
        });
    }

    //菜单被选中时
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_note:
                Intent intent = new Intent(NoteListActivity.this,NoteNewActivity.class);
                startActivity(intent);
                break;
        }
        return true;
    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_list,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //创建上下文菜单(删除菜单)长按删除
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //参数（groupID,自己的id,排序，文本内容）
        menu.add(1,DEL_ITEM,100,"删除");//添加一个删除按钮
    }
    //点击监听事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case DEL_ITEM:
                //
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                View view = info.targetView;//就是当前目标（点击的目标）
                //测试
                System.out.println(info);
                String objectId = (String) view.getTag();
                Note note = new Note();
                DataOperate.deleteDate(NoteListActivity.this,note,objectId);
                DataOperate.setOnOverLoad(new DataOperate.OverLoad() {
                    @Override
                    public void overLoad() {
                        loadData();
                    }
                });
                /*note.delete(this, objectId, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        loadData();//重新更新一下
                    }

                    @Override
                    public void onFailure(int i, String s) {

                    }
                });*/
                break;
        }
        return super.onContextItemSelected(item);
    }
}
