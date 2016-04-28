package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ListMainAdapter;
import id.co.viva.news.app.adapter.ListMainSmallAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.GoogleMusicDicesDrawable;
import id.co.viva.news.app.component.LoadMoreListView;
import id.co.viva.news.app.interfaces.OnLoadMoreListener;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.EntityMain;
import id.co.viva.news.app.services.Analytics;

/**
 * Created by reza on 10/07/15.
 */
public class ActTagPopularResult extends ActionBarActivity implements
        View.OnClickListener, AdapterView.OnItemClickListener, OnLoadMoreListener {

    //Components
    private LinearLayout mParentLayout;
    private ProgressBar loading_layout;
    private TextView labelLoadData;
    private RippleView rippleView;
    private LoadMoreListView listView, listViewSmallCard;
    private FloatingActionButton floatingActionButton;

    //Collection
    public static ArrayList<EntityMain> entityList;
    private ArrayList<Ads> adsArrayList;

    //Paging
    private int dataSize = 0;

    //Flag
    private boolean isInternetPresent = false;
    private boolean isLoadMoreContent = false;

    //Parameters
    private String name;
    private String url;
    private String key;

    private ListMainAdapter adapter;
    private SwingBottomInAnimationAdapter swingBottomInAnimationAdapter;
    private ListMainSmallAdapter smallAdapter;
    private ScaleInAnimationAdapter mAnimAdapter;

    //Ads
    private PublisherAdView publisherAdViewTop;
    private PublisherAdView publisherAdViewBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_main_list);
        //Check existing connection
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();
        //Get parameter
        getParameters();
        //Set header
        setHeader(name);
        //Set analytic
        setAnalytic(name, key);
        //Set all views
        defineViews();
        //Get content result
        if (isInternetPresent) {
            retrieveData(url);
        } else {
            checkCache(url);
            Toast.makeText(this, R.string.title_no_connection,
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            adapter.initScreenOrientation();
        }
    }

    private void getParameters() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        url = intent.getStringExtra("url");
        key = intent.getStringExtra("key");
    }

    private void defineViews() {
        //Parent layout
        mParentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        //Loading progress
        loading_layout = (ProgressBar) findViewById(R.id.loading_progress_layout_headline_terbaru);
        labelLoadData = (TextView) findViewById(R.id.text_loading_data);
        Rect bounds = loading_layout.getIndeterminateDrawable().getBounds();
        loading_layout.setIndeterminateDrawable(getProgressDrawable());
        loading_layout.getIndeterminateDrawable().setBounds(bounds);
        //Retry Button
        rippleView = (RippleView) findViewById(R.id.layout_ripple_view);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);
        //Label text
        TextView labelText = (TextView) findViewById(R.id.text_main_list);
        labelText.setText(key);
        //Big Card List Content
        listView = (LoadMoreListView) findViewById(R.id.list_main_list);
        listView.setVisibility(View.GONE);
        listView.setOnItemClickListener(this);
        listView.setOnLoadMoreListener(this);
        //Small Card List Content
        listViewSmallCard = (LoadMoreListView) findViewById(R.id.list_main_list_small_card);
        listViewSmallCard.setOnItemClickListener(this);
        listViewSmallCard.setOnLoadMoreListener(this);
        //Set 'go to the top' button
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
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

    private void retrieveData(final String mUrl) {
        StringRequest request = new StringRequest(Request.Method.GET,
                mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                parseJson(s, isLoadMoreContent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                checkCache(mUrl);
            }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(this).getRequestQueue().getCache().invalidate(mUrl, true);
        Global.getInstance(this).getRequestQueue().getCache().get(mUrl);
        Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void loadMore(String mUrl, String page) {
        StringRequest request = new StringRequest(Request.Method.GET,
                mUrl + "/s/" + page, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                isLoadMoreContent = true;
                parseJson(s, isLoadMoreContent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (listView.getVisibility() == View.VISIBLE) {
                    listView.onLoadMoreComplete();
                } else if (listViewSmallCard.getVisibility() == View.VISIBLE) {
                    listViewSmallCard.onLoadMoreComplete();
                }
                Toast.makeText(ActTagPopularResult.this, R.string.label_error, Toast.LENGTH_SHORT).show();
            }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(this).getRequestQueue().getCache().invalidate(mUrl + "/s/" + page, true);
        Global.getInstance(this).getRequestQueue().getCache().get(mUrl + "/s/" + page);
        Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void checkCache(String mUrl) {
        if (Global.getInstance(this).getRequestQueue().getCache().get(mUrl) != null) {
            String cachedResponse = new String(Global.getInstance(this)
                    .getRequestQueue().getCache().get(mUrl).data);
            parseJson(cachedResponse, isLoadMoreContent);
        } else {
            loading_layout.setVisibility(View.GONE);
            labelLoadData.setVisibility(View.GONE);
            rippleView.setVisibility(View.VISIBLE);
        }
    }

    private void parseJson(String response, boolean isLoadMore) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject objResponse = jsonObject.getJSONObject(Constant.response);
            //Get search result list
            JSONArray jsonArrayResponses = objResponse.getJSONArray(Constant.search);
            if (jsonArrayResponses.length() > 0) {
                for (int i=0; i<jsonArrayResponses.length(); i++) {
                    JSONObject json = jsonArrayResponses.getJSONObject(i);
                    String id = json.getString(Constant.id);
                    String title = json.getString(Constant.title);
                    String channel = json.getString(Constant.kanal);
                    String url = json.getString(Constant.url);
                    String image_url = json.getString(Constant.image_url);
                    String date_publish = json.getString(Constant.date_publish);
                    String timestamp = json.getString(Constant.timestamp);
                    entityList.add(new EntityMain(id, title, channel, url, image_url, date_publish, timestamp));
                    Log.i(Constant.TAG, "RESULT TAG POPULAR : " + entityList.get(i).getTitle());
                }
            }
            //Get ads list
            if (isInternetPresent && !isLoadMore) {
                JSONArray jsonAds = objResponse.getJSONArray(Constant.adses);
                if (jsonAds.length() > 0) {
                    Log.i(Constant.TAG, "Loading Ads...");
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
            }
            if (entityList.size() > 0) {
                //Big Card List Style
                if (!isLoadMore) {
                    if (adapter == null) {
                        adapter = new ListMainAdapter(this, entityList, "popular");
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
                        smallAdapter = new ListMainSmallAdapter(this, Constant.TAG_POPULAR_RESULT_LIST, null, entityList);
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
            if (isInternetPresent && !isLoadMore) {
                //Show Ads
                showAds();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private Drawable getProgressDrawable() {
        Drawable progressDrawable;
        progressDrawable = new GoogleMusicDicesDrawable.Builder().build();
        return progressDrawable;
    }

    private void setAnalytic(String mName, String mKey) {
        Analytics analytics = new Analytics(this);
        analytics.getAnalyticByATInternet(mName.replace(" ", "_") + mKey.replace(" ", "_") + "_Screen");
        analytics.getAnalyticByGoogleAnalytic(mName.replace(" ", "_") + mKey.replace(" ", "_") + "_Screen");
    }

    private void showAds() {
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
                    retrieveData(url);
                } else {
                    Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (entityList.size() > 0) {
            EntityMain entityMain = entityList.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("id", entityMain.getId());
            bundle.putString("screen", name);
            bundle.putString("name", name);
            Intent intent = new Intent(this, ActDetailMain.class);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
        }
    }

    @Override
    public void onLoadMore() {
        if (isInternetPresent) {
            String data = String.valueOf(dataSize += 10);
            loadMore(url, data);
        } else {
            if (listView.getVisibility() == View.VISIBLE) {
                listView.onLoadMoreComplete();
            } else if (listViewSmallCard.getVisibility() == View.VISIBLE) {
                listViewSmallCard.onLoadMoreComplete();
            }
            Toast.makeText(this, R.string.label_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void setHeader(String mName) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(mName);
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
    public boolean onCreateOptionsMenu(Menu menu) {
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        //Switch View
        if (listViewSmallCard.getVisibility() == View.VISIBLE) {
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
            listView.setVisibility(View.GONE);
            listViewSmallCard.setVisibility(View.VISIBLE);
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
        return super.onPrepareOptionsMenu(menu);
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

}
