package com.NUAA.nuaaclub.StringRequestOverride;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
public class StringRequestWithToken extends StringRequest{
    private String token;
    public StringRequestWithToken(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public StringRequestWithToken(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    public StringRequestWithToken(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener
    ,String token) {
        super(method, url, listener, errorListener);
        this.token=token;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers=new HashMap<>();
        headers.put("token",token);
        return headers;
    }
}
