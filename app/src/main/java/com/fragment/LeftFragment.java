package com.fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.constant.Constant;
import com.applocation.QzlApplication;
import com.glassky.main.R;
import com.lidroid.xutils.exception.DbException;
import com.user.List_food;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeftFragment extends Fragment implements View.OnClickListener{

    private TextView textView2,textView3,textView4,textView5,textView6,textView7,textView8;
    private QzlApplication app;
    private List_food listfood;
    public LeftFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_left,container, false);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        textView3 = (TextView) view.findViewById(R.id.textView3);
        textView4 = (TextView) view.findViewById(R.id.textView4);
        textView5 = (TextView) view.findViewById(R.id.textView5);
        textView6 = (TextView) view.findViewById(R.id.textView6);
        textView7 = (TextView) view.findViewById(R.id.textView7);
        textView8 = (TextView) view.findViewById(R.id.textView8);
        app = new QzlApplication();

        findAllClick();
        //注册事件
        textView2.setClickable(true);
        textView3.setClickable(true);
        textView4.setClickable(true);
        textView5.setClickable(true);
        textView6.setClickable(true);
        textView7.setClickable(true);
        textView8.setClickable(true);

        textView2.setOnClickListener(this);
        textView3.setOnClickListener(this);
        textView4.setOnClickListener(this);
        textView5.setOnClickListener(this);
        textView6.setOnClickListener(this);
        textView7.setOnClickListener(this);
        textView8.setOnClickListener(this);

        return view;
    }

    public void findAllClick(){

        List<List_food> list = null;//通过反射的方式
        try {
            list = app.dbUtils.findAll(List_food.class);
            listfood = list.get(0);
            textView2.setText(listfood.getName_bread());
            textView3.setText(listfood.getName_dairy_food());
            textView4.setText(listfood.getName_egg());
            textView5.setText(listfood.getName_Forzen_fillets());
            textView6.setText(listfood.getName_meat());
            textView7.setText(listfood.getName_nut());
            textView8.setText(listfood.getName_vegetable());
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView2:
                //提示
                hint(Constant.str[0]);
                break;
            case R.id.textView3:
                hint(Constant.str[1]);
                break;
            case R.id.textView4:
                hint(Constant.str[6]);
                break;
            case R.id.textView5:
                hint(Constant.str[5]);
                break;
            case R.id.textView6:
                hint(Constant.str[3]);
                break;
            case R.id.textView7:
                hint(Constant.str[2]);
                break;
            case R.id.textView8:
                hint(Constant.str[4]);
                break;
        }
    }

    private void hint(String strr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("说明");
        builder.setMessage(strr);
        builder.setCancelable(true);
        builder.show();
    }
}
