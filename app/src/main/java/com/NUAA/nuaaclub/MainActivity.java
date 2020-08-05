package com.NUAA.nuaaclub;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.VolumeAutomation;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.NUAA.nuaaclub.base.BaseFragment;
import com.NUAA.nuaaclub.fragment.homeFragment;
import com.NUAA.nuaaclub.fragment.infoFragment;
import com.NUAA.nuaaclub.fragment.infoFragment_ok;
import com.NUAA.nuaaclub.fragment.messageFragment;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {
    public FragmentTransaction ft;
    public static SharedPreferences sharedPreferences;

    public RadioGroup mRg_main;
    private List<BaseFragment> mBaseFragmentList;
    private int position;//选择的Fragment对应的位置
    private Fragment mContent;//上次要保存的Fragment

    private Object homeFragment= new homeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences= getSharedPreferences("data",Context.MODE_PRIVATE);
        //初始化view
        initView();
        //初始化fragment
        initFragment();
        //设置监听
        setListener();
        //默认选中首页
        mRg_main.check(R.id.rb_home);
    }

    private void setListener() {
        mRg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            String uuid=sharedPreferences.getString("token","");
            switch (checkedId)
            {
                case R.id.rb_home://主页
                    position=0;
                    break;
                case R.id.rb_info:
                    String token=sharedPreferences.getString("token","");
                    if(token.length()<5)
                        position=2;//尚未登录
                    else
                        position=3;//已经登录
                    break;
                case R.id.rb_message:
                    position=1;//私信页
                    break;
                default:
                    position=0;//首页
                    break;
            }

            //根据位置得到对应的Fragment
            BaseFragment to =getFrament(position);
            //替换
            switchFragment(mContent,to);
        }
    }

    public void switchFragment(Fragment from,Fragment to) {
        if(from!=to)
        {
            Fragment last=mContent;
            mContent=to;
            ft = getSupportFragmentManager().beginTransaction();
            if(from!=null)
            {
                ft.hide(from);
            }
            if(to.equals(homeFragment))
                ft.replace(from.getId(),new homeFragment()).commit();
            if(!to.isAdded())//to未被添加
            {
                if(to!=null)
                {
                    ft.add(R.id.fl_content,to).commit();
                }
            }
            else
            {
                if(to!=null)
                {
                    if(to.equals(homeFragment))
                        ft.replace(from.getId(),new homeFragment()).commit();
                    else
                        ft.show(to).commit();
                }
            }
        }
    }

    //根据位置得到对应的Fragment
    public BaseFragment getFrament(int position) {
        BaseFragment fragment = mBaseFragmentList.get(position);
        return fragment;
    }

    private void initFragment() {
        mBaseFragmentList=new ArrayList<>();
        mBaseFragmentList.add(new homeFragment());//主页: 0
        mBaseFragmentList.add(new messageFragment());//私信界面: 1
        mBaseFragmentList.add(new infoFragment());//个人信息界面: 2
        mBaseFragmentList.add(new infoFragment_ok());//已登录界面: 3
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        mRg_main = (RadioGroup) findViewById(R.id.rg_main);
    }
}
