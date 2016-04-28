package id.co.viva.news.app.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.viewpagerindicator.LinePageIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ImageSliderAdapter;
import id.co.viva.news.app.adapter.RelatedAdapter;
import id.co.viva.news.app.ads.AdsConfig;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.Ads;
import id.co.viva.news.app.model.Comment;
import id.co.viva.news.app.model.Favorites;
import id.co.viva.news.app.model.RelatedArticle;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 27/10/14.
 */
public class ActDetailContentDefault extends ActionBarActivity
        implements AdapterView.OnItemClickListener, View.OnClickListener {

    //All From JSON
    private String ids;
    private String title;
    private String channel_id;
    private String channel;
    private String image_url;
    private String date_publish;
    private String reporter_name;
    private String url_shared;
    private String image_caption;
    private String urlVideo;
    private String id;
    private String typeFrom;
    private String fromChannel;
    private String shared_url;
    private String sliderPhotoUrl;
    private String sliderTitle;

    private int count = 0;
    private int pageCount = 0;

    private DisplayImageOptions options;

    private int width;

    //Define Views
    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvReporterDetail;
    private ParallaxScrollView scrollView;
    private TextView tvContentDetail;
    private KenBurnsView ivThumbDetail;
    private Button btnComment;
    private TextView tvPreviewCommentUser;
    private TextView tvPreviewCommentContent;
    private LinearLayout layoutCommentPreview;
    private LinearLayout mPagingButtonLayout;
    private LinearLayout mParentLayout;
    private RelativeLayout headerRelated;
    private TextView tvNoResult;
    private ImageView next;
    private ImageView nextEnd;
    private ImageView previous;
    private ImageView previousStart;
    private TextView textPageSize;
    private TextView textPageIndex;
    private ProgressWheel progressWheel;
    private TextView textLinkVideo;

    //Checking Flag Internet
    private boolean isInternetPresent = false;

    //Data Collection
    private RelatedAdapter adapter;
    private ImageSliderAdapter imageSliderAdapter;
    private ListView listView;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;

    //Ads
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;

    //Data List
    private ArrayList<Favorites> favoritesArrayList;
    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private ArrayList<Comment> commentArrayList;
    private ArrayList<Ads> adsArrayList;
    private ArrayList<SliderContentImage> sliderContentImages;
    private ArrayList<String> pagingContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(ActDetailContentDefault.this));
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

        //Get Parameter Intent
        getParameterIntent();

        //Check internet connection
        isInternetPresent = Global.getInstance(this).
                getConnectionStatus().isConnectingToInternet();

        setContentView(R.layout.item_detail_content_default);

        //Define All View
        defineViews();

        //Set actionbar based on channel
        setThemes();

        //Get data
        if (isInternetPresent) {
            StringRequest request = new StringRequest(Request.Method.GET, Constant.NEWS_DETAIL + "id/" + id + "/screen/" + "search_detail_screen",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, volleyResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                JSONObject response = jsonObject.getJSONObject(Constant.response);
                                //Get detail content
                                JSONObject detail = response.getJSONObject(Constant.detail);
                                ids = detail.getString(Constant.id);
                                channel_id = detail.getString(Constant.channel_id);
                                channel = detail.getString(Constant.kanal);
                                title = detail.getString(Constant.title);
                                image_url = detail.getString(Constant.image_url);
                                date_publish = detail.getString(Constant.date_publish);
                                reporter_name = detail.getString(Constant.reporter_name);
                                url_shared = detail.getString(Constant.url);
                                image_caption = detail.getString(Constant.image_caption);
                                //Get detail content(s)
                                JSONArray content = detail.getJSONArray(Constant.content);
                                if (content.length() > 0) {
                                    for (int i=0; i<content.length(); i++) {
                                        String detailContent = content.getString(i);
                                        pagingContents.add(detailContent);
                                    }
                                }
                                //Get list image content
                                JSONArray sliderImageArray = detail.getJSONArray(Constant.content_images);
                                if (sliderImageArray.length() > 0) {
                                    for (int i=0; i<sliderImageArray.length(); i++) {
                                        JSONObject objSlider = sliderImageArray.getJSONObject(i);
                                        sliderPhotoUrl = objSlider.getString("src");
                                        sliderTitle = objSlider.getString("title");
                                        sliderContentImages.add(new SliderContentImage(sliderPhotoUrl, sliderTitle));
                                    }
                                }
                                //Get video content
                                JSONArray content_video = detail.getJSONArray(Constant.content_video);
                                if (content_video != null && content_video.length() > 0) {
                                    JSONObject objVideo = content_video.getJSONObject(0);
                                    urlVideo = objVideo.getString("src_1");
                                }
                                //Get related article
                                JSONArray related_article = response.getJSONArray(Constant.related_article);
                                if (related_article.length() > 0) {
                                    for (int i=0; i<related_article.length(); i++) {
                                        JSONObject objRelated = related_article.getJSONObject(i);
                                        String id = objRelated.getString(Constant.id);
                                        String article_id = objRelated.getString(Constant.article_id);
                                        String related_article_id = objRelated.getString(Constant.related_article_id);
                                        String related_title = objRelated.getString(Constant.related_title);
                                        String related_channel_level_1_id = objRelated.getString(Constant.related_channel_level_1_id);
                                        String channel_id = objRelated.getString(Constant.channel_id);
                                        String related_date_publish = objRelated.getString(Constant.related_date_publish);
                                        String image = objRelated.getString(Constant.image);
                                        String channel = objRelated.getString(Constant.kanal);
                                        String shared_url = objRelated.getString(Constant.url);
                                        relatedArticleArrayList.add(new RelatedArticle(id, article_id, related_article_id, related_title,
                                                related_channel_level_1_id, channel_id, related_date_publish, image, channel, shared_url));
                                    }
                                }
                                //Get comment list
                                JSONArray comment_list = response.getJSONArray(Constant.comment_list);
                                if (comment_list.length() > 0) {
                                    for (int i=0; i<comment_list.length(); i++) {
                                        JSONObject objRelated = comment_list.getJSONObject(i);
                                        String id = objRelated.getString(Constant.id);
                                        String name = objRelated.getString(Constant.name);
                                        String comment_text = objRelated.getString(Constant.comment_text);
                                        commentArrayList.add(new Comment(id, null, name, null, comment_text, null, null, null));
                                    }
                                }
                                //Check Ads if exists
                                JSONArray adsList = response.getJSONArray(Constant.adses);
                                if (adsList.length() > 0) {
                                    for (int j=0; j<adsList.length(); j++) {
                                        JSONObject jsonAds = adsList.getJSONObject(j);
                                        String name = jsonAds.getString(Constant.name);
                                        int position = jsonAds.getInt(Constant.position);
                                        int type = jsonAds.getInt(Constant.type);
                                        String unit_id = jsonAds.getString(Constant.unit_id);
                                        adsArrayList.add(new Ads(name, type, position, unit_id));
                                        Log.i(Constant.TAG, "ADS : " + adsArrayList.get(j).getmUnitId());
                                    }
                                }
                                //Set analytic
                                setAnalytics(title, ids);
                                //Paging check process
                                if (pagingContents.size() > 0) {
                                    setTextViewHTML(tvContentDetail, pagingContents.get(0));
                                    if (pagingContents.size() > 1) {
                                        textPageIndex.setText(String.valueOf(pageCount + 1));
                                        textPageSize.setText(String.valueOf(pagingContents.size()));
                                        mPagingButtonLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                                //Set data to view
                                tvTitleDetail.setText(title);
                                tvDateDetail.setText(date_publish);
                                tvReporterDetail.setText(reporter_name);
                                ImageLoader.getInstance().displayImage(image_url, ivThumbDetail, options);
                                //Set image(s) content
                                if (sliderContentImages.size() > 0) {
                                    imageSliderAdapter = new ImageSliderAdapter(getSupportFragmentManager(), sliderContentImages);
                                    viewPager.setAdapter(imageSliderAdapter);
                                    viewPager.setCurrentItem(0);
                                    imageSliderAdapter.notifyDataSetChanged();
                                    linePageIndicator.setViewPager(viewPager);
                                    viewPager.setVisibility(View.VISIBLE);
                                    linePageIndicator.setVisibility(View.VISIBLE);
                                }
                                //Related article
                                if (relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                                    adapter = new RelatedAdapter(ActDetailContentDefault.this, relatedArticleArrayList);
                                    listView.setAdapter(adapter);
                                    Constant.setListViewHeightBasedOnChildren(listView);
                                    adapter.notifyDataSetChanged();
                                    headerRelated.setVisibility(View.VISIBLE);
                                    if (fromChannel != null) {
                                        if (fromChannel.equalsIgnoreCase("bola") || fromChannel.equalsIgnoreCase("sport")) {
                                            headerRelated.setBackgroundResource(R.color.color_bola);
                                        } else if (fromChannel.equalsIgnoreCase("vivalife")) {
                                            headerRelated.setBackgroundResource(R.color.color_life);
                                        } else if (fromChannel.equalsIgnoreCase("otomotif")) {
                                            headerRelated.setBackgroundResource(R.color.color_auto);
                                        } else {
                                            headerRelated.setBackgroundResource(R.color.color_news);
                                        }
                                    } else {
                                        headerRelated.setBackgroundResource(R.color.news_base_color);
                                    }
                                }
                                //Preview comment list
                                if (commentArrayList.size() > 0) {
                                    layoutCommentPreview.setVisibility(View.VISIBLE);
                                    Thread thread = new Thread() {
                                        @Override
                                        public void run() {
                                            try {
                                                while (true) {
                                                    Thread.sleep(3000);
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            tvPreviewCommentUser.setText(commentArrayList.get(count).getUsername());
                                                            tvPreviewCommentContent.setText(commentArrayList.get(count).getComment_text());
                                                            count++;
                                                            if (count >= commentArrayList.size()) {
                                                                count = 0;
                                                            }
                                                        }
                                                    });
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    };
                                    thread.start();
                                } else {
                                    btnComment.setVisibility(View.VISIBLE);
                                    if (fromChannel != null) {
                                        if (fromChannel.equalsIgnoreCase("bola") || fromChannel.equalsIgnoreCase("sport")) {
                                            btnComment.setBackgroundColor(ContextCompat.getColor(ActDetailContentDefault.this, R.color.color_bola));
                                        } else if (fromChannel.equalsIgnoreCase("vivalife")) {
                                            btnComment.setBackgroundColor(ContextCompat.getColor(ActDetailContentDefault.this, R.color.color_life));
                                        } else if (fromChannel.equalsIgnoreCase("otomotif")) {
                                            btnComment.setBackgroundColor(ContextCompat.getColor(ActDetailContentDefault.this, R.color.color_auto));
                                        } else {
                                            btnComment.setBackgroundColor(ContextCompat.getColor(ActDetailContentDefault.this, R.color.color_news));
                                        }
                                    } else {
                                        btnComment.setBackgroundColor(ContextCompat.getColor(ActDetailContentDefault.this, R.color.news_base_color));
                                    }
                                }
                                //Invalidate menu
                                invalidateOptionsMenu();
                                //Hide progress
                                progressWheel.setVisibility(View.GONE);
                                //Show ads
                                showAds();
                                //Check video url
                                if (urlVideo.length() > 0) {
                                    textLinkVideo.setVisibility(View.VISIBLE);
                                }
                            } catch (Exception e) {
                                e.getMessage();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            finish();
                            Toast.makeText(ActDetailContentDefault.this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                        }
                    });
            request.setShouldCache(true);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(this).getRequestQueue().getCache().invalidate(Constant.NEWS_DETAIL + "id/" + id + "/screen/" + "search_detail_screen", true);
            Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEWS_DETAIL + "id/" + id + "/screen/" + "search_detail_screen" + id);
            Global.getInstance(this).addToRequestQueue(request, Constant.JSON_REQUEST);
        } else {
            if (Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEWS_DETAIL + "id/" + id + "/screen/" + "search_detail_screen") != null) {
                String cachedResponse = new String(Global.getInstance(this).
                        getRequestQueue().getCache().get(Constant.NEWS_DETAIL + "id/" + id + "/screen/" + "search_detail_screen").data);
                try {
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    JSONObject response = jsonObject.getJSONObject(Constant.response);
                    JSONObject detail = response.getJSONObject(Constant.detail);
                    ids = detail.getString(Constant.id);
                    channel_id = detail.getString(Constant.channel_id);
                    channel = detail.getString(Constant.kanal);
                    title = detail.getString(Constant.title);
                    image_url = detail.getString(Constant.image_url);
                    date_publish = detail.getString(Constant.date_publish);
                    reporter_name = detail.getString(Constant.reporter_name);
                    url_shared = detail.getString(Constant.url);
                    image_caption = detail.getString(Constant.image_caption);

                    JSONArray content = detail.getJSONArray(Constant.content);
                    if (content.length() > 0) {
                        for (int i=0; i<content.length(); i++) {
                            String detailContent = content.getString(i);
                            pagingContents.add(detailContent);
                        }
                    }

                    JSONArray sliderImageArray = detail.getJSONArray(Constant.content_images);
                    if (sliderImageArray.length() > 0) {
                        for (int i=0; i<sliderImageArray.length(); i++) {
                            JSONObject objSlider = sliderImageArray.getJSONObject(i);
                            sliderPhotoUrl = objSlider.getString("src");
                            sliderTitle = objSlider.getString("title");
                            sliderContentImages.add(new SliderContentImage(sliderPhotoUrl, sliderTitle));
                        }
                    }

                    JSONArray related_article = response.getJSONArray(Constant.related_article);
                    if (related_article.length() > 0) {
                        for (int i=0; i<related_article.length(); i++) {
                            JSONObject objRelated = related_article.getJSONObject(i);
                            String id = objRelated.getString(Constant.id);
                            String article_id = objRelated.getString(Constant.article_id);
                            String related_article_id = objRelated.getString(Constant.related_article_id);
                            String related_title = objRelated.getString(Constant.related_title);
                            String related_channel_level_1_id = objRelated.getString(Constant.related_channel_level_1_id);
                            String channel_id = objRelated.getString(Constant.channel_id);
                            String related_date_publish = objRelated.getString(Constant.related_date_publish);
                            String image = objRelated.getString(Constant.image);
                            String channel = objRelated.getString(Constant.kanal);
                            String shared_url = objRelated.getString(Constant.url);
                            relatedArticleArrayList.add(new RelatedArticle(id, article_id, related_article_id, related_title,
                                    related_channel_level_1_id, channel_id, related_date_publish, image, channel, shared_url));
                            Log.i(Constant.TAG, "RELATED ARTICLE CACHED : " + relatedArticleArrayList.get(i).getRelated_title());
                        }
                    }

                    JSONArray comment_list = response.getJSONArray(Constant.comment_list);
                    if (comment_list.length() > 0) {
                        for (int i=0; i<comment_list.length(); i++) {
                            JSONObject objRelated = comment_list.getJSONObject(i);
                            String id = objRelated.getString(Constant.id);
                            String name = objRelated.getString(Constant.name);
                            String comment_text = objRelated.getString(Constant.comment_text);
                            commentArrayList.add(new Comment(id, null, name, null, comment_text, null, null, null));
                            Log.i(Constant.TAG, "COMMENTS PREVIEW : " + commentArrayList.get(i).getComment_text());
                        }
                    }

                    tvTitleDetail.setText(title);
                    tvDateDetail.setText(date_publish);
                    if (pagingContents.size() > 0) {
                        setTextViewHTML(tvContentDetail, pagingContents.get(0));
                        if (pagingContents.size() > 1) {
                            textPageIndex.setText(String.valueOf(pageCount + 1));
                            textPageSize.setText(String.valueOf(pagingContents.size()));
                            mPagingButtonLayout.setVisibility(View.VISIBLE);
                        }
                    }
                    tvReporterDetail.setText(reporter_name);
                    ImageLoader.getInstance().displayImage(image_url, ivThumbDetail, options);

                    if (sliderContentImages.size() > 0) {
                        imageSliderAdapter = new ImageSliderAdapter(getSupportFragmentManager(), sliderContentImages);
                        viewPager.setAdapter(imageSliderAdapter);
                        viewPager.setCurrentItem(0);
                        imageSliderAdapter.notifyDataSetChanged();
                        linePageIndicator.setViewPager(viewPager);
                        viewPager.setVisibility(View.VISIBLE);
                        linePageIndicator.setVisibility(View.VISIBLE);
                    }

                    if (relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                        adapter = new RelatedAdapter(this, relatedArticleArrayList);
                        listView.setAdapter(adapter);
                        Constant.setListViewHeightBasedOnChildren(listView);
                        adapter.notifyDataSetChanged();
                        headerRelated.setVisibility(View.VISIBLE);
                        if (fromChannel != null) {
                            if (fromChannel.equalsIgnoreCase("bola") || fromChannel.equalsIgnoreCase("sport")) {
                                headerRelated.setBackgroundResource(R.color.color_bola);
                            } else if (fromChannel.equalsIgnoreCase("vivalife")) {
                                headerRelated.setBackgroundResource(R.color.color_life);
                            } else if (fromChannel.equalsIgnoreCase("otomotif")) {
                                headerRelated.setBackgroundResource(R.color.color_auto);
                            } else {
                                headerRelated.setBackgroundResource(R.color.color_news);
                            }
                        } else {
                            headerRelated.setBackgroundResource(R.color.news_base_color);
                        }
                    }

                    if (commentArrayList.size() > 0) {
                        layoutCommentPreview.setVisibility(View.VISIBLE);
                        Thread thread = new Thread() {
                            @Override
                            public void run() {
                                try {
                                    while (true) {
                                        Thread.sleep(3000);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                tvPreviewCommentUser.setText(commentArrayList.get(count).getUsername());
                                                tvPreviewCommentContent.setText(commentArrayList.get(count).getComment_text());
                                                count++;
                                                if (count >= commentArrayList.size()) {
                                                    count = 0;
                                                }
                                            }
                                        });
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                    } else {
                        btnComment.setVisibility(View.VISIBLE);
                        if (fromChannel != null) {
                            if (fromChannel.equalsIgnoreCase("bola") || fromChannel.equalsIgnoreCase("sport")) {
                                btnComment.setBackgroundColor(ContextCompat.getColor(this, R.color.color_bola));
                            } else if (fromChannel.equalsIgnoreCase("vivalife")) {
                                btnComment.setBackgroundColor(ContextCompat.getColor(this, R.color.color_life));
                            } else if (fromChannel.equalsIgnoreCase("otomotif")) {
                                btnComment.setBackgroundColor(ContextCompat.getColor(this, R.color.color_auto));
                            } else {
                                btnComment.setBackgroundColor(ContextCompat.getColor(this, R.color.color_news));
                            }
                        } else {
                            btnComment.setBackgroundColor(ContextCompat.getColor(this, R.color.news_base_color));
                        }
                    }

                    progressWheel.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.getMessage();
                }
            } else {
                Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
                progressWheel.setVisibility(View.GONE);
                tvNoResult.setVisibility(View.VISIBLE);
            }
        }

        initScreenOrientation();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            initScreenOrientation();
        }
    }

    public void initScreenOrientation() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = Function.getScreenOrientation(this) == Configuration.ORIENTATION_LANDSCAPE ? displayMetrics.heightPixels : displayMetrics.widthPixels;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ivThumbDetail.getLayoutParams().height = width * 9 / 16;
                ivThumbDetail.requestLayout();
                viewPager.getLayoutParams().width = width;
                viewPager.requestLayout();
            }
        }, 100);
    }

    private void defineViews() {
        mParentLayout = (LinearLayout) findViewById(R.id.parent_layout);
        scrollView = (ParallaxScrollView) findViewById(R.id.scroll_layout);
        mPagingButtonLayout = (LinearLayout) findViewById(R.id.layout_button_next_previous);

        viewPager = (ViewPager) findViewById(R.id.horizontal_list);
        viewPager.setVisibility(View.GONE);

        linePageIndicator = (LinePageIndicator) findViewById(R.id.indicator);
        linePageIndicator.setVisibility(View.GONE);

        progressWheel = (ProgressWheel) findViewById(R.id.progress_wheel);
        headerRelated = (RelativeLayout) findViewById(R.id.header_related_article);
        headerRelated.setVisibility(View.GONE);
        tvNoResult = (TextView) findViewById(R.id.text_no_result_detail_content);
        tvNoResult.setVisibility(View.GONE);

        listView = (ListView) findViewById(R.id.list_related_article_default);
        listView.setOnItemClickListener(this);

        btnComment = (Button) findViewById(R.id.btn_comment);
        btnComment.setOnClickListener(this);
        btnComment.setTransformationMethod(null);

        layoutCommentPreview = (LinearLayout) findViewById(R.id.layout_preview_comment_list);
        layoutCommentPreview.setOnClickListener(this);
        layoutCommentPreview.setVisibility(View.GONE);
        tvPreviewCommentContent = (TextView) findViewById(R.id.text_preview_comment_content);
        tvPreviewCommentUser = (TextView) findViewById(R.id.text_preview_comment_user);

        relatedArticleArrayList = new ArrayList<>();
        commentArrayList = new ArrayList<>();
        sliderContentImages = new ArrayList<>();
        adsArrayList = new ArrayList<>();
        pagingContents = new ArrayList<>();

        tvTitleDetail = (TextView) findViewById(R.id.title_detail_content_default);
        tvDateDetail = (TextView) findViewById(R.id.date_detail_content_default);
        tvReporterDetail = (TextView) findViewById(R.id.reporter_detail_content_default);
        tvContentDetail = (TextView) findViewById(R.id.content_detail_content_default);

        ivThumbDetail = (KenBurnsView) findViewById(R.id.thumb_detail_content_default);
        ivThumbDetail.setOnClickListener(this);
        ivThumbDetail.setFocusableInTouchMode(true);

        textLinkVideo = (TextView)findViewById(R.id.text_move_video);
        textLinkVideo.setOnClickListener(this);
        textLinkVideo.setVisibility(View.GONE);

        next = (ImageView) findViewById(R.id.page_next);
        nextEnd = (ImageView) findViewById(R.id.page_next_end);
        next.setOnClickListener(this);
        nextEnd.setOnClickListener(this);

        previous = (ImageView) findViewById(R.id.page_previous);
        previousStart = (ImageView) findViewById(R.id.page_previous_start);
        previous.setOnClickListener(this);
        previousStart.setOnClickListener(this);
        previous.setEnabled(false);
        previousStart.setEnabled(false);

        textPageIndex = (TextView) findViewById(R.id.text_page_index);
        textPageSize = (TextView) findViewById(R.id.text_page_size);

        if (Constant.isTablet(this)) {
            ivThumbDetail.getLayoutParams().height =
                    Constant.getDynamicImageSize(this, Constant.DYNAMIC_SIZE_GRID_TYPE);
            viewPager.getLayoutParams().height =
                    Constant.getDynamicImageSize(this, Constant.DYNAMIC_SIZE_SLIDER_TYPE);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (shared_url == null) {
            try {
                if (Global.getInstance(this).getRequestQueue().getCache().get(
                        Constant.NEWS_DETAIL + "id/" + id + "/screen/" + "search_detail_screen") != null) {
                    String cachedResponse = new String(Global.getInstance(this).
                            getRequestQueue().getCache().get(
                            Constant.NEWS_DETAIL + "id/" + id + "/screen/" + "search_detail_screen").data);
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    JSONObject response = jsonObject.getJSONObject(Constant.response);
                    JSONObject detail = response.getJSONObject(Constant.detail);
                    url_shared = detail.getString(Constant.url);
                    shared_url = url_shared;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void getParameterIntent() {
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        typeFrom = intent.getStringExtra("type");
        fromChannel = intent.getStringExtra("kanal");
        shared_url = intent.getStringExtra("shared_url");
    }

    private void setThemes() {
        ColorDrawable colorDrawable = new ColorDrawable();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        //Set Background
        if (fromChannel != null) {
            if (fromChannel.equalsIgnoreCase("bola") || fromChannel.equalsIgnoreCase("sport")) {
                colorDrawable.setColor(ContextCompat.getColor(this, R.color.color_bola));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_bola);
                progressWheel.setBarColor(ContextCompat.getColor(this, R.color.color_bola));
            } else if (fromChannel.equalsIgnoreCase("vivalife")) {
                colorDrawable.setColor(ContextCompat.getColor(this, R.color.color_life));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                if (typeFrom != null) {
                    if (typeFrom.equals("editor_choice")) {
                        getSupportActionBar().setTitle("Editor's Choice");
                    } else {
                        getSupportActionBar().setTitle(R.string.label_item_navigation_life);
                    }
                } else {
                    getSupportActionBar().setTitle(R.string.label_item_navigation_life);
                }
                progressWheel.setBarColor(ContextCompat.getColor(this, R.color.color_life));
            } else if (fromChannel.equalsIgnoreCase("otomotif")) {
                colorDrawable.setColor(ContextCompat.getColor(this, R.color.color_auto));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_otomotif);
                progressWheel.setBarColor(ContextCompat.getColor(this, R.color.color_auto));
            } else {
                colorDrawable.setColor(ContextCompat.getColor(this, R.color.color_news));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_news);
                progressWheel.setBarColor(ContextCompat.getColor(this, R.color.color_news));
            }
        } else {
            colorDrawable.setColor(ContextCompat.getColor(this, R.color.news_base_color));
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
            progressWheel.setBarColor(ContextCompat.getColor(this, R.color.news_base_color));
        }
    }

    private void moveBrowserPage(String url, String mChannel) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        bundle.putString("channel", mChannel);
        Intent intent = new Intent(this, ActBrowser.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveVideoPage(String video) {
        Bundle bundle = new Bundle();
        bundle.putString("urlVideo", video);
        Intent intent = new Intent(this, ActVideo.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveCommentPage() {
        Bundle bundle = new Bundle();
        bundle.putString("imageurl", image_url);
        bundle.putString("title", title);
        bundle.putString("article_id", ids);
        bundle.putString("type_kanal", channel);
        Intent intent = new Intent(this, ActComment.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveRatingPage() {
        Bundle bundles = new Bundle();
        bundles.putString("imageurl", image_url);
        bundles.putString("title", title);
        bundles.putString("article_id", ids);
        bundles.putString("type_kanal", channel);
        Intent intents = new Intent(this, ActRating.class);
        intents.putExtras(bundles);
        startActivity(intents);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void doFavorites() {
        String favoriteList = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.FAVORITES_LIST, "");
        if (favoriteList == null || favoriteList.length() <= 0) {
            favoritesArrayList = Global.getInstance(this).getFavoritesList();
        } else {
            favoritesArrayList = Global.getInstance(this).getInstanceGson().
                    fromJson(favoriteList, Global.getInstance(this).getTypeFavorites());
        }
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getResources().getString(R.string.label_favorite_navigation_title))
                .setContentText(title)
                .setConfirmText(getResources().getString(R.string.label_favorite_ok))
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        String contents = Global.getInstance(ActDetailContentDefault.this).getInstanceGson().toJson(pagingContents);
                        favoritesArrayList.add(new Favorites(ids, title, channel_id, channel,
                                image_url, date_publish, reporter_name, url_shared, contents, image_caption, sliderContentImages));
                        String favorite = Global.getInstance(ActDetailContentDefault.this).getInstanceGson().toJson(favoritesArrayList);
                        Global.getInstance(ActDetailContentDefault.this).getDefaultEditor().putString(Constant.FAVORITES_LIST, favorite);
                        Global.getInstance(ActDetailContentDefault.this).getDefaultEditor().putInt(Constant.FAVORITES_LIST_SIZE, favoritesArrayList.size());
                        Global.getInstance(ActDetailContentDefault.this).getDefaultEditor().commit();
                        sDialog.setTitleText(getResources().getString(R.string.label_favorite_navigation_title_confirm))
                                .setContentText(getResources().getString(R.string.label_favorite_navigation_content))
                                .setConfirmText(getResources().getString(R.string.label_favorite_ok))
                                .setConfirmClickListener(null)
                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.subaction_rate:
                moveRatingPage();
                return true;
            case R.id.subaction_comments:
                moveCommentPage();
                return true;
            case R.id.subaction_favorites:
                doFavorites();
                return true;
            case R.id.subaction_browser:
                if (shared_url != null && channel != null) {
                    if (shared_url.length() > 0 && channel.length() > 0)  {
                        moveBrowserPage(shared_url, channel);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_frag_detail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        android.support.v7.widget.ShareActionProvider myShareActionProvider =
                (android.support.v7.widget.ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent myIntent = new Intent();
        myIntent.setAction(Intent.ACTION_SEND);
        myIntent.putExtra(Intent.EXTRA_TEXT, shared_url);
        myIntent.setType("text/plain");
        myShareActionProvider.setShareIntent(myIntent);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView listview = (ListView) adapterView;
        if (listview.getId() == R.id.list_related_article_default) {
            if (relatedArticleArrayList.size() > 0) {
                RelatedArticle relatedArticles = relatedArticleArrayList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("id", relatedArticles.getRelated_article_id());
                if (typeFrom != null) {
                    if (typeFrom.length() > 0) {
                        bundle.putString("type", typeFrom);
                    }
                }
                bundle.putString("kanal", relatedArticles.getKanal());
                bundle.putString("shared_url", relatedArticles.getShared_url());
                Intent intent = new Intent(this, ActDetailContentDefault.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        }
    }

    private void showPagingNext() {
        pageCount += 1;
        if (pageCount > 0) {
            previous.setEnabled(true);
            previousStart.setEnabled(true);
        }
        if (pageCount < pagingContents.size()) {
            setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
            scrollView.smoothScrollTo(0, 0);
            textPageIndex.setText(String.valueOf(pageCount + 1));
        }
        if (pageCount == pagingContents.size() - 1) {
            next.setEnabled(false);
            nextEnd.setEnabled(false);
        }
    }

    private void showPagingPrevious() {
        pageCount -= 1;
        if (pageCount < pagingContents.size() - 1) {
            next.setEnabled(true);
            nextEnd.setEnabled(true);
        }
        if (pageCount == 0) {
            setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
            scrollView.smoothScrollTo(0, 0);
            previous.setEnabled(false);
            previousStart.setEnabled(false);
            textPageIndex.setText(String.valueOf(pageCount + 1));
        } else {
            previous.setEnabled(true);
            previousStart.setEnabled(true);
            if (pageCount > -1 && pageCount < pagingContents.size()) {
                setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
                scrollView.smoothScrollTo(0, 0);
                textPageIndex.setText(String.valueOf(pageCount + 1));
            }
        }
    }

    private void toDetailThumbnail() {
        Bundle bundle = new Bundle();
        bundle.putString("photoUrl", image_url);
        bundle.putString("image_caption", image_caption);
        Intent intent = new Intent(this, ActDetailPhotoThumb.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.thumb_detail_content_default) {
            if (image_url != null) {
                if (image_url.length() > 0) {
                    toDetailThumbnail();
                }
            }
        } else if (view.getId() == R.id.layout_preview_comment_list) {
            moveCommentPage();
        } else if (view.getId() == R.id.text_move_video) {
            moveVideoPage(urlVideo);
        } else if (view.getId() == R.id.btn_comment) {
            moveCommentPage();
        } else if (view.getId() == R.id.page_next) {
            showPagingNext();
        } else if (view.getId() == R.id.page_previous) {
            showPagingPrevious();
        } else if (view.getId() == R.id.page_next_end) {
            if (pageCount < pagingContents.size() - 1) {
                pageCount = pagingContents.size() - 1;
                setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
                scrollView.smoothScrollTo(0, 0);
                next.setEnabled(false);
                nextEnd.setEnabled(false);
                previous.setEnabled(true);
                previousStart.setEnabled(true);
                textPageIndex.setText(String.valueOf(pageCount + 1));
            }
        } else if (view.getId() == R.id.page_previous_start) {
            if (pageCount > 0) {
                pageCount = 0;
                setTextViewHTML(tvContentDetail, pagingContents.get(pageCount));
                scrollView.smoothScrollTo(0, 0);
                previous.setEnabled(false);
                previousStart.setEnabled(false);
                next.setEnabled(true);
                nextEnd.setEnabled(true);
                textPageIndex.setText(String.valueOf(pageCount + 1));
            }
        }
    }

    private void setAnalytics(String title, String id) {
        if (isInternetPresent) {
            Analytics analytics = new Analytics(this);
            if (typeFrom != null) {
                if (typeFrom.equalsIgnoreCase("search")) {
                    analytics.getAnalyticByATInternet(Constant.FROM_SEARCH_RESULT_DETAIL_CONTENT + fromChannel.toUpperCase() + "_" + id + "_" + title);
                    analytics.getAnalyticByGoogleAnalytic(Constant.FROM_SEARCH_RESULT_DETAIL_CONTENT + fromChannel.toUpperCase() + "_" + id + "_" + title);
                } else if (typeFrom.equalsIgnoreCase("editor_choice")) {
                    analytics.getAnalyticByATInternet(Constant.FROM_EDITOR_CHOICE + id + "_" + title);
                    analytics.getAnalyticByGoogleAnalytic(Constant.FROM_EDITOR_CHOICE + id + "_" + title);
                } else if (typeFrom.equals(getResources().getString(R.string.label_item_navigation_scan_berita))) {
                    analytics.getAnalyticByATInternet(getResources().getString(R.string.label_item_navigation_scan_berita) + id + "_" + title);
                    analytics.getAnalyticByGoogleAnalytic(getResources().getString(R.string.label_item_navigation_scan_berita) + id + "_" + title);
                }
            } else {
                analytics.getAnalyticByATInternet(Constant.FROM_RELATED_ARTICLE_DETAIL_CONTENT + id + "_" + title);
                analytics.getAnalyticByGoogleAnalytic(Constant.FROM_RELATED_ARTICLE_DETAIL_CONTENT + id + "_" + title);
            }
        }
    }

    private void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
        text.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                String url = span.getURL();
                handleClickBodyText(url);
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    private void handleClickBodyText(String url) {
        if (isInternetPresent) {
            if (url.contains(Constant.LINK_YOUTUBE)) {
                moveVideoPage(url);
            } else if (url.contains(Constant.LINK_ARTICLE_VIVA)) {
                if (url.length() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("id", Constant.getArticleViva(url));
                    Intent intent = new Intent(ActDetailContentDefault.this, ActDetailContentDefault.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            } else if (url.contains(Constant.LINK_VIDEO_VIVA)) {
                moveBrowserPage(url, channel);
            } else {
                moveBrowserPage(url, channel);
            }
        } else {
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
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

}
