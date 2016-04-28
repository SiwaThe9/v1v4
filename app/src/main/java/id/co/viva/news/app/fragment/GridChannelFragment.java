package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActDetailChannel;
import id.co.viva.news.app.activity.ActDetailContentDefault;
import id.co.viva.news.app.adapter.ChannelGridAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.ExpandableHeightGridView;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.Channel;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 22/10/14.
 */
public class GridChannelFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {
    private DisplayImageOptions options;

    private ArrayList<Channel> channels;
    private ArrayList<Ads> adsArrayList;
    private Activity mActivity;
    private boolean isInternetPresent = false;
    private ExpandableHeightGridView gridChannel;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;
    private LinearLayout mParentLayout;
    private ProgressWheel progressWheel;
    private TextView tvNoResult;
    private TextView textHeader;
    private RippleView rippleView;
    private ImageView imageHeader;
    private String channel_title_header_grid;
    private String id_header_grid;
    private String image_url_header_grid;
    private String level_header_grid;
    private String channel_header_grid;
    private RelativeLayout layoutTransparentHeader;
    private Button buttonRetry;

    //Parameters
    private String name;
    private String color;
    private String screen;
    private String url;
    private String index;

    public static GridChannelFragment newInstance(String name, String color,
                                               String screen, String url, String index) {
        GridChannelFragment gridChannelFragment = new GridChannelFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("color", color);
        bundle.putString("screen", screen);
        bundle.putString("url", url);
        bundle.putString("index", index);
        gridChannelFragment.setArguments(bundle);
        return gridChannelFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(getActivity()));
        }

        options = new DisplayImageOptions.Builder()
                .preProcessor(new BitmapProcessorCenterCrop())
//                .displayer(new RoundedBitmapDisplayer(512))
                .showImageOnLoading(R.drawable.default_image)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .build();

        isInternetPresent = Global.getInstance(getActivity())
                .getConnectionStatus().isConnectingToInternet();
        getBundle();
        AppCompatActivity activity = (AppCompatActivity) mActivity;
        if (activity != null) {
            activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(color)));
            activity.getSupportActionBar().setIcon(R.drawable.logo_viva_coid_second);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setHasOptionsMenu(true);
        mActivity = activity;
    }

    private void getBundle() {
        name = getArguments().getString("name");
        color = getArguments().getString("color");
        screen = getArguments().getString("screen");
        url = getArguments().getString("url");
        index = getArguments().getString("index");
    }

    private void defineViews(View rootView) {
        mParentLayout = (LinearLayout) rootView.findViewById(R.id.parent_layout);

        if (isInternetPresent) {
            Analytics analytics = new Analytics(getActivity());
            analytics.getAnalyticByATInternet(name.replace(" ", "_") + "_Screen");
            analytics.getAnalyticByGoogleAnalytic(name.replace(" ", "_") + "_Screen");
        }

        tvNoResult = (TextView) rootView.findViewById(R.id.text_no_result);
        tvNoResult.setVisibility(View.GONE);

        textHeader = (TextView) rootView.findViewById(R.id.header_title_channel);
        imageHeader = (ImageView) rootView.findViewById(R.id.header_grid);
        imageHeader.setOnClickListener(this);
        imageHeader.setFocusableInTouchMode(true);

        layoutTransparentHeader = (RelativeLayout) rootView.findViewById(R.id.header_grid_transparent);
        layoutTransparentHeader.setVisibility(View.GONE);

        if (Constant.isTablet(getActivity())) {
            imageHeader.getLayoutParams().height = Constant.getDynamicImageSize(getActivity(), Constant.DYNAMIC_SIZE_GRID_TYPE);
            layoutTransparentHeader.getLayoutParams().height = Constant.getDynamicImageSize(getActivity(), Constant.DYNAMIC_SIZE_GRID_TYPE);
        }

        progressWheel = (ProgressWheel) rootView.findViewById(R.id.progress_wheel);
        progressWheel.setVisibility(View.VISIBLE);

        rippleView = (RippleView) rootView.findViewById(R.id.layout_ripple_view);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        buttonRetry = (Button) rootView.findViewById(R.id.btn_retry_channel_grid);

        gridChannel = (ExpandableHeightGridView) rootView.findViewById(R.id.grid_channel);
        gridChannel.setExpanded(true);
        gridChannel.setOnItemClickListener(this);

        adsArrayList = new ArrayList<>();
        channels = new ArrayList<>();

        setThemeColor(screen);
    }

    private void checkCache() {
        if (Global.getInstance(getActivity()).getRequestQueue().getCache().get(url + "/screen/" + screen + "_screen") != null) {
            String cachedResponse = new String(Global.getInstance(getActivity())
                    .getRequestQueue().getCache().get(url + "/screen/" + screen + "_screen").data);
            parseJson(cachedResponse, index);
        } else {
            progressWheel.setVisibility(View.GONE);
            tvNoResult.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void setThemeColor(String screenChannel) {
        switch (screenChannel) {
            case Constant.CHANNEL_NEWS:
                progressWheel.setBarColor(ContextCompat.getColor(getActivity(), R.color.color_news));
                buttonRetry.setBackgroundResource(R.drawable.shadow_button_news);
                break;
            case Constant.CHANNEL_BOLA:
                progressWheel.setBarColor(ContextCompat.getColor(getActivity(), R.color.color_bola));
                buttonRetry.setBackgroundResource(R.drawable.shadow_button_bola);
                break;
            case Constant.CHANNEL_LIFE:
                progressWheel.setBarColor(ContextCompat.getColor(getActivity(), R.color.color_life));
                buttonRetry.setBackgroundResource(R.drawable.shadow_button_life);
                break;
            case Constant.CHANNEL_AUTO:
                progressWheel.setBarColor(ContextCompat.getColor(getActivity(), R.color.color_auto));
                buttonRetry.setBackgroundResource(R.drawable.shadow_button_otomotif);
                break;
            default:
                progressWheel.setBarColor(ContextCompat.getColor(getActivity(), R.color.news_base_color));
                buttonRetry.setBackgroundResource(R.drawable.shadow_button);
        }
    }

    private void retrieveData(final String url, final String arrayType, String screenType) {
        StringRequest request = new StringRequest(Request.Method.GET,
                url + "/screen/" + screenType + "_screen", new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                parseJson(s, arrayType);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                progressWheel.setVisibility(View.GONE);
                rippleView.setVisibility(View.VISIBLE);
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

    private void parseJson(String response, String arrayType) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray responses = jsonObject.getJSONArray(Constant.response);
            //Header
            int lastIndex = responses.length() - 1;
            JSONObject object = responses.getJSONObject(lastIndex);
            if (object != null) {
                JSONArray channels = object.getJSONArray("all");
                for (int j=0; j<channels.length(); j++) {
                    JSONObject field = channels.getJSONObject(j);
                    id_header_grid = field.getString("channel_id");
                    channel_title_header_grid = field.getString("channel_title");
                    image_url_header_grid = field.getString("image_url");
                    level_header_grid = field.getString("level");
                    channel_header_grid = field.getString("kanal");
                }
                textHeader.setText(channel_title_header_grid.toUpperCase());
                layoutTransparentHeader.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(image_url_header_grid, imageHeader, options);
            }
            //Grid view
            for (int i=0; i<responses.length() - 1; i++) {
                JSONObject obj = responses.getJSONObject(i);
                JSONArray channelNews = obj.getJSONArray(arrayType);
                if (channelNews.length() > 0) {
                    for (int j=0; j<channelNews.length(); j++) {
                        JSONObject field = channelNews.getJSONObject(j);
                        String channel_title = field.getString("channel_title");
                        String id = field.getString("id");
                        String channel_id = field.getString("channel_id");
                        String level = field.getString("level");
                        String title = field.getString("title");
                        String kanal = field.getString("kanal");
                        String image_url = field.getString("image_url");
                        String url = field.getString("url");
                        int content_type = -1;
                        if(!field.isNull("content_type")) content_type = field.getInt("content_type");
                        if (!channel_title.equalsIgnoreCase("Semua Berita")) {
                            channels.add(new Channel(channel_title, id,
                                    channel_id, level, title, kanal, image_url, url, content_type));
                            //Log.i(Constant.TAG, "TITLE GRID : " + channels.get(j).getChannel_title());
                        }
                    }
                }
            }
            //Check Ads if exists
            if (isInternetPresent) {
                JSONArray adsList = jsonObject.getJSONArray(Constant.adses);
                if (adsList.length() > 0) {
                    for (int j=0; j<adsList.length(); j++) {
                        JSONObject jsonAds = adsList.getJSONObject(j);
                        String name = jsonAds.getString(Constant.name);
                        int position = jsonAds.getInt(Constant.position);
                        int type = jsonAds.getInt(Constant.type);
                        String unit_id = jsonAds.getString(Constant.unit_id);
                        adsArrayList.add(new Ads(name, type, position, unit_id));
                        //Log.i(Constant.TAG, "ADS : " + adsArrayList.get(j).getmUnitId());
                    }
                }
            }
            //Collect for grid type
            if (channels.size() > 0 || !channels.isEmpty()) {
                SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(
                        new ChannelGridAdapter(getActivity(), channels));
                swingBottomInAnimationAdapter.setAbsListView(gridChannel);
                assert swingBottomInAnimationAdapter.getViewAnimator() != null;
                swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(0000);
                gridChannel.setAdapter(swingBottomInAnimationAdapter);
            }
            //End of process
            progressWheel.setVisibility(View.GONE);
            //Show ads
            if (isInternetPresent) {
                showAds();
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_grid_channel, container, false);
        defineViews(rootView);
        if (isInternetPresent) {
            retrieveData(url, index, screen);
        } else {
            checkCache();
        }
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.layout_ripple_view) {
            if (isInternetPresent) {
                rippleView.setVisibility(View.GONE);
                progressWheel.setVisibility(View.VISIBLE);
                retrieveData(url, index, screen);
            } else {
                Toast.makeText(getActivity(), R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.header_grid) {
            if (id_header_grid != null & channel_title_header_grid != null) {
                Bundle bundle = new Bundle();
                bundle.putString("id", id_header_grid);
                bundle.putString("channel_title", channel_title_header_grid);
                bundle.putString("color", color);
                bundle.putString("name", name);
                bundle.putString("level", level_header_grid);
                bundle.putString("channel", channel_header_grid);
                Intent intent = new Intent(getActivity(), ActDetailChannel.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        }
    }

    private void showAds() {
        if (getActivity() != null) {
            if (adsArrayList != null) {
                if (adsArrayList.size() > 0) {
                    AdsConfig adsConfig = new AdsConfig();
                    for (int i=0; i<adsArrayList.size(); i++) {
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (channels.size() > 0) {
            Channel channel = channels.get(position);
            Bundle bundle = new Bundle();
            int lastIndex = channels.size() - 1;
            //if (position == lastIndex) {
            if(channel.getContentType() == -1) {
                Intent intent = new Intent(getActivity(), ActDetailContentDefault.class);
                bundle.putString("id", channel.getId());
                bundle.putString("kanal", channel.getKanal());
                bundle.putString("shared_url", channel.getUrl());
                bundle.putString("type", "editor_choice");
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            } else {
                bundle.putString("id", channel.getChannel_id());
                bundle.putString("channel_title", channel.getChannel_title());
                bundle.putString("color", color);
                bundle.putString("name", name);
                bundle.putString("level", channels.get(position).getLevel());
                bundle.putString("channel", channels.get(position).getKanal());
                Intent intent = new Intent(getActivity(), ActDetailChannel.class);
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        }
    }

}
