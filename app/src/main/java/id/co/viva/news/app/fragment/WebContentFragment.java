package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import id.co.viva.news.app.R;
import id.co.viva.news.app.component.AdsView;
import id.co.viva.news.app.object.Ads;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.share.DB;

public class WebContentFragment extends Fragment {
    public final static String TAG = WebContentFragment.class.getSimpleName();

    public final static String BUNDLE_NAME = "bundle_name";
    public final static String BUNDLE_URL_CONTENT = "bundle_url_content";
    public final static String BUNDLE_HTML_CONTENT = "bundle_html_content";

    private String name;
    private String url_content;
    private String html_content;
    private String screen_name;
    private String unit_id_top;
    private String unit_id_bottom;

    private DB db;
    private Analytics analytics;

    private LinearLayout viewLayout;
    private SwipeRefreshLayout viewSwipeRefresh;
    private WebView viewWidget;
    private TextView viewMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_NAME)) name = bundle.getString(BUNDLE_NAME);
            if (bundle.containsKey(BUNDLE_URL_CONTENT)) url_content = bundle.getString(BUNDLE_URL_CONTENT);
            if (bundle.containsKey(BUNDLE_HTML_CONTENT)) html_content = bundle.getString(BUNDLE_HTML_CONTENT);
        }
        screen_name = name + "_screen";

        db = DB.getInstance(getActivity());
        analytics = new Analytics(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web_content, container, false);

        viewLayout = (LinearLayout) rootView.findViewById(R.id.layout);
        viewSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        viewWidget = (WebView) rootView.findViewById(R.id.widget);
        viewMessage = (TextView) rootView.findViewById(R.id.message);

        viewMessage.setVisibility(View.GONE);

        WebSettings webSettings = viewWidget.getSettings();
        webSettings.setJavaScriptEnabled(true);
        viewWidget.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                setSwipeRefresh(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                setSwipeRefresh(false);
                viewSwipeRefresh.setEnabled(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                viewSwipeRefresh.setEnabled(true);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);

                viewSwipeRefresh.setEnabled(true);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                viewWidget.loadUrl(url);
                return true;
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                viewWidget.reload();
            }
        });

        initAds();

        if(!url_content.isEmpty()) {
            viewWidget.loadUrl(url_content);
        } else if(!html_content.isEmpty()) {
//            viewWidget.loadData(html_content, "text/html", "utf-8");
            viewWidget.loadDataWithBaseURL("http://localhost/", html_content, "text/html", "utf-8", null);
        }

        requestAds();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (unit_id_top != null) {
            AdsView.onResume(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP));
        }
        if (unit_id_bottom != null) {
            AdsView.onResume(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (unit_id_top != null) {
            AdsView.onPause(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP));
        }
        if (unit_id_bottom != null) {
            AdsView.onPause(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (unit_id_top != null) {
            AdsView.onDestroy(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP));
        }
        if (unit_id_bottom != null) {
            AdsView.onDestroy(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //adapter.initScreenOrientation();
        }
    }

    private void setSwipeRefresh(final boolean is_refresing) {
//        if (viewSwipeRefresh.isRefreshing() != is_refresing) {
            viewSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    viewSwipeRefresh.setRefreshing(is_refresing);
                }
            });
//        }
    }

    private void initAds() {
        Ads ads_top = db.getAds(screen_name, String.valueOf(Ads.TYPE_BANNER), String.valueOf(Ads.POSITION_BANNER_TOP));
        Ads ads_bottom = db.getAds(screen_name, String.valueOf(Ads.TYPE_BANNER), String.valueOf(Ads.POSITION_BANNER_BOTTOM));
        if (ads_top != null) unit_id_top = ads_top.unit_id;
        if (ads_bottom != null) unit_id_bottom = ads_bottom.unit_id;
        if (unit_id_top != null) {
            AdsView.initAds(getActivity(),
                    screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP),
                    unit_id_top,
                    viewLayout,
                    Ads.POSITION_BANNER_TOP);
        }
        if (unit_id_bottom != null) {
            AdsView.initAds(getActivity(),
                    screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM),
                    unit_id_bottom,
                    viewLayout,
                    Ads.POSITION_BANNER_BOTTOM);
        }
    }

    private void requestAds() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        if (unit_id_top != null) {
            AdsView.requestAds(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP), adRequest);
        }
        if (unit_id_bottom != null) {
            AdsView.requestAds(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM), adRequest);
        }
    }
}