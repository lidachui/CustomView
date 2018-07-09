package com.lee.customview.guideview;

import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.animation.Animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by LeeJing on 2018/3/30.
 * 引导页
 */

public class GuidePage {
	
	private List<HighLight> mHighLightList = new ArrayList<>();
	private boolean clickAnywhereCanceled = true;
	private int backgroundColor;
	private int layoutResId;
	private int[] clickDismissIds;
	private OnLayoutInflatedListener mOnLayoutInflatedListener;
	private Animation enterAnimation;
	private Animation exitAnimation;
	
	public static GuidePage newInstance() {
		return new GuidePage();
	}
	
	private GuidePage() {
	}
	
	public GuidePage addHighLight(View view) {
		return addHighLight(view, HighLight.Shape.RECTANGLE, 0, 0);
	}
	
	public GuidePage addHighLight(View view, HighLight.Shape shape) {
		return addHighLight(view, shape, 0, 0);
	}
	
	public GuidePage addHighLight(View view, HighLight.Shape shape, int padding) {
		return addHighLight(view, shape, 0, padding);
	}
	
	/**
	 * @param view
	 * @param shape
	 * @param round
	 * @param padding
	 * @return
	 */
	public GuidePage addHighLight(View view, HighLight.Shape shape, int round, int padding) {
		mHighLightList.add(HighLight.newInstance(view)
				.setShape(shape)
				.setRound(round)
				.setPadding(padding));
		return this;
	}
	
	public GuidePage addHighLight(HighLight... highLights) {
		mHighLightList.addAll(Arrays.asList(highLights));
		return this;
	}
	
	public GuidePage addHighLight(List<HighLight> list) {
		mHighLightList.addAll(list);
		return this;
	}
	
	/**
	 * 添加引导层布局
	 *
	 * @param resId 布局id
	 * @param id    布局中点击消失引导页的控件id
	 */
	public GuidePage setLayoutRes(@LayoutRes int resId, int... id) {
		this.layoutResId = resId;
		clickDismissIds = id;
		return this;
	}
	
	/**
	 * 设置点击任何地方都可以消失
	 *
	 * @param everywhereCancelable
	 * @return
	 */
	public GuidePage setEverywhereCancelable(boolean everywhereCancelable) {
		this.clickAnywhereCanceled = everywhereCancelable;
		return this;
	}
	
	/**
	 * 设置背景色，一般设置半透明
	 */
	public GuidePage setBackgroundColor(@ColorInt int backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}
	
	/**
	 * 设置引导层布局加载完成监听
	 * 用于动态设置一些View的显示数据
	 *
	 * @param listener
	 * @return
	 */
	public GuidePage setOnLayoutInflatedListener(OnLayoutInflatedListener listener) {
		this.mOnLayoutInflatedListener = listener;
		return this;
	}
	
	/**
	 * 设置进入动画
	 */
	public GuidePage setEnterAnimation(Animation enterAnimation) {
		this.enterAnimation = enterAnimation;
		return this;
	}
	
	/**
	 * 设置退出动画
	 */
	public GuidePage setExitAnimation(Animation exitAnimation) {
		this.exitAnimation = exitAnimation;
		return this;
	}
	
	public boolean isEverywhereCancelable() {
		return clickAnywhereCanceled;
	}
	
	public boolean isEmpty() {
		return layoutResId == 0 && mHighLightList.size() == 0;
	}
	
	public List<HighLight> getHighLights() {
		return mHighLightList;
	}
	
	public int getBackgroundColor() {
		return backgroundColor;
	}
	
	public int getLayoutResId() {
		return layoutResId;
	}
	
	public int[] getClickToDismissIds() {
		return clickDismissIds;
	}
	
	public Animation getEnterAnimation() {
		return enterAnimation;
	}
	
	public Animation getExitAnimation() {
		return exitAnimation;
	}
	
	public OnLayoutInflatedListener getOnLayoutInflatedListener() {
		return mOnLayoutInflatedListener;
	}
	
	
	public interface OnLayoutInflatedListener {
		void onLayoutInflated(View view);
	}
	
	
}
