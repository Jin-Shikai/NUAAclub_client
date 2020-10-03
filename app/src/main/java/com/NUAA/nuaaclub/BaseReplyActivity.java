package com.NUAA.nuaaclub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NUAA.nuaaclub.adapter.baseReplyAdapter;
import com.NUAA.nuaaclub.adapter.essayFragmentAdapter;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseReplyActivity extends Activity {
    private TextView firstSender;
    private TextView firstText;
    private TextView firstCreateTime;
    private TextView firstReplyCount;
    private ListView mEassyListView;
    private String essayID;
    private int floor;
    private SwipeRefreshLayout mEssayRefreshView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        floor = (int) getIntent().getExtras().get("floor");
        essayID = (String) getIntent().getExtras().get("essayID");

        setContentView(R.layout.activity_basereply);

        firstSender = (TextView) findViewById(R.id.firstSender);
        firstText = (TextView) findViewById(R.id.firstText);
        firstCreateTime = (TextView) findViewById(R.id.firstCreateTime);
        firstReplyCount = (TextView) findViewById(R.id.firstReplyCount);

        //设置页面刷新
        mEassyListView = (ListView) findViewById(R.id.baseReplyListView);
        mEssayRefreshView = (SwipeRefreshLayout) findViewById(R.id.baseReplyRefresh);
        mEssayRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        mEssayRefreshView.setRefreshing(false);
                    }
                }, 2500);
            }
        });
        initData();
    }

    protected void initData() {
        //准备数据
        //1. 创建请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //2. 创建get请求
        String url = "http://192.168.1.100:8080/LoginDemo/essay/"+essayID+".json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {
                    loadFloor((JSONObject) jsonObject.getJSONArray("replyList").get(floor));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(BaseReplyActivity.this, "网络似乎不通了", Toast.LENGTH_SHORT).show();
            }
        } )
        {
            @Override
            protected Response<org.json.JSONObject> parseNetworkResponse(NetworkResponse response) {
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
    protected void loadFloor(JSONObject floor) {
        Map<String, Object> map;
        //准备List资源文件
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            //将1L的信息硬写上去
            firstSender.setText(floor.getString("creator"));
            firstText.setText(floor.getString("text"));
            firstCreateTime.setText(floor.getString("createDate").substring(5,16));

            JSONArray replyArray = (JSONArray)floor.get("BaseReplyList");

            for (int i = 0; i < replyArray.length(); i++) {
                JSONObject reply = (JSONObject) replyArray.get(i);
                //准备每一项的资源文件
                map = new HashMap<String, Object>();
                map.put("creator", reply.getString("creator"));//回复创建者
                map.put("text", reply.getString("text"));//回复的文本
                map.put("createDate", reply.getString("createDate").substring(5, 16));//回复的时间
                map.put("floor",0);
                map.put("ID",reply.getString("userID"));
                //map.put("replyStatus", reply.get("replyStatus"));//该回复的状态,"1"正常  "0"删除
                //这条信息在之后使用
                list.add(map);
            }
            final baseReplyAdapter baseAdap = new baseReplyAdapter(BaseReplyActivity.this);
            baseAdap.setList(list);
            mEassyListView.setAdapter(baseAdap);
        } catch (NullPointerException n) {
            n.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
