package id.co.viva.news.app.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.PhotoDetailPagerAdapter;
import id.co.viva.news.app.object.PhotoCategory;
import id.co.viva.news.app.share.DB;

public class ActivityPhotoDetail extends AppCompatActivity {
    public static final String EXTRA_AB_COLOR = "extra_ab_color";
    public static final String EXTRA_CHANNEL = "extra_name";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_PHOTO_ID = "extra_photo_id";

    private String ab_color;
    private String channel;
    private String title;
    private String photo_id;
    private boolean is_full_screen;

    private DB db;
    private PhotoDetailPagerAdapter adapter;

    private Toolbar viewToolbar;
    private TextView viewToolbarTitle;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra(EXTRA_AB_COLOR)) ab_color = intent.getStringExtra(EXTRA_AB_COLOR);
            if (intent.hasExtra(EXTRA_CHANNEL)) channel = intent.getStringExtra(EXTRA_CHANNEL);
            if (intent.hasExtra(EXTRA_TITLE)) title = intent.getStringExtra(EXTRA_TITLE);
            if (intent.hasExtra(EXTRA_PHOTO_ID)) photo_id = intent.getStringExtra(EXTRA_PHOTO_ID);
        }

        db = DB.getInstance(this);

        setContentView(R.layout.activity_photo_detail);

        viewToolbar = (Toolbar) findViewById(R.id.actionBar);
        viewToolbarTitle = (TextView) findViewById(R.id.title);
        viewPager = (ViewPager) findViewById(R.id.pager);

        setSupportActionBar(viewToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        if (ab_color != null) {
            viewToolbar.setBackgroundColor(Color.parseColor(ab_color));
        }
        if (title != null) {
            viewToolbarTitle.setText(title);
        }

        is_full_screen = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        rotateScreen(is_full_screen);

        ArrayList<PhotoCategory> datas = db.getAllPhotoCategory(channel);
        int position = 0;
        int i = 0;
        for (PhotoCategory categoryPhoto : datas) {
            if (categoryPhoto.photo_id.equals(photo_id)) {
                position = i;
            }
            i++;
        }

        adapter = new PhotoDetailPagerAdapter(getSupportFragmentManager(), ab_color, title, datas);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        Log.e("TAG", "onConfigurationChanged=" + newConfig.orientation);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (!is_full_screen) {
                rotateScreen(true);
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (is_full_screen) {
                rotateScreen(false);
            }
        }
    }

    public void rotateScreen(boolean is_full_screen) {
        this.is_full_screen = is_full_screen;
        if (is_full_screen) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        viewToolbar.setVisibility(is_full_screen ? View.GONE : View.VISIBLE);
    }
}