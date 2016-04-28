package id.co.viva.news.app.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flaviofaria.kenburnsview.KenBurnsView;
import com.nirhart.parallaxscroll.views.ParallaxScrollView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.viewpagerindicator.LinePageIndicator;

import java.util.ArrayList;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.ImageSliderAdapter;
import id.co.viva.news.app.model.SliderContentImage;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 24/11/14.
 */
public class ActDetailFavorite extends ActionBarActivity implements View.OnClickListener {
    private DisplayImageOptions options;

    private KenBurnsView imageDetail;
    private ParallaxScrollView scrollView;
    private LinearLayout mPagingButtonLayout;
    private TextView tvTitle;
    private TextView tvDatePublish;
    private TextView tvContent;
    private TextView tvReporterName;
    private ViewPager viewPager;
    private LinePageIndicator linePageIndicator;
    private ImageView nextEnd;
    private ImageView next;
    private ImageView previous;
    private ImageView previousStart;
    private TextView textPageIndex;
    private TextView textPageSize;

    private boolean isInternetPresent = false;
    private ArrayList<SliderContentImage> sliderContentImages;
    private ArrayList<String> pagingContents;

    private String title;
    private String image_url;
    private String date_publish;
    private String contents;
    private String reporter_name;
    private String image_caption;
    private String sThumbList;
    private int thumbSize;
    private int pageCount = 0;

    private int width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(ActDetailFavorite.this));
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

        //Current connection
        isInternetPresent = Global.getInstance(this)
                .getConnectionStatus().isConnectingToInternet();
        //Get parameters
        getParams();
        //Check connection
        getAnalytics(title);

        setContentView(R.layout.act_detail_favorite);

        //Set header
        getHeaderActionBar();
        //Define all views
        defineViews();

//        if (image_url != null) {
//            if (image_url.length() > 0) {
                ImageLoader.getInstance().displayImage(image_url, imageDetail, options);
//            } else {
//				imageDetail.setImageResource(R.drawable.default_image);
//			}
//        } else {
//			imageDetail.setImageResource(R.drawable.default_image);
//		}

        imageDetail.setOnClickListener(this);
        tvTitle.setText(title);
        tvDatePublish.setText(date_publish);
        tvReporterName.setText(reporter_name);

        if (pagingContents.size() > 0) {
            tvContent.setText(Html.fromHtml(pagingContents.get(0)).toString());
            if (pagingContents.size() > 1) {
                textPageIndex.setText(String.valueOf(pageCount + 1));
                textPageSize.setText(String.valueOf(pagingContents.size()));
                mPagingButtonLayout.setVisibility(View.VISIBLE);
            }
        }

        if (thumbSize > 0 && sliderContentImages != null) {
            ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(
                    getSupportFragmentManager(), sliderContentImages);
            viewPager.setAdapter(imageSliderAdapter);
            viewPager.setCurrentItem(0);
            imageSliderAdapter.notifyDataSetChanged();
            linePageIndicator.setViewPager(viewPager);
            viewPager.setVisibility(View.VISIBLE);
            linePageIndicator.setVisibility(View.VISIBLE);
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
        Log.e("TAG", "width=" + width);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                imageDetail.getLayoutParams().height = width * 9 / 16;
                imageDetail.requestLayout();
                Log.e("TAG", "imageDetail.getLayoutParams().height=" + imageDetail.getLayoutParams().height);
            }
        }, 100);
    }

    private void getParams() {
        Intent intent = getIntent();
        title = intent.getStringExtra("title");
        image_url = intent.getStringExtra("image_url");
        date_publish = intent.getStringExtra("date_publish");
        contents = intent.getStringExtra("content");
        reporter_name = intent.getStringExtra("reporter_name");
        image_caption = intent.getStringExtra("image_caption");
        thumbSize = intent.getIntExtra("list_thumbnail_body_size", 0);
        sThumbList = intent.getStringExtra("list_thumbnail_body");
    }

    private void defineViews() {
        mPagingButtonLayout = (LinearLayout) findViewById(R.id.layout_button_next_previous);
        scrollView = (ParallaxScrollView) findViewById(R.id.scroll_layout);
        sliderContentImages = Global.getInstance(this).getInstanceGson().
                fromJson(sThumbList, Global.getInstance(this).getTypeSlider());
        pagingContents = Global.getInstance(this).getInstanceGson().
                fromJson(contents, Global.getInstance(this).getContents());
        imageDetail = (KenBurnsView)findViewById(R.id.thumb_detail_content_favorite);
        tvTitle = (TextView)findViewById(R.id.title_detail_content_favorite);
        tvDatePublish = (TextView)findViewById(R.id.date_detail_content_favorite);
        tvContent = (TextView)findViewById(R.id.content_detail_content_favorite);
        tvReporterName = (TextView)findViewById(R.id.reporter_detail_content_favorite);
        viewPager = (ViewPager) findViewById(R.id.horizontal_list);
        linePageIndicator = (LinePageIndicator)findViewById(R.id.indicator);
        linePageIndicator.setVisibility(View.GONE);
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
            imageDetail.getLayoutParams().height =
                    Constant.getDynamicImageSize(this, Constant.DYNAMIC_SIZE_GRID_TYPE);
            viewPager.getLayoutParams().height =
                    Constant.getDynamicImageSize(this, Constant.DYNAMIC_SIZE_SLIDER_TYPE);
        }
    }

    private void getHeaderActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.label_favorites_lowercase));
    }

    private void getAnalytics(String title) {
        if (isInternetPresent) {
            Analytics analytics = new Analytics(this);
            analytics.getAnalyticByATInternet(Constant.FAVORITES_PAGE_DETAIL + title);
            analytics.getAnalyticByGoogleAnalytic(Constant.FAVORITES_PAGE_DETAIL + title);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.thumb_detail_content_favorite) {
            if (image_url.length() > 0) {
                Bundle bundle = new Bundle();
                bundle.putString("photoUrl", image_url);
                bundle.putString("image_caption", image_caption);
                Intent intent = new Intent(this, ActDetailPhotoThumb.class);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            }
        } else if (view.getId() == R.id.page_next) {
            showPagingNext();
        } else if (view.getId() == R.id.page_previous) {
            showPagingPrevious();
        } else if (view.getId() == R.id.page_next_end) {
            if (pageCount < pagingContents.size() - 1) {
                pageCount = pagingContents.size() - 1;
                tvContent.setText(Html.fromHtml(pagingContents.get(pageCount)).toString());
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
                tvContent.setText(Html.fromHtml(pagingContents.get(pageCount)).toString());
                scrollView.smoothScrollTo(0, 0);
                previous.setEnabled(false);
                previousStart.setEnabled(false);
                next.setEnabled(true);
                nextEnd.setEnabled(true);
                textPageIndex.setText(String.valueOf(pageCount + 1));
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
            tvContent.setText(Html.fromHtml(pagingContents.get(pageCount)).toString());
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
            tvContent.setText(Html.fromHtml(pagingContents.get(pageCount)).toString());
            scrollView.smoothScrollTo(0, 0);
            previous.setEnabled(false);
            previousStart.setEnabled(false);
            textPageIndex.setText(String.valueOf(pageCount + 1));
        } else {
            previous.setEnabled(true);
            previousStart.setEnabled(true);
            if (pageCount > -1 && pageCount < pagingContents.size()) {
                tvContent.setText(Html.fromHtml(pagingContents.get(pageCount)).toString());
                scrollView.smoothScrollTo(0, 0);
                textPageIndex.setText(String.valueOf(pageCount + 1));
            }
        }
    }

}
