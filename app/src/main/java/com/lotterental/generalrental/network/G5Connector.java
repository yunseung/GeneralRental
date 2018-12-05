package com.lotterental.generalrental.network;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.lotterental.LLog;
import com.lotterental.common.Common;
import com.lotterental.common.util.network.NetRequestQueue;
import com.lotterental.generalrental.BuildConfig;
import com.lotterental.generalrental.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class G5Connector {

    private static class LazyHolder {
        private static final G5Connector instance = new G5Connector();
    }

    public static G5Connector getInstance() {
        return LazyHolder.instance;
    }

    public interface G5ConnetorCallbackListener {
        void onSuccess(JSONObject body) throws JSONException;

        void onErrorResponse(String errorMsg);
    }

    public void conn(final Context context, String url, final JSONObject reqJson, final G5ConnetorCallbackListener listener) {
        StringRequest strRequest = new StringRequest(Request.Method.POST, (BuildConfig.API_URL + url),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        LLog.d(response.toString());
                        try {
                            JSONObject res = new JSONObject(response);
                            JSONObject body = res.getJSONObject("_msg_").getJSONObject("_body_");
                            listener.onSuccess(body);
                        } catch (JSONException e) {
                            Toast.makeText(context, "서버 응답 데이터가 잘못되었습니다." ,Toast.LENGTH_LONG).show();
                            Common.printException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String msg = context.getString(R.string.g5error);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            msg = "서버 응답이 없습니다.";
                        } else if (error instanceof AuthFailureError) {
                            msg = "서버 인증 실패입니다.";
                        } else if (error instanceof ServerError) {
                            msg = "서버 에러입니다.";
                        } else if (error instanceof NetworkError) {
                            msg = "서버 네트워크 에러입니다.";
                        } else if (error instanceof ParseError) {
                            msg = "서버 응답 에러입니다.";
                        }
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> param = new HashMap<String, String>();
                param.put("param", reqJson.toString());
                return param;
            }
        };

        strRequest.setRetryPolicy(new DefaultRetryPolicy(
                1000*60*2,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        NetRequestQueue.getInstance(context).addToRequestQueue(strRequest, strRequest);
    }
}
