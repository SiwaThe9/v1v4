package id.co.viva.news.app.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.PhotoDetailFragment;
import id.co.viva.news.app.object.PhotoCategory;

public class PhotoDetailPagerAdapter extends FragmentStatePagerAdapter {
    String ab_color;
    String title;
    private ArrayList<PhotoCategory> datas = new ArrayList<PhotoCategory>();

    public PhotoDetailPagerAdapter(FragmentManager fm, String ab_color, String title, ArrayList<PhotoCategory> datas) {
        super(fm);

        this.ab_color = ab_color;
        this.title = title;
        this.datas = datas;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        args.putString(PhotoDetailFragment.BUNDLE_AB_COLOR, ab_color);
        args.putString(PhotoDetailFragment.BUNDLE_CHANNEL, datas.get(position).category);
        args.putString(PhotoDetailFragment.BUNDLE_TITLE, title);
        args.putString(PhotoDetailFragment.BUNDLE_PHOTO_ID, datas.get(position).photo_id);

        Fragment fragment = new PhotoDetailFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return datas.size();
    }
}
