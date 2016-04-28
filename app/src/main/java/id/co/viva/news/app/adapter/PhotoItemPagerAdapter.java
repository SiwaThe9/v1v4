package id.co.viva.news.app.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.PhotoItemFragment;
import id.co.viva.news.app.object.PhotoItem;

public class PhotoItemPagerAdapter extends FragmentStatePagerAdapter {
    private String photo_id = "";
    private ArrayList<PhotoItem> datas = new ArrayList<PhotoItem>();

    public PhotoItemPagerAdapter(FragmentManager fm, String photo_id) {
        super(fm);

        this.photo_id = photo_id;
    }

    /*
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    */

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
//        args.putString(PhotoItemFragment.BUNDLE_PHOTO_ID, photo_id);
//        args.putString(PhotoItemFragment.BUNDLE_PHOTO_ITEM_ID, datas.get(position).photo_item_id);
        args.putString(PhotoItemFragment.BUNDLE_DESCRIPTION, datas.get(position).description);
        args.putString(PhotoItemFragment.BUNDLE_IMAGE_URL, datas.get(position).image_url);
        args.putString(PhotoItemFragment.BUNDLE_SOURCE, datas.get(position).source);
        args.putInt(PhotoItemFragment.BUNDLE_POSITION, position + 1);
        args.putInt(PhotoItemFragment.BUNDLE_TOTAL, datas.size());

        Fragment fragment = new PhotoItemFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    public void setDatas(ArrayList<PhotoItem> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }
}
