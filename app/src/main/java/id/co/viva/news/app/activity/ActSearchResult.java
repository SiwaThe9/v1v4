package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ChannelBigAdapter;
import id.co.viva.news.app.adapter.SearchResultAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.LoadMoreListView;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.interfaces.OnLoadMoreListener;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.SearchResult;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 13/10/14.
 */
public class ActSearchResult extends AppCompatActivity implements
        AdapterView.OnItemClickListener, OnLoadMoreListener {

    private TextView tvSearchResult;
    private TextView tvSearchResultNumber;
    private TextView tvNoResult;
    private LoadMoreListView listSearchResult;
    private LoadMoreListView listSearchResultBigCard;
    private String mQuery;
    private boolean isInternetPresent = false;
    private ArrayList<SearchResult> resultArrayList;
    private ArrayList<Ads> adsArrayList;
    private AnimationAdapter mAnimAdapter;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private Analytics analytics;
    private Menu mMenu;
    private int dataSize = 0;
    private String data;
    private String mTotalFound;
    private ProgressWheel progressWheel;
    private SearchResultAdapter searchResultAdapter;
    private ChannelBigAdapter bigAdapter;
    private String mKeywordFromScan;
    private LinearLayout mParentLayout;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_search_result);
        //Header
        getSupportActionBar()
                .setTitle(getResources().getString(R.string.label_pencarian));

        //Check current internet connection
        isInternetPresent = Global.getInstance(this).
                getConnectionStatus().isConnectingToInternet();

        //Param From Scan Result
        getParamFromScan();

        //Define Views
        defineViews();

        if (isInternetPresent) {
            tvNoResult.setVisibility(View.GONE);
            progressWheel.setVisibility(View.VISIBLE);
            if (mKeywordFromScan != null) {
                if (mKeywordFromScan.length() > 0) {
                    setAnalytics(mKeywordFromScan);
                    tvSearchResult.setText(Constant.SEARCH_RESULT_LABEL + mKeywordFromScan.replace("%20", " "));
                    loadQuery(mKeywordFromScan);
                }
            } else {
                handleIntent(getIntent());
            }
        } else {
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            progressWheel.setVisibility(View.GONE);
            tvNoResult.setVisibility(View.VISIBLE);
            tvSearchResult.setVisibility(View.GONE);
        }
    }

    private void defineViews() {
        mParentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        //Collections
        adsArrayList = new ArrayList<>();
        resultArrayList = new ArrayList<>();
        //Adapter
        searchResultAdapter = new SearchResultAdapter(this, resultArrayList);
        bigAdapter = new ChannelBigAdapter(this, Constant.BIG_CARD_SEARCH_RESULT, null, resultArrayList);
        //Result label
        tvSearchResult = (TextView)findViewById(R.id.text_search_result);
        tvSearchResultNumber = (TextView)findViewById(R.id.text_search_result_number);
        tvSearchResultNumber.setVisibility(View.GONE);
        tvNoResult = (TextView)findViewById(R.id.text_no_result);
        //Small Card List
        listSearchResult = (LoadMoreListView)findViewById(R.id.list_search_result);
        listSearchResult.setVisibility(View.GONE);
        listSearchResult.setOnItemClickListener(this);
        listSearchResult.setOnLoadMoreListener(this);
        //Big Card List
        listSearchResultBigCard = (LoadMoreListView)findViewById(R.id.list_search_result_big_card);
        listSearchResultBigCard.setOnItemClickListener(this);
        listSearchResultBigCard.setOnLoadMoreListener(this);
        //Progress wheel
        progressWheel = (ProgressWheel)findViewById(R.id.progress_wheel);
    }

    private void getParamFromScan() {
        Intent intent = getIntent();
        mKeywordFromScan = intent.getExtras().getString("keyword");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void setAnalytics(String key) {
        analytics = new Analytics(this);
        analytics.getAnalyticByATInternet(Constant.SEARCH_RESULT_PAGE + key.toUpperCase());
        analytics.getAnalyticByGoogleAnalytic(Constant.SEARCH_RESULT_PAGE + key.toUpperCase());
    }

    private void loadQuery(String query) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEWS_SEARCH + "q/"
                + query.replaceAll(" ", "%20") + "/s/0/" + Constant.search_screen,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String volleyResponse) {
                        Log.i(Constant.TAG, "SEARCH RESPONSES : " + volleyResponse);
                        try {
                            JSONObject jsonObject = new JSONObject(volleyResponse);
                            JSONObject response = jsonObject.getJSONObject(Constant.response);
                            //Get total news found
                            mTotalFound = response.getString(Constant.numFound);
                            //Get search result list
                            JSONArray jsonArrayResponses = response.getJSONArray(Constant.search);
                            if (jsonArrayResponses.length() > 0) {
                                for (int i=0; i<jsonArrayResponses.length(); i++) {
                                    JSONObject jsonHeadline = jsonArrayResponses.getJSONObject(i);
                                    String id = jsonHeadline.getString(Constant.id);
                                    String kanal = jsonHeadline.getString(Constant.kanal);
                                    String image_url = jsonHeadline.getString(Constant.image_url);
                                    String title = jsonHeadline.getString(Constant.title);
                                    String slug = jsonHeadline.getString(Constant.slug);
                                    String date_publish = jsonHeadline.getString(Constant.date_publish);
                                    String url = jsonHeadline.getString(Constant.url);
                                    resultArrayList.add(new SearchResult(id, kanal, image_url,
                                            title, slug, date_publish, url));
                                    Log.i(Constant.TAG, "SEARCH RESULTS : " + resultArrayList.get(i).getTitle());
                                }
                            }
                            //Get ads list
                            JSONArray jsonAds = response.getJSONArray(Constant.adses);
                            if (jsonAds.length() > 0) {
                                for (int j=0; j<jsonAds.length(); j++) {
                                    JSONObject adObj = jsonAds.getJSONObject(j);
                                    String name = adObj.getString(Constant.name);
                                    int position = adObj.getInt(Constant.position);
                                    int type = adObj.getInt(Constant.type);
                                    String unit_id = adObj.getString(Constant.unit_id);
                                    adsArrayList.add(new Ads(name, type, position, unit_id));
                                    Log.i(Constant.TAG, "ADS : " + adsArrayList.get(j).getmUnitId());
                                }
                            }
                            //Populate content
                            if (resultArrayList.size() > 0 || !resultArrayList.isEmpty()) {
                                //Show amount of query result
                                tvSearchResultNumber.setVisibility(View.VISIBLE);
                                tvSearchResultNumber.setText(Constant.SEARCH_RESULT_NUMBER_LABEL + mTotalFound);
                                //Small Card List Style
                                mAnimAdapter = new ScaleInAnimationAdapter(searchResultAdapter);
                                mAnimAdapter.setAbsListView(listSearchResult);
                                listSearchResult.setAdapter(mAnimAdapter);
                                mAnimAdapter.notifyDataSetChanged();
                                //Big Card List Style
                                swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(bigAdapter);
                                swingBottomInAnimationAdapter.setAbsListView(listSearchResultBigCard);
                                assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                listSearchResultBigCard.setAdapter(swingBottomInAnimationAdapter);
                                bigAdapter.notifyDataSetChanged();
                                //Hide progress
                                progressWheel.setVisibility(View.GONE);
                            }
                            //Set ads if exists
                            showAds();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (tvSearchResultNumber.getVisibility() == View.VISIBLE) {
                    tvSearchResultNumber.setVisibility(View.GONE);
                }
                progressWheel.setVisibility(View.GONE);
                MenuItem searchItem = mMenu.findItem(R.id.action_search);
                android.support.v7.widget.SearchView searchView =
                        (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
                searchView.setIconified(false);
            }
        });
        stringRequest.setShouldCache(true);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT_REGISTRATION,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.NEWS_SEARCH + "q/"
                + query.replaceAll(" ", "%20") + "/s/0/" + Constant.search_screen, true);
        Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEWS_SEARCH + "q/"
                + query.replaceAll(" ", "%20") + "/s/0/" + Constant.search_screen);
        Global.getInstance(this).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equalsIgnoreCase(intent.getAction())) {
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            tvSearchResult.setText(Constant.SEARCH_RESULT_LABEL + mQuery);
            //Analytic
            setAnalytics(mQuery);
            //Load Data
            loadQuery(mQuery);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (resultArrayList.size() > 0) {
            SearchResult searchResult = resultArrayList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id", searchResult.getId());
            bundle.putString("type", "search");
            bundle.putString("kanal", searchResult.getKanal());
            bundle.putString("shared_url", searchResult.getUrl());
            Intent intent = new Intent(this, ActDetailContentDefault.class);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_channel_detail, menu);
        //SearchView OnClick
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView =
                (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }
        return true;
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
    public void onLoadMore() {
        data = String.valueOf(dataSize += 10);
        if (mKeywordFromScan != null) {
            loadMoreData(mKeywordFromScan);
        } else {
            loadMoreData(mQuery);
        }
    }

    private void loadMoreData(final String query) {
        if (isInternetPresent) {
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.NEWS_SEARCH + "q/"
                    + query.replaceAll(" ", "%20") + "/s/" + data + "/" + Constant.search_screen,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                JSONObject response = jsonObject.getJSONObject(Constant.response);
                                JSONArray jsonArrayResponses = response.getJSONArray(Constant.search);
                                if (jsonArrayResponses != null) {
                                    for (int i=0; i<jsonArrayResponses.length(); i++) {
                                        JSONObject jsonHeadline = jsonArrayResponses.getJSONObject(i);
                                        String id = jsonHeadline.getString(Constant.id);
                                        String kanal = jsonHeadline.getString(Constant.kanal);
                                        String image_url = jsonHeadline.getString(Constant.image_url);
                                        String title = jsonHeadline.getString(Constant.title);
                                        String slug = jsonHeadline.getString(Constant.slug);
                                        String date_publish = jsonHeadline.getString(Constant.date_publish);
                                        String url = jsonHeadline.getString(Constant.url);
                                        resultArrayList.add(new SearchResult(id, kanal, image_url,
                                                title, slug, date_publish, url));
                                        Log.i(Constant.TAG, "LOAD MORE SEARCH RESULTS : " + resultArrayList.get(i).getTitle());
                                    }
                                }
                                if (resultArrayList.size() > 0 || !resultArrayList.isEmpty()) {
                                    //Small Card List Style
                                    mAnimAdapter = new ScaleInAnimationAdapter(searchResultAdapter);
                                    mAnimAdapter.setAbsListView(listSearchResult);
                                    mAnimAdapter.notifyDataSetChanged();
                                    listSearchResult.onLoadMoreComplete();
                                    //Big Card List Style
                                    swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(bigAdapter);
                                    swingBottomInAnimationAdapter.setAbsListView(listSearchResultBigCard);
                                    assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                                    swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(1000);
                                    bigAdapter.notifyDataSetChanged();
                                    listSearchResultBigCard.onLoadMoreComplete();
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if (listSearchResult.getVisibility() == View.VISIBLE) {
                        listSearchResult.onLoadMoreComplete();
                        listSearchResult.setSelection(0);
                    } else if (listSearchResultBigCard.getVisibility() == View.VISIBLE) {
                        listSearchResultBigCard.onLoadMoreComplete();
                        listSearchResultBigCard.setSelection(0);
                    }
                    Toast.makeText(ActSearchResult.this, R.string.label_error, Toast.LENGTH_SHORT).show();
                }
            });
            stringRequest.setShouldCache(true);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.NEWS_SEARCH + "q/"
                    + query.replaceAll(" ", "%20") + "/s/" + data + "/" + Constant.search_screen, true);
            Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEWS_SEARCH + "q/"
                    + query.replaceAll(" ", "%20") + "/s/" + data + "/" + Constant.search_screen);
            Global.getInstance(this).addToRequestQueue(stringRequest, Constant.JSON_REQUEST);
        } else {
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void showAds() {
        if (ActSearchResult.this != null) {
            if (adsArrayList != null) {
                if (adsArrayList.size() > 0) {
                    AdsConfig adsConfig = new AdsConfig();
                    for (int i=0; i<adsArrayList.size(); i++) {
                        if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_TOP) {
                            if (publisherAdViewTop == null) {
                                publisherAdViewTop = new PublisherAdView(this);
                                adsConfig.setAdsBanner(publisherAdViewTop,
                                        adsArrayList.get(i).getmUnitId(), Constant.POSITION_BANNER_TOP, mParentLayout);
                            }
                        } else if (adsArrayList.get(i).getmPosition() == Constant.POSITION_BANNER_BOTTOM) {
                            if (publisherAdViewBottom == null) {
                                publisherAdViewBottom = new PublisherAdView(this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        if (item.getItemId() == R.id.action_change_layout) {
            invalidateOptionsMenu();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Switch View
        if (listSearchResult.getVisibility() == View.VISIBLE) {
            listSearchResult.setVisibility(View.GONE);
            listSearchResultBigCard.setVisibility(View.VISIBLE);
            if (menu != null) {
                if (menu.hasVisibleItems()) {
                    if (menu.findItem(R.id.action_change_layout) != null) {
                        menu.removeItem(R.id.action_change_layout);
                    }
                }
            }
            MenuItem mi = menu.add(Menu.NONE, R.id.action_change_layout, 2, "");
            mi.setIcon(R.drawable.ic_preview_small);
            mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            listSearchResultBigCard.setVisibility(View.GONE);
            listSearchResult.setVisibility(View.VISIBLE);
            if (menu != null) {
                if (menu.hasVisibleItems()) {
                    if (menu.findItem(R.id.action_change_layout) != null) {
                        menu.removeItem(R.id.action_change_layout);
                    }
                }
            }
            MenuItem mi = menu.add(Menu.NONE, R.id.action_change_layout, 2, "");
            mi.setIcon(R.drawable.ic_preview_big);
            mi.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
        return super.onPrepareOptionsMenu(menu);
    }

}