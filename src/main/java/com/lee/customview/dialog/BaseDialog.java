package com.lee.customview.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import com.lee.customview.utils.ScreenUtils;

/**
 * Created by LeeJing on 2018/7/9.
 * 
 */
public abstract class BaseDialog<V extends View> implements DialogInterface.OnKeyListener, DialogInterface.OnDismissListener {
    
    public static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
    protected Context context;
    protected int screenWidthPixels;
    protected int screenHeightPixels;
    private Dialog dialog;
    private FrameLayout contentLayout;
    private boolean isPrepared = false;

    public BaseDialog(Context activity) {
        this.context = activity;
        screenWidthPixels = ScreenUtils.getScreenWidth(activity);
        screenHeightPixels = ScreenUtils.getScreenHeight(activity);
        initDialog();
    }

    private void initDialog() {
        contentLayout = new FrameLayout(context);
        contentLayout.setLayoutParams(new ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        contentLayout.setFocusable(true);
        contentLayout.setFocusableInTouchMode(true);
        //contentLayout.setFitsSystemWindows(true);
        dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);//触摸屏幕取消窗体
        dialog.setCancelable(false);//按返回键取消窗体
        dialog.setOnKeyListener(this);
        dialog.setOnDismissListener(this);
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //AndroidRuntimeException: requestFeature() must be called before adding content
            window.requestFeature(Window.FEATURE_NO_TITLE);
            window.setContentView(contentLayout);
        }
        setSize(screenWidthPixels, WRAP_CONTENT);
    }
    
    public boolean onBackPress() {
        dismiss();
        return false;
    }

    @Override
    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPress();
        }
        return false;
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        dismiss();
    }

    /**
     * 设置弹窗的宽和高
     *
     * @param width  宽
     * @param height 高
     */
    public void setSize(int width, int height) {
        if (width == MATCH_PARENT) {
            //360奇酷等手机对话框MATCH_PARENT时两边还会有边距，故强制填充屏幕宽
            width = screenWidthPixels;
        }
        if (width == 0 && height == 0) {
            width = screenWidthPixels;
            height = WRAP_CONTENT;
        } else if (width == 0) {
            width = screenWidthPixels;
        } else if (height == 0) {
            height = WRAP_CONTENT;
        }
        ViewGroup.LayoutParams params = contentLayout.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(width, height);
        } else {
            params.width = width;
            params.height = height;
        }
        contentLayout.setLayoutParams(params);
    }

    public int getScreenWidthPixels() {
        return screenWidthPixels;
    }

    public int getScreenHeightPixels() {
        return screenHeightPixels;
    }

    /**
     * 创建弹窗的内容视图
     *
     * @return the view
     */
    protected abstract V makeContentView();

    /**
     * dialog消失
     */
    public void dismiss() {
        dismissImmediately();
    }

    protected final void dismissImmediately() {
        dialog.dismiss();
    }

    /**
     * 弹框的内容视图
     */
    public View getContentView() {
        // IllegalStateException: The specified child already has a parent.
        // You must call removeView() on the child's parent first.
        return contentLayout.getChildAt(0);
    }


    /**
     * 弹框的根视图
     */
    public ViewGroup getRootView() {
        return contentLayout;
    }

    /**
     * show
     */
    public final void show() {
        if (isPrepared) {
            dialog.show();
            showAfter();
            return;
        }
        setContentViewBefore();
        V view = makeContentView();
        setContentView(view);// 设置弹出窗体的布局
        setContentViewAfter(view);
        isPrepared = true;
        dialog.show();
        showAfter();
    }

    /**
     * 显示后执行
     */
    protected void showAfter() {
    }

    /**
     * 设置弹窗的内容视图之前执行
     */
    protected void setContentViewBefore() {
    }

    /**
     * 设置弹窗的内容视图之后执行
     *
     * @param contentView 弹窗的内容视图
     */
    protected void setContentViewAfter(V contentView) {
    }

    public void setContentView(View view) {
        contentLayout.removeAllViews();
        contentLayout.addView(view);
    }
}
