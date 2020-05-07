package com.NUAA.nuaaclub.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.NUAA.nuaaclub.base.BaseFragment;

public class messageFragment extends BaseFragment {
    private static final String TAG = messageFragment.class.getSimpleName();//得到类名称
    private TextView textView;
    @Override
    protected View initView() {
        Log.e(TAG,"message页面已初始化");
        textView=new TextView(mContext);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    protected void initData() {
        super.initData();
        Log.e(TAG,"私信页面");
        textView.setText("私信信息");
    }
}
