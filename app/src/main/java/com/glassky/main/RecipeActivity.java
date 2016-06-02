package com.glassky.main;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

public class RecipeActivity extends Activity implements ViewPager.OnPageChangeListener{

    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;
    private WebView webView;

    //定义一组标题
    private String[] titles = {"冰箱食谱","今日推荐"};
    //用来装布局用的
    private ArrayList<View> views = new ArrayList<View>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        initView();
    }

    private void initView(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pagerTabStrip = (PagerTabStrip) findViewById(R.id.pagertab);
        //将四个布局加入到views里面去
        views.add(getLayoutInflater().inflate(R.layout.layout2,null));
        views.add(getLayoutInflater().inflate(R.layout.layout1, null));

        //对PagerTabStrip进行设置
        pagerTabStrip.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(android.R.color.holo_blue_bright));//设置指示器的颜色
        pagerTabStrip.setTextColor(Color.WHITE);
        pagerTabStrip.setTextSize(1, 16.0f);
        viewPager.setOnPageChangeListener(this);//注册事件
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setCurrentItem(1);
    }

    //正在滚动
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    //页面被选中了
    @Override
    public void onPageSelected(int position) {
        //加载今日推荐
        if(position == 0){

        }else if(position == 1){
            webView = (WebView)views.get(position).findViewById(R.id.web_view);
            String url = "http://m.yz.sm.cn/s?q=%E7%BE%8E%E9%A3%9F&from=wm936310";
            webViewLoad(webView,url);
        }
    }

    private void webViewLoad(WebView webView,String url) {
        WebSettings settings = webView.getSettings();
        //适应屏幕
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);//根据传入的数据再去加载新的网页
                return true;
            }
        });
        webView.loadUrl(url);
    }

    //状态正在发生变化
    @Override
    public void onPageScrollStateChanged(int state) {

    }

    class MyPagerAdapter extends PagerAdapter {
        //获取总数
        @Override
        public int getCount() {
            return views.size();
        }
        //判断view和视图的对象v是否相等
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //实例化每一个选项卡
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = views.get(position);
            container.addView(v);
            return v;
        }

        //删除选项卡
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        //获取标题
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}
