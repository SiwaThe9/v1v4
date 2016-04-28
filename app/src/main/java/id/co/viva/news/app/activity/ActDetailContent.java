package id.co.viva.news.app.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.DetailContentChannelAdapter;
import id.co.viva.news.app.component.ZoomOutPageTransformer;
import id.co.viva.news.app.model.ChannelList;

/**
 * Created by reza on 24/10/14.
 */
public class ActDetailContent extends ActionBarActivity {

    private String id;
    private String color;
    private String channel_title;
    private String channel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get parameter
        getParameters();

        setContentView(R.layout.act_detail_content);

        //Set Actionbar
        setActionBar(color, channel_title, channel);

        //Set Detail Pager
        int position = 0;
        if (ActDetailChannel.channelListArrayList != null) {
            if (ActDetailChannel.channelListArrayList.size() > 0) {
                for (ChannelList channelList : ActDetailChannel.channelListArrayList) {
                    if (channelList.getId().equals(id)) break;
                    position++;
                }
            }
            DetailContentChannelAdapter adapter = new DetailContentChannelAdapter(getSupportFragmentManager(), ActDetailChannel.channelListArrayList, channel_title, channel);
            ViewPager viewPager = (ViewPager) findViewById(R.id.vp_detail_content);
            viewPager.setAdapter(adapter);
            viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
            viewPager.setCurrentItem(position);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, R.string.label_error, Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
    }

    private void getParameters() {
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        channel_title = bundle.getString("channel_title");
        channel = bundle.getString("channel");
        color = bundle.getString("color");
    }

    private void setActionBar(String mColor, String mChannelTitle, String mChannel) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if (mColor != null) {
            if (mColor.length() > 0) {
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor(mColor)));
            } else {
                setThemeColor(mChannel);
            }
        } else {
            setThemeColor(mChannel);
        }
        if (mChannelTitle != null) {
            getSupportActionBar().setTitle(mChannelTitle);
        }
    }

    private void setThemeColor(String textChannel) {
        if (textChannel != null) {
            if (textChannel.length() > 0) {
                if (textChannel.equalsIgnoreCase(Constant.CHANNEL_BOLA) || textChannel.equalsIgnoreCase("sport")) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_bola)));
                } else if (textChannel.toLowerCase().contains("life")) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_life)));
                } else if (textChannel.equalsIgnoreCase(Constant.CHANNEL_AUTO)) {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.color_auto)));
                } else {
                    getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.news_base_color)));
                }
            }
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

}
