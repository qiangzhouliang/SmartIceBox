package com.fragment;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.glassky.main.R;
import com.user.Note;
import com.util.DataOperate;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * A simple {@link Fragment} subclass.
 */
public class RightFragment extends Fragment {
    //放note
    private List<Note> notes = new ArrayList<Note>();
    private ListView listView2_right;
    private NoteAdapter na;
    private LoadCompare loadCompare;
    public RightFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_right, container, false);
        loadCompare = (LoadCompare) getActivity();
        listView2_right = (ListView) view.findViewById(R.id.listView2_right);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //从后台加载数据
        loadData();
    }

    private void loadData(){
        //直接查询
        DataOperate.getFoodInfo(getActivity());
        DataOperate.setOnListFood(new DataOperate.ListFood() {
            @Override
            public void listFood(List list) {
                notes = list;
                loadCompare.listFood(notes);
                na = new NoteAdapter(getActivity(),notes);
                listView2_right.setAdapter(na);
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
            view = LayoutInflater.from(context).inflate(R.layout.layout_note_item_fragment, null);

            TextView name = (TextView) view.findViewById(R.id.textView_name);
            TextView num = (TextView) view.findViewById(R.id.textView_num);
            TextView shelf_life = (TextView) view.findViewById(R.id.textView_shelf_life);
            final ImageView imageView_icon = (ImageView) view.findViewById(R.id.imageView_iconn);
            DataOperate.getIcon(getActivity(),imageView_icon,note.getIcon().getFilename(),"",note.getIcon().getUrl());
            DataOperate.setOnPhotoPath(new DataOperate.PhotoPath() {
                @Override
                public void photoPath(final ImageView img, String str) {
                    Bitmap bitmap = null;
                    bitmap = BitmapFactory.decodeFile(str);
                    final Bitmap finalBitmap = bitmap;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            img.setImageBitmap(finalBitmap);
                        }
                    });
                }
            });
            name.setText(note.getFoodname());//设置显示内容
            String str = note.getNum() + "";
            num.setText(str);
            shelf_life.setText(note.getShelf_life());
            return view;
        }
    }
    //查询数据完成时
    public static interface LoadCompare{
        public void listFood(List<Note> list);
    }
}
