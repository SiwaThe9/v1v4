package id.co.viva.news.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.Locale;

import id.co.viva.news.app.R;
import id.co.viva.news.app.fragment.SubChannel3Fragment;
import id.co.viva.news.app.object.SubChannel;
import id.co.viva.news.app.util.Function;

public class SubChannel3Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private int last_position = -1;
    private int width;

    private Context context;
    private Fragment fragment;
    private ArrayList<SubChannel> datas = new ArrayList<SubChannel>();

    private DisplayImageOptions options;

    public SubChannel3Adapter(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(context));
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

        initScreenOrientation();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SubChannel data = datas.get(position);

        ViewHolderSubChannel holderSubChannel = (ViewHolderSubChannel) holder;

        holderSubChannel.viewLayout.getLayoutParams().height = width * 9 / 16;
//        if(!data.image_url.isEmpty()) {
        if(holderSubChannel.viewImage.getTag() == null || holderSubChannel.viewImage.getTag().equals(data.image_url)) {
            ImageLoader.getInstance().displayImage(data.image_url, holderSubChannel.viewImage, options);

            holderSubChannel.viewImage.setTag(data.image_url);
        }
//        } else {
//            holderSubChannel.viewImage.setImageResource(R.drawable.default_image);
//        }
        holderSubChannel.viewTitle.setText(data.title.toUpperCase(Locale.getDefault()));

        holderSubChannel.viewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(data.action.equals("list-sub-channel-2")) {
                    ((SubChannel3Fragment) fragment).openListSubChannel2(data.name, data.ab_color, data.title, data.api_url);
                } else if(data.action.equals("list-sub-channel-3")) {
                    ((SubChannel3Fragment) fragment).openListSubChannel3(data.name, data.ab_color, data.title, data.api_url);
                } else if(data.action.equals("list-photo")) {
                    ((SubChannel3Fragment) fragment).openListPhoto(data.name, data.ab_color, data.title, data.api_url);
                } else if(data.action.equals("list-video")) {
                    ((SubChannel3Fragment) fragment).openListVideo(data.name, data.ab_color, data.title, data.api_url);
                }
            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewSubChannel = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_channel_middle, parent, false);
        return new ViewHolderSubChannel(viewSubChannel);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getPosition();
        if (position > last_position) {
            last_position = position;

            int anim = 0;
            if (position % 2 != 0) {
                anim = R.anim.slide_left_enter;
            } else {
                anim = R.anim.slide_right_enter;
            }
            Animation animation = AnimationUtils.loadAnimation(context, anim);

            if (holder instanceof ViewHolderSubChannel) {
                ((ViewHolderSubChannel) holder).viewLayout.startAnimation(animation);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (holder instanceof ViewHolderSubChannel) {
            ((ViewHolderSubChannel) holder).viewLayout.clearAnimation();
        }
    }

    public void setDatas(ArrayList<SubChannel> datas) {
        this.datas = datas;
        last_position = -1;
    }

    public void initScreenOrientation() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = Function.getScreenOrientation(context) == Configuration.ORIENTATION_LANDSCAPE ? displayMetrics.heightPixels : displayMetrics.widthPixels;

        notifyDataSetChanged();
    }

    public static class ViewHolderSubChannel extends RecyclerView.ViewHolder {
        public CardView viewLayout;
        public ImageView viewImage;
        public TextView viewTitle;

        public ViewHolderSubChannel(View view) {
            super(view);

            viewLayout = (CardView) view.findViewById(R.id.layout);
            viewImage = (ImageView) view.findViewById(R.id.image);
            viewTitle = (TextView) view.findViewById(R.id.title);
        }
    }
}