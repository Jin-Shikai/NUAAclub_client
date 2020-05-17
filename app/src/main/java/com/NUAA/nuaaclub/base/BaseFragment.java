package com.NUAA.nuaaclub.base;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.NUAA.nuaaclub.MainActivity;

public abstract class BaseFragment extends Fragment {
    public int position;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    protected Context mContext;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
        //步骤1：创建一个SharedPreferences对象
        sharedPreferences= MainActivity.sharedPreferences;
        //步骤2： 实例化SharedPreferences.Editor对象
        editor = sharedPreferences.edit();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return initView();
    }

    //强制子类重写,实现子类特有的Fragment
    protected abstract View initView();
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    //当孩子需要初始化数据,或联网请求绑定数据,展示数据等时可以重写
    protected void initData()
    {
    }
}
