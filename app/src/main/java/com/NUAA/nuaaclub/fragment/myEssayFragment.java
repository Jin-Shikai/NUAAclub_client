package com.NUAA.nuaaclub.fragment;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NUAA.nuaaclub.EssayActivity;
import com.NUAA.nuaaclub.MainActivity;
import com.NUAA.nuaaclub.R;
import com.NUAA.nuaaclub.StringRequestOverride.PostJsonArrayRequest;
import com.NUAA.nuaaclub.adapter.homeFragmentAdapter;
import com.NUAA.nuaaclub.base.BaseFragment;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
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

public class myEssayFragment extends BaseFragment {
    private homeFragmentAdapter adapter;
    private ListView mMyEssayListView;
    private SwipeRefreshLayout refreshview;


    @Override
    protected void initData() {
        //准备数据
        //1. 创建请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        //2. 创建get请求
        String url = "http://"+getResources().getString(R.string.address)+":8080/LoginDemo/requestMyEssayListServlet";
        final String ID = sharedPreferences.getString("ID", "");
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("ID", ID);
        Log.i("myID:", ID);

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
                                                JSONObject essay = (JSONObject) response.get(i);
                                                //准备每一项的资源文件
                                                Map<String, Object> map = new HashMap<String, Object>();
                                                map.put("creator", essay.getString("creator"));
                                                map.put("text", essay.getString("text"));
                                                map.put("createDate", essay.getString("createDate").substring(5,16));
                                                map.put("replyCount", essay.get("replyCount"));
                                                map.put("essayID", essay.get("essayID"));
                                                list.add(map);
                                            }
                                            final homeFragmentAdapter adapter = new homeFragmentAdapter(mContext);
                                            adapter.setList(list);
                                            //为essay列表设置点击事件, 根据essayID跳转
                                            mMyEssayListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    Intent intent = new Intent(mContext, EssayActivity.class);
                                                    intent.putExtra("essayID",adapter.getItem(position).toString());
                                                    startActivity(intent);
                                                }
                                            });
                                            mMyEssayListView.setAdapter(adapter);
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
        @Override
        protected View initView () {
            View view = View.inflate(mContext, R.layout.myessay, null);
            mMyEssayListView = (ListView) view.findViewById(R.id.myEssaylistView);
            refreshview = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
            refreshview.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                            refreshview.setRefreshing(false);
                        }
                    }, 2500);
                }
            });
            return view;
        }
    }
