package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.melnykov.fab.FloatingActionButton;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActDetailMain;
import id.co.viva.news.app.adapter.ListMainAdapter;
import id.co.viva.news.app.adapter.ListMainSmallAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.GoogleMusicDicesDrawable;
import id.co.viva.news.app.component.LoadMoreListView;
import id.co.viva.news.app.interfaces.OnLoadMoreListener;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.EntityMain;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.share.Prefs;

/**
 * Created by reza on 29/06/15.
 */
public class ListMainFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, OnLoadMoreListener {

    private Prefs prefs;
    private boolean is_first_run = true;

    //Internet connection flag
    private boolean isInternetPresent = false;

    //Activity attached
    private Activity mActivity;

    //Pagination
    private int page = 1;

    //Parameters
    private String name;
    private String color;
    private String screen;
    private String url;
    private String index;
    private String layout;

    //All views
    public static ArrayList<EntityMain> entityList;
    private LinearLayout mParentLayout;
    private ProgressBar loading_layout;
    private TextView labelLoadData;
    private RippleView rippleView;
    private Analytics analytics;
    private TextView lastUpdate;
    private LoadMoreListView listViewSmallCard;
    private LoadMoreListView listView;
    private FloatingActionButton floatingActionButton;
    private ArrayList<Ads> adsArrayList;
    private PublisherAdView publisherAdViewTop;
    private PublisherAdView publisherAdViewBottom;

    //Adapter
    private ListMainAdapter adapter;
    private ListMainSmallAdapter smallAdapter;
    private ScaleInAnimationAdapter mAnimAdapter;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;

    //Load more stuff
    private String lastPublished;
    private boolean isLoadMoreContent = false;

    public static ListMainFragment newInstance(String name, String color,
                                               String screen, String url, String index, String layout) {
        ListMainFragment listMainFragment = new ListMainFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("color", color);
        bundle.putString("screen", screen);
        bundle.putString("url", url);
        bundle.putString("index", index);
        bundle.putString("layout", layout);
        listMainFragment.setArguments(bundle);
        return listMainFragment;
    }

    private void getBundle() {
        name = getArguments().getString("name");
        color = getArguments().getString("color");
        screen = getArguments().getString("screen");
        url = getArguments().getString("url");
        index = getArguments().getString("index");
        layout = getArguments().getString("layout");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = Prefs.getInstance(getActivity());

        setHasOptionsMenu(true);
        mActivity = getActivity();

        isInternetPresent = Global.getInstance(getActivity())
                .getConnectionStatus().isConnectingToInternet();
        getBundle();
//        AppCompatActivity activity = (AppCompatActivity) mActivity;
//        if (activity != null) {
//            activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
//            activity.getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
//        }
    }

    private Drawable getProgressDrawable() {
        Drawable progressDrawable;
        progressDrawable = new GoogleMusicDicesDrawable.Builder().build();
        return progressDrawable;
    }

    private void checkCache() {
        if (Global.getInstance(getActivity()).getRequestQueue().getCache().get(url + "/screen/" + screen + "_screen") != null) {
            String cachedResponse = new String(Global.getInstance(getActivity())
                    .getRequestQueue().getCache().get(url + "/screen/" + screen + "_screen").data);
            parseJson(cachedResponse, index, isLoadMoreContent);
        } else {
            loading_layout.setVisibility(View.GONE);
            labelLoadData.setVisibility(View.GONE);
            rippleView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_main_list, container, false);
        defineViews(rootView);
        if (isInternetPresent) {
            retrieveData(url, index, screen);
        } else {
            checkCache();
            Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
        ab.setIcon(R.drawable.logo_viva_coid_second);

        return rootView;
    }

    private void defineViews(View rootView) {
        mParentLayout = (LinearLayout) rootView.findViewById(R.id.parent_layout);

        //Loading Progress
        loading_layout = (ProgressBar) rootView.findViewById(R.id.loading_progress_layout_headline_terbaru);
        labelLoadData = (TextView) rootView.findViewById(R.id.text_loading_data);
        Rect bounds = loading_layout.getIndeterminateDrawable().getBounds();
        loading_layout.setIndeterminateDrawable(getProgressDrawable());
        loading_layout.getIndeterminateDrawable().setBounds(bounds);
        labelLoadData.setVisibility(View.VISIBLE);
        loading_layout.setVisibility(View.VISIBLE);

        //Retry Button
        rippleView = (RippleView) rootView.findViewById(R.id.layout_ripple_view);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        //All about analytic
        analytics = new Analytics(getActivity());
        analytics.getAnalyticByATInternet(name.replace(" ", "_") + "_Screen_" + String.valueOf(page));
        analytics.getAnalyticByGoogleAnalytic(name.replace(" ", "_") + "_Screen_" + String.valueOf(page));

        //Set some text on top of view
        TextView labelText = (TextView) rootView.findViewById(R.id.text_main_list);
        labelText.setText(name.toUpperCase());
        lastUpdate = (TextView) rootView.findViewById(R.id.date_main_list);

        //Big Card List Content
        listView = (LoadMoreListView) rootView.findViewById(R.id.list_main_list);
        listView.setVisibility(View.GONE);
        listView.setOnItemClickListener(this);
        listView.setOnLoadMoreListener(this);

        //Small Card List Content
        listViewSmallCard = (LoadMoreListView) rootView.findViewById(R.id.list_main_list_small_card);
        listViewSmallCard.setOnItemClickListener(this);
        listViewSmallCard.setOnLoadMoreListener(this);

        //Set 'go to the top' button
        floatingActionButton = (FloatingActionButton) rootView.findViewById(R.id.fab);
        floatingActionButton.setVisibility(View.GONE);
        //Set floating button into big card list
        floatingActionButton.attachToListView(listView, new FloatingActionButton.FabOnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                int firstIndex = listView.getFirstVisiblePosition();
                if (firstIndex > Constant.NUMBER_OF_TOP_LIST_ITEMS_BIG_CARD) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        });
        //Set floating button into small card list
        floatingActionButton.attachToListView(listViewSmallCard, new FloatingActionButton.FabOnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                super.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                int firstIndex = listViewSmallCard.getFirstVisiblePosition();
                if (firstIndex > Constant.NUMBER_OF_TOP_LIST_ITEMS_SMALL_CARD) {
                    floatingActionButton.setVisibility(View.VISIBLE);
                } else {
                    floatingActionButton.setVisibility(View.GONE);
                }
            }
        });
        //Handle floating onClick
        floatingActionButton.setOnClickListener(this);

        //Populate content
        entityList = new ArrayList<>();
        adsArrayList = new ArrayList<>();
    }

    private void retrieveData(final String url, final String arrayType, String screenType) {
        StringRequest request = new StringRequest(Request.Method.GET,
                url + "/screen/" + screenType + "_screen", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                parseJson(s, arrayType, isLoadMoreContent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                checkCache();
            }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(url + "/screen/" + screenType + "_screen", true);
        Global.getInstance(getActivity()).getRequestQueue().getCache().get(url + "/screen/" + screenType + "_screen");
        Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void loadMore(final String url, final String arrayType, String screenType, String lastPublished) {
        StringRequest request = new StringRequest(Request.Method.GET,
                url + "/screen/" + screenType + "_screen" + "/published/" + lastPublished, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                isLoadMoreContent = true;
                parseJson(s, arrayType, isLoadMoreContent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (listView.getVisibility() == View.VISIBLE) {
                    listView.onLoadMoreComplete();
                    //listView.setSelection(0);
                } else if (listViewSmallCard.getVisibility() == View.VISIBLE) {
                    listViewSmallCard.onLoadMoreComplete();
                    //listViewSmallCard.setSelection(0);
                }
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.label_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(getActivity()).getRequestQueue().getCache()
                .invalidate(url + "/screen/" + screenType + "_screen" + "/published/" + lastPublished, true);
        Global.getInstance(getActivity()).getRequestQueue().getCache()
                .get(url + "/screen/" + screenType + "_screen" + "/published/" + lastPublished);
        Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void parseJson(String response, String arrayType, boolean isLoadMore) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArrayResponses = jsonObject.getJSONArray(Constant.response);
            if (jsonArrayResponses.length() > 0) {
                //Get content list
                JSONObject obj = jsonArrayResponses.getJSONObject(0);
                JSONArray jsonArraySegmentNews = obj.getJSONArray(arrayType);
                if (jsonArraySegmentNews.length() > 0) {
                    for (int z = 0; z < jsonArraySegmentNews.length(); z++) {
                        JSONObject json = jsonArraySegmentNews.getJSONObject(z);
                        String id = json.getString(Constant.id);
                        String title = json.getString(Constant.title);
                        String channel = json.getString(Constant.kanal);
                        String url = json.getString(Constant.url);
                        String image_url = json.getString(Constant.image_url);
                        String date_publish = json.getString(Constant.date_publish);
                        String timestamp = json.getString(Constant.timestamp);

                        boolean exist = false;
                        for(EntityMain entityMain : entityList) {
                            if(id.equals(entityMain.getId())) {
                                exist = true;
                                break;
                            }
                        }
                        if(!exist) {
                            entityList.add(new EntityMain(id, title, channel, url, image_url, date_publish, timestamp));
                        }
                    }
                }
                //Check Ads if exists
                if (isInternetPresent && !isLoadMore) {
                    if (jsonArrayResponses.length() > 1) {
//                        Log.i(Constant.TAG, "Loading Ads...");
                        JSONObject objAds = jsonArrayResponses.getJSONObject(jsonArrayResponses.length() - 1);
                        JSONArray jsonArraySegmentAds = objAds.getJSONArray(Constant.adses);
                        if (jsonArraySegmentAds.length() > 0) {
                            for (int j = 0; j < jsonArraySegmentAds.length(); j++) {
                                JSONObject jsonAds = jsonArraySegmentAds.getJSONObject(j);
                                String name = jsonAds.getString(Constant.name);
                                int position = jsonAds.getInt(Constant.position);
                                int type = jsonAds.getInt(Constant.type);
                                String unit_id = jsonAds.getString(Constant.unit_id);
                                adsArrayList.add(new Ads(name, type, position, unit_id));
//                                Log.i(Constant.TAG, "ADS : " + adsArrayList.get(j).getmUnitId());
                            }
                        }
                    }
                }
            }
            //Get last published
            lastPublished = entityList.get(entityList.size() - 1).getTimeStamp();
            //Fill data from API
            if (entityList.size() > 0 || !entityList.isEmpty()) {
                //Big Card List Style
                if (!isLoadMore) {
                    if (adapter == null) {
                        adapter = new ListMainAdapter(getActivity(), entityList, "main");
                    }
                    if (swingBottomInAnimationAdapter == null) {
                        swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
                    }
                    swingBottomInAnimationAdapter.setAbsListView(listView);
                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                    listView.setAdapter(swingBottomInAnimationAdapter);
                    adapter.notifyDataSetChanged();
                } else {
                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(adapter);
                    swingBottomInAnimationAdapter.setAbsListView(listView);
                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                    adapter.notifyDataSetChanged();
                    listView.onLoadMoreComplete();
                }
                //Small Card List Style
                if (!isLoadMore) {
                    if (smallAdapter == null) {
                        smallAdapter = new ListMainSmallAdapter(getActivity(), Constant.SMALL_LIST_DEFAULT, null, entityList);
                    }
                    if (mAnimAdapter == null) {
                        mAnimAdapter = new ScaleInAnimationAdapter(smallAdapter);
                    }
                    mAnimAdapter.setAbsListView(listViewSmallCard);
                    listViewSmallCard.setAdapter(mAnimAdapter);
                    mAnimAdapter.notifyDataSetChanged();
                } else {
                    mAnimAdapter = new ScaleInAnimationAdapter(smallAdapter);
                    mAnimAdapter.setAbsListView(listViewSmallCard);
                    mAnimAdapter.notifyDataSetChanged();
                    listViewSmallCard.onLoadMoreComplete();
                }
                //Hide progress
                if (rippleView.getVisibility() == View.VISIBLE) {
                    rippleView.setVisibility(View.GONE);
                }
                loading_layout.setVisibility(View.GONE);
                labelLoadData.setVisibility(View.GONE);
            }
            //Internet checking process
            if (!isLoadMore) {
                if (isInternetPresent) {
                    //Show Ads
                    showAds();
                    //Set local time
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat date = new SimpleDateFormat("dd MMM yyyy");
                    SimpleDateFormat time = new SimpleDateFormat("HH:mm");
                    String date_name = date.format(cal.getTime());
                    String time_name = time.format(cal.getTime());
                    lastUpdate.setText(date_name + " | " + time_name);
                } else {
                    lastUpdate.setText(R.string.label_content_not_update);
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void showAds() {
        if (getActivity() != null) {
            if (adsArrayList != null) {
                if (adsArrayList.size() > 0) {
                    AdsConfig adsConfig = new AdsConfig();
                    for (int i = 0; i < adsArrayList.size(); i++) {
                        if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_TOP) {
                            if (publisherAdViewTop == null) {
                                publisherAdViewTop = new PublisherAdView(getActivity());
                                adsConfig.setAdsBanner(publisherAdViewTop,
                                        adsArrayList.get(i).getmUnitId(), Constant.POSITION_BANNER_TOP, mParentLayout);
                            }
                        } else if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_BOTTOM) {
                            if (publisherAdViewBottom == null) {
                                publisherAdViewBottom = new PublisherAdView(getActivity());
                                adsConfig.setAdsBanner(publisherAdViewBottom,
                                        adsArrayList.get(i).getmUnitId(), Constant.POSITION_BANNER_BOTTOM, mParentLayout);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                if (listView.getVisibility() == View.VISIBLE) {
                    listView.setSelection(0);
                } else if (listViewSmallCard.getVisibility() == View.VISIBLE) {
                    listViewSmallCard.setSelection(0);
                }
                break;
            case R.id.layout_ripple_view:
                if (isInternetPresent) {
                    loading_layout.setVisibility(View.VISIBLE);
                    labelLoadData.setVisibility(View.VISIBLE);
                    rippleView.setVisibility(View.GONE);
                    retrieveData(url, index, screen);
                } else {
                    Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (entityList.size() > 0) {
            EntityMain entityMain = entityList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id", entityMain.getId());
            bundle.putString("screen", screen);
            bundle.putString("name", name);
            Intent intent = new Intent(getActivity(), ActDetailMain.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public void onLoadMore() {
        if (screen.equalsIgnoreCase("headline") || screen.equalsIgnoreCase("populer")) {
            if (listView.getVisibility() == View.VISIBLE) {
                listView.onLoadMoreComplete();
            } else if (listViewSmallCard.getVisibility() == View.VISIBLE) {
                listViewSmallCard.onLoadMoreComplete();
            }
        } else {
            if (isInternetPresent) {
                page += 1;
                analytics.getAnalyticByATInternet(
                        name.replace(" ", "_") + "_Screen_" + String.valueOf(page));
                analytics.getAnalyticByGoogleAnalytic(
                        name.replace(" ", "_") + "_Screen_" + String.valueOf(page));
//                Log.i(Constant.TAG, "Last Published : " + lastPublished);
                loadMore(url, index, screen, lastPublished);
            } else {
                if (listView.getVisibility() == View.VISIBLE) {
                    listView.onLoadMoreComplete();
                } else if (listViewSmallCard.getVisibility() == View.VISIBLE) {
                    listViewSmallCard.onLoadMoreComplete();
                }
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_change_layout) {
            if (getActivity() != null) {
                getActivity().invalidateOptionsMenu();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (layout.equalsIgnoreCase("text-image")) {
            inflater.inflate(R.menu.menu_frag_channel, menu);
            boolean is_small_card = prefs.isSettingSmallCard();;
            if (is_first_run) {
                is_first_run = !is_first_run;
            } else {
                prefs.setSettingSmallCard(!is_small_card);
                prefs.storeSetting();
            }
            is_small_card = prefs.isSettingSmallCard();

//            if (listViewSmallCard.getVisibility() == View.VISIBLE) {
            if (!is_small_card) {
                listViewSmallCard.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                if (menu != null) {
                    if (menu.hasVisibleItems()) {
                        if (menu.findItem(R.id.action_change_layout) != null) {
                            menu.removeItem(R.id.action_change_layout);
                        }
                    }
                }
                assert menu != null;
                MenuItem mi = menu.add(Menu.NONE, R.id.action_change_layout, 2, "");
                mi.setIcon(R.drawable.ic_preview_small);
                mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            } else {
                listViewSmallCard.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                if (menu != null) {
                    if (menu.hasVisibleItems()) {
                        if (menu.findItem(R.id.action_change_layout) != null) {
                            menu.removeItem(R.id.action_change_layout);
                        }
                    }
                }
                assert menu != null;
                MenuItem mi = menu.add(Menu.NONE, R.id.action_change_layout, 2, "");
                mi.setIcon(R.drawable.ic_preview_big);
                mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.resume();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.resume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.pause();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.pause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (publisherAdViewBottom != null) {
            publisherAdViewBottom.destroy();
        }
        if (publisherAdViewTop != null) {
            publisherAdViewTop.destroy();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            adapter.initScreenOrientation();
        }
    }
}