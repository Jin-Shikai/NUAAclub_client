package com.NUAA.nuaaclub.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.NUAA.nuaaclub.EditEssayActivity;
import com.NUAA.nuaaclub.EssayActivity;
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
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public SharedPreferences sharedPreferences;
    protected Context mContext;

    @Override
    public View getView(final int position, View converView, ViewGroup parent)
    {
        View view=inflater.inflate(R.layout.replyitem,null);
        Button creator=(Button) view.findViewById(R.id.essaySender);
        TextView text=(TextView) view.findViewById(R.id.essayContent);
        TextView createDate=(TextView) view.findViewById(R.id.createTime_New);
        TextView replyFloor=(TextView) view.findViewById(R.id.replyFloor);
        Button deleteButton=(Button) view.findViewById(R.id.deleteBtn);
        Button replyButton=(Button) view.findViewById(R.id.replyBtn);
        mContext=inflater.getContext();
        //创建一个SharedPreferences对象
        sharedPreferences = MainActivity.sharedPreferences;

        final Map map=list.get(position);
        creator.setText(map.get("creator").toString());
        text.setText(map.get("text").toString());
        createDate.setText(map.get("createDate").toString());
        replyFloor.setText(map.get("floor").toString());

        //为自己的回复设置操作可见
        String ID = sharedPreferences.getString("ID","");
        if(map.get("ID").toString().equals(ID))
            deleteButton.setVisibility(View.VISIBLE);
        //为发送者的ID按钮设置监听
        creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,map.get("creator").toString(), Toast.LENGTH_SHORT).show();
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,"删除".toString(), Toast.LENGTH_SHORT).show();
            }
        });
        replyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token=sharedPreferences.getString("token","");
                if(token.length()<5) {
                    Toast.makeText(mContext, "请先登录", Toast.LENGTH_SHORT).show();
                }
                else {
                    String essayID=sharedPreferences.getString("nowEssayID","");
                    String essayCreateDate = sharedPreferences.getString("essayCreateDate","");
                    Intent intent = new Intent(inflater.getContext(), EditEssayActivity.class);
                    intent.putExtra("flag", 3);
                    intent.putExtra("essayID", essayID);
                    intent.putExtra("floor", map.get("floor").toString());
                    intent.putExtra("essayCreateDate",essayCreateDate);
                    mContext.startActivity(intent);
                }
            }
        });
        return view;
    }
}
