package com.lee.customview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;


public class ScreenUtils {
	
	private static final String TAG = "ScreenUtils";
	
	private ScreenUtils() {
		throw new AssertionError();
	}
	
	/**
	 * 获取屏幕宽度
	 */
	public static int getScreenWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}
	
	/**
	 * 获取屏幕高度
	 */
	public static int getScreenHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}
	
	/**
	 * 获取状态栏高度
	 */
	public static int getStatusBarHeight(Context context) {
		int height = DesityUtil.dip2px(context, 20);
		Log.d(TAG, "common statusBar height:" + height);
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			height = context.getResources().getDimensionPixelSize(resourceId);
			Log.d(TAG, "real statusBar height:" + height);
		}
		Log.d(TAG, "finally statusBar height:" + height);
		return height;
	}
	
	/**
	 * 虚拟操作拦（home等）是否显示
	 */
	public static boolean isNavigationBarShow(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Display display = activity.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			Point realSize = new Point();
			display.getSize(size);
			display.getRealSize(realSize);
			return realSize.y != size.y;
		} else {
			boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
			boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
			if (menu || back) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	/**
	 * 获取虚拟操作拦（home等）高度
	 */
	public static int getNavigationBarHeight(Activity activity) {
		if (!isNavigationBarShow(activity))
			return 0;
		int height = 0;
		Resources resources = activity.getResources();
		//获取NavigationBar的高度
		int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
		if (resourceId > 0)
			height = resources.getDimensionPixelSize(resourceId);
		Log.d(TAG, "NavigationBar的高度:" + height);
		return height;
	}
}
