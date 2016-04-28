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
import id.co.viva.news.app.fragment.SettingFragment;

public class ActivitySetting extends AppCompatActivity {
    private Toolbar viewToolbar;
    private TextView viewToolbarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_setting);

        viewToolbar = (Toolbar) findViewById(R.id.actionBar);
        viewToolbarTitle = (TextView) findViewById(R.id.title);

        setSupportActionBar(viewToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        viewToolbarTitle.setText(getString(R.string.label_setting));

        Fragment fragment = new SettingFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container_setting, fragment)
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