package id.co.viva.news.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.at.ATParams;
import com.at.ATTag;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;

import id.co.viva.news.app.connection.ConnectionDetector;
import id.co.viva.news.app.model.Favorites;
import id.co.viva.news.app.model.SliderContentImage;

/**
 * Created by reza on 08/12/14.
 */
public class Global {

    private RequestQueue mRequestQueue;
    private ConnectionDetector mConnectionDetector;
    //private EasyTracker easyTracker;
    private Tracker tracker;
    private ATTag atTag;
    private ATParams atParams;
    private ArrayList<Favorites> favoritesList;
    private Type typeFavorites;
    private Type typeSlider;
    private Type typeContents;
    private Gson gson;
    private SharedPreferences.Editor editor;
    private Context mContext;
    private static Global mInstance;

    public Global(Context mContext) {
        this.mContext = mContext;
    }

    public static synchronized Global getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Global(context);
        }
        return mInstance;
    }

    /*
    public EasyTracker getEasyTracker() {
        if (easyTracker == null) {
            easyTracker = EasyTracker.getInstance(mContext);
        }
        return easyTracker;
    }
    */
    public Tracker getTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mContext);
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }

    public ATParams getAtParams() {
        if (atTag == null) {
            atTag = ATTag.init(mContext, Constant.AT_SUB_DOMAIN,
                    Constant.AT_SITE_ID, Constant.AT_SUB_SITE);
            atTag.setLogDomain(Constant.AT_LOG_DOMAIN);
        }
        if (atParams == null) {
            atParams = new ATParams();
        }
        return atParams;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext);
        }
        return mRequestQueue;
    }

    public ConnectionDetector getConnectionStatus() {
        if (mConnectionDetector == null) {
            mConnectionDetector = new ConnectionDetector(mContext);
        }
        return mConnectionDetector;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? Constant.TAG : tag);
        getRequestQueue().add(req);
    }

    public ArrayList<Favorites> getFavoritesList() {
        if (favoritesList == null) {
            favoritesList = new ArrayList<>();
        }
        return favoritesList;
    }

    public Type getTypeFavorites() {
        if (typeFavorites == null) {
            typeFavorites = new TypeToken<ArrayList<Favorites>>(){}.getType();
        }
        return typeFavorites;
    }

    public Type getTypeSlider() {
        if (typeSlider == null) {
            typeSlider = new TypeToken<ArrayList<SliderContentImage>>(){}.getType();
        }
        return typeSlider;
    }

    public Type getContents() {
        if (typeContents == null) {
            typeContents = new TypeToken<ArrayList<String>>(){}.getType();
        }
        return typeContents;
    }

    public Gson getInstanceGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public SharedPreferences.Editor getDefaultEditor() {
        if (editor == null) {
            editor = getSharedPreferences(mContext).edit();
        }
        return editor;
    }

}
