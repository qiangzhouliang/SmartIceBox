package com.glassky.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.notification.SendNotification;
import com.user.Note;
import com.user.Temp;
import com.user._User;
import com.util.Vioce;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.GetListener;
import cn.bmob.v3.listener.UpdateListener;

public class SlidingMenuList1Activity extends Activity implements View.OnClickListener {

    protected static final int NID_1 = 0x1;
    protected static final int NID_2 = 0x2;

    private final String str = "温馨提示，您的冰箱内有将要过期的食品,请及时使用！";
    private final String str1 = "温馨提示，您的冰箱内有已经过期的食品，请及时处理，误食过期食品对身体不好！";
    private boolean flag = true;//语音提示标志
    private boolean flag1 = true;//过期提示标志
    private boolean flag2 = true; //发送通知标志
    //放note
    private List<Note> notes = new ArrayList<Note>();
    private ListView listView;
    private NoteAdapter na;

    private int flag_date = 3;//默认保质期天数为 3 天
    private Button button_query, button_food, button_analyze, button_generate, button_temp, button_list_food;
    private TextView textView_hint, textView_head_name,textView_temp,textView_open;
    private ImageView imageView_head;

    private String string = "";//存储过期食品信息
    private int open_down = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_menu_list1);
        Bmob.initialize(this, "7111b1b0ca4263017a2765f3cdeacfd2");
        //初始化语音配置对象
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "= 572ef5b8");

        //创建侧滑菜单
        SlidingMenu menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);//从左侧滑
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);//触摸平的方式为全屏
        menu.setFadeDegree(0.55f);//渐变度
        menu.setMenu(R.layout.menu_layout);
        menu.setBehindScrollScale(1.0f);//与下方视图的移动速度比
        menu.setBehindOffsetRes(R.dimen.menu_offset);//设置相对屏幕偏移量
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);//添加到界面

        button_query = (Button) findViewById(R.id.button_query);
        button_food = (Button) findViewById(R.id.button_food);
        button_analyze = (Button) findViewById(R.id.button_analyze);
        button_generate = (Button) findViewById(R.id.button_generate);
        button_temp = (Button) findViewById(R.id.button_temp);
        button_list_food = (Button) findViewById(R.id.button_list_food);
        imageView_head = (ImageView) findViewById(R.id.imageView_head);

        listView = (ListView) findViewById(R.id.listView);
        textView_hint = (TextView) findViewById(R.id.textView_hint);
        textView_head_name = (TextView) findViewById(R.id.textView_head_name);
        textView_temp = (TextView) findViewById(R.id.textView_temp);
        textView_open = (TextView) findViewById(R.id.textView_open);

        button_query.setOnClickListener(this);
        button_food.setOnClickListener(this);
        button_analyze.setOnClickListener(this);
        button_generate.setOnClickListener(this);
        button_temp.setOnClickListener(this);
        button_list_food.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        final _User user = (_User) bundle.get("user");
        textView_head_name.setText(user.getUsername().toString());
        new Thread(new Runnable() {
            @Override
            public void run() {
                BmobFile bf = user.getIcon();
                if (bf != null) {
                    //缩列图
                    bf.loadImageThumbnail(SlidingMenuList1Activity.this, imageView_head, 64, 64, 100);
                }
            }
        }).start();

        //上下文菜单
        registerForContextMenu(listView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag2 = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        string = "";
                        loadData();
                        Thread.sleep(1000 * 60 * 2);//86400000
                        if(flag2 == true) {
                            new SendNotification().sendNotification1(SlidingMenuList1Activity.this, string, (NotificationManager) getSystemService(NOTIFICATION_SERVICE), NID_1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    //查询温度
                    queryTemp();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        flag1 = true;

    }

    ArrayList<String> list = new ArrayList<String>();
    //温度查询
    private void queryTemp() {
        BmobQuery<Temp> bmobQuery = new BmobQuery<Temp>();
        //查询一个
        bmobQuery.getObject(this, "10166ef327", new GetListener<Temp>() {
            @Override
            public void onSuccess(Temp temp) {
                Temp t = new Temp();
                t = temp;
                openDown(t.getOpenflag());
                Message message = new Message();
                message.what = 1;
                message.obj = t;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(int i, String s) {
               // Toast.makeText(SlidingMenuList1Activity.this, "查询数据失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //判断门的状态
    private void openDown(String openflag) {
        if(openflag.equals("01")){
            open_down = 0;
        }else if(openflag.equals("02")){
            open_down++;
        }
        if(open_down >= 60){
            open_down = 0;
            new SendNotification().sendNotification1(this,"您的冰箱门开着，请及时关门，以节约电费",(NotificationManager) getSystemService(NOTIFICATION_SERVICE),NID_2);
        }
        System.out.println("open_down = "+open_down);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:
                Temp t = new Temp();
                t = (Temp) msg.obj;
                textView_temp.setText(t.getTemp());
                if (t.getOpenflag().equals("01")) {
                    textView_open.setText("关");
                } else if (t.getOpenflag().equals("02")) {
                    textView_open.setText("开");
                }
                break;
            default:
                break;
        }
        }
    };

    //从后台取出数据
    private void loadData() {
        flag1 = true;
        //直接查询
        BmobQuery<Note> query = new BmobQuery<Note>();
        query.setLimit(50);//限定条数（默认是10条）
        //查询多个
        query.findObjects(this, new FindListener<Note>() {
            @Override
            public void onSuccess(List<Note> list) {
                notes = list;
                na = new NoteAdapter(SlidingMenuList1Activity.this, notes);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(na);
                    }
                });

                System.out.println(notes.size());
                if (notes.size() == 0) {
                    textView_hint.setText("温馨提示：您的冰箱内没有将要过期的食物...");
                    textView_hint.setTextColor(Color.BLACK);
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //食品查询
            case R.id.button_query:
                Intent intent = new Intent(this, NoteListActivity.class);
                startActivity(intent);
                break;
            //冰箱食谱
            case R.id.button_food:
                Intent intent3 = new Intent(this, RecipeActivity.class);
                startActivity(intent3);
                break;
            //营养分析
            case R.id.button_analyze:
                trophic_analysis();
                break;
            case R.id.button_generate:
                Intent intent2 = new Intent(this, Qr_code_generatedActivity.class);
                startActivity(intent2);
                break;
            case R.id.button_temp:
                //温度更新
                update_temp();
                break;
            case R.id.button_list_food:
                //购物清单
                startActivity(new Intent(SlidingMenuList1Activity.this, Shopping_listActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag2 = false;
        System.out.println("onDestroy"+flag2);
    }

    private void update_temp() {

        final String[] items = new String[]{"冷冻（-18）", "冷藏（7）", "春秋季（3~4）", "夏季（2~3）", "冬季（4~5）", "调试", "完成"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SlidingMenuList1Activity.this);
        builder.setIcon(R.drawable.temp);
        builder.setTitle("请选择合适的温度：");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        temperatureTo("-18");
                        break;
                    case 1:
                        temperatureTo("7");
                        break;
                    case 2:
                        temperatureTo("4");
                        break;
                    case 3:
                        temperatureTo("3");
                        break;
                    case 4:
                        temperatureTo("5");
                        break;
                    case 5:
                        temperatureTo("30");
                        break;
                    case 6:
                        temperatureTo("0");
                        break;
                }
            }
        });
        builder.create().show();
    }

    private void temperatureTo(String in) {
        Temp t = new Temp();
        t.setFlagtemp(in);
       // t.save(this);
        //更新数据
        t.update(this, "10166ef327", new UpdateListener() {
            @Override
            public void onSuccess() {
                //保存成功
                Toast.makeText(SlidingMenuList1Activity.this, "修改数据成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(SlidingMenuList1Activity.this, "修改数据失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //营养分析
    private void trophic_analysis() {
        startActivity(new Intent(this, Trophic_analysis_Activity.class));
    }

    //自定义Note适配器（静态内部类）
    class NoteAdapter extends BaseAdapter {

        private Context context;
        private List<Note> list;

        //传递参数
        public NoteAdapter(Context context, List<Note> list) {
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
            int num_flag = Integer.parseInt(note.getShelf_life());

            if ((num_flag) <= flag_date) {
                flag1 = false;
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
                if (num_flag > 0) {
                    textView_hint.setText("提示：您的如下食物即将过期...");
                    textView_hint.setTextColor(Color.RED);
                    if (flag) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                            //语音提示
                            Vioce.xunfeiVicos(str, SlidingMenuList1Activity.this);
                            flag = false;
                            }
                        }).start();
                    }

                } else if (num_flag <= 0) {
                    textView_hint.setText("提示：您的如下食物已经过期...");
                    textView_hint.setTextColor(Color.RED);
                    if (flag) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                //语音提示
                                Vioce.xunfeiVicos(str1, SlidingMenuList1Activity.this);
                                flag = false;
                            }
                        }).start();
                    }
                }
                name.setText(note.getFoodname());//设置显示内容
                String str = note.getNum() + "";
                num.setText(str);
                shelf_life.setText(note.getShelf_life());
                remark.setText(note.getRemark());
                view.setTag(note.getObjectId());//保存ID

                string += "食物名称："+note.getFoodname()+","+"    食物数量："+note.getNum()+","+"    保质期(天)："+note.getShelf_life()+","+"    备注："+note.getRemark()+","+"    购买日期： "+note.getCreatedAt()+"!\n";
                final ImageView imageView_icon = (ImageView) view.findViewById(R.id.imageView_iconn);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BmobFile bf = note.getIcon();
                        if (bf != null) {
                            //缩列图
                            bf.loadImageThumbnail(SlidingMenuList1Activity.this, imageView_icon, 50, 50, 100);
                        }
                    }
                }).start();
            } else if (flag1) {
                textView_hint.setText("提示：您的冰箱内没有将要过期的食物...");
                textView_hint.setTextColor(Color.BLACK);
            }
            //new SendNotification().sendNotification1(SlidingMenuList1Activity.this,string,(NotificationManager) getSystemService(NOTIFICATION_SERVICE));
            return view;
        }
    }

    //创建上下文菜单(删除菜单)长按删除
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //参数（groupID,自己的id,排序，文本内容）
        menu.add(1, 1, 100, "删除");//添加一个删除按钮
    }

    //点击监听事件
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                View view = info.targetView;//就是当前目标（点击的目标）
                //测试
                System.out.println(info);
                String objectId = (String) view.getTag();
                Note note = new Note();
                note.delete(this, objectId, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        loadData();//重新更新一下
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        System.out.println("-----" + s);
                    }
                });
                break;
        }
        return super.onContextItemSelected(item);
    }

}
