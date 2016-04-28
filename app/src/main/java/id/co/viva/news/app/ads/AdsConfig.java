package id.co.viva.news.app.ads;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;

/**
 * Created by reza on 30/04/15.
 */
public class AdsConfig {

    public AdsConfig() {}

    public void setAdsBanner(PublisherAdView publisherAdView, String unitId, int position, LinearLayout parentLayout) {
        if (unitId != null) {
            if (unitId.length() > 0) {
                PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
                switch (position) {
                    case Constant.POSITION_BANNER_TOP:
                        publisherAdView.setAdUnitId(unitId);
                        publisherAdView.setAdSizes(AdSize.SMART_BANNER);
                        parentLayout.addView(publisherAdView, 0);
                        break;
                    case Constant.POSITION_BANNER_BOTTOM:
                        publisherAdView.setAdUnitId(unitId);
                        publisherAdView.setAdSizes(AdSize.SMART_BANNER);
                        parentLayout.addView(publisherAdView);
                        break;
                }
                publisherAdView.loadAd(adRequest);
            }
        }
    }

    public void setAdsInterstitial(final Activity activity, final InterstitialAd interstitialAd,
                                   String unitId, final Class <? extends AppCompatActivity> aClass,
                                   final String type, final Fragment fragment, final AppCompatActivity actionBarActivity) {
        if (unitId != null) {
            if (unitId.length() > 0) {
                AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
                interstitialAd.setAdUnitId(unitId);
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        interstitialAd.show();
                        super.onAdLoaded();
                    }
                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        if (type.equalsIgnoreCase(Constant.ADS_TYPE_OPENING)) {
                            adOpeningMoveTo(aClass, activity);
                        } else if (type.equalsIgnoreCase(Constant.ADS_TYPE_CLOSING)) {
                            adClosingMoveTo(fragment, actionBarActivity);
                        }
                        super.onAdFailedToLoad(errorCode);
                    }
                    @Override
                    public void onAdClosed() {
                        if (type.equalsIgnoreCase(Constant.ADS_TYPE_OPENING)) {
                            adOpeningMoveTo(aClass, activity);
                        } else if (type.equalsIgnoreCase(Constant.ADS_TYPE_CLOSING)) {
                            adClosingMoveTo(fragment, actionBarActivity);
                        }
                        super.onAdClosed();
                    }
                });
                interstitialAd.loadAd(adRequestBuilder.build());
            } else {
                if (type.equalsIgnoreCase(Constant.ADS_TYPE_OPENING)) {
                    adOpeningMoveTo(aClass, activity);
                } else if (type.equalsIgnoreCase(Constant.ADS_TYPE_CLOSING)) {
                    adClosingMoveTo(fragment, actionBarActivity);
                }
            }
        } else {
            if (type.equalsIgnoreCase(Constant.ADS_TYPE_OPENING)) {
                adOpeningMoveTo(aClass, activity);
            } else if (type.equalsIgnoreCase(Constant.ADS_TYPE_CLOSING)) {
                adClosingMoveTo(fragment, actionBarActivity);
            }
        }
    }

    private void adOpeningMoveTo(Class<? extends AppCompatActivity> aClass, Activity activity) {
        if (activity != null) {
            if (aClass != null) {
                Intent intent = new Intent(activity, aClass);
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                activity.finish();
            }
        }
    }

    private void adClosingMoveTo(Fragment fragment, AppCompatActivity activity) {
        if (activity != null) {
            if (fragment != null) {
                activity.getSupportFragmentManager().beginTransaction().remove(fragment);
                activity.finish();
            }
        }
    }

}
