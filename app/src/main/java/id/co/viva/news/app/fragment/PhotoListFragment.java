package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActivityPhotoDetail;
import id.co.viva.news.app.adapter.PhotoListAdapter;
import id.co.viva.news.app.adapter.VideoListAdapter;
import id.co.viva.news.app.component.AdsView;
import id.co.viva.news.app.gson.GSONListPhoto;
import id.co.viva.news.app.object.Ads;
import id.co.viva.news.app.object.ItemView;
import id.co.viva.news.app.object.PhotoCategory;
import id.co.viva.news.app.object.Photo;
import id.co.viva.news.app.object.ScreenUpdate;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.share.DB;
import id.co.viva.news.app.util.Function;
import id.co.viva.news.app.util.VolleySingleton;
import id.co.viva.news.app.util.VolleyStringRequest;

public class PhotoListFragment extends Fragment {
    public final static String TAG = PhotoListFragment.class.getSimpleName();
    public final static String TAG_LIST_PHOTO = "list_photo";

    public final static String BUNDLE_AB_COLOR = "bundle_ab_color";
    public final static String BUNDLE_NAME = "bundle_name";
    public final static String BUNDLE_TITLE = "bundle_title";
    public final static String BUNDLE_API_URL = "bundle_api_url";

    private String ab_color;
    private String name;
    private String title;
    private String api_url;
    private String screen_name;
    private String unit_id_top;
    private String unit_id_bottom;

    private ArrayList<ItemView> datas = new ArrayList<ItemView>();

    private DB db;
    private Analytics analytics;
    private PhotoListAdapter adapter;
    private LinearLayoutManager layoutManager;

    private LinearLayout viewLayout;
    private TextView viewLastUpdate;
    private SwipeRefreshLayout viewSwipeRefresh;
    private RecyclerView viewRecycler;
    private TextView viewMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_AB_COLOR)) ab_color = bundle.getString(BUNDLE_AB_COLOR);
            if (bundle.containsKey(BUNDLE_NAME)) name = bundle.getString(BUNDLE_NAME);
            if (bundle.containsKey(BUNDLE_TITLE)) title = bundle.getString(BUNDLE_TITLE);
            if (bundle.containsKey(BUNDLE_API_URL)) api_url = bundle.getString(BUNDLE_API_URL);
        }
        screen_name = name + "_screen";

        db = DB.getInstance(getActivity());
        analytics = new Analytics(getActivity());

        adapter = new PhotoListAdapter(getActivity(), this);
        layoutManager = new LinearLayoutManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_list, container, false);

        viewLayout = (LinearLayout) rootView.findViewById(R.id.layout);
        viewLastUpdate = (TextView) rootView.findViewById(R.id.last_update);
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
                startRequestListPhoto(false);
            }
        });

        viewRecycler.setHasFixedSize(true);
        viewRecycler.setLayoutManager(layoutManager);
        viewRecycler.setAdapter(adapter);
        viewRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            private boolean first_visible = false;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int totalItemCount = recyclerView.getLayoutManager().getItemCount();
                int lastVisibleItemPositions = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();

                if ((lastVisibleItemPositions + 1) == totalItemCount) {
                    if (first_visible && !viewSwipeRefresh.isRefreshing()) {
                        startRequestListPhoto(true);
                    }
                    first_visible = false;
                } else {
                    first_visible = true;
                }
            }
        });

        initAds();

        fillData(false);

        updateLastUpdate();

        startRequestListPhoto(false);
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
        VolleySingleton.getInstance(getActivity()).cancelPendingRequests(TAG_LIST_PHOTO);

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

    private void showLastUpdate() {
        ScreenUpdate screenUpdate = db.getScreenUpdate(screen_name);
        if (screenUpdate != null) {
            Date date;
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy | HH:m", Locale.getDefault());
                date = new Date(Long.parseLong(screenUpdate.update_date));
                viewLastUpdate.setText(simpleDateFormat.format(date));
            } catch (NumberFormatException e) {
                viewLastUpdate.setText("-");
            }
        } else {
            viewLastUpdate.setText("-");
        }
    }

    private void updateLastUpdate() {
        db.deleteScreenUpdate(screen_name);
        db.addScreenUpdate(new ScreenUpdate(screen_name, String.valueOf(System.currentTimeMillis())));

        showLastUpdate();
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
            ItemView itemView = datas.get(datas.size() - 1);
            itemView.type = PhotoListAdapter.TYPE_STATE;
            itemView.string_1 = message + "\n" + getString(R.string.label_tap_to_refresh);
            adapter.notifyItemChanged(datas.size() - 1);
        } else {
            viewMessage.setVisibility(View.VISIBLE);
            viewMessage.setText(message + "\n" + getString(R.string.label_pull_down_to_refresh));
        }
    }

    private void showProgress() {
        if (datas.size() > 0) {
            ItemView itemView = datas.get(datas.size() - 1);
            itemView.type = PhotoListAdapter.TYPE_LOADING;
            adapter.notifyItemChanged(datas.size() - 1);
        }
    }

    private void showEndList() {
        if (datas.size() > 0) {
            ItemView itemView = datas.get(datas.size() - 1);
            itemView.type = PhotoListAdapter.TYPE_NONE;
            adapter.notifyItemChanged(datas.size() - 1);
        }
    }

    private void addData(ArrayList<Photo> photos, boolean is_more) {
        if (!is_more) {
            db.deletePhotoCategory(name);
        }
        for (Photo photo : photos) {
            PhotoCategory dbPhotoCategory = db.getPhotoCategory(name, photo.photo_id);
            if(dbPhotoCategory == null) {
                db.addPhotoCategory(new PhotoCategory(name, photo.photo_id));
            }

            Photo dbPhoto = db.getPhoto(photo.photo_id);
            if (dbPhoto == null) {
                db.addPhoto(photo);
            }
        }

        fillData(is_more);
    }

    private void fillData(boolean is_more) {
        datas.clear();
        ArrayList<PhotoCategory> categoryPhotos = db.getAllPhotoCategory(name);
        for (PhotoCategory categoryPhoto : categoryPhotos) {
            Photo photo = db.getPhoto(categoryPhoto.photo_id);
            ItemView itemView = new ItemView(PhotoListAdapter.TYPE_PHOTO_PREVIEW, photo.photo_id, photo.title, photo.story, photo.image_url, photo.logo_url, photo.publish_date, photo.num);
            datas.add(itemView);
        }

        if (datas.size() > 0) {
            ItemView itemView = new ItemView(VideoListAdapter.TYPE_STATE, getString(R.string.label_load_more));
            datas.add(itemView);
        }

        adapter.setDatas(datas, is_more);
        adapter.notifyDataSetChanged();
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

    public void startRequestListPhoto(final boolean is_more) {
        analytics.getAnalyticByATInternet(screen_name);
        analytics.getAnalyticByGoogleAnalytic(screen_name);

        requestAds();

        if (!Global.getInstance(getActivity()).getConnectionStatus().isConnectingToInternet()) {
            showMessage(getString(R.string.title_no_connection));
            setSwipeRefresh(false);
        } else {
            viewMessage.setVisibility(View.GONE);
            showProgress();
            setSwipeRefresh(true);

            VolleyStringRequest requestMasterConfig = new VolleyStringRequest(
                    Request.Method.GET,
                    api_url + "/s/" + (is_more ? (datas.size() > 0 ? datas.size() - 1 : 0) : 0),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new GsonBuilder().create();
                            try {
                                GSONListPhoto gson_list_photo = gson.fromJson(response, GSONListPhoto.class);
                                if (gson_list_photo.photos != null) {
                                    addData(gson_list_photo.photos, is_more);

                                    if (gson_list_photo.photos.size() == 0) {
                                        showEndList();
                                    }
                                }

                                if (!is_more) {
                                    updateLastUpdate();
                                }

                                if (datas.size() == 0) {
                                    showMessage(getString(R.string.label_empty_list));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                showMessage(getString(R.string.label_error_json));
                            }

                            setSwipeRefresh(false);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            setSwipeRefresh(false);

                            showMessage(Function.parseVolleyError(getActivity(), error));
                        }
                    });
            VolleySingleton.getInstance(getActivity()).cancelPendingRequests(TAG_LIST_PHOTO);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(requestMasterConfig, TAG_LIST_PHOTO);
        }
    }

    public void openPhotoDetail(String photo_id) {
        Intent intent = new Intent(getActivity(), ActivityPhotoDetail.class);
        intent.putExtra(ActivityPhotoDetail.EXTRA_AB_COLOR, ab_color);
        intent.putExtra(ActivityPhotoDetail.EXTRA_CHANNEL, name);
        intent.putExtra(ActivityPhotoDetail.EXTRA_TITLE, title);
        intent.putExtra(ActivityPhotoDetail.EXTRA_PHOTO_ID, photo_id);
        getActivity().startActivity(intent);
    }
}