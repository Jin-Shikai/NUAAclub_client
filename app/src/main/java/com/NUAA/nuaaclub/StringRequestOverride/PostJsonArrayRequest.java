package com.NUAA.nuaaclub.StringRequestOverride;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PostJsonArrayRequest extends JsonRequest<JSONArray> {

    /**
     * Creates a new request.
     * @param url URL to fetch the JSON from
     * @param listener Listener to receive the JSON response
     * @param errorListener Error listener, or null to ignore errors.
     */
    private JSONObject params;
    public PostJsonArrayRequest(String url, JSONObject params, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url,null, listener, errorListener);
        this.params = params;
    }

    public PostJsonArrayRequest(int method, String url, JSONObject params, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url,null, listener, errorListener);
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        return null;
    };

    @Override
    protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
        response.headers.put("HTTP.CONTENT_TYPE", "utf-8");
        try {
            String jsonString = new String(response.data, "utf-8");
            return Response.success(new JSONArray(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
}
