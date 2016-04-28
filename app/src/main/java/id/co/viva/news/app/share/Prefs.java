package id.co.viva.news.app.share;

import android.content.Context;
import android.content.SharedPreferences;

import id.co.viva.news.app.util.Function;

public class Prefs {
    private static Prefs instance;

    public static Prefs getInstance(Context context) {
        if (instance == null) instance = new Prefs(context);
        return instance;
    }

    private SharedPreferences prefs;

    private static int app_version_code = 0;
    private static String app_version_name = "";

    public int getAppVersionCode() {
        return app_version_code;
    }

    public void setAppVersionCode(int app_version_code) {
        Prefs.app_version_code = app_version_code;
    }

    public String getAppVersionName() {
        return app_version_name;
    }

    public void setAppVersionName(String app_version_name) {
        Prefs.app_version_name = app_version_name;
    }

    private static final String PROPERTY_GCM_CLIENT_ID = "gcm_client_id";
    private static final String PROPERTY_GCM_TOKEN = "gcm_token";
    private static final String PROPERTY_GCM_REG_APP_VERSION = "gcm_reg_app_version";
    private static final String PROPERTY_GCM_IS_REG_GCM_SERVER = "gcm_is_reg_gcm_server";
    private static final String PROPERTY_GCM_IS_REG_BACKEND_SERVER = "gcm_is_reg_backend_server";

    private static String gcm_client_id = "";
    private static String gcm_token = "";
    private static int gcm_reg_app_version = 0;
    private static boolean gcm_reg_gcm_server = false, gcm_reg_backend_server = false;

    public String getGCMClientId() {
        return gcm_client_id;
    }

    public void setGCMClientId(String gcm_client_id) {
        Prefs.gcm_client_id = gcm_client_id;
    }

    public String getGCMToken() {
        return gcm_token;
    }

    public void setGCMToken(String gcm_token) {
        Prefs.gcm_token = gcm_token;
    }

    public int getGCMRegAppVersion() {
        return gcm_reg_app_version;
    }

    public void setGCMRegAppVersion(int gcm_reg_app_version) {
        Prefs.gcm_reg_app_version = gcm_reg_app_version;
    }

    public boolean isGCMRegGCMServer() {
        return gcm_reg_gcm_server;
    }

    public void setGCMRegGCMServer(boolean gcm_reg_gcm_server) {
        Prefs.gcm_reg_gcm_server = gcm_reg_gcm_server;
    }

    public boolean isGCMRegBackendServer() {
        return gcm_reg_backend_server;
    }

    public void setGCMRegBackendServer(boolean gcm_reg_backend_server) {
        Prefs.gcm_reg_backend_server = gcm_reg_backend_server;
    }

    private static final String SETTING_IS_SMALL_CARD = "setting_is_small_card";
    private static boolean setting_is_small_card = false;

    public boolean isSettingSmallCard() {
        return setting_is_small_card;
    }

    public void setSettingSmallCard(boolean setting_is_small_card) {
        Prefs.setting_is_small_card = setting_is_small_card;
    }

    public Prefs(Context context) {
        prefs = Function.getSharedPreferences(context);

        app_version_code = Function.getAppVersionCode(context);
        app_version_name = Function.getAppVersionName(context);

        loadGCM();
        loadSetting();
    }

    public void loadGCM() {
        gcm_client_id = prefs.getString(PROPERTY_GCM_CLIENT_ID, "");
        gcm_token = prefs.getString(PROPERTY_GCM_TOKEN, "");
        gcm_reg_app_version = prefs.getInt(PROPERTY_GCM_REG_APP_VERSION, 0);
        gcm_reg_gcm_server = prefs.getBoolean(PROPERTY_GCM_IS_REG_GCM_SERVER, false);
        gcm_reg_backend_server = prefs.getBoolean(PROPERTY_GCM_IS_REG_BACKEND_SERVER, false);
    }

    public void storeGCM() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_GCM_CLIENT_ID, gcm_client_id);
        editor.putString(PROPERTY_GCM_TOKEN, gcm_token);
        editor.putInt(PROPERTY_GCM_REG_APP_VERSION, gcm_reg_app_version);
        editor.putBoolean(PROPERTY_GCM_IS_REG_GCM_SERVER, gcm_reg_gcm_server);
        editor.putBoolean(PROPERTY_GCM_IS_REG_BACKEND_SERVER, gcm_reg_backend_server);
        editor.commit();
    }

    public void loadSetting() {
        setting_is_small_card = prefs.getBoolean(SETTING_IS_SMALL_CARD, false);
    }

    public void storeSetting() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SETTING_IS_SMALL_CARD, setting_is_small_card);
        editor.commit();
    }
}