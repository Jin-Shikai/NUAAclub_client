package com.NUAA.nuaaclub.adapter;

import android.content.Context;

import com.NUAA.nuaaclub.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.*;

public class homeFragmentAdapter extends BaseAdapter {

    //数据列表
    List<Map<String,Object>> list;
    //反射器
    LayoutInflater inflater;

    public homeFragmentAdapter(Context context) {
        this.inflater=LayoutInflater.from(context);
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).get("essayID");
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent)
    {
        View view=inflater.inflate(R.layout.essayitem,null);
        TextView creator=(TextView) view.findViewById(R.id.essaySender);
        TextView text=(TextView) view.findViewById(R.id.essayContent);
        TextView createDate=(TextView) view.findViewById(R.id.createTime_New);
        TextView replyCount=(TextView) view.findViewById(R.id.replyCount);

        Map map=list.get(position);
        creator.setText(map.get("creator").toString().substring(3,8));
        text.setText(map.get("text").toString());
        createDate.setText(map.get("createDate").toString().substring(5,16));
        replyCount.setText(map.get("replyCount").toString());

        return view;
    }
}
