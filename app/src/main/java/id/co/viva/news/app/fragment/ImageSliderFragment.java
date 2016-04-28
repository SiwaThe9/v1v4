package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActDetailPhotoThumb;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 06/01/15.
 */
public class ImageSliderFragment extends Fragment implements View.OnClickListener {
    private String mPhotoUrl;
    private String mTitle;

    private DisplayImageOptions options;

    public static ImageSliderFragment newInstance(String photo_url, String title) {
        ImageSliderFragment imageSliderFragment = new ImageSliderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("photo_url", photo_url);
        bundle.putString("title", title);
        imageSliderFragment.setArguments(bundle);
        return imageSliderFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoUrl = getArguments().getString("photo_url");
        mTitle = getArguments().getString("title");

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(getActivity()));
        }

        options = new DisplayImageOptions.Builder()
                .preProcessor(new BitmapProcessorCenterCrop())
//                .displayer(new RoundedBitmapDisplayer(512))
                .showImageOnLoading(R.drawable.default_image)
                .showImageForEmptyUri(R.drawable.default_image)
                .showImageOnFail(R.drawable.default_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .build();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_slider_detail_image, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image_item_slider_detail);
        imageView.setOnClickListener(this);
        if (mPhotoUrl.length() > 0) {
            if (getActivity() != null) {
                if (Constant.isTablet(getActivity())) {
                    imageView.getLayoutParams().height =
                            Constant.getDynamicImageSize(getActivity(), Constant.DYNAMIC_SIZE_SLIDER_TYPE);
                }
            }
            ImageLoader.getInstance().displayImage(mPhotoUrl, imageView, options);
        }
        return rootView;
    }

    private void toDetailThumbnail() {
        Bundle bundle = new Bundle();
        bundle.putString("photoUrl", mPhotoUrl);
        bundle.putString("image_caption", mTitle);
        Intent intent = new Intent(getActivity(), ActDetailPhotoThumb.class);
        intent.putExtras(bundle);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.image_item_slider_detail) {
            toDetailThumbnail();
        }
    }

}
