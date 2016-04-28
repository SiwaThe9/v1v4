package id.co.viva.news.app.component;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.HashMap;

import id.co.viva.news.app.R;
import id.co.viva.news.app.object.Ads;

public class AdsView {
    public static HashMap<String, String> unit_ids = new HashMap<String, String>();
    public static HashMap<String, LinearLayout> layouts = new HashMap<String, LinearLayout>();
    public static HashMap<String, PublisherAdView> views = new HashMap<String, PublisherAdView>();

    public static void initAds(final Context context, String id, String unit_id, LinearLayout layout, final int position) {
        unit_ids.put(id, unit_id);

        final PublisherAdView view = new PublisherAdView(context);
        view.setTag(id);
        view.setVisibility(View.GONE);
        view.setAdUnitId(unit_id);
        view.setAdSizes(AdSize.SMART_BANNER);
        view.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                view.setVisibility(View.GONE);
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                view.setVisibility(View.GONE);
                super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdLoaded() {
                Animation animation = AnimationUtils.loadAnimation(context, position == Ads.POSITION_BANNER_TOP ? R.anim.slide_top_enter : R.anim.slide_bottom_enter);
                view.setAnimation(animation);
                view.setVisibility(View.VISIBLE);
                view.getLayoutParams().height = view.getLayoutParams().WRAP_CONTENT;
                super.onAdLoaded();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });
        views.put(id, view);

        if (position == Ads.POSITION_BANNER_TOP) {
            layout.addView(views.get(id), 0);
        } else {
            layout.addView(views.get(id));
        }
        layouts.put(id, layout);
    }

    public static void requestAds(String id, PublisherAdRequest request) {
        if (views.containsKey(id)) {
            views.get(id).loadAd(request);
        }
    }

    public static void onResume(String id) {
        if (views.containsKey(id)) {
            views.get(id).resume();
        }
    }

    public static void onPause(String id) {
        if (views.containsKey(id)) {
            views.get(id).pause();
        }
    }

    public static void onDestroy(String id) {
        if (views.containsKey(id)) {
            views.get(id).destroy();
        }
    }
}