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

import com.NUAA.nuaaclub.EssayActivity;
import com.NUAA.nuaaclub.MainActivity;
import com.NUAA.nuaaclub.R;
import com.NUAA.nuaaclub.base.BaseFragment;
import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

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
    private Button getCodeBtn;
    private String code;
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
            getCodeBtn = (Button) view.findViewById(R.id.getcode);
            getCodeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                    Random random = new Random();
                    code = String.valueOf(random.nextInt(10000) % (10000 - 1000 + 1) + 1000);
                    if (ID.length() != 11)
                        Toast.makeText(mContext, "手机号格式不正确", Toast.LENGTH_SHORT).show();
                    else {
                        String url = "https://tianqiapi.com/api/sms?appid=55334614&appsecret=3dLpfLYk&code=" + code + "&mobile=" + ID;
                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, null, new Response.Listener<org.json.JSONObject>() {
                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                try {
                                    if (jsonObject.get("errcode").equals(0))
                                        Toast.makeText(mContext, "验证码发送成功", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(mContext, "验证码发送失败", Toast.LENGTH_SHORT).show();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Toast.makeText(mContext, "网络似乎不通了", Toast.LENGTH_SHORT).show();
                            }
                        }) {
                            @Override
                            protected Response<org.json.JSONObject> parseNetworkResponse(NetworkResponse response) {
                                response.headers.put("HTTP.CONTENT_TYPE", "utf-8");
                                JSONObject jsonObject;
                                try {
                                    jsonObject = new JSONObject(new String(response.data, "UTF-8"));
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
            });

            //登录按钮监听
            mBtnLogin = (Button) view.findViewById(R.id.loginbtn);
            mBtnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(code.length()!=4)
                        Toast.makeText(mContext, "验证码格式不正确", Toast.LENGTH_SHORT).show();
                    else if (code.equals(Password)) {
                        editor.remove("ID");
                        editor.putString("ID", ID);
                        //生成token
                        String token = UUID.randomUUID() + "";
                        editor.remove("token");
                        editor.putString("token", token);
                        editor.commit();
                        MainActivity mainActivity = (MainActivity) getActivity();
                        BaseFragment to = mainActivity.getFrament(3);
                        //替换
                        mainActivity.switchFragment(infoFragment.this,to);
                    }
                    else{
                        Toast.makeText(mContext, "登录失败", Toast.LENGTH_SHORT).show();
                    }
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
