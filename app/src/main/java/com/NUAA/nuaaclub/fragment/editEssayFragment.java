package com.NUAA.nuaaclub.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.NUAA.nuaaclub.BuildConfig;
import com.NUAA.nuaaclub.MainActivity;
import com.NUAA.nuaaclub.R;
import com.NUAA.nuaaclub.base.BaseFragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class editEssayFragment extends BaseFragment {
    String token;
    String textContent;
    Button mSubmit;
    EditText mText;
    @Override
    protected View initView() {
        View view = View.inflate(mContext, R.layout.fragment_editessay, null);
        mSubmit = (Button)  view.findViewById(R.id.essaySubmit);
        mText=(EditText)view.findViewById(R.id.textEssay);
        mText.setText("");
        mText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                textContent=s.toString();
            }
        });//获取文本
        //为发送按钮设置监听器
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //1. 创建请求队列
                RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                //2. 创建post请求
                String url = "http://192.168.1.37:8080/LoginDemo/submitEssay";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i("s", s);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
                        SimpleDateFormat formatterForName = new SimpleDateFormat("MM-dd HH-mm-ss");
                        Date curDate=new Date(System.currentTimeMillis());
                        String timeStr=formatter.format(curDate);
                        String timeStrForName=formatterForName.format(curDate);
                        //map.put("value1","param1");
                        token=sharedPreferences.getString("token","");
                        map.put("text", textContent);
                        map.put("createDate",timeStr);
                        map.put("latestDate",timeStr);
                        map.put("creatorID",token);
                        map.put("creator",token.substring(0,5));//此处后期应修改为匿名token
                        map.put("essayID",token.substring(0,5)+"_"+timeStrForName);//权宜之计
                        map.put("status","2");
                        return map;
                    }
                };
                //3. 将请求添加入请求队列
                requestQueue.add(stringRequest);

                MainActivity mainActivity=(MainActivity)getActivity();
                BaseFragment to =mainActivity.getFrament(0);
                //替换
                mainActivity.switchFragment(editEssayFragment.this,to);
            }
        });
        return view;
    }
}
