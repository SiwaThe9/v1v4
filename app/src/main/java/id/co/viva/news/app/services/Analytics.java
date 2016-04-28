package id.co.viva.news.app.services;

import android.content.Context;

import com.at.ATParams;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import id.co.viva.news.app.Global;

/**
 * Created by reza on 03/11/14.
 */
public class Analytics {

    private Context mContext;

    public Analytics(Context context) {
        mContext = context;
    }

    public void getAnalyticByATInternet(String page) {
        Global.getInstance(mContext).getAtParams().setPage(page);
        Global.getInstance(mContext).getAtParams().xt_sendTag();
    }

    public void getAnalyticByATInternetFromNotification(String page, String key) {
        Global.getInstance(mContext).getAtParams().setPage(page);
        Global.getInstance(mContext).getAtParams().xt_click("1", key, ATParams.clicType.navigation);
        Global.getInstance(mContext).getAtParams().xt_sendTag();
    }

    public void getAnalyticByGoogleAnalytic(String page) {
        /*
        Global.getInstance(mContext).getEasyTracker().set(Fields.SCREEN_NAME, page);
        Global.getInstance(mContext).getEasyTracker().set(Fields.APP_VERSION,
                Global.getInstance(mContext).getEasyTracker().get(Fields.APP_VERSION));
        Global.getInstance(mContext).getEasyTracker().send(MapBuilder.createAppView().build());
        */

        Tracker tracker = Global.getInstance(mContext).getTracker();
        tracker.setScreenName(page);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
