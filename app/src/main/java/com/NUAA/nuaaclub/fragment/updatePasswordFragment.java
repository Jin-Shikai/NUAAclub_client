package com.NUAA.nuaaclub.fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.NUAA.nuaaclub.BaseReplyActivity;
import com.NUAA.nuaaclub.MainActivity;
import com.NUAA.nuaaclub.R;
import com.NUAA.nuaaclub.StringRequestOverride.StringRequestWithToken;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class updatePasswordFragment extends BaseFragment {
    EditText newPassword;
    Button saveNewPasswordBtn;
    private String newPasswordStr;
    @Override
    protected View initView() {
        View view=null;
        view = View.inflate(mContext, R.layout.fragment_updatepassword, null);
        newPassword = (EditText)view.findViewById(R.id.et_newPassword);
        saveNewPasswordBtn = (Button)view.findViewById(R.id.saveNewPasswordBtn);
        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                newPasswordStr = s.toString();
            }
        });
        saveNewPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newPasswordStr.length()<4)
                    Toast.makeText(mContext, "密码至少6位", Toast.LENGTH_SHORT).show();
                else if(newPasswordStr.length()>15)
                    Toast.makeText(mContext, "密码至多15位", Toast.LENGTH_SHORT).show();
                else
                {
                    //1. 创建请求队列
                    RequestQueue requestQueue = Volley.newRequestQueue(mContext);
                    //2. 创建post请求
                    String url = "http://"+getResources().getString(R.string.address)+":8080/LoginDemo/updatePassword";
                    StringRequest stringRequest = new StringRequestWithToken(Request.Method.POST, url, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Toast.makeText(mContext, "网络似乎不通了", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            String ID = MainActivity.sharedPreferences.getString("ID", "");
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("newPassword",newPasswordStr);
                            map.put("ID",ID);
                            return map;
                        }
                    };
                    //3. 将请求添加入请求队列
                    requestQueue.add(stringRequest);
                    editor.putString("password",newPasswordStr);
                    editor.commit();
                    Toast.makeText(mContext, "新密码已保存", Toast.LENGTH_SHORT).show();
                    MainActivity mainActivity=(MainActivity)getActivity();
                    BaseFragment to =mainActivity.getFrament(3);
                    //替换
                    mainActivity.switchFragment(updatePasswordFragment.this,to);
                }
            }
        });
        return view;
    }
}
