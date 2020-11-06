package com.NUAA.nuaaclub.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NUAA.nuaaclub.EssayActivity;
import com.NUAA.nuaaclub.MessageActivity;
import com.NUAA.nuaaclub.R;
import com.NUAA.nuaaclub.adapter.baseReplyAdapter;
import com.NUAA.nuaaclub.adapter.homeFragmentAdapter;
import com.NUAA.nuaaclub.base.BaseFragment;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class messageFragment extends BaseFragment {
    private static final String TAG = messageFragment.class.getSimpleName();//得到类名称
    private Button replyMsgBtn;
    private ListView msgListView;
    private SwipeRefreshLayout refreshView;
    private homeFragmentAdapter adapter;
    @Override
    protected View initView() {
        Log.e(TAG,"message页面已初始化");
        View view = View.inflate(mContext, R.layout.myessay, null);
        msgListView=(ListView) view.findViewById(R.id.myEssaylistView);
        refreshView=(SwipeRefreshLayout)view.findViewById(R.id.refresh);
        refreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        refreshView.setRefreshing(false);
                    }
                }, 2500);
            }
        });
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        //准备数据
        //1. 创建请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        //2. 创建请求
        String url = "http://"+getResources().getString(R.string.address)+":8080/LoginDemo/requestMyMsgListServlet";
        final String ID = sharedPreferences.getString("ID", "");
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ID", ID);
        JSONObject jsonStr = new JSONObject();
        try {
            jsonStr.put("ID", ID);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        Volley.newRequestQueue(mContext).add(
                new JsonRequest<JSONArray>
                        (Request.Method.POST, url, ID,
                                new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        //准备List资源文件
                                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                                        try {
                                            for (int i = 0; i < response.length() - 1; i++) {
                                                JSONObject msg = (JSONObject) response.get(i);
                                                //准备每一项的资源文件
                                                Map<String, Object> map = new HashMap<String, Object>();
                                                map.put("creator", msg.getString("fromCreator"));
                                                map.put("text", msg.getString("text"));
                                                map.put("createDate", msg.getString("createDate").substring(5,16));
                                                map.put("essayID", msg.getString("fileName"));
                                                map.put("replyCount", msg.getString("replyCnt"));
                                                list.add(map);
                                            }
                                            adapter = new homeFragmentAdapter(mContext);
                                            adapter.setList(list);
                                            msgListView.setAdapter(adapter);

                                            adapter.setList(list);
                                            //为message列表设置点击事件, 根据messageID跳转
                                            msgListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    Intent intent = new Intent(mContext, MessageActivity.class);
                                                    intent.putExtra("fileName",adapter.getItem(position).toString());
                                                    startActivity(intent);
                                                }
                                            });
                                            msgListView.setAdapter(adapter);
                                        } catch (NullPointerException n) {
                                            n.printStackTrace();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(mContext, "网络似乎不通了", Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("ID", ID);
                        return params;
                    }
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Accept", "application/json");
                        headers.put("Content-Type", "application/json; charset=UTF-8");
                        return headers;
                    }
                    @Override
                    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                        response.headers.put("HTTP.CONTENT_TYPE", "utf-8");
                        try {
                            String jsonString = new String(response.data, "utf-8");
                            return Response.success(
                                    new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        } catch (JSONException je) {
                            return Response.error(new ParseError(je));
                        }
                    }
                });
    }
}
