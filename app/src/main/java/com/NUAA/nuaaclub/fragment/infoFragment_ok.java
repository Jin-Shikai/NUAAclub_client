package com.NUAA.nuaaclub.fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.NUAA.nuaaclub.MainActivity;
import com.NUAA.nuaaclub.R;
import com.NUAA.nuaaclub.base.BaseFragment;

public class infoFragment_ok extends BaseFragment {
    private Button logoutBtn;
    private Button myEssayBtn;

    @Override
    protected View initView() {
        View view=null;
        view = View.inflate(mContext, R.layout.fragment_info_ok, null);
        logoutBtn=(Button)view.findViewById(R.id.logoutBtn);
        myEssayBtn=(Button)view.findViewById(R.id.myEssay);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tokenPre=sharedPreferences.getString("token","");
                Log.i("tokenPre",tokenPre);
                editor.remove("token");
                editor.commit();
                String tokenAfter=sharedPreferences.getString("token","");
                Log.i("tokenAfter",tokenAfter);
                MainActivity mainActivity=(MainActivity)getActivity();
                BaseFragment to =mainActivity.getFrament(2);
                //替换
                mainActivity.switchFragment(infoFragment_ok.this,to);
            }
        });
        myEssayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity=(MainActivity)getActivity();
                BaseFragment to =mainActivity.getFrament(4);
                //替换
                mainActivity.switchFragment(infoFragment_ok.this,to);
            }
        });
        return view;
    }
}
