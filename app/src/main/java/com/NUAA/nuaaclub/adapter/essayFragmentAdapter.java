package com.NUAA.nuaaclub.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.NUAA.nuaaclub.MainActivity;
import com.NUAA.nuaaclub.R;
import com.NUAA.nuaaclub.base.BaseFragment;

import java.util.List;
import java.util.Map;

public class essayFragmentAdapter extends BaseAdapter {

    //数据列表
    List<Map<String,Object>> list;
    //反射器
    LayoutInflater inflater;

    public essayFragmentAdapter(Context context) {
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
        View view=inflater.inflate(R.layout.replyitem,null);
        Button creator=(Button) view.findViewById(R.id.essaySender);
        TextView text=(TextView) view.findViewById(R.id.essayContent);
        TextView createDate=(TextView) view.findViewById(R.id.createTime_New);
        TextView replyFloor=(TextView) view.findViewById(R.id.replyFloor);
        Button operate=(Button) view.findViewById(R.id.operate);
        final Map map=list.get(position);
        creator.setText(map.get("creator").toString());
        text.setText(map.get("text").toString());
        createDate.setText(map.get("createDate").toString());
        replyFloor.setText(map.get("floor").toString());

        //为自己的回复设置操作可见
        String ID = MainActivity.sharedPreferences.getString("ID","");
        if(map.get("ID").toString().equals(ID))
            operate.setVisibility(View.VISIBLE);
        //为发送者的ID按钮设置监听
        creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(inflater.getContext(),map.get("creator").toString(), Toast.LENGTH_SHORT).show();
            }
        });
        operate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(inflater.getContext(),"删除".toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
