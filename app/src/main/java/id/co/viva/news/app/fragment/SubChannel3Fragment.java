package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import id.co.viva.news.app.activity.ActivityPhotoList;
import id.co.viva.news.app.activity.ActivitySubChannel2List;
import id.co.viva.news.app.activity.ActivitySubChannel3List;
import id.co.viva.news.app.activity.ActivityVideoList;
import id.co.viva.news.app.adapter.SubChannel2Adapter;
import id.co.viva.news.app.adapter.SubChannel3Adapter;
import id.co.viva.news.app.component.AdsView;
import id.co.viva.news.app.gson.GSONListSubChannel;
import id.co.viva.news.app.object.Ads;
import id.co.viva.news.app.object.SubChannel;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.share.DB;
import id.co.viva.news.app.util.Function;
import id.co.viva.news.app.util.VolleySingleton;
import id.co.viva.news.app.util.VolleyStringRequest;

public class SubChannel3Fragment extends Fragment {
    public final static String TAG = SubChannel3Fragment.class.getSimpleName();
    public final static String TAG_LIST_SUB_CHANNEL = "list_sub_channel";

    public final static String BUNDLE_NAME = "bundle_name";
    public final static String BUNDLE_AB_COLOR = "bundle_ab_color";
    public final static String BUNDLE_API_URL = "bundle_api_url";

    private String name;
    private String ab_color;
    private String api_url;
    private String screen_name;
    private String unit_id_top;
    private String unit_id_bottom;

    private ArrayList<SubChannel> datas = new ArrayList<SubChannel>();

    private DB db;
    private Analytics analytics;
    private SubChannel3Adapter adapter;
    private LinearLayoutManager layoutManager;

    private LinearLayout viewLayout;
    private SwipeRefreshLayout viewSwipeRefresh;
    private RecyclerView viewRecycler;
    private TextView viewMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_NAME)) name = bundle.getString(BUNDLE_NAME);
            if (bundle.containsKey(BUNDLE_AB_COLOR)) ab_color = bundle.getString(BUNDLE_AB_COLOR);
            if (bundle.containsKey(BUNDLE_API_URL)) api_url = bundle.getString(BUNDLE_API_URL);
        }
        screen_name = name + "_screen";

        db = DB.getInstance(getActivity());
        analytics = new Analytics(getActivity());

        adapter = new SubChannel3Adapter(getActivity(), this);
        layoutManager = new LinearLayoutManager(getActivity());
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

        if (ab_color != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(ab_color)));
        }

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
    public void onDestroy() {
        VolleySingleton.getInstance(getActivity()).cancelPendingRequests(TAG_LIST_SUB_CHANNEL);

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

            VolleyStringRequest requestMasterConfig = new VolleyStringRequest(
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
            VolleySingleton.getInstance(getActivity()).cancelPendingRequests(TAG_LIST_SUB_CHANNEL);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(requestMasterConfig, TAG_LIST_SUB_CHANNEL);
        }
    }

    public void openListSubChannel2(String name, String ab_color, String title, String api_url) {
        Intent intent = new Intent(getActivity(), ActivitySubChannel2List.class);
        intent.putExtra(ActivitySubChannel2List.EXTRA_NAME, name);
        intent.putExtra(ActivitySubChannel2List.EXTRA_AB_COLOR, ab_color);
        intent.putExtra(ActivitySubChannel2List.EXTRA_TITLE, title);
        intent.putExtra(ActivitySubChannel2List.EXTRA_API_URL, api_url);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    public void openListSubChannel3(String name, String ab_color, String title, String api_url) {
        Intent intent = new Intent(getActivity(), ActivitySubChannel3List.class);
        intent.putExtra(ActivitySubChannel2List.EXTRA_NAME, name);
        intent.putExtra(ActivitySubChannel2List.EXTRA_AB_COLOR, ab_color);
        intent.putExtra(ActivitySubChannel2List.EXTRA_TITLE, title);
        intent.putExtra(ActivitySubChannel2List.EXTRA_API_URL, api_url);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    public void openListPhoto(String name, String ab_color, String title, String api_url) {
        Intent intent = new Intent(getActivity(), ActivityPhotoList.class);
        intent.putExtra(ActivityPhotoList.EXTRA_AB_COLOR, ab_color);
        intent.putExtra(ActivityPhotoList.EXTRA_NAME, name);
        intent.putExtra(ActivityPhotoList.EXTRA_TITLE, title);
        intent.putExtra(ActivityPhotoList.EXTRA_API_URL, api_url);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    public void openListVideo(String name, String ab_color, String title, String api_url) {
        Intent intent = new Intent(getActivity(), ActivityVideoList.class);
        intent.putExtra(ActivityVideoList.EXTRA_AB_COLOR, ab_color);
        intent.putExtra(ActivityVideoList.EXTRA_NAME, name);
        intent.putExtra(ActivityVideoList.EXTRA_TITLE, title);
        intent.putExtra(ActivityVideoList.EXTRA_API_URL, api_url);
        getActivity().startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }
}