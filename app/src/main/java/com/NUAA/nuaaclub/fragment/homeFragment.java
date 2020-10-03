package com.NUAA.nuaaclub.fragment;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.NUAA.nuaaclub.EssayActivity;
import com.NUAA.nuaaclub.EditEssayActivity;
import com.NUAA.nuaaclub.MainActivity;
import com.NUAA.nuaaclub.R;
import com.NUAA.nuaaclub.StringRequestOverride.StringRequestWithToken;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class homeFragment extends BaseFragment {
    private Button newEassy;
    private homeFragmentAdapter adapter;
    private ListView mlistView;
    private static final String TAG = homeFragment.class.getSimpleName();//得到类名称
    private SwipeRefreshLayout refreshview;
    private int mPosition;


    @Override
    protected View initView() {
        Log.e(TAG,"home页面已初始化");
        View view = View.inflate(mContext, R.layout.fragment_home, null);
        mlistView=(ListView) view.findViewById(R.id.listView);
        refreshview=(SwipeRefreshLayout)view.findViewById(R.id.refresh);
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


        Button newEassy=(Button)view.findViewById(R.id.newEssay);
        //为发新帖按钮设置监听器
        newEassy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 创建请求队列
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                //2. 创建post请求
                String url = "http://192.168.1.100:8080/LoginDemo/newEssayServlet";
                StringRequest stringRequest = new StringRequestWithToken(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i("s",s);
                        System.out.println(s);
                        MainActivity mainActivity=(MainActivity)getActivity();
                        if(s.equals("101"))
                        {
                            mainActivity.mRg_main.check(R.id.rb_info);
                        }
                        else
                        {
                            Intent intent = new Intent(mainActivity, EditEssayActivity.class);
                            intent.putExtra("flag",1);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("token",sharedPreferences.getString("token",""));
                        return map;
                    }
                };
                //3. 将请求添加入请求队列
                requestQueue.add(stringRequest);
            }
        });

        return view;
    }

    @Override
    protected void initData() {
        Log.e(TAG, "主页页面");
        //准备数据
        //1. 创建请求队列
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        //2. 创建get请求
        String url = "http://192.168.1.100:8080/LoginDemo/requestListServlet";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                //准备List资源文件
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                try {
                    for (int i = 0; i < jsonArray.length() - 1; i++) {
                        JSONObject essay = (JSONObject) jsonArray.get(i);
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
                    mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(mContext, EssayActivity.class);
                            intent.putExtra("essayID",adapter.getItem(position).toString());
                            startActivity(intent);
                        }
                    });
                    mlistView.setAdapter(adapter);
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
        }) {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                response.headers.put("HTTP.CONTENT_TYPE", "utf-8");
                try {
                    String jsonString = new String(response.data, "utf-8");
                    return Response.success(new JSONArray(jsonString), HttpHeaderParser.parseCacheHeaders(response));
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
        requestQueue.add(jsonArrayRequest);

    }
}
