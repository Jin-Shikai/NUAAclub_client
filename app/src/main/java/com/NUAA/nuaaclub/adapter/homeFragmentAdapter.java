package com.NUAA.nuaaclub.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.NUAA.nuaaclub.base.BaseFragment;

import org.w3c.dom.Text;

import java.util.List;

public class homeFragmentAdapter extends BaseAdapter {
    private String mDatas[];
    private Context mContext;
    public homeFragmentAdapter(Context context,String[] datas)
    {
        this.mContext=context;
        this.mDatas=datas;
    }

    @Override
    public int getCount() {
        return mDatas.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent)
    {
        TextView textView = new TextView(mContext);
        textView.setTextColor(Color.BLACK);
        textView.setPadding(10,10,0,10);
        textView.setTextSize(20);
        textView.setText(mDatas[position]);
        return textView;
    }
}
