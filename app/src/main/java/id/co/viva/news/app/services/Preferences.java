package id.co.viva.news.app.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Set;

import id.co.viva.news.app.Global;

/**
 * Created by reza on 03/11/14.
 */
public class Preferences {

    private final static String TAG = "VIVA-PREFS";

    private static final String PROPERTY_GCM_CLIENT_ID = "gcm_client_id";
    private static final String PROPERTY_GCM_REG_ID = "gcm_reg_id";
    private static final String PROPERTY_GCM_REG_APP_VERSION = "gcm_reg_app_version";
    private static final String PROPERTY_GCM_IS_REG_GCM_SERVER = "gcm_is_reg_gcm_server";
    private static final String PROPERTY_GCM_IS_REG_BACKEND_SERVER = "gcm_is_reg_backend_server";

    private static String gcm_client_id = "", gcm_reg_id = "";
    private static int gcm_reg_app_version = 0;
    private static boolean gcm_reg_gcm_server = false, gcm_reg_backend_server = false;

    public Preferences() {}

    public static void setGCMClientId(String m_gcm_client_id) {
        gcm_client_id = m_gcm_client_id;
    }

    public static String getGCMRegId() {
        return gcm_reg_id;
    }

    public static void setGCMRegId(String gcm_reg_id) {
        Preferences.gcm_reg_id = gcm_reg_id;
    }

    public static int getGCMRegAppVersion() {
        return gcm_reg_app_version;
    }

    public static void setGCMRegAppVersion(int gcm_reg_app_version) {
        Preferences.gcm_reg_app_version = gcm_reg_app_version;
    }

    public static boolean isGCMRegGCMServer() {
        return gcm_reg_gcm_server;
    }

    public static void setGCMRegGCMServer(boolean gcm_reg_gcm_server) {
        Preferences.gcm_reg_gcm_server = gcm_reg_gcm_server;
    }

    public static boolean isGCMRegBackendServer() {
        return gcm_reg_backend_server;
    }

    public static void setGCMRegBackendServer(boolean gcm_reg_backend_server) {
        Preferences.gcm_reg_backend_server = gcm_reg_backend_server;
    }

    public static void storeSharedPreferencesGCM(Context context) {
        Log.i(TAG, "storeSharedPreferencesGCM()");

        SharedPreferences Preferences = Global.getInstance(context).getSharedPreferences(context);
        SharedPreferences.Editor editor = Preferences.edit();
        editor.putString(PROPERTY_GCM_CLIENT_ID, gcm_client_id);
        editor.putString(PROPERTY_GCM_REG_ID, gcm_reg_id);
        editor.putInt(PROPERTY_GCM_REG_APP_VERSION, gcm_reg_app_version);
        editor.putBoolean(PROPERTY_GCM_IS_REG_GCM_SERVER, gcm_reg_gcm_server);
        editor.putBoolean(PROPERTY_GCM_IS_REG_BACKEND_SERVER, gcm_reg_backend_server);
        editor.commit();

        Set<String> keys = Preferences.getAll().keySet();
        for(String key : keys) {
            Log.d(TAG, key);
        }
    }

}
