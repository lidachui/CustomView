package com.lee.customview.badgeview;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TabWidget;

/**
 * Created by LeeJing on 2018/2/7.
 */

public class BadgeView extends android.support.v7.widget.AppCompatTextView {
	public static final int POSITION_TOP_LEFT = 1;
	public static final int POSITION_TOP_RIGHT = 2;
	public static final int POSITION_BOTTOM_LEFT = 3;
	public static final int POSITION_BOTTOM_RIGHT = 4;
	public static final int POSITION_CENTER = 5;
	
	private static final int DEFAULT_POSITION = POSITION_TOP_RIGHT;
	private static final int DEFAULT_BADGE_COLOR = Color.parseColor("#CCFF0000"); //Color.RED;
	private static final int DEFAULT_MARGIN_DIP = 5;
	private static final int DEFAULT_LR_PADDING_DIP = 5;
	private static final int DEFAULT_TEXT_COLOR = Color.WHITE;
	private static final int DEFAULT_CORNER_RADIUS_DIP = 8;
	
	public BadgeView(Context context) {
		this(context, (AttributeSet) null);
	}
	
	public BadgeView(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.textViewStyle);
	}
	
	public BadgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		this(context, attrs, defStyleAttr, null, 0);
	}
	
	public BadgeView(Context context, View target) {
		this(context, null, android.R.attr.textViewStyle, target, 0);
	}
	
	public BadgeView(Context context, View target, int index) {
		this(context, null, android.R.attr.textViewStyle, target, index);
	}
	
	public BadgeView(Context context, AttributeSet attrs, int defStyle, View target, int tabIndex) {
		super(context, attrs, defStyle);
		init(context, target, tabIndex);
	}
	
	private Context mContext;
	private View mTargetView;
	private int mTabIndex;
	
	private int mBadgePosition;
	private int mBadgeMarginH;
	private int mBadgeMarginV;
	private int mBadgeColor;
	
	private static Animation mFadeIn;
	private static Animation mFadeOut;
	
	private boolean mIsShown;
	private ShapeDrawable badgeBg;
	
	private void init(Context context, View target, int tabIndex) {
		this.mContext = context;
		this.mTargetView = target;
		this.mTabIndex = tabIndex;
		// apply defaults
		mBadgePosition = DEFAULT_POSITION; //位置
		mBadgeMarginH = dipToPixels(DEFAULT_MARGIN_DIP); //marginH
		mBadgeMarginV = mBadgeMarginH; //marginV
		mBadgeColor = DEFAULT_BADGE_COLOR; //back ground color
		setTypeface(Typeface.DEFAULT_BOLD); //text typeface
		int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
		setPadding(paddingPixels, 0, paddingPixels, 0);
		setTextColor(DEFAULT_TEXT_COLOR);// text color
		//animation
		mFadeIn = new AlphaAnimation(0, 1);
		mFadeIn.setInterpolator(new DecelerateInterpolator());
		mFadeIn.setDuration(200);
		mFadeOut = new AlphaAnimation(1, 0);
		mFadeOut.setInterpolator(new AccelerateInterpolator());
		mFadeOut.setDuration(200);
		// show flag
		mIsShown = false;
		if (mTargetView != null) {
			applyTo(mTargetView);
		} else {
			show();
		}
	}
	
	private void applyTo(View target) {
		ViewGroup.LayoutParams lp = target.getLayoutParams();
		ViewParent parent = target.getParent();
		FrameLayout container = new FrameLayout(mContext);
		if (target instanceof TabWidget) {
			// set target to the relevant tab child container
			target = ((TabWidget) target).getChildTabViewAt(mTabIndex);
			mTargetView = target;
			
			((ViewGroup) target).addView(container,
					new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
			this.setVisibility(View.GONE);
			container.addView(this);
		} else {
			ViewGroup group = (ViewGroup) parent;
			int index = group.indexOfChild(target);
			group.removeView(target);
			group.addView(container, index, lp);
			container.addView(target);
			this.setVisibility(View.GONE);
			container.addView(this);
			group.invalidate();
		}
	}
	
	public void show() {
		show(false, null);
	}
	
	public void show(boolean animate) {
		show(animate, mFadeIn);
	}
	
	public void show(Animation animation) {
		show(true, animation);
	}
	
	private void show(boolean isShowAnimation, Animation animation) {
		if (getBackground() == null) {
			if (badgeBg == null) {
				badgeBg = getDefaultBackground();
			}
			setBackgroundDrawable(badgeBg);
		}
		applyLayoutParams();
		
		if (isShowAnimation) {
			this.startAnimation(animation);
		}
		this.setVisibility(View.VISIBLE);
		mIsShown = true;
	}
	
	public void hide() {
		hide(false, null);
	}
	
	public void hide(boolean animate) {
		hide(animate, mFadeOut);
	}
	
	public void hide(Animation animation) {
		hide(true, animation);
	}
	
	private void hide(boolean isShowAnimation, Animation animation) {
		this.setVisibility(View.GONE);
		if (isShowAnimation) {
			this.startAnimation(animation);
		}
		mIsShown = false;
	}
	
	private void applyLayoutParams() {
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		switch (mBadgePosition) {
			case POSITION_TOP_LEFT:
				lp.gravity = Gravity.LEFT | Gravity.TOP;
				lp.setMargins(mBadgeMarginH, mBadgeMarginV, 0, 0);
				break;
			case POSITION_TOP_RIGHT:
				lp.gravity = Gravity.RIGHT | Gravity.TOP;
				lp.setMargins(0, mBadgeMarginV, mBadgeMarginH, 0);
				break;
			case POSITION_BOTTOM_LEFT:
				lp.gravity = Gravity.LEFT | Gravity.BOTTOM;
				lp.setMargins(mBadgeMarginH, 0, 0, mBadgeMarginV);
				break;
			case POSITION_BOTTOM_RIGHT:
				lp.gravity = Gravity.RIGHT | Gravity.BOTTOM;
				lp.setMargins(0, 0, mBadgeMarginH, mBadgeMarginV);
				break;
			case POSITION_CENTER:
				lp.gravity = Gravity.CENTER;
				lp.setMargins(0, 0, 0, 0);
				break;
			default:
				break;
		}
		setLayoutParams(lp);
	}
	
	private ShapeDrawable getDefaultBackground() {
		ArcShape arcShape = new ArcShape(0, 360);
		ShapeDrawable drawable = new ShapeDrawable(arcShape);
		drawable.getPaint().setColor(mBadgeColor);
		return drawable;
	}
	
	public void setTargetView(View targetView) {
		mTargetView = targetView;
	}
	
	public void setBadgePosition(int badgePosition) {
		mBadgePosition = badgePosition;
	}
	
	public void setHorizontalBadgeMargin(int badgeMarginH) {
		mBadgeMarginH = badgeMarginH;
	}
	
	public void setVerticalBadgeMargin(int badgeMarginV) {
		mBadgeMarginV = badgeMarginV;
	}
	
	public void setBadgeColor(int badgeColor) {
		mBadgeColor = badgeColor;
	}
	
	private int dipToPixels(int dip) {
		Resources r = getResources();
		float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
		return (int) px;
	}
}
