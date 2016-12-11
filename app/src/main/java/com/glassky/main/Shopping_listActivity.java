package com.glassky.main;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.constant.Constant;
import com.fragment.RightFragment;
import com.user.Note;

import java.util.List;
/**
 * 购物清单界面
 */
public class Shopping_listActivity extends ActionBarActivity implements RightFragment.LoadCompare{
    private String stt = "";//需要购置的食品
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);
    }

    //菜单被选中时
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.shopp_note:
                hint(stt);
                break;
            default:
                break;
        }
        return true;
    }

    //创建菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shopp_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void hint(String strr) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Shopping_listActivity.this);
        builder.setTitle("需要购置的食品清单");
        builder.setMessage(strr);
        builder.setCancelable(true);
        builder.show();
    }

    @Override
    public void listFood(List<Note> list) {
        int x = 0;
        int list_Size = list.size();
        int list_food = Constant.str_list.length;
        for (int i = 0; i < list_food; i++){
            for (int j = 0; j <list_Size ;j++){
                if(Constant.str_list[i].equals(list.get(j).getFoodname())){
                    break;
                }else if(!Constant.str_list[i].equals(list.get(j).getFoodname())) {
                    if (j == list_Size - 1) {
                        if (x == 3) {
                            stt += Constant.str_list[i]+"\n";
                            x = 0;
                        }else {
                            if(i == list_food-1){
                                stt += Constant.str_list[i] + "\n";
                            }else {
                                stt += Constant.str_list[i] + " 、";
                            }
                            x++;
                        }
                    }
                }
            }
        }
    }
}
