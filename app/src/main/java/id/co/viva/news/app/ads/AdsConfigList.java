package id.co.viva.news.app.ads;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.model.Ads;

/**
 * Created by reza on 05/05/15.
 */
public class AdsConfigList {

    private SharedPreferences settings;

    public AdsConfigList() {}

    private void saveAds(Context context, ArrayList<Ads> adsArrayList) {
        settings = context.getSharedPreferences(Constant.PREFS_ADS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        Gson gson = new Gson();
        String jsonAds = gson.toJson(adsArrayList);
        editor.putString(Constant.ADS_LIST, jsonAds);
        editor.apply();
    }

    public void removeAds(Context context, ArrayList<Ads> adsArrayList) {
        if (adsArrayList != null) {
            //if (adsArrayList.size() > 0) {
            //    adsArrayList.clear();
                saveAds(context, adsArrayList);
            //}
        }
    }

    public void addAds(Context context, Ads ads) {
        ArrayList<Ads> adsArrayList = getAdList(context);
        if (adsArrayList == null) {
            adsArrayList = new ArrayList<>();
        }
        adsArrayList.add(ads);
        saveAds(context, adsArrayList);
    }

    public ArrayList<Ads> getAdList(Context context) {
        ArrayList<Ads> adsArrayList = new ArrayList<Ads>();
        settings = context.getSharedPreferences(Constant.PREFS_ADS_NAME, Context.MODE_PRIVATE);
        if (settings.contains(Constant.ADS_LIST)) {
            String jsonFavorites = settings.getString(Constant.ADS_LIST, "");
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Ads>>(){}.getType();
            adsArrayList = gson.fromJson(jsonFavorites, type);
        }
        return adsArrayList;
    }

}
