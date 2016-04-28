package id.co.viva.news.app.model;

import android.app.Activity;
import android.content.Intent;

import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActBrowser;
import id.co.viva.news.app.activity.ActDetailChannel;
import id.co.viva.news.app.activity.ActDetailContentDefault;
import id.co.viva.news.app.activity.ActLanding;
import id.co.viva.news.app.activity.ActSearchResult;

/**
 * Created by reza on 15/04/15.
 */
public class ChannelMapper {

    private Activity mActivity;
    private Intent mIntent;

    public ChannelMapper(Activity activity) {
        this.mActivity = activity;
    }

    public void checkDetailArticle(String article_id, String content) {
        mIntent = new Intent(mActivity, ActDetailContentDefault.class);
        mIntent.putExtra("id", article_id);
        mIntent.putExtra("type", mActivity.getResources().getString(R.string.label_item_navigation_scan_berita));
        mIntent.putExtra("shared_url", content);
    }

    public void checkSearchURL(String keyword) {
        mIntent = new Intent(mActivity, ActSearchResult.class);
        mIntent.putExtra("keyword", keyword);
    }

    public void checkNonVivaURL(String content) {
        mIntent = new Intent(mActivity, ActBrowser.class);
        mIntent.putExtra("url", content);
    }

    public void checkChannelUrl(String url) {
        if (url != null) {
            if (ActLanding.channelURLMaps.size() > 0) {
                for (int i=0; i<ActLanding.channelURLMaps.size(); i++) {
                    if (ActLanding.channelURLMaps.get(i).getUrl().equals(url)) {
                        mIntent = new Intent(mActivity, ActDetailChannel.class);
                        mIntent.putExtra("id", ActLanding.channelURLMaps.get(i).getChannel_id());
                        mIntent.putExtra("channel_title", ActLanding.channelURLMaps.get(i).getName());
                        mIntent.putExtra("channel", ActLanding.channelURLMaps.get(i).getKanal());
                        mIntent.putExtra("level", ActLanding.channelURLMaps.get(i).getLevel());
                    }
                }
            }
        }
    }

    public void executeResult() {
        if (mIntent != null) {
            mActivity.startActivity(mIntent);
        }
    }

}
