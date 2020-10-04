package com.NUAA.nuaaclub.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Printer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

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

import org.w3c.dom.Text;

import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

public class infoFragment extends BaseFragment{
    private RadioGroup mRg_main;
    private Button logoutBtn;
    private EditText mID;
    private Button mBtnLogin;
    private EditText mPassword;
    private static final String TAG = infoFragment.class.getSimpleName();//得到类名称
    private TextView textView;
    private String ID;
    private String Password;
    private String Status;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_info, null);
//    }

    @Override
    protected View initView() {
        View view=null;
        Log.e(TAG,"info页面已初始化");
            view = View.inflate(mContext, R.layout.fragment_info, null);
            //手机号栏监听
            mID = (EditText) view.findViewById(R.id.et_phone);
            mID.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    ID = s.toString();
                }
            });

            //密码栏监听
            mPassword = (EditText) view.findViewById(R.id.et_password);
            mPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Password = s.toString();
                }
            });

            //登录按钮监听
            mBtnLogin = (Button) view.findViewById(R.id.loginbtn);
            mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editor.remove("ID");
                    editor.putString("ID", ID);
                    //1. 创建请求队列
                    RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                    //2. 创建post请求
                    String url = "http://"+getResources().getString(R.string.address)+":8080/LoginDemo/LoginServlet";
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Log.i("s", s);
                            if (s.length() > 10) {
                                Toast.makeText(mContext, "登录成功, 欢迎您", Toast.LENGTH_SHORT).show();
                                //步骤3：将获取过来的值放入文件
                                editor.remove("token");
                                editor.putString("token", s);
                                //步骤4：提交
                                editor.commit();
                                MainActivity mainActivity = (MainActivity)getActivity();
                                BaseFragment to = mainActivity.getFrament(3);
                                //替换
                                mainActivity.switchFragment(infoFragment.this,to);
                            } else
                                Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
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
                            Status="2";
                            //map.put("value1","param1");
                            map.put("ID", ID);
                            map.put("password", Password);
                            map.put("status",Status);
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
        super.initData();
        Log.e(TAG,"个人信息页面");
    }
}
