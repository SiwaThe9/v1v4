package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActivityPhotoDetail;
import id.co.viva.news.app.adapter.PhotoDetailAdapter;
import id.co.viva.news.app.adapter.PhotoItemPagerAdapter;
import id.co.viva.news.app.component.AdsView;
import id.co.viva.news.app.gson.GSONListPhotoItem;
import id.co.viva.news.app.object.Ads;
import id.co.viva.news.app.object.PhotoCategory;
import id.co.viva.news.app.object.Photo;
import id.co.viva.news.app.object.ItemView;
import id.co.viva.news.app.object.PhotoDetail;
import id.co.viva.news.app.object.PhotoItem;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.share.DB;
import id.co.viva.news.app.util.Function;
import id.co.viva.news.app.util.VolleySingleton;
import id.co.viva.news.app.util.VolleyStringRequest;

public class PhotoDetailFragment extends Fragment {
    public final static String TAG = PhotoDetailFragment.class.getSimpleName();
    public final static String TAG_PHOTO_DETAIL = "tag_photo_detail";

    public final static String BUNDLE_AB_COLOR = "bundle_ab_color";
    public final static String BUNDLE_CHANNEL = "bundle_channel";
    public final static String BUNDLE_TITLE = "bundle_title";
    public final static String BUNDLE_PHOTO_ID = "bundle_photo_id";

    private String ab_color;
    private String channel;
    private String title;
    private String photo_id;
    private String name = "photo_detail";
    private String screen_name;
    private String unit_id_top;
    private String unit_id_bottom;

    private int width;

    private Photo photo;
    private ArrayList<ItemView> datas = new ArrayList<ItemView>();
    private ArrayList<PhotoItem> photo_items = new ArrayList<PhotoItem>();

    private DB db;
    private Analytics analytics;
    private PhotoDetailAdapter adapter;
    private PhotoItemPagerAdapter adapterPhotoItem;
    private LinearLayoutManager layoutManager;

    private LinearLayout viewLayout;
    private FrameLayout viewFramePager;
    private ViewPager viewPager;
//    private ImageView viewPrevious;
//    private ImageView viewNext;
    private SwipeRefreshLayout viewSwipeRefresh;
    private RecyclerView viewRecycler;
    private TextView viewMessage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_AB_COLOR)) ab_color = bundle.getString(BUNDLE_AB_COLOR);
            if (bundle.containsKey(BUNDLE_CHANNEL)) channel = bundle.getString(BUNDLE_CHANNEL);
            if (bundle.containsKey(BUNDLE_TITLE)) title = bundle.getString(BUNDLE_TITLE);
            if (bundle.containsKey(BUNDLE_PHOTO_ID)) photo_id = bundle.getString(BUNDLE_PHOTO_ID);
        }
        screen_name = name + "_" + channel + "_screen";

        db = DB.getInstance(getActivity());
        analytics = new Analytics(getActivity());

        adapter = new PhotoDetailAdapter(getActivity(), this);
        adapterPhotoItem = new PhotoItemPagerAdapter(getChildFragmentManager(), photo_id);
        layoutManager = new LinearLayoutManager(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_detail, container, false);

        viewLayout = (LinearLayout) rootView.findViewById(R.id.layout);
        viewFramePager = (FrameLayout) rootView.findViewById(R.id.frame_pager);
        viewPager = (ViewPager) rootView.findViewById(R.id.pager);
        viewSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        viewRecycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        viewMessage = (TextView) rootView.findViewById(R.id.message);

        viewMessage.setVisibility(View.GONE);

        initScreenOrientation();

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startRequestPhotoItem();
            }
        });
        viewRecycler.setHasFixedSize(true);
        viewRecycler.setLayoutManager(layoutManager);
        viewRecycler.setAdapter(adapter);
        viewPager.setAdapter(adapterPhotoItem);

        initAds();

        photo = db.getPhoto(photo_id);
        fillPhotoItems();
        fillData();

        if (photo_items.size() == 0) {
            startRequestPhotoItem();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        requestAds();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (unit_id_top != null) {
            AdsView.onResume(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP) + "-" + photo_id);
        }
        if (unit_id_bottom != null) {
            AdsView.onResume(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM) + "-" + photo_id);
        }
    }

    @Override
    public void onPause() {
        if (unit_id_top != null) {
            AdsView.onPause(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP) + "-" + photo_id);
        }
        if (unit_id_bottom != null) {
            AdsView.onPause(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM) + "-" + photo_id);
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        VolleySingleton.getInstance(getActivity()).cancelPendingRequests(TAG_PHOTO_DETAIL + "_" + photo_id);

        if (unit_id_top != null) {
            AdsView.onDestroy(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP) + "-" + photo_id);
        }
        if (unit_id_bottom != null) {
            AdsView.onDestroy(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM) + "-" + photo_id);
        }

        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            initScreenOrientation();
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

    private void addPhotoItems(ArrayList<PhotoItem> photoItems) {
        db.deletePhotoDetail(photo_id);
        for (PhotoItem photoItem : photoItems) {
            db.addPhotoDetail(new PhotoDetail(photo_id, photoItem.photo_item_id));

            PhotoItem dbPhotoItem = db.getPhotoItem(photoItem.photo_item_id);
            if (dbPhotoItem == null) {
                db.addPhotoItem(photoItem);
            }
        }

        fillPhotoItems();
    }

    private void fillPhotoItems() {
        photo_items.clear();

        ArrayList<PhotoDetail> photoDetails = db.getAllPhotoDetail(photo_id);
        for (PhotoDetail photoDetail : photoDetails) {
            PhotoItem photoItem = db.getPhotoItem(photoDetail.photo_item_id);
            photo_items.add(photoItem);
        }

        if (photo_items.size() > 0) {
            viewSwipeRefresh.setEnabled(false);
        }

        adapterPhotoItem.setDatas(photo_items);
    }

    public void initScreenOrientation() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = Function.getScreenOrientation(getActivity()) == Configuration.ORIENTATION_LANDSCAPE ? displayMetrics.heightPixels : displayMetrics.widthPixels;

        viewFramePager.getLayoutParams().height = width * 9 / 16;
    }

    private void fillData() {
        datas.clear();
        datas.add(new ItemView(PhotoDetailAdapter.TYPE_PHOTO_DETAIL, photo.title, photo.publish_date, photo.story));
        datas.add(new ItemView(PhotoDetailAdapter.TYPE_HEADER, getString(R.string.label_other_photo)));

        ArrayList<PhotoCategory> photoCategories = db.getAllPhotoCategoryOtherThan(channel, photo_id);
        for (PhotoCategory photoCategory : photoCategories) {
            Photo photo = db.getPhoto(photoCategory.photo_id);
            datas.add(new ItemView(PhotoDetailAdapter.TYPE_PHOTO_PREVIEW_SMALL, photo.photo_id, photo.title, photo.image_url));
        }

        adapter.setDatas(datas);
        adapter.notifyDataSetChanged();
    }

    private void initAds() {
        Ads ads_top = db.getAds(screen_name, String.valueOf(Ads.TYPE_BANNER), String.valueOf(Ads.POSITION_BANNER_TOP));
        Ads ads_bottom = db.getAds(screen_name, String.valueOf(Ads.TYPE_BANNER), String.valueOf(Ads.POSITION_BANNER_BOTTOM));
        if (ads_top != null) {
            unit_id_top = ads_top.unit_id;

            AdsView.initAds(getActivity(),
                    screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP) + "-" + photo_id,
                    unit_id_top,
                    viewLayout,
                    Ads.POSITION_BANNER_TOP);
        }
        if (ads_bottom != null) {
            unit_id_bottom = ads_bottom.unit_id;

            AdsView.initAds(getActivity(),
                    screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM) + "-" + photo_id,
                    unit_id_bottom,
                    viewLayout,
                    Ads.POSITION_BANNER_BOTTOM);
        }
    }

    private void requestAds() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        if (unit_id_top != null) {
            AdsView.requestAds(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP) + "-" + photo_id, adRequest);
        }
        if (unit_id_bottom != null) {
            AdsView.requestAds(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM) + "-" + photo_id, adRequest);
        }
    }

    public void startRequestPhotoItem() {
        analytics.getAnalyticByATInternet(screen_name);
        analytics.getAnalyticByGoogleAnalytic(screen_name);

        if (!Global.getInstance(getActivity()).getConnectionStatus().isConnectingToInternet()) {
            setSwipeRefresh(false);
        } else {
            viewMessage.setVisibility(View.GONE);
            setSwipeRefresh(true);

            VolleyStringRequest requestMasterConfig = new VolleyStringRequest(
                    Request.Method.GET,
                    photo.api_url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Gson gson = new GsonBuilder().create();
                            try {
                                GSONListPhotoItem gson_list_photo_item = gson.fromJson(response, GSONListPhotoItem.class);
                                if (gson_list_photo_item.photo_items != null) {
                                    addPhotoItems(gson_list_photo_item.photo_items);

                                    /*
                                    if (datas.size() == 0) {
                                        showMessage(getString(R.string.label_empty_list_photo));
                                    }
                                    */
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            setSwipeRefresh(false);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            setSwipeRefresh(false);
                        }
                    });
            VolleySingleton.getInstance(getActivity()).cancelPendingRequests(TAG_PHOTO_DETAIL + "_" + photo_id);
            VolleySingleton.getInstance(getActivity()).addToRequestQueue(requestMasterConfig, TAG_PHOTO_DETAIL + "_" + photo_id);
        }
    }

    public void openPhotoDetail(String photo_id) {
        Intent intent = new Intent(getActivity(), ActivityPhotoDetail.class);
        intent.putExtra(ActivityPhotoDetail.EXTRA_AB_COLOR, ab_color);
        intent.putExtra(ActivityPhotoDetail.EXTRA_CHANNEL, channel);
        intent.putExtra(ActivityPhotoDetail.EXTRA_TITLE, title);
        intent.putExtra(ActivityPhotoDetail.EXTRA_PHOTO_ID, photo_id);
        getActivity().startActivity(intent);
    }
}