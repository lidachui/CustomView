package com.lee.customview.guideview;

import android.app.Activity;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


import com.lee.customview.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by LeeJing on 2018/3/30.
 */

public class GuideController {
	
	private static final String TAG = "GuideController";
	
	private List<GuidePage> mGuidePages = new ArrayList<>();
	private GuideLayout mCurrentGuideLayout;
	
	private int currentIndex;
	private String label;
	
	private Activity activity;
	private SharedPreferences sp;
	private boolean isAlwaysShow;
	private FrameLayout mParentView;
	
	private int topMargin;//statusBar的高度
	
	private OnPageChangedListener onPageChangedListener;
	private OnGuideChangedListener onGuideChangedListener;
	
	private GuideController(Activity activity, List<GuidePage> pages, String label, boolean isAlwaysShow, OnGuideChangedListener onGuideChangedListener, OnPageChangedListener onPageChangedListener) {
		this.activity = activity;
		this.label = label;
		this.isAlwaysShow = isAlwaysShow;
		this.onGuideChangedListener = onGuideChangedListener;
		this.onPageChangedListener = onPageChangedListener;
		this.mGuidePages = pages;
		mParentView = (FrameLayout) activity.getWindow().getDecorView();
		sp = activity.getSharedPreferences(TAG, Activity.MODE_PRIVATE);
	}
	
	public void show() {
		// 判断是否每次都显示
		if (!isAlwaysShow) {
			boolean showed = sp.getBoolean(label, false);
			if (showed) {
				return;
			}
		}
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED, WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
		
		mParentView.post(new Runnable() {
			@Override
			public void run() {
				getTopMargin();
				if (mGuidePages == null || mGuidePages.size() == 0) {
					throw new IllegalStateException("there is no guide to show!! Please add alast one Page.");
				}
				currentIndex = 0;
				showGuidePage();
				if (onGuideChangedListener != null) {
					onGuideChangedListener.onShowed(GuideController.this);
				}
				sp.edit().putBoolean(label, true).apply();
			}
		});
	}
	
	private void showGuidePage() {
		GuidePage page = mGuidePages.get(currentIndex);
		GuideLayout guideLayout = new GuideLayout(activity);
		guideLayout.setGuidePage(page);
		addCustomToLayout(guideLayout, page);
		guideLayout.setOnGuideLayoutDismissListener(new GuideLayout.OnGuideLayoutDismissListener() {
			@Override
			public void onGuideLayoutDismiss(GuideLayout guideLayout) {
				//跳转下一页
				if (currentIndex < mGuidePages.size() - 1) {
					currentIndex++;
					showGuidePage();
					if (onPageChangedListener != null) {
						onPageChangedListener.onPageChanged(currentIndex);
					}
				} else {
					if (onGuideChangedListener != null) {
						onGuideChangedListener.onRemoved(GuideController.this);
					}
				}
			}
		});
		mParentView.addView(guideLayout, new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
		mCurrentGuideLayout = guideLayout;
	}
	
	private void addCustomToLayout(final GuideLayout guideLayout, GuidePage guidePage) {
		guideLayout.removeAllViews();
		int layoutResId = guidePage.getLayoutResId();
		if (layoutResId != 0) {
			View view = LayoutInflater.from(activity).inflate(layoutResId, guideLayout, false);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
			params.topMargin = topMargin;
			params.bottomMargin = ScreenUtils.getNavigationBarHeight(activity);
			int[] viewIds = guidePage.getClickToDismissIds();
			if (viewIds != null && viewIds.length > 0) {
				for (int viewId : viewIds) {
					View click = view.findViewById(viewId);
					if (click != null) {
						click.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								guideLayout.remove();
							}
						});
					} else {
						Log.e(TAG, "can't find the view by id : " + viewId + " which used to remove guide page");
					}
				}
			}
			//layout加载完成后，调用page加载完成的接口
			GuidePage.OnLayoutInflatedListener inflatedListener = guidePage.getOnLayoutInflatedListener();
			if (inflatedListener != null) {
				inflatedListener.onLayoutInflated(view);
			}
			guideLayout.addView(view, params);
		}
	}
	
	private void getTopMargin() {
		final View contentView = activity.findViewById(android.R.id.content);
		int[] location = new int[2];
		contentView.getLocationOnScreen(location);
		topMargin = location[1];
		Log.i(TAG, "contentView top:" + topMargin);
	}
	
	/**
	 * 清除"显示过"的标记
	 */
	public void resetLabel() {
		resetLabel(label);
	}
	
	public void resetLabel(String label) {
		sp.edit().putBoolean(label, false).apply();
	}
	
	/**
	 * 中断引导层的显示，后续未显示的page将不再显示
	 */
	public void remove() {
		if (mCurrentGuideLayout != null && mCurrentGuideLayout.getParent() != null) {
			((ViewGroup) mCurrentGuideLayout.getParent()).removeView(mCurrentGuideLayout);
			if (onGuideChangedListener != null) {
				onGuideChangedListener.onRemoved(this);
			}
		}
	}
	
	
	public static class Builder {
		private Activity activity;
		private String label;
		private boolean alwaysShow;
		private List<GuidePage> guidePages = new ArrayList<>();
		
		private OnGuideChangedListener onGuideChangedListener;
		private OnPageChangedListener onPageChangedListener;
		
		public Builder(Activity activity) {
			this.activity = activity;
		}
		
		/**
		 * 是否总是显示引导层
		 */
		public Builder alwaysShow(boolean b) {
			this.alwaysShow = b;
			return this;
		}
		
		/**
		 * 添加引导页
		 */
		public Builder addGuidePage(GuidePage page) {
			guidePages.add(page);
			return this;
		}
		
		/**
		 * 设置引导层隐藏，显示监听
		 */
		public Builder setOnGuideChangedListener(OnGuideChangedListener listener) {
			onGuideChangedListener = listener;
			return this;
		}
		
		/**
		 * 设置引导页切换监听
		 */
		public Builder setOnPageChangedListener(OnPageChangedListener onPageChangedListener) {
			this.onPageChangedListener = onPageChangedListener;
			return this;
		}
		
		/**
		 * 设置引导层的辨识名，必须设置项，否则报错
		 */
		public Builder setLabel(String label) {
			this.label = label;
			return this;
		}
		
		/**
		 * 构建引导层controller
		 *
		 * @return controller
		 */
		public GuideController build() {
			checkAndSaveAsPage();
			GuideController controller = new GuideController(activity, guidePages, label, alwaysShow, onGuideChangedListener, onPageChangedListener);
			return controller;
		}
		
		private void checkAndSaveAsPage() {
			if (TextUtils.isEmpty(label)) {
				throw new IllegalArgumentException("the param 'label' is missing, please call setLabel()");
			}
			if (activity == null) {
				throw new IllegalStateException("activity is null, please make sure that fragment is showing when call NewbieGuide");
			}
		}
	}
	
}
