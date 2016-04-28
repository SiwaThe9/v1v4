package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.interfaces.OnCompleteListener;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.services.UserAccount;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 05/12/14.
 */
public class ActRating extends ActionBarActivity implements OnCompleteListener,
        View.OnClickListener, RatingBar.OnRatingBarChangeListener {

    private DisplayImageOptions options;

    private String mImageUrl;
    private String mTitle;
    private String mIds;
    private String mFromKanal;
    private ActionProcessButton btnRate;
    private String fullName;
    private String email;
    private String amountRate;
    private TextView text_title;
    private TextView text_amount_rate;
    private ImageView image_content;
    private UserAccount userAccount;
    private LinearLayout mLayout;
    private RatingBar ratingBar;
    private boolean isInternetPresent = false;
    private Analytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getParameters();
        getAnalytics(mTitle);
        getStateUser();

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(ActRating.this));
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

        setContentView(R.layout.act_rating);

        isInternetPresent = Global.getInstance(this).getConnectionStatus().isConnectingToInternet();

        mLayout = (LinearLayout)findViewById(R.id.separator_line);
        text_title = (TextView)findViewById(R.id.text_title_content_rate);
        text_amount_rate = (TextView)findViewById(R.id.text_amount_rate);
        image_content = (ImageView)findViewById(R.id.img_thumb_content_rate);
        ratingBar = (RatingBar)findViewById(R.id.rating_article);
        btnRate = (ActionProcessButton)findViewById(R.id.btn_send_rate);
        btnRate.setMode(ActionProcessButton.Mode.ENDLESS);

        setThemes(mFromKanal);

        btnRate.setOnClickListener(this);
        ratingBar.setOnRatingBarChangeListener(this);

        if (mTitle != null) {
            if (mTitle.length() > 0) {
                text_title.setText(mTitle);
            }
        }

//        if (mImageUrl != null) {
//            if (mImageUrl.length() > 0) {
                ImageLoader.getInstance().displayImage(mImageUrl, image_content, options);
//            } else {
//				image_content.setImageResource(R.drawable.default_image);
//			}
//        }
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

    private void getParameters() {
        Bundle bundle = getIntent().getExtras();
        mIds = bundle.getString("article_id");
        mImageUrl = bundle.getString("imageurl");
        mTitle = bundle.getString("title");
        mFromKanal = bundle.getString("type_kanal");
    }

    private void getStateUser() {
        Global.getInstance(this).getDefaultEditor();
        fullName = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_FULL_NAME, "");
        email = Global.getInstance(this).getSharedPreferences(this)
                .getString(Constant.LOGIN_STATES_EMAIL, "");
    }

    private void setThemes(String kanal) {
        ColorDrawable colorDrawable = new ColorDrawable();
        if (kanal != null) {
            if (kanal.equalsIgnoreCase("bola")) {
                colorDrawable.setColor(ContextCompat.getColor(this, R.color.color_bola));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                mLayout.setBackgroundResource(R.color.color_bola);
                btnRate.setBackgroundResource(R.color.color_bola);
            } else if (kanal.equalsIgnoreCase("vivalife")) {
                colorDrawable.setColor(ContextCompat.getColor(this, R.color.color_life));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                mLayout.setBackgroundResource(R.color.color_life);
                btnRate.setBackgroundResource(R.color.color_life);
            } else if (kanal.equalsIgnoreCase("otomotif")) {
                colorDrawable.setColor(ContextCompat.getColor(this, R.color.color_auto));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                mLayout.setBackgroundResource(R.color.color_auto);
                btnRate.setBackgroundResource(R.color.color_auto);
            } else {
                colorDrawable.setColor(ContextCompat.getColor(this, R.color.color_news));
                getSupportActionBar().setBackgroundDrawable(colorDrawable);
                mLayout.setBackgroundResource(R.color.color_news);
                btnRate.setBackgroundResource(R.color.color_news);
            }
        } else {
            colorDrawable.setColor(ContextCompat.getColor(this, R.color.news_base_color));
            getSupportActionBar().setBackgroundDrawable(colorDrawable);
            mLayout.setBackgroundResource(R.color.news_base_color);
            btnRate.setBackgroundResource(R.color.news_base_color);
        }
        getHeaderActionBar();
    }

    private void getHeaderActionBar() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setIcon(new ColorDrawable(getResources().
                getColor(android.R.color.transparent)));
        getSupportActionBar().setTitle("Rate Artikel");
    }

    @Override
    public void onComplete(String message) {
        btnRate.setProgress(100);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Runnable r = new Runnable() {
            @Override
            public void run() {
                finish();
                startActivity(getIntent());
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 1000);
    }

    @Override
    public void onDelay(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailed(String message) {
        enableView();
        btnRate.setProgress(0);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(String message) {
        enableView();
        btnRate.setProgress(0);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void doLoginFirst() {
        finish();
        Intent intent = new Intent(this, ActLogin.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_send_rate) {
            if (isInternetPresent) {
                if (fullName.length() > 0 || email.length() > 0) {
                    if (amountRate != null) {
                        userAccount = new UserAccount(fullName, email, mIds, amountRate, this, ActRating.this);
                        disableView();
                        btnRate.setProgress(1);
                        userAccount.sendRating();
                    } else {
                        Toast.makeText(this, R.string.title_validate_rate, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    doLoginFirst();
                }
            } else {
                Toast.makeText(this, R.string.title_no_connection, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getAnalytics(String title) {
        analytics = new Analytics(this);
        analytics.getAnalyticByATInternet(Constant.RATING_ARTICLE + "_" + title);
        analytics.getAnalyticByGoogleAnalytic(Constant.RATING_ARTICLE + "_" + title);
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        amountRate = String.valueOf(v);
        text_amount_rate.setText(amountRate);
    }

    private void disableView() {
        ratingBar.setEnabled(false);
        btnRate.setEnabled(false);
    }

    private void enableView() {
        ratingBar.setEnabled(true);
        btnRate.setEnabled(true);
    }

}
