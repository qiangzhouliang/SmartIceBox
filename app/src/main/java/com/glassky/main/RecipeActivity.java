package com.glassky.main;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.fragment.CookBookFragment;
import com.fragment.DetailFragment;
import com.fragment.MenuFragment;

import java.util.ArrayList;

/**
 * 冰箱食谱
 */
public class RecipeActivity extends Activity implements CookBookFragment.CookBookChange,MenuFragment.CookBookStep{

    private ViewPager viewPager;
    private PagerTabStrip pagerTabStrip;
    private WebView webView;
    private CookBookFragment cbf;

    //定义一组标题
    private String[] titles = {"冰箱食谱","今日推荐","食材大全"};
    //用来存放布局
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
        //将三个布局加入到views里面去
        views.add(getLayoutInflater().inflate(R.layout.layout2,null));
        views.add(getLayoutInflater().inflate(R.layout.layout1, null));
        views.add(getLayoutInflater().inflate(R.layout.layout3, null));

        //对PagerTabStrip进行设置
        pagerTabStrip.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        pagerTabStrip.setTabIndicatorColor(getResources().getColor(android.R.color.holo_blue_bright));//设置指示器的颜色
        pagerTabStrip.setTextColor(Color.WHITE);
        pagerTabStrip.setTextSize(1, 16.0f);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setCurrentItem(1);
        cookRecommend(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                System.out.println("position : "+position);
                //加载冰箱食谱
                if(position == 0){
                    cbf = CookBookFragment.getInstance("");
                    //动态加载fragment
                    loadFragment(cbf);
                }else if(position == 1){
                    System.out.println("今日推荐页面开始加载。。。");
                    //今日推荐功能
                    cookRecommend(position);
                }else if(position == 2){
                    //食材大全
                    webView = (WebView)views.get(position).findViewById(R.id.web_view2);
                    String url = "http://www.douguo.com/shicai/";
                    webViewLoad(webView,url);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //今日推荐功能
    private void cookRecommend(int position) {
        webView = (WebView)views.get(position).findViewById(R.id.web_view);
        String url = "http://m.meishichina.com/recipe/";
        webViewLoad(webView,url);
    }

    /**
     * 动态加载fragment
     * @param fragment
     */
    private void loadFragment(Fragment fragment) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content, fragment);//删除+加载
        //把当前Fragment添加到Activity栈中
        ft.addToBackStack(null);
        ft.commit();
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

    /**
     * 按实物名称查找
     * @param name
     */
    @Override
    public void cookbookchange(String name) {
        MenuFragment mf = MenuFragment.getInstance(name);
        loadFragment(mf);
    }

    @Override
    public void searchCookBook(String str) {
        MenuFragment mf = MenuFragment.getInstance(str);
        loadFragment(mf);
    }

    /**
     * 点击某一种食品时，自动跳转到详细做法
     * @param id
     * @param title
     * @param tags
     * @param imtro
     * @param ingredients
     * @param burden
     * @param albums
     * @param steps
     */
    @Override
    public void cookStep(String id, String title, String tags, String imtro, String ingredients, String burden, String albums, String steps) {
        DetailFragment df = DetailFragment.getInstance(id, title, tags, imtro, ingredients, burden, albums, steps);
        loadFragment(df);
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
    //键盘按下
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if(getFragmentManager().getBackStackEntryCount() == 0){
                finish();
            }else{
                getFragmentManager().popBackStack();//出栈操作
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
