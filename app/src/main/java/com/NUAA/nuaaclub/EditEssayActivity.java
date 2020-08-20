package com.NUAA.nuaaclub;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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

public class EditEssayActivity extends AppCompatActivity {

    String token;
    String textContent;
    private Button mSubmit;
    private EditText mText;
    private int flag;
    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editessay);

        //获取标志: 1为发贴文essay, 2为发回复reply
        flag = (int)getIntent().getExtras().get("flag");

        mSubmit = (Button)findViewById(R.id.essaySubmit);
        mText=(EditText)findViewById(R.id.textEssay);
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
                RequestQueue requestQueue = Volley.newRequestQueue(EditEssayActivity.this);
                //2. 创建post请求
                if(flag==1)
                    url = "http://192.168.1.37:8080/LoginDemo/submitEssay";//发帖
                else if(flag==2)
                    url = "http://192.168.1.37:8080/LoginDemo/submitReplyServlet";//发回复
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
                        //获取日期格式
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        //获取日期
                        Date curDate = new Date(System.currentTimeMillis());
                        //得到用于显示的时间
                        String timeStr = formatter.format(curDate);
                        //得到标识符token
                        token = MainActivity.sharedPreferences.getString("token", "");
                        //初始化共性参数
                        map.put("createDate_New", timeStr);//发送时间
                        map.put("text", textContent);//发送内容
                        map.put("userID", token);//发送者ID
                        if(flag==1)
                        {
                            String creator = getRandomID(timeStr, token);
                            SimpleDateFormat formatterForName = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            String timeStrForName = formatterForName.format(curDate);
                            map.put("latestDate_New", timeStr);
                            map.put("essayID", creator + "_" + timeStrForName.substring(0,10));//权宜之计
                            map.put("status", "2");//普通贴子
                            map.put("creator", creator);//发送者匿名ID
                        }
                        else if(flag==2)
                        {
                            //如果发的是回复在请求参数中给出帖子ID
                            map.put("essayID",(String)getIntent().getExtras().get("essayID"));
                            //从帖子基本信息中得到贴文创建时间, 算法生成回复者匿名ID
                            String essayCreateDateStr=(String)getIntent().getExtras().get("essayCreateDate");
                            map.put("creator", getRandomID(essayCreateDateStr, token));
                        }
                        return map;
                    }
                };
                //3. 将请求添加入请求队列
                requestQueue.add(stringRequest);


                if(flag==1)
                {
                    Intent intent = new Intent(EditEssayActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }
                else
                {
                    finish();
                }
            }
        });
    }

    public String getRandomID(String dateTime,String token)
    {
        return (((Integer.valueOf(dateTime.charAt(18))+3)*71)%10)+token.substring(1,8)+dateTime.charAt(18)+token.substring(10,12);
    }

}
