package id.co.viva.news.app.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import id.co.viva.news.app.Global;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActLanding;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 10/03/15.
 */
public class TutorialFragment extends Fragment {

    private DisplayImageOptions options;

    private String mUrl;
    private boolean isInternetPresent = false;
    private Activity mActivity;
    private ProgressWheel progressWheel;
    private TextView labelText;

    public static TutorialFragment newInstance(String url) {
        TutorialFragment tutorialFragment = new TutorialFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        tutorialFragment.setArguments(bundle);
        return tutorialFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


        isInternetPresent = Global.getInstance(getActivity())
                .getConnectionStatus().isConnectingToInternet();
        mUrl = getArguments().getString("url");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.item_coachmark, container, false);
        defineViews(view);
        return view;
    }

    private void defineViews(View mView) {
        ImageView imageView = (ImageView) mView.findViewById(R.id.tutorial_screen_image);
        labelText = (TextView) mView.findViewById(R.id.text_on_tutorial_page);
        labelText.setText(getResources().getString(R.string.label_process_get_image_tutorial));
        progressWheel = (ProgressWheel) mView.findViewById(R.id.progress_wheel);
        if (isInternetPresent) {
            if (getActivity() != null) {
                ImageLoader.getInstance().displayImage(mUrl, imageView, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                progressWheel.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                progressWheel.setVisibility(View.GONE);
                                if (isAdded()) {
                                    labelText.setText(getResources().getString(R.string.label_fail_get_image_tutorial));
                                }
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                progressWheel.setVisibility(View.GONE);
                                labelText.setVisibility(View.GONE);
                            }
                        });
            } else {
                ImageLoader.getInstance().displayImage(mUrl, imageView, options,
                        new SimpleImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String imageUri, View view) {
                                progressWheel.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                                progressWheel.setVisibility(View.GONE);
                                if (isAdded()) {
                                    labelText.setText(getResources().getString(R.string.label_fail_get_image_tutorial));
                                }
                            }

                            @Override
                            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                                progressWheel.setVisibility(View.GONE);
                                labelText.setVisibility(View.GONE);
                            }
                        });
            }
        } else {
            moveToApplication();
        }
    }

    private void moveToApplication() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), ActLanding.class);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            getActivity().finish();
        } else {
            Intent intent = new Intent(mActivity, ActLanding.class);
            startActivity(intent);
            mActivity.overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
            mActivity.finish();
        }
    }

}
