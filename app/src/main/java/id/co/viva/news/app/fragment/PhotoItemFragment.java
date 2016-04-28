package id.co.viva.news.app.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import id.co.viva.news.app.R;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

public class PhotoItemFragment extends Fragment {
//    public final static String BUNDLE_PHOTO_ID = "bundle_photo_id";
//    public final static String BUNDLE_PHOTO_ITEM_ID = "bundle_photo_item_id";
    public final static String BUNDLE_DESCRIPTION = "bundle_description";
    public final static String BUNDLE_IMAGE_URL = "bundle_image_url";
    public final static String BUNDLE_SOURCE = "bundle_source";
    public final static String BUNDLE_POSITION = "bundle_position";
    public final static String BUNDLE_TOTAL = "bundle_total";

//    private String photo_id;
//    private String photo_item_id;
    private String description;
    private String image_url;
    private String source;
    private int position;
    private int total;

    private DisplayImageOptions options;

    private ImageView viewImage;
    private LinearLayout viewLayoutDescription;
    private TextView viewDescription;
    private TextView viewSource;
    private TextView viewIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
//            if (bundle.containsKey(BUNDLE_PHOTO_ID)) photo_id = bundle.getString(BUNDLE_PHOTO_ID);
//            if (bundle.containsKey(BUNDLE_PHOTO_ITEM_ID)) photo_item_id = bundle.getString(BUNDLE_PHOTO_ITEM_ID);
            if (bundle.containsKey(BUNDLE_DESCRIPTION)) description = bundle.getString(BUNDLE_DESCRIPTION);
            if (bundle.containsKey(BUNDLE_IMAGE_URL)) image_url = bundle.getString(BUNDLE_IMAGE_URL);
            if (bundle.containsKey(BUNDLE_SOURCE)) source = bundle.getString(BUNDLE_SOURCE);
            if (bundle.containsKey(BUNDLE_POSITION)) position = bundle.getInt(BUNDLE_POSITION);
            if (bundle.containsKey(BUNDLE_TOTAL)) total = bundle.getInt(BUNDLE_TOTAL);
        }

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(getActivity()));
        }

        options = new DisplayImageOptions.Builder()
//                .preProcessor(new BitmapProcessorCenterCrop())
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo_item, container, false);

        viewImage = (ImageView) rootView.findViewById(R.id.image);
        viewLayoutDescription = (LinearLayout) rootView.findViewById(R.id.layout_description);
        viewDescription = (TextView) rootView.findViewById(R.id.description);
        viewSource = (TextView) rootView.findViewById(R.id.source);
        viewIndicator = (TextView) rootView.findViewById(R.id.indicator);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        if (!image_url.isEmpty()) {
            ImageLoader.getInstance().displayImage(image_url, viewImage, options);
//        } else {
//			viewImage.setImageResource(R.drawable.default_image);
//		}
        viewDescription.setText(description);
        viewSource.setText(source);
        viewIndicator.setText(position + "/" + total);

        viewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewLayoutDescription.setVisibility(viewLayoutDescription.getVisibility() != View.VISIBLE ? View.VISIBLE : View.GONE);
            }
        });
    }
}