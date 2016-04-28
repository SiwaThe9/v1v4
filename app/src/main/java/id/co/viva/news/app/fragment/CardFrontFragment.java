package id.co.viva.news.app.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import id.co.viva.news.app.R;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 19/12/14.
 */
public class CardFrontFragment extends Fragment {
    public final static String BUNDLE_PHOTO_URL = "bundle_photo_url";

    private String photoUrl;

    private DisplayImageOptions options;

//    public CardFrontFragment(String photoUrl, Context context) {
//        this.photoUrl = photoUrl;
//        this.context = context;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_PHOTO_URL)) photoUrl = bundle.getString(BUNDLE_PHOTO_URL);
        }

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_card_front, container, false);
        ImageView mFrontImage = (ImageView) v.findViewById(R.id.image_card_front);
//        if (photoUrl.length() > 0) {
            ImageLoader.getInstance().displayImage(photoUrl, mFrontImage, options);
//        } else {
//			mFrontImage.setImageResource(R.drawable.default_image);
//		}
        return v;
    }

}
