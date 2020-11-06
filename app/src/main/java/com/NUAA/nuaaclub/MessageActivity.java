package com.NUAA.nuaaclub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NUAA.nuaaclub.adapter.baseReplyAdapter;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageActivity extends Activity {
    private TextView firstSender;
    private TextView firstText;
    private TextView firstCreateTime;
    private TextView firstReplyCount;
    private ListView msgListView;
    private String fileName;
    private SwipeRefreshLayout msgRefreshView;
    private Button msgReplyBtn;
    private String fromID;
    private String toID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fileName = (String) getIntent().getExtras().get("fileName");
        setContentView(R.layout.activity_message);

        firstSender = (TextView) findViewById(R.id.firstSender);
        firstText = (TextView) findViewById(R.id.firstText);
        firstCreateTime = (TextView) findViewById(R.id.firstCreateTime);
        fromID = MainActivity.sharedPreferences.getString("ID", "");
        //firstReplyCount = (TextView) findViewById(R.id.firstReplyCount);

        //设置页面刷新
        msgListView = (ListView) findViewById(R.id.msgListView);
        msgRefreshView = (SwipeRefreshLayout) findViewById(R.id.msgRefresh);
        msgRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        msgRefreshView.setRefreshing(false);
                    }
                }, 2500);
            }
        });
        msgReplyBtn = findViewById(R.id.msgReplyBtn);
        initData();
        msgReplyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, EditEssayActivity.class);
                intent.putExtra("flag",5);
                intent.putExtra("fileName",fileName);
                intent.putExtra("fromID",fromID);
                intent.putExtra("toID",toID);
                startActivity(intent);
            }
        });
    }

    protected void initData() {
        //准备数据
        //1. 创建请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //2. 创建get请求
        String url = "http://"+getResources().getString(R.string.address)+":8080/LoginDemo/message/"+fileName+".json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    toID = (fromID.equals(jsonObject.getString("fromID"))?jsonObject.getString("toID"):jsonObject.getString("fromID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                loadMsg(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(MessageActivity.this, "网络似乎不通了", Toast.LENGTH_SHORT).show();
            }
        } )
        {
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                response.headers.put("HTTP.CONTENT_TYPE", "utf-8");
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(new String(response.data,"UTF-8"));
                    return Response.success(jsonObject, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    e.printStackTrace();
                    return Response.error(new ParseError(e));
                }
            }
        };
        //5. 将请求添加入请求队列
        requestQueue.add(jsonObjectRequest);
    }
    protected void loadMsg(JSONObject msg) {
        Map<String, Object> map;
        //准备List资源文件
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            //将1L的信息硬写上去
            firstSender.setText(msg.getString("fromCreator"));
            firstText.setText(msg.getString("text"));
            firstCreateTime.setText(msg.getString("createDate").substring(5,16));
            JSONArray replyArray = (JSONArray)msg.get("replyList");

            for (int i = 0; i < replyArray.length(); i++) {
                JSONObject reply = (JSONObject) replyArray.get(i);
                //准备每一项的资源文件
                map = new HashMap<String, Object>();
                map.put("creator", reply.getString("creator"));//回复创建者
                map.put("text", reply.getString("text"));//回复的文本
                map.put("createDate", reply.getString("createDate").substring(5, 16));//回复的时间
                map.put("floor",0);
                map.put("ID",reply.getString("userID"));
                map.put("isMessage", 1);//表明这是一条私信的回复
                //这条信息在之后使用
                list.add(map);
            }
            final baseReplyAdapter baseAdap = new baseReplyAdapter(MessageActivity.this);
            baseAdap.setList(list);
            msgListView.setAdapter(baseAdap);
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
