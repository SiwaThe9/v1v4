package id.co.viva.news.app.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActivityPhotoList;
import id.co.viva.news.app.activity.ActivityVideoList;
import id.co.viva.news.app.activity.ActivityWebContent;
import id.co.viva.news.app.fragment.SubChannel2Fragment;

public class Function {
    public static final String TAG = Function.class.getSimpleName();

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public static int getAppVersionCode(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static String parseVolleyError(Context context, VolleyError error) {
        String result = "";

        Resources res = context.getResources();
        if (error instanceof NoConnectionError) {
            result = "No Connection Error";
        } else if (error instanceof ServerError) {
            result = "Server Error";
        } else if (error instanceof AuthFailureError) {
            result = "Auth Failure Error";
        } else if (error instanceof ParseError) {
            result = "Parse Error";
        } else if (error instanceof NetworkError) {
            result = "Network Error";
        } else if (error instanceof TimeoutError) {
            result = "Timeout Error";
        } else {
            result = "Unknown Error";
        }

        return result;
    }

    public static int getScreenOrientation(Context context) {
        int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch (rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Log.e(TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }

    public static ImageLoaderConfiguration getImageLoaderConfiguration(Context context) {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        return new ImageLoaderConfiguration.Builder(context)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(cacheSize))
                .memoryCacheSize(cacheSize)
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .build();
    }

    public static void openListSubChannel(Context context, String action, String name, String ab_color, String title, String api_url) {
        Intent intent = new Intent(context, ActivityPhotoList.class);
        intent.putExtra(SubChannel2Fragment.BUNDLE_ACTION, action);
        intent.putExtra(SubChannel2Fragment.BUNDLE_NAME, name);
        intent.putExtra(SubChannel2Fragment.BUNDLE_AB_COLOR, ab_color);
        intent.putExtra(SubChannel2Fragment.BUNDLE_TITLE, title);
        intent.putExtra(SubChannel2Fragment.BUNDLE_API_URL, api_url);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    public static void openListPhoto(Context context, String name, String ab_color, String title, String api_url) {
        Intent intent = new Intent(context, ActivityPhotoList.class);
        intent.putExtra(ActivityPhotoList.EXTRA_AB_COLOR, ab_color);
        intent.putExtra(ActivityPhotoList.EXTRA_NAME, name);
        intent.putExtra(ActivityPhotoList.EXTRA_TITLE, title);
        intent.putExtra(ActivityPhotoList.EXTRA_API_URL, api_url);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    public static void openListVideo(Context context, String name, String ab_color, String title, String api_url) {
        Intent intent = new Intent(context, ActivityVideoList.class);
        intent.putExtra(ActivityVideoList.EXTRA_AB_COLOR, ab_color);
        intent.putExtra(ActivityVideoList.EXTRA_NAME, name);
        intent.putExtra(ActivityVideoList.EXTRA_TITLE, title);
        intent.putExtra(ActivityVideoList.EXTRA_API_URL, api_url);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    public static void openWebContent(Context context, String name, String ab_color, String title, String url_content, String html_content) {
        Intent intent = new Intent(context, ActivityWebContent.class);
        intent.putExtra(ActivityWebContent.EXTRA_AB_COLOR, ab_color);
        intent.putExtra(ActivityWebContent.EXTRA_NAME, name);
        intent.putExtra(ActivityWebContent.EXTRA_TITLE, title);
        intent.putExtra(ActivityWebContent.EXTRA_URL_CONTENT, url_content);
        intent.putExtra(ActivityWebContent.EXTRA_HTML_CONTENT, html_content);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

}