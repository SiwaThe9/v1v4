package id.co.viva.news.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
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

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.model.NavigationItem;
import id.co.viva.news.app.object.SubChannel;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

public class SubChannel2Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int TYPE_NONE = 0;
    public final static int TYPE_SUB_CHANNEL_TITLE_TOP = 1;
    public final static int TYPE_SUB_CHANNEL_TITLE_MIDDLE = 2;

    private int last_position = -1;
    private int width;

    private Context context;
    private Fragment fragment;
    private String action;
    private ArrayList<SubChannel> datas = new ArrayList<SubChannel>();

    private DisplayImageOptions optionsDefault;
    private DisplayImageOptions optionsCropCenter;

    public SubChannel2Adapter(Context context, Fragment fragment, String action) {
        this.context = context;
        this.fragment = fragment;
        this.action = action;

        if (!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(context));
        }

        optionsDefault = new DisplayImageOptions.Builder()
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

        optionsCropCenter = new DisplayImageOptions.Builder()
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

        initScreenOrientation();
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (datas.get(position).type.equals(NavigationItem.TYPE_SUB_CHANNEL_TITLE_TOP)) {
            return TYPE_SUB_CHANNEL_TITLE_TOP;
        } else if (datas.get(position).type.equals(NavigationItem.TYPE_SUB_CHANNEL_TITLE_MIDDLE)) {
            return TYPE_SUB_CHANNEL_TITLE_MIDDLE;
        } else {
            return TYPE_NONE;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SubChannel data = datas.get(position);

        ViewHolderSubChannel holderSubChannel = (ViewHolderSubChannel) holder;

        int gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        int height = width / 2;
        DisplayImageOptions options = optionsDefault;

        switch (getItemViewType(position)) {
            case TYPE_SUB_CHANNEL_TITLE_TOP:
                gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                break;
            case TYPE_SUB_CHANNEL_TITLE_MIDDLE:
                gravity = Gravity.CENTER;
                break;
        }
        if (action.equals(NavigationItem.ACTION_GRID_SUB_CHANNEL)) {
            if (position == 0 || position == datas.size() - 1) {
                height = width * 9 / 16;
                options = optionsDefault;
            } else {
                height = width / 2;
                options = optionsCropCenter;
            }
        } else if (action.equals(NavigationItem.ACTION_LIST_SUB_CHANNEL)) {
            height = width * 9 / 16;
            options = optionsDefault;
        }
//        holderSubChannel.viewLayout.getLayoutParams().height = position == 0 ? width * 9 / 16 : width / 2;
//        holderSubChannel.viewTitle.setGravity(Gravity.TOP);
        holderSubChannel.viewLayout.getLayoutParams().height = height;
        holderSubChannel.viewTitle.setGravity(gravity);

        if (holderSubChannel.viewImage.getTag() == null || !holderSubChannel.viewImage.getTag().equals(data.image_url)) {
            ImageLoader.getInstance().displayImage(data.image_url, holderSubChannel.viewImage, options);

            holderSubChannel.viewImage.setTag(data.image_url);
        }
        holderSubChannel.viewTitle.setText(data.title.toUpperCase(Locale.getDefault()));

        holderSubChannel.viewTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (data.action.equals(NavigationItem.ACTION_LIST_SUB_CHANNEL)) {
                    Function.openListSubChannel(context, data.action, data.name, data.ab_color, data.title, data.api_url);
                } else if (data.action.equals(NavigationItem.ACTION_LIST_PHOTO)) {
                    Function.openListPhoto(context, data.name, data.ab_color, data.title, data.api_url);
                } else if (data.action.equals(NavigationItem.ACTION_LIST_VIDEO)) {
                    Function.openListVideo(context, data.name, data.ab_color, data.title, data.api_url);
                } else if (data.action.equals(NavigationItem.ACTION_WEB_CONTENT)) {
                    Function.openWebContent(context, data.name, data.ab_color, data.title, data.url_content, data.html_content);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewSubChannel = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_channel, parent, false);
        return new ViewHolderSubChannel(viewSubChannel);
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getPosition();
        if (position > last_position) {
            last_position = position;

            int anim = 0;
            if (action.equals(NavigationItem.ACTION_GRID_SUB_CHANNEL)) {
                if (position == 0) {
                    anim = R.anim.slide_top_enter;
                } else if (datas.size() % 2 == 0 && position == datas.size() - 1) {
                    anim = R.anim.slide_bottom_enter;
                } else if (position % 2 == 0) {
                    anim = R.anim.slide_left_enter;
                } else {
                    anim = R.anim.slide_right_enter;
                }
            } else { //NavigationItem.ACTION_LIST_SUB_CHANNEL
                if (position % 2 == 0) {
                    anim = R.anim.slide_left_enter;
                } else {
                    anim = R.anim.slide_right_enter;
                }
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