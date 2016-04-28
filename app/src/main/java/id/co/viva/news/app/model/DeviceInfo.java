package id.co.viva.news.app.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * Created by reza on 04/11/14.
 */
public class DeviceInfo {

    private Context context;
    private TelephonyManager telephonyManager;
    private static String UNKNOWN = "Unknown";

    public DeviceInfo(Context ctx) {
        context = ctx;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public String getPhoneFromTelephony() {
        String phoneNumber;
        try {
            phoneNumber = telephonyManager.getLine1Number();
            if(phoneNumber != null) {
                Log.i(DeviceInfo.class.getSimpleName(), "Phone number from Telephony : " + phoneNumber);
                return phoneNumber;
            } else {
                return UNKNOWN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSIMOperatorName() {
        String simOperatorName = telephonyManager.getSimOperatorName();
        if(simOperatorName != null) {
            Log.i(DeviceInfo.class.getSimpleName(), "SIM Operator Name : " + simOperatorName);
            return simOperatorName;
        } else {
            return UNKNOWN;
        }
    }

    public String getSIMCountryISO() {
        String simCountryIso = telephonyManager.getSimCountryIso();
        if(simCountryIso != null) {
            Log.i(DeviceInfo.class.getSimpleName(), "Country ISO : " + simCountryIso);
            return simCountryIso;
        } else {
            return UNKNOWN;
        }
    }

    public String getModel() {
        String model = android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
        if(model != null) {
            Log.i(DeviceInfo.class.getSimpleName(), "Model : " + model);
            return model;
        } else {
            return UNKNOWN;
        }
    }

    public String getOSVersion() {
        String osVersion = android.os.Build.VERSION.RELEASE;
        if(osVersion != null) {
            Log.i(DeviceInfo.class.getSimpleName(), "OS Version : " + osVersion);
            return osVersion;
        } else {
            return UNKNOWN;
        }
    }

    public int getAppVersion() {
        int appVersion = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = pInfo.versionCode;
            Log.i(DeviceInfo.class.getSimpleName(), "App Version Code : " + appVersion);
        } catch (Exception e) {
            e.getMessage();
        }
        return appVersion;
    }

    public String getAppVersionName() {
        String appVersion = null;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            appVersion = pInfo.versionName;
            Log.i(DeviceInfo.class.getSimpleName(), "App Version Name : " + appVersion);
        } catch (Exception e) {
            e.getMessage();
        }
        return appVersion;
    }

}
