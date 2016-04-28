package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.SubChannel2Adapter;
import id.co.viva.news.app.component.AdsView;
import id.co.viva.news.app.gson.GSONListSubChannel;
import id.co.viva.news.app.model.NavigationItem;
import id.co.viva.news.app.object.Ads;
import id.co.viva.news.app.object.SubChannel;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.share.DB;
import id.co.viva.news.app.util.Function;
import id.co.viva.news.app.util.VolleySingleton;
import id.co.viva.news.app.util.VolleyStringRequest;

public class SubChannel2Fragment extends Fragment {
    public final static String TAG = SubChannel2Fragment.class.getSimpleName();
    public final static String TAG_LIST_SUB_CHANNEL = "list_sub_channel";

    public final static String BUNDLE_ACTION = "bundle_action";
    public final static String BUNDLE_NAME = "bundle_name";
    public final static String BUNDLE_AB_COLOR = "bundle_ab_color";
    public final static String BUNDLE_TITLE = "bundle_title";
    public final static String BUNDLE_API_URL = "bundle_api_url";

    private String action;
    private String name;
    private String title;
    private String ab_color;
    private String api_url;
    private String screen_name;
    private String unit_id_top;
    private String unit_id_bottom;

    private ArrayList<SubChannel> datas = new ArrayList<SubChannel>();

    private DB db;
    private Analytics analytics;
    private SubChannel2Adapter adapter;
    private GridLayoutManager layoutManager;

    private LinearLayout viewLayout;
    private SwipeRefreshLayout viewSwipeRefresh;
    private RecyclerView viewRecycler;
    private TextView viewMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_ACTION)) action = bundle.getString(BUNDLE_ACTION);
            if (bundle.containsKey(BUNDLE_NAME)) name = bundle.getString(BUNDLE_NAME);
            if (bundle.containsKey(BUNDLE_TITLE)) title = bundle.getString(BUNDLE_TITLE);
            if (bundle.containsKey(BUNDLE_AB_COLOR)) ab_color = bundle.getString(BUNDLE_AB_COLOR);
            if (bundle.containsKey(BUNDLE_API_URL)) api_url = bundle.getString(BUNDLE_API_URL);
        }
        screen_name = name + "_screen";

//        if (ab_color != null) {
//            ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
//            if(ab != null) {
//                ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(ab_color)));
//            }
//        }

        db = DB.getInstance(getActivity());
        analytics = new Analytics(getActivity());

        adapter = new SubChannel2Adapter(getActivity(), this, action);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return initSpan(position);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sub_channel, container, false);

        viewLayout = (LinearLayout) rootView.findViewById(R.id.layout);
        viewSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        viewRecycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        viewMessage = (TextView) rootView.findViewById(R.id.message);

        viewMessage.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startRequestListChannel();
            }
        });

        viewRecycler.setHasFixedSize(true);
        viewRecycler.setLayoutManager(layoutManager);
        viewRecycler.setAdapter(adapter);

        initAds();

        fillData();

        startRequestListChannel();
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
        if (unit_id_top != null) {
            AdsView.onPause(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP));
        }
        if (unit_id_bottom != null) {
            AdsView.onPause(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM));
        }

        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        VolleySingleton.getInstance(getActivity()).cancelPendingRequests(TAG_LIST_SUB_CHANNEL + "_" + name);
    }

    @Override
    public void onDestroy() {

        if (unit_id_top != null) {
            AdsView.onDestroy(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP));
        }
        if (unit_id_bottom != null) {
            AdsView.onDestroy(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM));
        }

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            adapter.initScreenOrientation();
        }
    }

    private int initSpan(int position) {
        int span = 1;
        if(action.equals(NavigationItem.ACTION_GRID_SUB_CHANNEL)) {
            if(datas.size() % 2 == 0 && position == datas.size() - 1) {
                span =  2;
            } else {
                span = position == 0 ? 2 : 1;
            }
        } else if(action.equals(NavigationItem.ACTION_LIST_SUB_CHANNEL)) {
            span = 2;
        }
        return span;
    }

    private void setSwipeRefresh(final boolean is_refresing) {
        if (viewSwipeRefresh.isRefreshing() != is_refresing) {
            viewSwipeRefresh.post(new Runnable() {
                @Override
                public void run() {
                    viewSwipeRefresh.setRefreshing(is_refresing);
                }
            });
        }
    }

    private void showMessage(String message) {
        viewMessage.setVisibility(View.GONE);
        if (datas.size() > 0) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        } else {
            viewMessage.setVisibility(View.VISIBLE);
            viewMessage.setText(message);
        }
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

    private void addData(ArrayList<SubChannel> subChannels) {
        db.deleteAllSubChannel(name);
        db.addAllSubChannel(subChannels, name);

        fillData();
    }

    private void fillData() {
        datas = db.getAllSubChannel(name);

        adapter.setDatas(datas);
        adapter.notifyDataSetChanged();
    }

    private void startRequestListChannel() {
        analytics.getAnalyticByATInternet(screen_name);
        analytics.getAnalyticByGoogleAnalytic(screen_name);

        requestAds();

        if (!Global.getInstance(getActivity()).getConnectionStatus().isConnectingToInternet()) {
            showMessage(getActivity().getString(R.string.title_no_connection) + "\n" + getActivity().getString(R.string.label_pull_down_to_refresh));
            setSwipeRefresh(false);
        } else {
            viewMessage.setVisibility(View.GONE);
            setSwipeRefresh(true);

            VolleyStringRequest requestListSubChannel = new VolleyStringRequest(
                    Request.Method.GET,
                    api_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new GsonBuilder().create();
                            try {
                                GSONListSubChannel gson_list_sub_channel = gson.fromJson(response, GSONListSubChannel.class);
                                if (gson_list_sub_channel.sub_channels != null) {
                                    addData(gson_list_sub_channel.sub_channels);
                                }

                                if (datas.size() == 0) {
                                    showMessage(getActivity().getString(R.string.label_empty_sub_channel) + "\n" + getActivity().getString(R.string.label_pull_down_to_refresh));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                showMessage(getActivity().getString(R.string.label_error_json) + "\n" + getActivity().getString(R.string.label_pull_down_to_refresh));
                            }

                            setSwipeRefresh(false);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            setSwipeRefresh(false);

                            showMessage(Function.parseVolleyError(getActivity(), error) + "\n" + getActivity().getString(R.string.label_pull_down_to_refresh));
                        }
                    });
            VolleySingleton.getInstance(getActivity()).cancelPendingRequests(TAG_LIST_SUB_CHANNEL + "_" + name);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(requestListSubChannel, TAG_LIST_SUB_CHANNEL + "_" + name);
        }
    }
}