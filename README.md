# Android-AutoSwitchViewPager
参考网上的资料，实现了一个自动切换的Viewpager控件，可用作App首页广告轮播使用。

当前版本：1.0

---

该控件实现主要考虑到以下几点：
* 实现真正的无限循环，通过在前后各加一张图片的形式
* 圆圈指示器跟着图片同步变化
* 通过Glide实现图片的加载
* 解决手势跟滑动跟自动滑动的冲突
* 页面不可见时，自动滑动停止，页面重新可见时，自动滑动开始。

---

![image](https://github.com/lie128/Android-AutoSwitchViewPager/blob/master/preview/preview.gif)

