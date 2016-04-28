package id.co.viva.news.app.component;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by root on 09/10/14.
 */
public class ZoomOutPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {
        view.setRotationY(position * - 50);
    }

}
