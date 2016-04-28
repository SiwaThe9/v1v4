package id.co.viva.news.app.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import id.co.viva.news.app.R;
import id.co.viva.news.app.component.OrientationManager;
import id.co.viva.news.app.fragment.VideoDetailFragment;

public class ActivityVideoDetail extends AppCompatActivity {
    public static final String EXTRA_AB_COLOR = "extra_ab_color";
    public static final String EXTRA_CHANNEL = "extra_channel";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_VIDEO_ID = "extra_video_id";

    private String channel;
    private String ab_color;
    private String title;
    private String video_id;
    private boolean is_full_screen;

    private Fragment fragment;

    private Toolbar viewToolbar;
    private TextView viewToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent != null) {
            if(intent.hasExtra(EXTRA_AB_COLOR)) ab_color = intent.getStringExtra(EXTRA_AB_COLOR);
            if(intent.hasExtra(EXTRA_CHANNEL)) channel = intent.getStringExtra(EXTRA_CHANNEL);
            if(intent.hasExtra(EXTRA_TITLE)) title = intent.getStringExtra(EXTRA_TITLE);
            if(intent.hasExtra(EXTRA_VIDEO_ID)) video_id = intent.getStringExtra(EXTRA_VIDEO_ID);
        }

        setContentView(R.layout.activity_video_detail);

        viewToolbar = (Toolbar) findViewById(R.id.actionBar);
        viewToolbarTitle = (TextView) findViewById(R.id.title);

        setSupportActionBar(viewToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        if(ab_color != null) {
            viewToolbar.setBackgroundColor(Color.parseColor(ab_color));
        }
        if(title != null) {
            viewToolbarTitle.setText(title);
        }

        is_full_screen = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        //viewToolbar.setVisibility(is_full_screen ? View.GONE : View.VISIBLE);
        rotateScreen(is_full_screen);

        Bundle args = new Bundle();
        args.putString(VideoDetailFragment.BUNDLE_CHANNEL, channel);
        args.putString(VideoDetailFragment.BUNDLE_TITLE, title);
        args.putString(VideoDetailFragment.BUNDLE_VIDEO_ID, video_id);
        args.putBoolean(VideoDetailFragment.BUNDLE_IS_FULL_SCREEN, is_full_screen);

        fragment = new VideoDetailFragment();
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_video_detail, fragment)
                .commit();
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
        if(is_full_screen) {
            ((VideoDetailFragment) fragment).initScreen(!is_full_screen);
            rotateScreen(!is_full_screen);
        } else {
            super.onBackPressed();
            overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        Log.e("TAG", "onConfigurationChanged=" + newConfig.orientation);
//
//        if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE) {
//            if(!is_full_screen) {
//                rotateScreen(true);
//            }
//        } else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT) {
//            if(is_full_screen) {
//                rotateScreen(false);
//            }
//        }
//    }

    public void rotateScreen(boolean is_full_screen) {
        this.is_full_screen = is_full_screen;
        if(is_full_screen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
        viewToolbar.setVisibility(is_full_screen ? View.GONE : View.VISIBLE);
    }
}