package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
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

import com.andexert.library.RippleView;
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
import id.co.viva.news.app.model.Favorites;
import id.co.viva.news.app.model.RelatedArticle;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 20/01/15.
 */
public class ActNotification extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    //Parameter from notification
    private String channelFromNotification;
    private String idFromNotification;

    private DisplayImageOptions options;

    //Internet Flag
    private boolean isInternetPresent = false;

    private int pageCount = 0;

    //Data List
    private ArrayList<SliderContentImage> sliderContentImages;
    private ArrayList<Favorites> favoritesArrayList;
    private ArrayList<RelatedArticle> relatedArticleArrayList;
    private ArrayList<Ads> adsArrayList;
    private ArrayList<String> pagingContents;

    //All Views
    private TextView tvTitleDetail;
    private TextView tvDateDetail;
    private TextView tvReporterDetail;
    private TextView tvContentDetail;
    private LinearLayout mPagingButtonLayout;
    private KenBurnsView ivThumbDetail;
    private RelativeLayout headerRelated;
    private RelatedAdapter adapter;
    private TextView textLinkVideo;
    private ListView listView;
    private Button btnComment;
    private ImageSliderAdapter imageSliderAdapter;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;
    private ProgressWheel progressWheel;
    private RippleView rippleView;
    private Button btnRetry;
    private TextView tvNoResult;
    private ParallaxScrollView scrollView;
    private TextView textPageIndex;
    private ImageView previousStart;
    private ImageView nextEnd;
    private TextView textPageSize;
    private ImageView next;
    private ImageView previous;

    //Ads AdMob DFP
    private LinearLayout mParentLayout;
    private PublisherAdView publisherAdViewBottom;
    private PublisherAdView publisherAdViewTop;

    //JSON Data
    private String title;
    private String image_url;
    private String date_publish;
    private String reporter_name;
    private String image_caption;
    private String sliderPhotoUrl;
    private String sliderTitle;
    private String urlVideo;
    private String shared_url;
    private String channel_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get some param from notification
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idFromNotification = extras.containsKey(Constant.id) ? extras.getString(Constant.id) : "";
            channelFromNotification = extras.containsKey(Constant.kanal) ? extras.getString(Constant.kanal) : "";
        }

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(ActNotification.this));
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
        setContentView(R.layout.act_notification);

        //Define All Views
        defineViews();

        //Set Color Theme
        setHeaderActionbar(channelFromNotification);

        //Checking current internet connection
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();

        //Fetch data
        getContent();
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
        btnRetry = (Button) findViewById(R.id.btn_retry);

        btnComment = (Button) findViewById(R.id.btn_comment);
        btnComment.setOnClickListener(this);
        btnComment.setTransformationMethod(null);

        rippleView = (RippleView) findViewById(R.id.layout_ripple_view_detail_subkanal);
        rippleView.setVisibility(View.GONE);
        rippleView.setOnClickListener(this);

        tvNoResult = (TextView) findViewById(R.id.text_no_result_detail_content);
        tvNoResult.setVisibility(View.GONE);

        relatedArticleArrayList = new ArrayList<>();
        sliderContentImages = new ArrayList<>();
        adsArrayList = new ArrayList<>();
        pagingContents = new ArrayList<>();

        listView = (ListView) findViewById(R.id.list_related_article_notification);
        listView.setOnItemClickListener(this);

        headerRelated = (RelativeLayout) findViewById(R.id.header_related_article);
        headerRelated.setVisibility(View.GONE);

        tvTitleDetail = (TextView) findViewById(R.id.title_detail_content);
        tvDateDetail = (TextView) findViewById(R.id.date_detail_content);
        tvReporterDetail = (TextView) findViewById(R.id.reporter_detail_content);
        tvContentDetail = (TextView) findViewById(R.id.content_detail_content);

        ivThumbDetail = (KenBurnsView) findViewById(R.id.thumb_detail_content);
        ivThumbDetail.setOnClickListener(this);
        ivThumbDetail.setFocusableInTouchMode(true);

        textLinkVideo = (TextView) findViewById(R.id.text_move_video);
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

    private void showPagingNext() {
        pageCount += 1;
        if (pageCount > 0) {
            previous.setEnabled(true);
            previousStart.setEnabled(true);
        }
        if (pageCount < pagingContents.size()) {
            tvContentDetail.setText(Html.fromHtml(pagingContents.get(pageCount)).toString());
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
            tvContentDetail.setText(Html.fromHtml(pagingContents.get(pageCount)).toString());
            scrollView.smoothScrollTo(0, 0);
            previous.setEnabled(false);
            previousStart.setEnabled(false);
            textPageIndex.setText(String.valueOf(pageCount + 1));
        } else {
            previous.setEnabled(true);
            previousStart.setEnabled(true);
            if (pageCount > -1 && pageCount < pagingContents.size()) {
                tvContentDetail.setText(Html.fromHtml(pagingContents.get(pageCount)).toString());
                scrollView.smoothScrollTo(0, 0);
                textPageIndex.setText(String.valueOf(pageCount + 1));
            }
        }
    }

    private void goDetailPhoto() {
        if (image_url != null) {
            if (image_url.length() > 0) {
                Bundle bundle = new Bundle();
                bundle.putString("photoUrl", image_url);
                bundle.putString("image_caption", image_caption);
                Intent intent = new Intent(this, ActDetailPhotoThumb.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        }
    }

    private void moveVideoPage(String mUrl) {
        Bundle bundle = new Bundle();
        bundle.putString("urlVideo", mUrl);
        Intent intent = new Intent(this, ActVideo.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveBrowserPage(String url) {
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        Intent intent = new Intent(this, ActBrowser.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.layout_ripple_view_detail_subkanal) {
            getContent();
        } else if(view.getId() == R.id.thumb_detail_content) {
            goDetailPhoto();
        } else if(view.getId() == R.id.text_move_video) {
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

    private void getContent() {
        if (isInternetPresent) {
            StringRequest request = new StringRequest(Request.Method.GET, Constant.NEWS_DETAIL + "id/" + idFromNotification,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String volleyResponse) {
                            Log.i(Constant.TAG, volleyResponse);
                            try {
                                JSONObject jsonObject = new JSONObject(volleyResponse);
                                JSONObject response = jsonObject.getJSONObject(Constant.response);
                                //Get content detail
                                JSONObject detail = response.getJSONObject(Constant.detail);
                                title = detail.getString(Constant.title);
                                image_url = detail.getString(Constant.image_url);
                                date_publish = detail.getString(Constant.date_publish);
                                reporter_name = detail.getString(Constant.reporter_name);
                                image_caption = detail.getString(Constant.image_caption);
                                shared_url = detail.getString(Constant.url);
                                channel_id = detail.getString((Constant.channel_id));
                                //Get detail content(s)
                                JSONArray content = detail.getJSONArray(Constant.content);
                                if (content.length() > 0) {
                                    for (int i=0; i<content.length(); i++) {
                                        String detailContent = content.getString(i);
                                        pagingContents.add(detailContent);
                                    }
                                }
                                //Get image content
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
                                //Get ads list
                                JSONArray ad_list = response.getJSONArray(Constant.adses);
                                if (ad_list.length() > 0) {
                                    for (int i=0; i<ad_list.length(); i++) {
                                        JSONObject jsonAds = ad_list.getJSONObject(i);
                                        String name = jsonAds.getString(Constant.name);
                                        int position = jsonAds.getInt(Constant.position);
                                        int type = jsonAds.getInt(Constant.type);
                                        String unit_id = jsonAds.getString(Constant.unit_id);
                                        adsArrayList.add(new Ads(name, type, position, unit_id));
                                    }
                                }
                                //Send Analytic
                                getAnalytics(title);
                                //Set data to view
                                tvTitleDetail.setText(title);
                                tvDateDetail.setText(date_publish);
                                //Paging check process
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
                                //Checking for image content
                                if (sliderContentImages.size() > 0) {
                                    imageSliderAdapter = new ImageSliderAdapter(getSupportFragmentManager(), sliderContentImages);
                                    viewPager.setAdapter(imageSliderAdapter);
                                    viewPager.setCurrentItem(0);
                                    imageSliderAdapter.notifyDataSetChanged();
                                    linePageIndicator.setViewPager(viewPager);
                                    viewPager.setVisibility(View.VISIBLE);
                                    linePageIndicator.setVisibility(View.VISIBLE);
                                }
                                //Show comment button
                                btnComment.setVisibility(View.VISIBLE);
                                if (channelFromNotification != null) {
                                    if (channelFromNotification.equalsIgnoreCase("bola") || channelFromNotification.equalsIgnoreCase("sport")) {
                                        btnComment.setBackgroundColor(ContextCompat.getColor(ActNotification.this, R.color.color_bola));
                                    } else if (channelFromNotification.equalsIgnoreCase("vivalife")) {
                                        btnComment.setBackgroundColor(ContextCompat.getColor(ActNotification.this, R.color.color_life));
                                    } else if (channelFromNotification.equalsIgnoreCase("otomotif")) {
                                        btnComment.setBackgroundColor(ContextCompat.getColor(ActNotification.this, R.color.color_auto));
                                    } else {
                                        btnComment.setBackgroundColor(ContextCompat.getColor(ActNotification.this, R.color.color_news));
                                    }
                                } else {
                                    btnComment.setBackgroundColor(ContextCompat.getColor(ActNotification.this, R.color.news_base_color));
                                }
                                //Checking for related article
                                if (relatedArticleArrayList.size() > 0 || !relatedArticleArrayList.isEmpty()) {
                                    adapter = new RelatedAdapter(ActNotification.this, relatedArticleArrayList);
                                    listView.setAdapter(adapter);
                                    Constant.setListViewHeightBasedOnChildren(listView);
                                    adapter.notifyDataSetChanged();
                                    headerRelated.setVisibility(View.VISIBLE);
                                    if (channelFromNotification != null) {
                                        if (channelFromNotification.equalsIgnoreCase("bola") || channelFromNotification.equalsIgnoreCase("sport")) {
                                            headerRelated.setBackgroundResource(R.color.color_bola);
                                        } else if (channelFromNotification.equalsIgnoreCase("vivalife")) {
                                            headerRelated.setBackgroundResource(R.color.color_life);
                                        } else if (channelFromNotification.equalsIgnoreCase("otomotif")) {
                                            headerRelated.setBackgroundResource(R.color.color_auto);
                                        } else {
                                            headerRelated.setBackgroundResource(R.color.color_news);
                                        }
                                    } else {
                                        headerRelated.setBackgroundResource(R.color.header_grey);
                                    }
                                }
                                //Hide 'Retry' button
                                if (rippleView.getVisibility() == View.VISIBLE) {
                                    rippleView.setVisibility(View.GONE);
                                }
                                //Hide progress bar
                                progressWheel.setVisibility(View.GONE);
                                //For updating content
                                invalidateOptionsMenu();
                                //Show Ads
                                showAds();
                                //Show url video if it exist
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
                            progressWheel.setVisibility(View.GONE);
                            rippleView.setVisibility(View.VISIBLE);
                            setButtonRetry(channelFromNotification);

                        }
                    });
            request.setShouldCache(true);
            request.setRetryPolicy(new DefaultRetryPolicy(
                    Constant.TIME_OUT_REGISTRATION,
                    0,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            Global.getInstance(ActNotification.this).getRequestQueue()
                    .getCache().invalidate(Constant.NEWS_DETAIL + "id/" + idFromNotification, true);
            Global.getInstance(ActNotification.this)
                    .getRequestQueue().getCache().get(Constant.NEWS_DETAIL + "id/" + idFromNotification);
            Global.getInstance(ActNotification.this).addToRequestQueue(request, Constant.JSON_REQUEST);
        } else {
            if (tvNoResult.getVisibility() == View.VISIBLE) {
                tvNoResult.setVisibility(View.GONE);
            } else {
                tvNoResult.setVisibility(View.VISIBLE);
                progressWheel.setVisibility(View.GONE);
            }
            Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
        }
    }

    private void setHeaderActionbar(String fromChannel) {
        ColorDrawable colorDrawable = new ColorDrawable();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (fromChannel != null) {
            if (fromChannel.equalsIgnoreCase("bola") || fromChannel.equalsIgnoreCase("sport")) {
                colorDrawable.setColor(ContextCompat.getColor(this, R.color.color_bola));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_bola);
                progressWheel.setBarColor(ContextCompat.getColor(this, R.color.color_bola));
            } else if (fromChannel.equalsIgnoreCase("vivalife")) {
                colorDrawable.setColor(ContextCompat.getColor(this, R.color.color_life));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                getSupportActionBar().setTitle(R.string.label_item_navigation_life);
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

    private void setButtonRetry(String mChannel) {
        if (mChannel != null) {
            if (mChannel.equalsIgnoreCase("bola") || mChannel.equalsIgnoreCase("sport")) {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_bola);
            } else if (mChannel.equalsIgnoreCase("vivalife")) {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_life);
            } else if (mChannel.equalsIgnoreCase("otomotif")) {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_otomotif);
            } else {
                btnRetry.setBackgroundResource(R.drawable.shadow_button_news);
            }
        }
    }

    private void goFirstFlow() {
        finish();
        Intent intent = new Intent(this, ActLanding.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void moveCommentPage() {
        Bundle bundle = new Bundle();
        bundle.putString("imageurl", image_url);
        bundle.putString("title", title);
        bundle.putString("article_id", idFromNotification);
        bundle.putString("type_kanal", channelFromNotification);
        Intent intent = new Intent(this, ActComment.class);
        intent.putExtras(bundle);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    private void moveRatingPage() {
        Bundle bundles = new Bundle();
        bundles.putString("imageurl", image_url);
        bundles.putString("title", title);
        bundles.putString("article_id", idFromNotification);
        bundles.putString("type_kanal", channelFromNotification);
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
                        String contents = Global.getInstance(ActNotification.this).getInstanceGson().toJson(pagingContents);
                        favoritesArrayList.add(new Favorites(idFromNotification, title, channel_id, channelFromNotification,
                                image_url, date_publish, reporter_name, shared_url, contents, image_caption, sliderContentImages));
                        String favorite = Global.getInstance(ActNotification.this).getInstanceGson().toJson(favoritesArrayList);
                        Global.getInstance(ActNotification.this).getDefaultEditor().putString(Constant.FAVORITES_LIST, favorite);
                        Global.getInstance(ActNotification.this).getDefaultEditor().putInt(Constant.FAVORITES_LIST_SIZE, favoritesArrayList.size());
                        Global.getInstance(ActNotification.this).getDefaultEditor().commit();
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
                goFirstFlow();
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (shared_url == null || shared_url.length() < 1) {
            try {
                if (Global.getInstance(this).getRequestQueue().getCache().get(Constant.NEWS_DETAIL + "id/" + idFromNotification) != null) {
                    String cachedResponse = new String(Global.getInstance(this).
                            getRequestQueue().getCache().get(Constant.NEWS_DETAIL + "/id/" + idFromNotification).data);
                    JSONObject jsonObject = new JSONObject(cachedResponse);
                    JSONObject response = jsonObject.getJSONObject(Constant.response);
                    JSONObject detail = response.getJSONObject(Constant.detail);
                    shared_url = detail.getString(Constant.url);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onPrepareOptionsMenu(menu);
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
    public void onBackPressed() {
        super.onBackPressed();
        goFirstFlow();
    }

    private void getAnalytics(String title) {
        if (isInternetPresent) {
            Analytics analytics = new Analytics(this);
            analytics.getAnalyticByATInternetFromNotification(Constant.ARTICLE_FROM_NOTIFICATION + "_"
                    + title.toUpperCase(), "Push Notifications::" + Constant.ARTICLE_FROM_NOTIFICATION + "_" + title.toUpperCase());
            analytics.getAnalyticByGoogleAnalytic(Constant.ARTICLE_FROM_NOTIFICATION + "_" + title.toUpperCase());
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ListView listview = (ListView) adapterView;
        if (listview.getId() == R.id.list_related_article_notification) {
            if (relatedArticleArrayList.size() > 0) {
                RelatedArticle relatedArticles = relatedArticleArrayList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("id", relatedArticles.getRelated_article_id());
                bundle.putString("kanal", relatedArticles.getKanal());
                bundle.putString("shared_url", relatedArticles.getShared_url());
                Intent intent = new Intent(this, ActDetailContentDefault.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
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
                    Intent intent = new Intent(ActNotification.this, ActDetailContentDefault.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                }
            } else if (url.contains(Constant.LINK_VIDEO_VIVA)) {
                moveBrowserPage(url);
            } else {
                moveBrowserPage(url);
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
