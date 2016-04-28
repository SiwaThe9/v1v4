package id.co.viva.news.app.services;

import android.content.Context;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.interfaces.OnSpinnerListener;

/**
 * Created by reza on 23/12/14.
 */
public class GetDataUtils {

    private OnSpinnerListener onSpinnerListener;
    private Context context;

    public GetDataUtils(OnSpinnerListener onSpinnerListener, Context context) {
        this.onSpinnerListener = onSpinnerListener;
        this.context = context;
    }

    public void getDataProvince() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_GET_PROVINCE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String volleyResponse) {
                        Log.i(Constant.TAG, "PROVINCE RESPONSE : " + volleyResponse);
                        onSpinnerListener.onSuccessLoadDataSpinner(volleyResponse, Constant.ADAPTER_PROVINCE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        onSpinnerListener.onErrorLoadDataSpinner(volleyError.getMessage(), Constant.ADAPTER_PROVINCE);
                    }
                });
        stringRequest.setShouldCache(true);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(context).getRequestQueue().getCache().invalidate(Constant.NEW_GET_PROVINCE, true);
        Global.getInstance(context).getRequestQueue().getCache().get(Constant.NEW_GET_PROVINCE);
        Global.getInstance(context).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    public void getDataCity(String province_id) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEW_GET_PROVINCE + "p/" + province_id,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String volleyResponse) {
                        Log.i(Constant.TAG, "CITY RESPONSE : " + volleyResponse);
                        onSpinnerListener.onSuccessLoadDataSpinner(volleyResponse, Constant.ADAPTER_CITY);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        onSpinnerListener.onErrorLoadDataSpinner(volleyError.getMessage(), Constant.ADAPTER_CITY);
                    }
                });
        stringRequest.setShouldCache(true);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(context).getRequestQueue().getCache().invalidate(Constant.NEW_GET_PROVINCE + "p/" + province_id, true);
        Global.getInstance(context).getRequestQueue().getCache().get(Constant.NEW_GET_PROVINCE + "p/" + province_id);
        Global.getInstance(context).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

}
