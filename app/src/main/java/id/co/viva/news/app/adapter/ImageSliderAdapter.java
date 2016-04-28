package id.co.viva.news.app.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

import id.co.viva.news.app.fragment.ImageSliderFragment;
import id.co.viva.news.app.model.SliderContentImage;

/**
 * Created by reza on 05/01/15.
 */
public class ImageSliderAdapter extends FragmentStatePagerAdapter {

    private ArrayList<SliderContentImage> sliderContentImages;

    public ImageSliderAdapter(FragmentManager fragmentManager, ArrayList<SliderContentImage> sliderContentImages) {
        super(fragmentManager);
        this.sliderContentImages = sliderContentImages;
    }

    @Override
    public Fragment getItem(int position) {
        return ImageSliderFragment.newInstance(sliderContentImages.get(position).getImgUrl(),
                sliderContentImages.get(position).getTitle());
    }

    @Override
    public int getCount() {
        return sliderContentImages.size();
    }

}
