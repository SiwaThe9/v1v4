package id.co.viva.news.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.ads.AdsConfigList;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 31/10/14.
 */
public class ActSplashScreen extends Activity {

    private static final int TIME_OUT_DELAY = 1000;
    private static final int TIME_OUT_DELAY_DYNAMIC = 3000;
    private ImageView imageSplash;
    private ImageView backgroundLayout;
    private LinearLayout layoutProgress;
    private boolean isInternet = false;
    private AdsConfigList mConfigList;
    //private GCM gcm;

    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash_screen);

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(ActSplashScreen.this));
        }

        options = new DisplayImageOptions.Builder()
//                .preProcessor(new BitmapProcessorCenterCrop())
//                .displayer(new RoundedBitmapDisplayer(512))
                .showImageOnLoading(R.drawable.default_image)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .build();

        //Check connection
        isInternet = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();
        //Initiate GCM Service
        //gcm = GCM.getInstance(this);
        //Initiate Ad list
        mConfigList = new AdsConfigList();
        //Initiate Animation Image
        imageSplash = (ImageView) findViewById(R.id.splash_offline_layout);
        //Background layout
        backgroundLayout = (ImageView) findViewById(R.id.splash_dynamic_layout);
        //Progress layout
        layoutProgress = (LinearLayout) findViewById(R.id.progress_layout_splash);
        //Check existing connection
        if (isInternet) {
            //new GetRegistrationID().execute();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    loadMainConfig();
                }
            }, TIME_OUT_DELAY);
        } else {
            layoutProgress.setVisibility(View.GONE);
            showDefaultSplash();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    checkPreferences();
                }
            }, TIME_OUT_DELAY);
        }
    }

    private void showDefaultSplash() {
        imageSplash.setVisibility(View.VISIBLE);
        imageSplash.setImageResource(R.drawable.icon_launcher);
    }

    private void checkFirstTime(String intentType) {
        if (intentType.equals(Constant.MOVE_APPLICATION)) {
            showAds();
        } else if (intentType.equals(Constant.MOVE_TUTORIAL)) {
            moveTo(ActTutorial.class);
        }
    }

    private void moveTo(Class<?> aClass) {
        Intent intent = new Intent(getApplicationContext(), aClass);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        finish();
    }

    private void getAds() {
        ArrayList<Ads> adsList = mConfigList.getAdList(this);
        if (adsList != null) {
            if (adsList.size() > 0) {
                for (int i = 0; i < adsList.size(); i++) {
                    if (adsList.get(i).getmType() == Constant.ADS_TYPE_INTERSTITIAL && adsList.get(i).getmPosition() == Constant.ADS_POSITION_OPEN) {
                        InterstitialAd interstitialAd = new InterstitialAd(this);
                        AdsConfig adsConfig = new AdsConfig();
                        adsConfig.setAdsInterstitial(this, interstitialAd, adsList.get(i).getmUnitId(),
                                ActLanding.class, Constant.ADS_TYPE_OPENING, null, null);
                    }
                }
            } else {
                moveTo(ActLanding.class);
            }
        } else {
            moveTo(ActLanding.class);
        }
    }

    private void showAds() {
        //if (isInternet) {
        //    getAds();
        //} else {
        moveTo(ActLanding.class);
        //}
    }

    private class GetRegistrationID extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {
            //gcm.doRegistration();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    loadMainConfig();
                }
            }, TIME_OUT_DELAY);
        }
    }

    private void loadMainConfig() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.MAIN_CONFIG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            //Remove Ads
                            ArrayList<Ads> adses = mConfigList.getAdList(ActSplashScreen.this);
                            ArrayList<Ads> adses_clean = new ArrayList<Ads>();
                            for (Ads ads : adses) {
                                if (ads.getmType() != Constant.ADS_TYPE_INTERSTITIAL) {
                                    adses_clean.add(ads);
                                }

                            }
                            mConfigList.removeAds(ActSplashScreen.this, adses_clean);

                            //Get Ads
                            JSONArray listAds = jsonObject.getJSONArray(Constant.adses);
                            if (listAds.length() > 0) {
                                for (int i = 0; i < listAds.length(); i++) {
                                    JSONObject data = listAds.getJSONObject(i);
                                    String screen_name = data.getString(Constant.screen_name);
                                    String unit_id = data.getString(Constant.unit_id);
                                    int type = data.getInt(Constant.type);
                                    int position = data.getInt(Constant.position);
                                    mConfigList.addAds(ActSplashScreen.this, new Ads(screen_name, type, position, unit_id));
                                }
                            }
                            //Get dynamic splash screen
                            JSONObject response = jsonObject.getJSONObject(Constant.response);
                            JSONArray splashScreen = response.getJSONArray("splash_screen");
                            if (splashScreen.length() > 0) {
                                String backgroundUrl = splashScreen.get(0).toString();
                                //Set dynamic background
                                ImageLoader.getInstance().displayImage(backgroundUrl, backgroundLayout, options,
                                        new SimpleImageLoadingListener() {
                                            @Override
                                            public void onLoadingStarted(String imageUri, View view) {
                                                layoutProgress.setVisibility(View.VISIBLE);
                                            }

                                            @Override
                                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                                layoutProgress.setVisibility(View.GONE);
                                                checkPreferences();
                                            }

                                            @Override
                                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                                layoutProgress.setVisibility(View.GONE);
                                                new Handler().postDelayed(new Runnable() {
                                                    public void run() {
                                                        checkPreferences();
                                                    }
                                                }, TIME_OUT_DELAY_DYNAMIC);
                                            }
                                        });
                            } else {
                                layoutProgress.setVisibility(View.GONE);
                                showDefaultSplash();
                                new Handler().postDelayed(new Runnable() {
                                    public void run() {
                                        checkPreferences();
                                    }
                                }, TIME_OUT_DELAY);
                            }
                        } catch (JSONException je) {
                            je.printStackTrace();
                            layoutProgress.setVisibility(View.GONE);
                            checkPreferences();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                layoutProgress.setVisibility(View.GONE);
                checkPreferences();
            }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.MAIN_CONFIG, true);
        Global.getInstance(this).getRequestQueue().getCache().get(Constant.MAIN_CONFIG);
        Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void checkPreferences() {
        if (Global.getInstance(ActSplashScreen.this).getSharedPreferences(ActSplashScreen.this)
                .getBoolean(Constant.FIRST_INSTALL_TUTORIAL, true)) {
            checkFirstTime(Constant.MOVE_TUTORIAL);
            Global.getInstance(ActSplashScreen.this).getSharedPreferences(ActSplashScreen.this).
                    edit().putBoolean(Constant.FIRST_INSTALL_TUTORIAL, false).commit();
        } else {
            checkFirstTime(Constant.MOVE_APPLICATION);
        }
    }
}