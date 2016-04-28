package id.co.viva.news.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import id.co.viva.news.app.R;
import id.co.viva.news.app.fragment.PhotoListFragment;
import id.co.viva.news.app.fragment.SubChannel2Fragment;

public class ActivitySubChannel2List extends AppCompatActivity {
    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_AB_COLOR = "extra_ab_color";
    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_API_URL = "extra_api_url";

    private String name;
    private String ab_color;
    private String title;
    private String api_url;

    private Toolbar viewToolbar;
    private TextView viewToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if(intent != null) {
            if(intent.hasExtra(EXTRA_NAME)) name = intent.getStringExtra(EXTRA_NAME);
            if(intent.hasExtra(EXTRA_AB_COLOR)) ab_color = intent.getStringExtra(EXTRA_AB_COLOR);
            if(intent.hasExtra(EXTRA_TITLE)) title = intent.getStringExtra(EXTRA_TITLE);
            if(intent.hasExtra(EXTRA_API_URL)) api_url = intent.getStringExtra(EXTRA_API_URL);
        }

        setContentView(R.layout.activity_sub_channel_2_list);

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

        Bundle args = new Bundle();
        args.putString(SubChannel2Fragment.BUNDLE_NAME, name);
        args.putString(SubChannel2Fragment.BUNDLE_AB_COLOR, ab_color);
        args.putString(SubChannel2Fragment.BUNDLE_API_URL, api_url);

        Fragment fragment = new SubChannel2Fragment();
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_sub_channel_2, fragment)
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
        super.onBackPressed();

        overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit);
    }
}