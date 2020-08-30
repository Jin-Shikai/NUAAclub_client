package com.NUAA.nuaaclub;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NUAA.nuaaclub.adapter.essayFragmentAdapter;
import com.NUAA.nuaaclub.adapter.homeFragmentAdapter;
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

public class EssayActivity extends Activity {
    private String mEssayID;
    private ListView mEassyListView;
    private SwipeRefreshLayout mEssayRefreshView;
    private Map<String, Object> mEssayBaseInfo=new HashMap<String, Object>();

    private TextView firstSender;
    private TextView firstText;
    private TextView firstCreateTime;
    private TextView firstReplyCount;
    private Button makeReplayBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_essay);
        mEssayID = (String)getIntent().getExtras().get("essayID");
        firstSender=(TextView)findViewById(R.id.firstSender);
        firstText=(TextView)findViewById(R.id.firstText);
        firstCreateTime=(TextView)findViewById(R.id.firstCreateTime);
        firstReplyCount=(TextView)findViewById(R.id.firstReplyCount);
        makeReplayBtn=(Button)findViewById(R.id.makeReplyBtn);

        //设置页面刷新
        mEassyListView=(ListView)findViewById(R.id.essayListView);
        mEssayRefreshView=(SwipeRefreshLayout)findViewById(R.id.essayrefresh);
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
        makeReplayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EssayActivity.this,EditEssayActivity.class);
                intent.putExtra("flag",2);
                intent.putExtra("essayID",mEssayID);
                intent.putExtra("essayCreateDate",(String)mEssayBaseInfo.get("createDate"));
                startActivity(intent);
            }
        });
    }

    protected void initData() {
        //准备数据
        //1. 创建请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //2. 创建get请求
        String url = "http://192.168.1.37:8080/LoginDemo/essay/"+mEssayID+".json";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Map<String, Object> map;
                //准备List资源文件
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                try {
                    //获取贴文基本信息
                    mEssayBaseInfo.put("essayCreator",jsonObject.getString("creator"));
                    mEssayBaseInfo.put("essayFirstText",jsonObject.getString("text"));
                    mEssayBaseInfo.put("replyCount",jsonObject.getString("replyCount"));
                    mEssayBaseInfo.put("createDate",jsonObject.getString("createDate"));
                    mEssayBaseInfo.put("essayID",jsonObject.getString("essayID"));

                    //将1L的信息硬写上去
                    firstSender.setText(jsonObject.getString("creator"));
                    firstText.setText(jsonObject.getString("text"));
                    firstCreateTime.setText(jsonObject.getString("createDate").substring(5,16));
                    firstReplyCount.setText(jsonObject.getString("replyCount"));

                    JSONArray replyArray= (JSONArray)jsonObject.get("replyList");

                    for (int i = 0; i < replyArray.length(); i++) {
                        JSONObject reply = (JSONObject) replyArray.get(i);
                        //准备每一项的资源文件
                        map = new HashMap<String, Object>();
                        map.put("creator", reply.getString("creator"));//回复创建者
                        map.put("text", reply.getString("text"));//回复的文本
                        map.put("createDate", reply.getString("createDate").substring(5, 16));//回复的时间
                        map.put("floor",reply.getString("floor"));
                        //map.put("replyStatus", reply.get("replyStatus"));//该回复的状态,"1"正常  "0"删除
                        //这条信息在之后使用
                        list.add(map);
                    }
                    final essayFragmentAdapter mEssayListAdapter = new essayFragmentAdapter(EssayActivity.this);
                    mEssayListAdapter.setList(list);
                    //为每一条reply设置点击事件
                    mEassyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(EssayActivity.this,"尚未完成",Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(EssayActivity.this, EssayActivity.class);
//                            intent.putExtra("essayID", mEssayListAdapter.getItem(position).toString());
//                            startActivity(intent);
                        }
                    });
                    mEassyListView.setAdapter(mEssayListAdapter);
                } catch (NullPointerException n) {
                    n.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

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
}