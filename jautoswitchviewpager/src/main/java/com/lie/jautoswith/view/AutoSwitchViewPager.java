package com.lie.jautoswith.view;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.lie.jautoswith.R;

import java.util.ArrayList;
import java.util.List;


/**
 * 自动轮播控件，也是广告轮播控件
 */
public class AutoSwitchViewPager extends FrameLayout {
    private static final String TAG = "AutoSwitchViewPager";

    // 定时器的时间间隔 单位：ms
    private static final long TIMER_DELAY = 3000;

    private ViewPager mPager;
    private LinearLayout mIndicator;

    private PagerAdapter mAdapter;
    private List<String> mImageList;
    private IPagerClick mSliderClick;

    //图片切换间隔时间
    private long mDelay = TIMER_DELAY;
    // 是否自动滚动
    private boolean isAutoScroll = true;
    //是否停止
    private boolean isStop;

    public AutoSwitchViewPager(Context context) {
        super(context);
        initLayout();
    }

    public AutoSwitchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    public AutoSwitchViewPager(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initLayout();
    }

    private void initLayout() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.layout_autoswitch, this);
        // 0、找到组件
        mPager = (ViewPager) findViewById(R.id.autoswitch_pager);
        mIndicator = (LinearLayout) findViewById(R.id.autoswitch_indicator);
        // 1、初始化List
        mImageList = new ArrayList<String>();

        // 2、设置viewpager适配器
        mAdapter = new MyPagerAdapter();
        mPager.setAdapter(mAdapter);

        // 3、设置滑动监听
        mPager.addOnPageChangeListener(onPageChangeListener);

        // 4、设置Touch事件，解决手动滑动和自动滑动的冲突
        mPager.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        // 开始图片滚动
                        startImageCycle();
                        break;
                    case MotionEvent.ACTION_DOWN:

                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 结束图片滚动
                        pushImageCycle();
                        break;
                }
                return false;
            }
        });
    }

    OnPageChangeListener onPageChangeListener = new OnPageChangeListener() {

        // 此页面表示页面跳转完的调用
        @Override
        public void onPageSelected(int arg0) {
            Log.d(TAG, "item_arg0 = " + arg0);
            refreshIndicator(arg0);
        }

        // 当页面正在滑动时，会调用此方法，在滑动停止前，此方法会一直被调用，arg0表示当前页面，即你点击的滑动的页面，arg1表示当前滑动的百分比,arg2当前页面偏移的像素位置
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        // 滑动状态的改变，arg0 == 1表示正在滑动，arg0 == 2表示滑动完毕，arg0==0表示什么都没做
        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (arg0 == 0) {
                // 当滑动到第一张时，让其定位到原集合的最后一张
                if (mPager.getCurrentItem() == 0) {
                    mPager.setCurrentItem(mImageList.size() - 2, false);
                }

                // 当滑动到最后一张时，让其定位到原集合的第1张
                if (mPager.getCurrentItem() == mImageList.size() - 1) {
                    mPager.setCurrentItem(1, false);
                }
            }
        }
    };

    /**
     * 填充ViewPager的页面适配器
     */
    private class MyPagerAdapter extends PagerAdapter {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView view = (ImageView) LayoutInflater
                    .from(getContext()).inflate(R.layout.layout_viewpager, null);

            Context context = getContext();
            if (context != null) {
                Glide.with(context).load(mImageList.get(position))
                        .placeholder(R.drawable.bg_ad_default).into(view);
            }

            // 设置点击事件
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSliderClick != null) {
                        //优化点击的图片索引
                        if (mImageList.size() > 0) {
                            int curPosition = position - 1;
                            mSliderClick.click(curPosition);
                        }
                    }
                }
            });

            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return mImageList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE ;
        }
    }

    /**
     * 设置图片集合
     */
    public void setImageList(List<String> imageList) {
        if (imageList.size() > 0) {
            this.mImageList.clear();
            this.mImageList.addAll(imageList);

            // 在集合前后加各加1张,在集合前加原集合最后一张，在集合后加原集合的第一张
            mImageList.add(0, mImageList.get(mImageList.size() - 1));
            mImageList.add(mImageList.get(1));

            //添加圆圈指示
            addIndicator(imageList.size());

            mAdapter.notifyDataSetChanged();
            // 默认显示第二张，因为第一张现在是原集合的最后一张
            mPager.setCurrentItem(1);

            startImageCycle();
        }
    }

    /**
     * 添加圆圈指示
     *
     * @param size
     */
    private void addIndicator(int size) {
        mIndicator.removeAllViews();
        for (int i = 0; i < size; i++) {
            ImageView indicator = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = 5;
            params.rightMargin = 5;
            indicator.setLayoutParams(params);
            if (i == 0) {
                //第一个默认选中
                indicator.setBackgroundResource(R.drawable.shape_induicator_pre);
            } else {
                indicator.setBackgroundResource(R.drawable.shape_induicator_nor);
            }
            mIndicator.addView(indicator);
        }
    }

    /**
     * 刷新圆圈指示
     */
    private void refreshIndicator(int position) {
        for (int i = 0; i < mIndicator.getChildCount(); i++) {
            if (position == 0) {
                position = mImageList.size() - 2;
            }
            if (position == mImageList.size() - 1) {
                position = 1;
            }

            if (position - 1 == i) {
                mIndicator.getChildAt(i)
                        .setBackgroundResource(R.drawable.shape_induicator_pre);
            } else {
                mIndicator.getChildAt(i)
                        .setBackgroundResource(R.drawable.shape_induicator_nor);
            }
        }
    }

    private Handler mHandler = new Handler();

    /**
     * 图片自动轮播Task
     */
    private Runnable mImageTimerTask = new Runnable() {
        @Override
        public void run() {
            if (mImageList != null && mImageList.size() > 0) {
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                if (!isStop) { // if isStop=false //当你退出后 要把这个给停下来 不然
                    // 这个一直存在
                    // 就一直在后台循环
                    mHandler.postDelayed(mImageTimerTask, mDelay);
                }
            }
        }
    };

    /**
     * 设置是否自动滚动
     */
    public void setAutoScroll(boolean isAutoScroll) {
        this.isAutoScroll = isAutoScroll;
    }

    /**
     * 设置图片点击监听
     *
     * @param sliderClick
     */
    public void setPagerClickListener(IPagerClick sliderClick) {
        this.mSliderClick = sliderClick;
    }

    /**
     * 设置图片切换间隔时间 , 间隔时间不能小于1000
     *
     * @param delayTime
     */
    public void setDelayTime(long delayTime) {
        if (delayTime < 1000) {
            this.mDelay = 1000;
        } else {
            this.mDelay = delayTime;
        }
    }

    /**
     * 图片轮播(手动控制自动轮播与否，便于资源控件）
     */
    public void startImageCycle() {
        if (isAutoScroll) {
            isStop = false;
            startImageTimerTask();
        }
    }

    /**
     * 暂停轮播—用于节省资源
     */
    public void pushImageCycle() {
        stopImageTimerTask();
    }

    /**
     * 图片滚动任务
     */
    private void startImageTimerTask() {
        if (mImageTimerTask != null) {
            mHandler.removeCallbacks(mImageTimerTask);
        }
        // 图片滚动
        mHandler.postDelayed(mImageTimerTask, mDelay);
    }

    /**
     * 停止图片滚动任务
     */
    private void stopImageTimerTask() {
        isStop = true;
        mHandler.removeCallbacks(mImageTimerTask);
    }

    public interface IPagerClick {
        void click(int position);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (hasWindowFocus) {
            //不可见的时候关闭计时器轮播
            startImageCycle();
        } else {
            // 可见的时候启动计时器轮播
            pushImageCycle();
        }
    }
}
