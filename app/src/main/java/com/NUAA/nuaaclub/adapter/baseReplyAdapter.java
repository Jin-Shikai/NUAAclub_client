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
import com.NUAA.nuaaclub.StringRequestOverride.StringRequestWithToken;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class baseReplyAdapter extends BaseAdapter {

    //数据列表
    List<Map<String,Object>> list;
    //反射器
    LayoutInflater inflater;

    public baseReplyAdapter(Context context) {
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
        replyFloor.setText(map.get("baseFloor").toString());

        final String essayID = map.get("essayID").toString();
        final String floorNum = map.get("floor").toString();
        final String baseFloor = map.get("baseFloor").toString();

        //为自己的回复设置操作可见
        final String ID = sharedPreferences.getString("ID","");//自己的ID
        if(map.get("ID").toString().equals(ID))//与这条回复的发送者的ID相同
            deleteButton.setVisibility(View.VISIBLE);
        //为发送者的ID按钮设置监听
        creator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, EditEssayActivity.class);
                intent.putExtra("fromID", ID);
                intent.putExtra("toID", map.get("ID").toString());
                intent.putExtra("toCreator", map.get("creator").toString());
                intent.putExtra("flag",4);
                mContext.startActivity(intent);
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                String url = "http://"+mContext.getResources().getString(R.string.address)+":8080/LoginDemo/deleteBaseReply";
                StringRequest stringRequest = new StringRequestWithToken(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Toast.makeText(mContext, "已删除", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(mContext, "网络似乎不通了", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("essayID",essayID);
                        map.put("floor",floorNum);
                        map.put("baseFloor",baseFloor);
                        return map;
                    }
                };
                //3. 将请求添加入请求队列
                requestQueue.add(stringRequest);
            }
        });

        replyButton.setVisibility(View.INVISIBLE);
        return view;
    }
}
