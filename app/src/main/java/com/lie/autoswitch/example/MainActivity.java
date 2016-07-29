package com.lie.autoswitch.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.lie.jautoswith.view.AutoSwitchViewPager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AutoSwitchViewPager myAutoSwitch;
    private List<String> mImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myAutoSwitch = (AutoSwitchViewPager) findViewById(R.id.my_autoswitch);
        //获取图片
        getData();
        //设置图片集合
        myAutoSwitch.setImageList(mImageList);
        //设置图片切换间隔时间
        myAutoSwitch.setDelayTime(3000);
        //设置图片点击事件
        myAutoSwitch.setPagerClickListener(new AutoSwitchViewPager.IPagerClick() {
            @Override
            public void click(int position) {
                Toast.makeText(MainActivity.this, "点击了第" + position + "张图片", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getData() {
        mImageList = new ArrayList<String>();
        mImageList.add("http://img4.imgtn.bdimg.com/it/u=1249974208,2325623016&fm=206&gp=0.jpg");
        mImageList.add("http://img3.imgtn.bdimg.com/it/u=1003865389,4203869868&fm=206&gp=0.jpg");
        mImageList.add("http://img1.imgtn.bdimg.com/it/u=3938674857,2903191994&fm=206&gp=0.jpg");
        mImageList.add("http://img4.imgtn.bdimg.com/it/u=3559811430,526315708&fm=206&gp=0.jpg");

    }
}
