package com.lee.customview.guideview;

/**
 * Created by LeeJing on 2018/7/8.
 * 引导页整体状态变化监听
 */
interface OnGuideChangedListener {
    void onShowed(GuideController guideController);

    void onRemoved(GuideController guideController);
}
