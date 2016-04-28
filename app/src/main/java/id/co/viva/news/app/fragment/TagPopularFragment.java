package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActTagPopularResult;
import id.co.viva.news.app.adapter.TagListAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.GoogleMusicDicesDrawable;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.Tag;
import id.co.viva.news.app.services.Analytics;
import info.hoang8f.widget.FButton;

/**
 * Created by reza on 08/07/15.
 */
public class TagPopularFragment extends Fragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    //Parameters
    private String name;
    private String color;
    private String url;
    private String index;

    private Activity mActivity;
    private boolean isInternetPresent = false;
    private LinearLayout mParentLayout;
    private ProgressBar loading_layout;
    private FButton buttonRetry;
    private TextView labelTextNoResult;
    private TextView labelTextLoading;
    private ListView listView;

    private ArrayList<Tag> tags;
    private ArrayList<Ads> adsArrayList;

    //Ads stuff
    private PublisherAdView publisherAdViewTop;
    private PublisherAdView publisherAdViewBottom;

    public static TagPopularFragment newInstance(String name, String color,
                                                 String url, String index) {
        TagPopularFragment tagPopularFragment = new TagPopularFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("color", color);
        bundle.putString("url", url);
        bundle.putString("index", index);
        tagPopularFragment.setArguments(bundle);
        return tagPopularFragment;
    }

    private void getBundle() {
        name = getArguments().getString("name");
        color = getArguments().getString("color");
        url = getArguments().getString("url");
        index = getArguments().getString("index");
    }

    private Drawable getProgressDrawable() {
        Drawable progressDrawable;
        progressDrawable = new GoogleMusicDicesDrawable.Builder().build();
        return progressDrawable;
    }

    private void checkCache(String mUrl, String mIndex) {
        if (Global.getInstance(getActivity()).getRequestQueue().getCache().get(mUrl) != null) {
            String cachedResponse = new String(Global.getInstance(getActivity())
                    .getRequestQueue().getCache().get(mUrl).data);
            parseJson(cachedResponse, mIndex);
        } else {
            loading_layout.setVisibility(View.GONE);
            labelTextLoading.setVisibility(View.GONE);
            buttonRetry.setVisibility(View.VISIBLE);
        }
    }

    private void setAnalytic(String screenName) {
        Analytics analytics = new Analytics(getActivity());
        analytics.getAnalyticByATInternet(screenName.replace(" ", "_") + "_Screen");
        analytics.getAnalyticByGoogleAnalytic(screenName.replace(" ", "_") + "_Screen");
    }

    private void defineViews(View rootView) {
        //Parent layout
        mParentLayout = (LinearLayout) rootView.findViewById(R.id.parent_layout);
        //Loading progress
        loading_layout = (ProgressBar) rootView.findViewById(R.id.progress_wheel_tag_popular_list);
        Rect bounds = loading_layout.getIndeterminateDrawable().getBounds();
        loading_layout.setIndeterminateDrawable(getProgressDrawable());
        loading_layout.getIndeterminateDrawable().setBounds(bounds);
        //Retry Button
        buttonRetry = (FButton) rootView.findViewById(R.id.btn_retry_list_tag_popular);
        buttonRetry.setOnClickListener(this);
        //Label text
        labelTextNoResult = (TextView) rootView.findViewById(R.id.text_no_result_tag_popular_list);
        labelTextLoading = (TextView) rootView.findViewById(R.id.text_loading_data);
        TextView labelText = (TextView) rootView.findViewById(R.id.text_label_tag_popular);
        labelText.setText(name.toUpperCase());
        //Tag list
        listView = (ListView) rootView.findViewById(R.id.list_tag_popular);
        listView.setOnItemClickListener(this);
        //Collection
        tags = new ArrayList<>();
        adsArrayList = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private void retrieveData(final String mUrl, final String arrayType) {
        StringRequest request = new StringRequest(Request.Method.GET,
                mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                parseJson(s, arrayType);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                checkCache(mUrl, arrayType);
            }
        });
        request.setShouldCache(true);
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constant.TIME_OUT,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Global.getInstance(getActivity()).getRequestQueue().getCache().invalidate(mUrl, true);
        Global.getInstance(getActivity()).getRequestQueue().getCache().get(mUrl);
        Global.getInstance(getActivity()).addToRequestQueue(request, Constant.JSON_REQUEST);
    }

    private void parseJson(String response, String arrayType) {
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
                        String key = json.getString("keypr");
                        String hitUrl = json.getString("hit_ori");
                        tags.add(new Tag(hitUrl, key));
                        Log.i(Constant.TAG, "TAG LIST : " + tags.get(z).getKey());
                    }
                }
                //Check Ads if exists
                if (isInternetPresent) {
                    if (jsonArrayResponses.length() > 1) {
                        Log.i(Constant.TAG, "Loading Ads...");
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
                                Log.i(Constant.TAG, "ADS : " + adsArrayList.get(j).getmUnitId());
                            }
                        }
                    }
                }
            }
            //Fill data from API
            if (tags.size() > 0 || !tags.isEmpty()) {
                TagListAdapter adapter = new TagListAdapter(getActivity(), tags);
                ScaleInAnimationAdapter mAnimAdapter = new ScaleInAnimationAdapter(adapter);
                mAnimAdapter.setAbsListView(listView);
                listView.setAdapter(mAnimAdapter);
                mAnimAdapter.notifyDataSetChanged();
                loading_layout.setVisibility(View.GONE);
                labelTextLoading.setVisibility(View.GONE);
            } else {
                loading_layout.setVisibility(View.GONE);
                labelTextLoading.setVisibility(View.GONE);
                labelTextNoResult.setVisibility(View.VISIBLE);
            }
            //Show Ads
            if (isInternetPresent) {
                showAds();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_tag_popular, container, false);
        defineViews(rootView);
        setAnalytic(name);
        if (isInternetPresent) {
            retrieveData(url, index);
        } else {
            checkCache(url, index);
        }

        ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
        ab.setIcon(R.drawable.logo_viva_coid_second);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        buttonRetry.setVisibility(View.GONE);
        loading_layout.setVisibility(View.VISIBLE);
        labelTextLoading.setVisibility(View.VISIBLE);
        if (isInternetPresent) {
            retrieveData(url, index);
        } else {
            Toast.makeText(getActivity(),
                    R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (tags.size() > 0) {
            Tag tag = tags.get(position);
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("key", tag.getKey());
            bundle.putString("url", tag.getUrl());
            Intent intent = new Intent(getActivity(), ActTagPopularResult.class);
            intent.putExtras(bundle);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
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
