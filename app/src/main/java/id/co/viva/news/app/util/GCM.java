package id.co.viva.news.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.gson.GSONSaveGCM;
import id.co.viva.news.app.services.RegistrationIntentService;
import id.co.viva.news.app.share.Prefs;

public class GCM {
    public static final String TAG = GCM.class.getSimpleName();
    public static final String TAG_SAVE_GCM = "save_gcm";

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String INFO_PACKAGE_NAME = "package_name";
    public static final String INFO_APP_VERSION_CODE = "app_version_code";
    public static final String INFO_APP_VERSION_NAME = "app_version_name";
    public static final String INFO_OS_VERSION = "os_version";
    public static final String INFO_MODEL = "model";
    public static final String INFO_COUNTRY = "country";
    public static final String INFO_OPERATOR = "operator";
//    public static final String INFO_MSISDN = "msisdn";

    private static GCM instance;

    public static GCM getInstance(Context context) {
        if(instance == null) instance = new GCM(context);
        return instance;
    }

    private Context context;
    private Prefs prefs;

    public GCM(Context context) {
        this.context = context;

        prefs = Prefs.getInstance(context);
    }

    public void checkGCM() {
        if(checkPlayServices()) {
            if(!prefs.isGCMRegGCMServer() || prefs.getGCMRegAppVersion() != prefs.getAppVersionCode()) {
                Intent intent = new Intent(context, RegistrationIntentService.class);
                context.startService(intent);
            } else if(!prefs.isGCMRegBackendServer()) {
                startRequestSaveGCM();
            }
        }
    }

    public Map<String, String> getClientInfo(){
        Map<String, String> result = new HashMap<String, String>();

        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String country = tm.getSimCountryIso();
        String operator = tm.getSimOperatorName();
//        String msisdn = tm.getLine1Number();

        result.put(INFO_PACKAGE_NAME, context.getPackageName());
        result.put(INFO_APP_VERSION_CODE, String.valueOf(prefs.getAppVersionCode()));
        result.put(INFO_APP_VERSION_NAME, String.valueOf(prefs.getAppVersionName()));
        result.put(INFO_OS_VERSION, android.os.Build.VERSION.RELEASE);
        result.put(INFO_MODEL, android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL);
        result.put(INFO_COUNTRY, country != null ? country : "");
        result.put(INFO_OPERATOR, operator != null ? operator : "");
//        result.put(INFO_MSISDN, msisdn != null ? msisdn : "");

        return result;
    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                try {
                    GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        return true;
    }

    public void startRequestSaveGCM() {
        Log.e(TAG, "startRequestSaveGCM()");
        String url = Constant.GCM_URL_BACKEND_SERVER;

        VolleyStringRequest requestMasterConfig = new VolleyStringRequest(
                Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new GsonBuilder().create();
                        try {
                            GSONSaveGCM gson_save_gcm = gson.fromJson(response, GSONSaveGCM.class);
                            if(gson_save_gcm.status == Constant.STATUS_OK) {
                                prefs.setGCMClientId(gson_save_gcm.client_id);
                                prefs.setGCMRegBackendServer(true);
                                prefs.storeGCM();
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, null) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lang", Locale.getDefault().getLanguage());
                params.put("token", prefs.getGCMToken());
                params.put("client_id", String.valueOf(prefs.getGCMClientId()));
                params.putAll(getClientInfo());

                return params;
            }
        };
        VolleySingleton.getInstance(context).cancelPendingRequests(TAG_SAVE_GCM);
        VolleySingleton.getInstance(context).addToRequestQueue(requestMasterConfig, TAG_SAVE_GCM);
    }
}