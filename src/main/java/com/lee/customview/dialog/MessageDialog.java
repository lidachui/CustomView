package com.lee.customview.dialog;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lee.customview.R;

/**
 * Created by LeeJing on 2018/7/9.
 */
public class MessageDialog extends ConfirmDialog{
    
    private View mView;
    private CharSequence mMessageText;
    private ButtonListener mListener;
    
    public MessageDialog(Activity activity) {
        super(activity);
    }

    @NonNull
    @Override
    protected View makeCenterView() {
        mView = LayoutInflater.from(context).inflate(R.layout.message_layout_view, null);
        mView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        init();
        return mView;
    }

    private void init() {
        TextView msgTextView = mView.findViewById(R.id.message_text_view);
        if (mMessageText != null) {
            msgTextView.setText(mMessageText);
        }
    }

    public void setMessage(CharSequence msg) {
        mMessageText = msg;
    }
    
    public void setMessage(int msgRes) {
        mMessageText = context.getString(msgRes);
    }

    @Override
    protected void onSubmit() {
        super.onSubmit();
        if (mListener != null) {
            mListener.onConfirm();
        }
    }

    @Override
    protected void onCancel() {
        super.onCancel();
        if (mListener != null) {
            mListener.onCancel();
        }
    }
    
    public interface ButtonListener {
        void onConfirm();
        
        void onCancel();
    }
}
