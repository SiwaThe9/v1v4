package id.co.viva.news.app.activity;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.NavigationAdapter;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.fragment.AboutFragment;
import id.co.viva.news.app.fragment.BeritaSekitarFragment;
import id.co.viva.news.app.fragment.FavoritesFragment;
import id.co.viva.news.app.fragment.GridChannelFragment;
import id.co.viva.news.app.fragment.ListMainFragment;
import id.co.viva.news.app.fragment.SubChannel2Fragment;
import id.co.viva.news.app.fragment.SubChannel3Fragment;
import id.co.viva.news.app.fragment.TagPopularFragment;
import id.co.viva.news.app.gson.GSONMainConfig;
import id.co.viva.news.app.model.ChannelURLMap;
import id.co.viva.news.app.model.NavigationItem;
import id.co.viva.news.app.object.Ads;
import id.co.viva.news.app.share.DB;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;
import id.co.viva.news.app.util.GCM;
import info.hoang8f.widget.FButton;

public class ActLanding extends AppCompatActivity implements View.OnClickListener {
    private DB db;
    private DisplayImageOptions options;

    private int navigationPosition = 0;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private android.support.v7.app.ActionBarDrawerToggle mDrawerToggle;
    private ArrayList<NavigationItem> navDrawerItems;
    private NavigationAdapter adapter;
    private Fragment fragment = null;
    private android.support.v4.app.FragmentManager fragmentManager;
    private String mFullName;
    private String mEmail;
    private String mPhotoUrl;
    private RelativeLayout mNavLayout;
    private RelativeLayout mBackground;
    private ImageView mImgProfile;
    private TextView mNameProfile;
    private TextView mEmailProfile;
    private ImageView viewSetting;
    private boolean isInternetPresent = false;
    private FButton btnRetryList;
    private ProgressWheel progressWheel;

    /**
     * Using by ChannelMapper
     */
    public static ArrayList<ChannelURLMap> channelURLMaps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = DB.getInstance(this);
        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(ActLanding.this));
        }

        options = new DisplayImageOptions.Builder()
                .preProcessor(new BitmapProcessorCenterCrop())
                .displayer(new RoundedBitmapDisplayer(512))
                .showImageOnLoading(R.drawable.default_image)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .build();

        GCM.getInstance(this).checkGCM();

        setContentView(R.layout.act_main);

        //Check Connection
        isInternetPresent = Global.getInstance(this).getConnectionStatus().isConnectingToInternet();

        //Check User Profile
        getProfile();

        //Define All Views
        defineViews();

        //Set Header
        showHeaderActionBar();

        //Define Item Navigation List
        if (savedInstanceState == null) {
            populateList();
        }
    }

    private void populateList() {
        StringRequest request = new StringRequest(Request.Method.GET, Constant.MAIN_CONFIG,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        getResponse(s);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (Global.getInstance(ActLanding.this).getRequestQueue().getCache().get(Constant.MAIN_CONFIG) != null) {
                    String cachedResponse = new String(Global.getInstance(ActLanding.this).getRequestQueue().getCache().get(Constant.MAIN_CONFIG).data);
                    getResponse(cachedResponse);
                } else {
                    progressWheel.setVisibility(View.GONE);
                    btnRetryList.setVisibility(View.VISIBLE);
                }
            }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.MAIN_CONFIG, true);
        Global.getInstance(this).getRequestQueue().getCache().get(Constant.MAIN_CONFIG);
        Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void getResponse(String response) {
        Gson gson = new GsonBuilder().create();
        try {
            GSONMainConfig gson_main_config = gson.fromJson(response, GSONMainConfig.class);
            if (gson_main_config.menus != null) {
                navDrawerItems = gson_main_config.menus;
                adapter = new NavigationAdapter(ActLanding.this, navDrawerItems);
                //adapter.selected_position = 0;
                mDrawerList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressWheel.setVisibility(View.GONE);
                mDrawerList.setVisibility(View.VISIBLE);
                //getDefaultPage(navDrawerItems);
                displayView(0);
            }
            if (gson_main_config.adses != null) {
                for (Ads ads : gson_main_config.adses) {
                    db.deleteAds(ads.screen_name, String.valueOf(ads.type), String.valueOf(ads.position));
                }
                db.addAllAds(gson_main_config.adses);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayView(int position) {
        NavigationItem items = navDrawerItems.get(position);
        switch (items.action) {
            case NavigationItem.ACTION_LIST_NEWS:
                fragment = ListMainFragment.newInstance(items.title, items.ab_color, items.name, items.url, items.json_key, items.card);
                break;
            case NavigationItem.ACTION_LIST_NEWS_BY_LOCATION:
                fragment = new BeritaSekitarFragment();
                break;
            case NavigationItem.ACTION_LIST_NEWS_BY_FAVORITE:
                fragment = new FavoritesFragment();
                break;
            case NavigationItem.ACTION_QR_CODE_SCANNER:
                scanNews();
                break;
            case NavigationItem.ACTION_LIST_TAG:
                fragment = TagPopularFragment.newInstance(items.title, items.ab_color, items.url, items.json_key);
                break;
//            case NavigationItem.ACTION_LIST_SUB_CHANNEL:
//                fragment = GridChannelFragment.newInstance(items.title, items.ab_color, items.name, items.url, items.json_key);
//                break;
            case NavigationItem.ACTION_SEND_MAIL:
                sendEmail();
                break;
            case NavigationItem.ACTION_RATE_APP:
                rateApp();
                break;
            case NavigationItem.ACTION_ABOUT:
                fragment = new AboutFragment();
                break;
            case NavigationItem.ACTION_GRID_SUB_CHANNEL:
            case NavigationItem.ACTION_LIST_SUB_CHANNEL:
                Bundle argsSubChannel2 = new Bundle();
                argsSubChannel2.putString(SubChannel2Fragment.BUNDLE_ACTION, items.action);
                argsSubChannel2.putString(SubChannel2Fragment.BUNDLE_NAME, items.name);
                argsSubChannel2.putString(SubChannel2Fragment.BUNDLE_AB_COLOR, items.ab_color);
                argsSubChannel2.putString(SubChannel2Fragment.BUNDLE_API_URL, items.url);

                fragment = new SubChannel2Fragment();
                fragment.setArguments(argsSubChannel2);

                break;
//            case NavigationItem.ACTION_LIST_SUB_CHANNEL_3:
//                Bundle argsSubChannel3 = new Bundle();
//                argsSubChannel3.putString(SubChannel2Fragment.BUNDLE_NAME, items.name);
//                argsSubChannel3.putString(SubChannel2Fragment.BUNDLE_AB_COLOR, items.ab_color);
//                argsSubChannel3.putString(SubChannel2Fragment.BUNDLE_API_URL, items.url);
//
//                fragment = new SubChannel3Fragment();
//                fragment.setArguments(argsSubChannel3);
//
//                break;
        }
        if (fragment != null) {
            navigationPosition = position;
            adapter.selected_position = position;
            adapter.notifyDataSetChanged();

            if (items.ab_color != null) {
                ActionBar ab = getSupportActionBar();
                if (ab != null) {
                    ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(items.ab_color)));
                }
            }

            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .replace(R.id.frame_container, fragment, "fragment")
                    .commit();
            //mDrawerList.setItemChecked(position, true);
            //mDrawerList.setSelection(position);
            mDrawerLayout.closeDrawer(mNavLayout);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.layout_profile:
                if (mFullName.length() > 0 && mEmail.length() > 0) {
                    Intent intent = new Intent(this, ActUserProfile.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                    mDrawerLayout.closeDrawer(mNavLayout);
                } else {
                    Intent intent = new Intent(this, ActLogin.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                    mDrawerLayout.closeDrawer(mNavLayout);
                }
                break;
            case R.id.setting:
                Intent intent = new Intent(this, ActivitySetting.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                break;
            case R.id.btn_retry_list_menu:
                if (btnRetryList.getVisibility() == View.VISIBLE) {
                    btnRetryList.setVisibility(View.GONE);
                }
                progressWheel.setVisibility(View.VISIBLE);
                populateList();
                break;
        }
    }

    private void showHeaderActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                Constant.EMAIL_SCHEME, Constant.SUPPORT_EMAIL, null));
        startActivity(Intent.createChooser(emailIntent, "Send Email"));
    }

    private void scanNews() {
        Intent intent = new Intent(this, ActScanner.class);
        startActivity(intent);
    }

    private void rateApp() {
        Uri uri = Uri.parse(getResources().getString(R.string.url_google_play) + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.label_failed_found_store, Toast.LENGTH_LONG).show();
        }
    }

    private void defineViews() {
        //Menu collection
        navDrawerItems = new ArrayList<>();

        //URL map collection
        channelURLMaps = new ArrayList<>();

        //Slider menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackgroundColor(Color.BLUE);

        //Menu list
        mDrawerList = (ListView) findViewById(R.id.list_slider_menu);

        //Toggle for slider list menu
        mDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle(this, mDrawerLayout, R.string.app_title_empty, R.string.app_title_empty) {
            public void onDrawerClosed(View view) {
            }

            public void onDrawerOpened(View drawerView) {
            }

            @Override
            public void setDrawerIndicatorEnabled(boolean enable) {
                super.setDrawerIndicatorEnabled(enable);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //Progress view
        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);

        //Button for retry getting data
        btnRetryList = (FButton) findViewById(R.id.btn_retry_list_menu);

        //Profile background
        mBackground = (RelativeLayout) findViewById(R.id.layout_profile);
        mNavLayout = (RelativeLayout) findViewById(R.id.nav_layout);
        mImgProfile = (ImageView) findViewById(R.id.img_profile);
        mNameProfile = (TextView) findViewById(R.id.tv_username);
        mEmailProfile = (TextView) findViewById(R.id.tv_user_email);
        viewSetting = (ImageView) findViewById(R.id.setting);

        btnRetryList.setOnClickListener(this);
        mBackground.setOnClickListener(this);
        if (mFullName.length() > 0 && mEmail.length() > 0) {
            mNameProfile.setText(mFullName);
            mEmailProfile.setText(mEmail);
            mEmailProfile.setVisibility(View.VISIBLE);
        } else {
            mNameProfile.setText(getResources().getString(R.string.label_not_logged_in));
            mEmailProfile.setVisibility(View.GONE);
        }
        if (mPhotoUrl.length() > 0) {
            if (isInternetPresent) {
                ImageLoader.getInstance().displayImage(mPhotoUrl, mImgProfile, options);
            } else {
                mImgProfile.setImageResource(R.drawable.ic_profile);
            }
        } else {
            mImgProfile.setImageResource(R.drawable.ic_profile);
        }
        viewSetting.setOnClickListener(this);
    }

//    private Target target = new Target() {
//        @Override
//        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//            try {
//                BitmapDrawable bitmapDrawable = new BitmapDrawable(Constant.blur(ActLanding.this, bitmap));
//                mBackground.setBackgroundDrawable(bitmapDrawable);
//            } catch (NoClassDefFoundError e) {
//                e.getMessage();
//            }
//        }
//        @Override
//        public void onBitmapFailed(Drawable errorDrawable) {
//            mBackground.setBackgroundDrawable(errorDrawable);
//        }
//        @Override
//        public void onPrepareLoad(Drawable placeHolderDrawable) {
//            mBackground.setBackgroundDrawable(placeHolderDrawable);
//        }
//    };

    private void getProfile() {
        Global.getInstance(this).getDefaultEditor();
        mFullName = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_FULL_NAME, "");
        mEmail = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_EMAIL, "");
        mPhotoUrl = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_URL_PHOTO, "");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_frag_default, menu);
        //SearchView OnClick
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavLayout)) {
            mDrawerLayout.closeDrawer(mNavLayout);
        } else {
            if (fragment != null) {
                if (navigationPosition != 0) {
                    displayView(0);
                } else {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    checkListURL();
                    finish();
                }
            } else {
                checkListURL();
                finish();
            }
        }
    }

    private void checkListURL() {
        if (channelURLMaps != null) {
            if (channelURLMaps.size() > 0) {
                channelURLMaps.clear();
            }
        }
    }
}
