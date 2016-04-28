package id.co.viva.news.app.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdError;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.CompanionAdSlot;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.player.VideoAdPlayer;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.Collection;

import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActivityVideoDetail;
import id.co.viva.news.app.adapter.VideoDetailAdapter;
import id.co.viva.news.app.component.AdsView;
import id.co.viva.news.app.object.Ads;
import id.co.viva.news.app.object.ItemView;
import id.co.viva.news.app.object.Video;
import id.co.viva.news.app.object.VideoCategory;
import id.co.viva.news.app.services.Analytics;
import id.co.viva.news.app.share.DB;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;
import id.co.viva.news.app.util.VolleySingleton;

public class VideoDetailFragment extends Fragment
        implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener,
        View.OnClickListener,
        SeekBar.OnSeekBarChangeListener,
        AdErrorEvent.AdErrorListener,
        AdsLoader.AdsLoadedListener,
        AdEvent.AdEventListener {
    public final static String TAG = VideoDetailFragment.class.getSimpleName();
    public final static String TAG_VIDEO_DETAIL = "tag_video_detail";

    public final static String BUNDLE_AB_COLOR = "bundle_ab_color";
    public final static String BUNDLE_CHANNEL = "bundle_channel";
    public final static String BUNDLE_TITLE = "bundle_title";
    public final static String BUNDLE_VIDEO_ID = "bundle_video_id";
    public final static String BUNDLE_IS_FULL_SCREEN = "bundle_is_full_screnn";

    public final static int HANDLER_CONTROL = 0;

    public static final int STATE_STOP = 0;
    public static final int STATE_PLAY = 1;
    public static final int STATE_PAUSE = 2;
    public static final int AD_STATE_NONE = -1;
    public static final int AD_STATE_WAIT = 0;
    public static final int AD_STATE_READY = 1;

    private DisplayImageOptions options;

    private String ab_color;
    private String channel;
    private String title;
    private String video_id;
    private boolean is_full_screen;
    private String name = "video_detail";
    private String screen_name;
    private String unit_id_video = "";
    private String unit_id_top = "";
    private String unit_id_bottom = "";

    private int player_state = STATE_STOP;
    private int ad_state = AD_STATE_WAIT;
    private boolean isSkipAble;

    private Video video;
    private ArrayList<ItemView> datas = new ArrayList<ItemView>();
    private int width;

    private DB db;
    private Analytics analytics;
    private VideoDetailAdapter adapter;
    private LinearLayoutManager layoutManager;

    private ImaSdkFactory imaSdkFactory;
    private AdsLoader adsLoader;
    private AdsManager adsManager;
    private AdDisplayContainer adDisplayContainer;
    private VideoAdPlayer videoAdPlayer;

    private LinearLayout viewLayout;
    private FrameLayout viewFrameVideo;
    private VideoView viewVideo;
    private VideoView viewVideoAd;
    private ImageView viewImage;
    private LinearLayout viewControllers;
    private ImageView viewPlayPause;
    private TextView viewProgress;
    private SeekBar viewSeek;
    private TextView viewTotal;
    private ImageView viewFullScreen;
    private ViewGroup viewAdUiContainer;
    private TextView viewTimeAd;
    private TextView viewSkipAd;
    private RecyclerView viewRecycler;
    private TextView viewMessage;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLER_CONTROL:
                    showControl(false);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Log.e(TAG, "onCreate");

        //setRetainInstance(true);

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.containsKey(BUNDLE_AB_COLOR)) ab_color = bundle.getString(BUNDLE_AB_COLOR);
            if (bundle.containsKey(BUNDLE_CHANNEL)) channel = bundle.getString(BUNDLE_CHANNEL);
            if (bundle.containsKey(BUNDLE_TITLE)) title = bundle.getString(BUNDLE_TITLE);
            if (bundle.containsKey(BUNDLE_VIDEO_ID)) video_id = bundle.getString(BUNDLE_VIDEO_ID);
            if (bundle.containsKey(BUNDLE_IS_FULL_SCREEN)) is_full_screen = bundle.getBoolean(BUNDLE_IS_FULL_SCREEN);
        }
        screen_name = name + "_" + channel + "_screen";
//        Log.e(TAG, screen_name);

        db = DB.getInstance(getActivity());
        analytics = new Analytics(getActivity());

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

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = is_full_screen ? displayMetrics.heightPixels : displayMetrics.widthPixels;

        adapter = new VideoDetailAdapter(getActivity(), this);
        layoutManager = new LinearLayoutManager(getActivity());

        imaSdkFactory = ImaSdkFactory.getInstance();
        adsLoader = imaSdkFactory.createAdsLoader(getActivity());
        adsLoader.addAdErrorListener(this);
        adsLoader.addAdsLoadedListener(this);

        videoAdPlayer = new VideoAdPlayer() {
            // 3
            @Override
            public void playAd() {
//                Log.e(TAG, "playAd()");
//                Log.e(TAG, "viewVideoAd.getDuration()=" + viewVideoAd.getDuration());
//                Log.e(TAG, "adsManager.getCurrentAd().getAdId()=" + adsManager.getCurrentAd().getAdId());
//                Log.e(TAG, "adsManager.getCurrentAd().isLinear()=" + (adsManager.getCurrentAd().isLinear() ? "TRUE" : "FALSE"));
//                Log.e(TAG, "adsManager.getCurrentAd().isSkippable()=" + (adsManager.getCurrentAd().isSkippable() ? "TRUE" : "FALSE"));
//                Log.e(TAG, "adsManager.getCurrentAd().getDuration()=" + adsManager.getCurrentAd().getDuration());
//                Log.e(TAG, "adsManager.getCurrentAd().getAdPodInfo().getPodIndex()=" + adsManager.getCurrentAd().getAdPodInfo().getPodIndex());
//                Log.e(TAG, "adsManager.getCurrentAd().getAdPodInfo().getAdPosition()=" + adsManager.getCurrentAd().getAdPodInfo().getAdPosition());
//                Log.e(TAG, "adsManager.getCurrentAd().getAdPodInfo().getMaxDuration()=" + adsManager.getCurrentAd().getAdPodInfo().getMaxDuration());
//                Log.e(TAG, "adsManager.getCurrentAd().getAdPodInfo().getTimeOffset()=" + adsManager.getCurrentAd().getAdPodInfo().getTimeOffset());
//                Log.e(TAG, "adsManager.getCurrentAd().getAdPodInfo().getTotalAds()=" + adsManager.getCurrentAd().getAdPodInfo().getTotalAds());
//                Log.e(TAG, "adsManager.getCurrentAd().getAdPodInfo().isBumper()=" + (adsManager.getCurrentAd().getAdPodInfo().isBumper() ? "TRUE" : "FALSE"));
//                Log.e(TAG, "adsManager.getCurrentAd().getAdSystem()=" + adsManager.getCurrentAd().getAdSystem());
//                Log.e(TAG, "adsManager.getCurrentAd().getContentType()=" + adsManager.getCurrentAd().getContentType());
//                Log.e(TAG, "adsManager.getCurrentAd().getTitle()=" + adsManager.getCurrentAd().getTitle());
//                Log.e(TAG, "adsManager.getCurrentAd().getDescription()=" + adsManager.getCurrentAd().getDescription());
//                Log.e(TAG, "adsManager.getCurrentAd().getWidth()=" + adsManager.getCurrentAd().getWidth());
//                Log.e(TAG, "adsManager.getCurrentAd().getHeight()=" + adsManager.getCurrentAd().getHeight());
//                Log.e(TAG, "adsManager.getCurrentAd().getTraffickingParameters()=" + adsManager.getCurrentAd().getTraffickingParameters());

                isSkipAble = adsManager.getCurrentAd().isSkippable();
//                viewTimeAd.setText(ConvertMSTOHMS((int) adsManager.getCurrentAd().getDuration() * 1000));
//                viewSkipAd.setText("Wait 5 second");
                ad_state = AD_STATE_READY;
                if (player_state == STATE_PLAY) {
                    startVideoAd();
                }
            }

            // 1
            @Override
            public void loadAd(String s) {
//                Log.e(TAG, "loadAd(" + s + ")");
                viewVideoAd.setVideoPath(s);
            }

            @Override
            public void stopAd() {
//                Log.e(TAG, "stopAd()");
//                viewVideoAd.stopPlayback();
//                viewAdUiContainer.setVisibility(View.GONE);
//
//                viewVideo.setVisibility(View.VISIBLE);
//                viewVideo.resume();
            }

            // 5
            @Override
            public void pauseAd() {
//                Log.e(TAG, "pauseAd()");
//                viewVideoAd.pause();
//                viewAdUiContainer.setVisibility(View.GONE);
//
//                viewVideo.setVisibility(View.VISIBLE);
//                viewVideo.resume();
            }

            @Override
            public void resumeAd() {
//                Log.e(TAG, "resumeAd()");
                playAd();
            }

            // 2
            @Override
            public void addCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
//                Log.e(TAG, "addCallback()");
            }

            // 4
            @Override
            public void removeCallback(VideoAdPlayerCallback videoAdPlayerCallback) {
//                Log.e(TAG, "removeCallback()");
            }

            @Override
            public VideoProgressUpdate getAdProgress() {
                return null;
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_detail, container, false);

        viewLayout = (LinearLayout) rootView.findViewById(R.id.layout);
        viewFrameVideo = (FrameLayout) rootView.findViewById(R.id.frame_video);
        viewVideo = (VideoView) rootView.findViewById(R.id.video);
        viewVideoAd = (VideoView) rootView.findViewById(R.id.video_ad);
        viewImage = (ImageView) rootView.findViewById(R.id.image);
        viewControllers = (LinearLayout) rootView.findViewById(R.id.controllers);
        viewPlayPause = (ImageView) rootView.findViewById(R.id.play_pause);
        viewProgress = (TextView) rootView.findViewById(R.id.progress);
        viewSeek = (SeekBar) rootView.findViewById(R.id.seek);
        viewTotal = (TextView) rootView.findViewById(R.id.total);
        viewFullScreen = (ImageView) rootView.findViewById(R.id.full_screen);
        viewAdUiContainer = (ViewGroup) rootView.findViewById(R.id.adUiContainer);
        viewTimeAd = (TextView) rootView.findViewById(R.id.time_ad);
        viewSkipAd = (TextView) rootView.findViewById(R.id.skip_ad);
        viewRecycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        viewMessage = (TextView) rootView.findViewById(R.id.message);

        initScreen(is_full_screen);

        viewFrameVideo.setOnClickListener(this);
        viewVideo.setOnErrorListener(this);
        viewVideo.setOnPreparedListener(this);
        viewVideo.setOnCompletionListener(this);
        viewVideoAd.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        viewTimeAd.setText(ConvertMSTOHMS(mp.getDuration() - mp.getCurrentPosition()));
                        if(mp.getCurrentPosition() < 15000) {
                            int skip = (int) Math.ceil((15000 - mp.getCurrentPosition()) / 1000);
                            viewSkipAd.setText("Wait " + skip + " second");
                            viewSkipAd.setClickable(false);
                        } else {
                            viewSkipAd.setText("SKIP >>");
                            viewSkipAd.setClickable(true);
                        }
                    }
                });
            }
        });
        viewVideoAd.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                completeVideoAd();
            }
        });
        viewSkipAd.setOnClickListener(this);
        viewPlayPause.setOnClickListener(this);
        viewSeek.setOnSeekBarChangeListener(this);
        viewFullScreen.setOnClickListener(this);
        viewMessage.setVisibility(View.GONE);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewRecycler.setHasFixedSize(true);
        viewRecycler.setLayoutManager(layoutManager);
        viewRecycler.setAdapter(adapter);

        video = db.getVideo(video_id);
        if (video != null) {
//            if (!video.image_url.isEmpty()) {
                ImageLoader.getInstance().displayImage(video.image_url, viewImage, options);
//            } else {
//                viewImage.setImageResource(R.drawable.default_image);
//            }
            viewVideo.setVideoPath(video.video_url);
//            viewVideo.setVideoPath("http://rmcdn.2mdn.net/MotifFiles/html/1248596/android_1330378998288.mp4");
            //viewVideo.start();
        }

        fillData();

//        requestVideoAds();

        initAds();

        analytics.getAnalyticByATInternet(screen_name);
        analytics.getAnalyticByGoogleAnalytic(screen_name);
    }

    @Override
    public void onStart() {
        super.onStart();

        requestAds();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (unit_id_top != null) {
            AdsView.onResume(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP));
        }
        if (unit_id_bottom != null) {
            AdsView.onResume(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM));
        }
    }

    @Override
    public void onPause() {
        if (unit_id_top != null) {
            AdsView.onPause(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP));
        }
        if (unit_id_bottom != null) {
            AdsView.onPause(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM));
        }

        super.onPause();
    }

    @Override
    public void onDestroy() {
        VolleySingleton.getInstance(getActivity()).cancelPendingRequests(TAG_VIDEO_DETAIL + "_" + video_id);

        if (unit_id_top != null) {
            AdsView.onDestroy(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP));
        }
        if (unit_id_bottom != null) {
            AdsView.onDestroy(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM));
        }

        handler.removeMessages(HANDLER_CONTROL);

        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_pause:
                if (player_state == STATE_PLAY) {
                    player_state = STATE_PAUSE;
                    viewPlayPause.setImageResource(R.drawable.ic_play_arrow_white_48dp);

                    if (ad_state == AD_STATE_NONE) {
                        viewVideo.pause();
                    } else if (ad_state == AD_STATE_READY) {
                        viewVideoAd.pause();
                    }

                    handler.removeMessages(HANDLER_CONTROL);
                } else {
                    player_state = STATE_PLAY;
                    viewPlayPause.setImageResource(R.drawable.ic_pause_white_48dp);

                    if (ad_state == AD_STATE_NONE) {
                        viewVideo.start();
                    } else if (ad_state == AD_STATE_READY) {
                        //viewVideoAd.pause();
                        startVideoAd();
                    }

                    viewImage.setVisibility(View.GONE);
                    handler.removeMessages(HANDLER_CONTROL);
                    handler.sendEmptyMessageDelayed(HANDLER_CONTROL, 3000);
                }
                break;
            case R.id.full_screen:
                rotateScreen(!is_full_screen);
//                ((ActivityVideoDetail) getActivity()).rotateScreen(!is_full_screen);
//                initScreen(!is_full_screen);
                break;
            case R.id.frame_video:
                if (player_state == STATE_PLAY) {
                    boolean is_show = viewPlayPause.getVisibility() != View.VISIBLE;
                    showControl(is_show);
                    if (is_show) {
                        handler.removeMessages(HANDLER_CONTROL);
                        handler.sendEmptyMessageDelayed(HANDLER_CONTROL, 3000);
                    }
                }
                break;
            case R.id.skip_ad:
                stopVideoAd();
                break;
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
//        Log.e("OPO", "onError");
        Toast.makeText(getActivity(), "Error " + String.valueOf(what), Toast.LENGTH_SHORT).show();
        player_state = STATE_STOP;
        handler.removeMessages(HANDLER_CONTROL);
        viewImage.setVisibility(View.VISIBLE);
        viewPlayPause.setImageResource(R.drawable.ic_play_arrow_white_48dp);
        showControl(true);
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
//        Log.e("OPO", "onPrepared");
        viewProgress.setText(ConvertMSTOHMS(mp.getCurrentPosition()));
        viewSeek.setMax(mp.getDuration());
        viewTotal.setText(ConvertMSTOHMS(mp.getDuration()));
        mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
//                Log.e("OPO", "onBufferingUpdate=" + percent);
                if (player_state == STATE_PLAY) {
                    viewProgress.setText(ConvertMSTOHMS(mp.getCurrentPosition()));
                    viewSeek.setProgress(mp.getCurrentPosition());
                } else if (player_state == STATE_PAUSE) {
                    viewVideo.pause();
                    mp.pause();
                }
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
//        Log.e("OPO", "onCompletion");
        player_state = STATE_STOP;
        handler.removeMessages(HANDLER_CONTROL);
        viewImage.setVisibility(View.VISIBLE);
        viewProgress.setText(viewTotal.getText().toString());
        viewPlayPause.setImageResource(R.drawable.ic_refresh_white_48dp);
        showControl(true);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (player_state == STATE_PLAY) {
            viewProgress.setText(ConvertMSTOHMS(progress));
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (player_state == STATE_PLAY) {
            handler.removeMessages(HANDLER_CONTROL);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (player_state == STATE_PLAY) {
            viewVideo.seekTo(seekBar.getProgress());
            viewVideo.start();
            handler.removeMessages(HANDLER_CONTROL);
            handler.sendEmptyMessageDelayed(HANDLER_CONTROL, 3000);
        } else {
            seekBar.setProgress(viewVideo.getCurrentPosition());
        }
    }

    private void fillData() {
        datas.clear();
        datas.add(new ItemView(VideoDetailAdapter.TYPE_VIDEO_DETAIL, video.title, video.publish_date, video.story));
        datas.add(new ItemView(VideoDetailAdapter.TYPE_HEADER, getString(R.string.label_other_video)));

        ArrayList<VideoCategory> videoCategories = db.getAllVideoCategoryOtherThan(channel, video_id);
        for (VideoCategory videoCategory : videoCategories) {
            Video video = db.getVideo(videoCategory.video_id);
            datas.add(new ItemView(VideoDetailAdapter.TYPE_VIDEO_PREVIEW_SMALL, video.video_id, video.title, video.image_url));
        }

        adapter.setDatas(datas);
        adapter.notifyDataSetChanged();
    }

    private void initAds() {
        Ads ads_video = db.getAds(screen_name, String.valueOf(Ads.TYPE_VIDEO), String.valueOf(Ads.POSITION_NONE));
        Ads ads_top = db.getAds(screen_name, String.valueOf(Ads.TYPE_BANNER), String.valueOf(Ads.POSITION_BANNER_TOP));
        Ads ads_bottom = db.getAds(screen_name, String.valueOf(Ads.TYPE_BANNER), String.valueOf(Ads.POSITION_BANNER_BOTTOM));

        if(ads_video != null) {
            unit_id_video = ads_video.unit_id;
            requestVideoAds();
        }
        if(ads_top != null) {
            unit_id_top = ads_top.unit_id;
            AdsView.initAds(getActivity(),
                    screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP),
                    unit_id_top,
                    viewLayout,
                    Ads.POSITION_BANNER_TOP);
        }
        if(ads_bottom != null) {
            unit_id_bottom = ads_bottom.unit_id;
            AdsView.initAds(getActivity(),
                    screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM),
                    unit_id_bottom,
                    viewLayout,
                    Ads.POSITION_BANNER_BOTTOM);
        }
    }

    private void requestAds() {
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        if (unit_id_top != null) {
            AdsView.requestAds(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_TOP), adRequest);
        }
        if (unit_id_bottom != null) {
            AdsView.requestAds(screen_name + "-" + String.valueOf(Ads.TYPE_BANNER) + "-" + String.valueOf(Ads.POSITION_BANNER_BOTTOM), adRequest);
        }
    }

    public void initScreen(boolean is_full_screen) {
        this.is_full_screen = is_full_screen;
        viewRecycler.setVisibility(is_full_screen ? View.GONE : View.VISIBLE);
        viewFrameVideo.getLayoutParams().height = is_full_screen ? width : width * 9 / 16;
    }

    private void rotateScreen(boolean is_full_screen) {
        ((ActivityVideoDetail) getActivity()).rotateScreen(is_full_screen);
        initScreen(is_full_screen);

        handler.removeMessages(HANDLER_CONTROL);
        if (player_state == STATE_PLAY) {
            handler.sendEmptyMessageDelayed(HANDLER_CONTROL, 3000);
        }
    }

    public void openVideoDetail(String video_id) {
        Intent intent = new Intent(getActivity(), ActivityVideoDetail.class);
        intent.putExtra(ActivityVideoDetail.EXTRA_AB_COLOR, ab_color);
        intent.putExtra(ActivityVideoDetail.EXTRA_CHANNEL, channel);
        intent.putExtra(ActivityVideoDetail.EXTRA_TITLE, title);
        intent.putExtra(ActivityVideoDetail.EXTRA_VIDEO_ID, video_id);
        getActivity().startActivity(intent);
    }

    private void showControl(boolean is_show) {
        if (!is_show) {
            viewPlayPause.setVisibility(View.GONE);
            viewControllers.setVisibility(View.GONE);
        } else {
            viewPlayPause.setVisibility(View.VISIBLE);
            viewControllers.setVisibility(View.VISIBLE);
        }
    }

    private String ConvertMSTOHMS(int ms) {
        int hour = ms / (1000 * 60 * 60);
        if (hour > 0) ms = ms - (1000 * 60 * hour);
        int minute = ms / (1000 * 60);
        if (minute > 0) ms = ms - (1000 * 60 * minute);
        int second = ms / 1000;
        String Hour = hour > 0 ? String.valueOf(hour) + ":" : "";
        String Minute = minute > 0 ? (minute < 10 ? "0" : "") + String.valueOf(minute) + ":" : "00:";
        String Second = second > 0 ? (second < 10 ? "0" : "") + String.valueOf(second) : "00";

        return Hour + Minute + Second;
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        AdError adError = adErrorEvent.getError();
        if(!adError.getErrorCode().equals(AdError.AdErrorCode.VAST_MEDIA_LOAD_TIMEOUT)) {
            stopVideoAd();
        }
//        Log.e(TAG, "onAdError().getErrorCode()=" + adErrorEvent.getError().getErrorCode().name());
//        Log.e(TAG, "onAdError().getMessage()=" + adErrorEvent.getError().getMessage());
    }

    @Override
    public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
//        Log.e(TAG, "onAdsManagerLoaded()");
        adsManager = adsManagerLoadedEvent.getAdsManager();
        adsManager.addAdErrorListener(this);
        adsManager.addAdEventListener(this);
        adsManager.init();
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        switch (adEvent.getType()) {
            case LOADED:
//                Log.e(TAG, "adEvent.getType()=LOADED");
                adsManager.start();
                break;
            case CONTENT_PAUSE_REQUESTED:
//                Log.e(TAG, "adEvent.getType()=CONTENT_PAUSE_REQUESTED");
//                pauseContent();
                break;
            case CONTENT_RESUME_REQUESTED:
//                Log.e(TAG, "adEvent.getType()=CONTENT_RESUME_REQUESTED");
//                resumeContent();
                break;
            case ALL_ADS_COMPLETED:
//                Log.e(TAG, "adEvent.getType()=ALL_ADS_COMPLETED");
//                if(adsManager != null) {
//                    adsManager.destroy();
//                    adsManager = null;
//                }
            default:
//                Log.e(TAG, "adEvent.getType()=" + adEvent.getType());
                break;
        }
    }

    private void requestVideoAds() {
        if(unit_id_video.isEmpty()) {
            ad_state = AD_STATE_NONE;
        } else {
            adDisplayContainer = imaSdkFactory.createAdDisplayContainer();
            adDisplayContainer.setPlayer(videoAdPlayer);
            adDisplayContainer.setAdContainer(viewAdUiContainer);

            CompanionAdSlot companionAdSlot = imaSdkFactory.createCompanionAdSlot();
            companionAdSlot.setContainer(viewAdUiContainer);
            companionAdSlot.setSize(300, 50);

            Collection<CompanionAdSlot> companionAdSlots = new ArrayList<CompanionAdSlot>();
            companionAdSlots.add(companionAdSlot);
            adDisplayContainer.setCompanionSlots(companionAdSlots);

            AdsRequest request = imaSdkFactory.createAdsRequest();
            request.setAdTagUrl(unit_id_video);
//          request.setAdTagUrl("https://pubads.g.doubleclick.net/gampad/ads?"); // ERROR TRIGER
//          request.setAdTagUrl("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/11225321/viva_apps_video_viva&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=[referrer_url]&description_url=[description_url]&correlator=" + String.valueOf(System.currentTimeMillis()));
//          request.setAdTagUrl("https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dlinear&correlator=" + String.valueOf(System.currentTimeMillis()));
//          request.setAdTagUrl("http://bs.serving-sys.com/BurstingPipe/adServer.bs?cn=is&c=23&pl=VAST&pli=15177436&PluID=0&pos=5300&ord=" + String.valueOf(System.currentTimeMillis()) + "&cim=1");
            request.setAdDisplayContainer(adDisplayContainer);
            adsLoader.requestAds(request);
        }
    }

    private void startVideoAd() {
        viewAdUiContainer.setVisibility(View.VISIBLE);
        viewVideoAd.start();

        viewVideo.pause();
        viewVideo.setVisibility(View.GONE);

        viewImage.setVisibility(View.GONE);
    }

    private void stopVideoAd() {
        ad_state = AD_STATE_NONE;
        if (player_state == STATE_PLAY) {
//            Log.e(TAG, "STATE_PLAY");
            viewVideo.setVisibility(View.VISIBLE);
            viewVideo.start();

            viewVideoAd.stopPlayback();
            viewAdUiContainer.setVisibility(View.GONE);
        } else {
//            Log.e(TAG, "STATE_PAUSE");
            viewVideo.setVisibility(View.VISIBLE);
            viewAdUiContainer.setVisibility(View.GONE);
            if (player_state == STATE_STOP) {
//                Log.e(TAG, "STATE_STOP");
                viewImage.setVisibility(View.VISIBLE);
            }
            showControl(true);
        }
    }

    private void completeVideoAd() {
        if (adsManager != null) {
            adsManager.destroy();
            adsManager = null;
        }

        adsLoader.contentComplete();

        stopVideoAd();
    }
}