package id.co.viva.news.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import id.co.viva.news.app.R;
import id.co.viva.news.app.fragment.PhotoListFragment;
import id.co.viva.news.app.object.ItemView;
import id.co.viva.news.app.share.ViewHolder;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

public class PhotoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int TYPE_NONE = 0;
    public final static int TYPE_PHOTO_PREVIEW = 1;
    public final static int TYPE_STATE = 2;
    public final static int TYPE_LOADING = 3;

    private int last_position = -1;
    private int width;

    private Context context;
    private Fragment fragment;
    private ArrayList<ItemView> datas = new ArrayList<ItemView>();

    private DisplayImageOptions options;

    public PhotoListAdapter(Context context, Fragment fragment) {
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
    public int getItemViewType(int position) {
        return datas.get(position).type;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemView data = datas.get(position);

        switch (data.type) {
            case TYPE_PHOTO_PREVIEW:
                ViewHolder.ViewHolderPhotoPreview holderPhotoPreview = (ViewHolder.ViewHolderPhotoPreview) holder;

                Date date;
                try{
                    date = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.getDefault()).parse(data.string_6);
                } catch (ParseException e) {
                    date = new Date();
                }

                holderPhotoPreview.viewImage.getLayoutParams().height = width * 9 / 16;
                holderPhotoPreview.viewNum.setText(String.valueOf(data.int_1));
                holderPhotoPreview.viewTitle.setText(data.string_2);
                holderPhotoPreview.viewPublishDate.setText(DateUtils.getRelativeTimeSpanString(date.getTime()));

                if(holderPhotoPreview.viewImage.getTag() == null || !holderPhotoPreview.viewImage.getTag().equals(data.string_4)) {
                    ImageLoader.getInstance().displayImage(data.string_4, holderPhotoPreview.viewImage, options);

                    holderPhotoPreview.viewImage.setTag(data.string_4);
                }
                if(holderPhotoPreview.viewLogo.getTag() == null || !holderPhotoPreview.viewLogo.getTag().equals(data.string_5)) {
                    ImageLoader.getInstance().displayImage(data.string_5, holderPhotoPreview.viewLogo, options);

                    holderPhotoPreview.viewLogo.setTag(data.string_5);
                }

                holderPhotoPreview.viewLayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((PhotoListFragment) fragment).openPhotoDetail(data.string_1);
                    }
                });
                break;
            case TYPE_STATE:
                ViewHolder.ViewHolderState holderState = (ViewHolder.ViewHolderState) holder;

                holderState.viewTitle.setText(data.string_1);
                holderState.viewTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(fragment instanceof PhotoListFragment) {
                            ((PhotoListFragment) fragment).startRequestListPhoto(true);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_NONE:
                View viewNone = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_none, parent, false);
                return new ViewHolder.ViewHolderNone(viewNone);
            case TYPE_PHOTO_PREVIEW:
                View viewPhotoPreview = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_preview, parent, false);
                return new ViewHolder.ViewHolderPhotoPreview(viewPhotoPreview);
            case TYPE_STATE:
                View viewState = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_state, parent, false);
                return new ViewHolder.ViewHolderState(viewState);
            case TYPE_LOADING:
                View viewLoading = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
                return new ViewHolder.ViewHolderLoading(viewLoading);
            default:
                return null;
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getPosition();
        if (position > last_position) {
            last_position = position;

            if (holder instanceof ViewHolder.ViewHolderPhotoPreview) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_left_enter);

                ((ViewHolder.ViewHolderPhotoPreview) holder).viewLayout.startAnimation(animation);
            } else if(holder instanceof ViewHolder.ViewHolderState) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_enter);

                ((ViewHolder.ViewHolderState) holder).viewLayout.startAnimation(animation);
            } else if(holder instanceof ViewHolder.ViewHolderLoading) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_enter);

                ((ViewHolder.ViewHolderLoading) holder).viewLayout.startAnimation(animation);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (holder instanceof ViewHolder.ViewHolderPhotoPreview) {
            ((ViewHolder.ViewHolderPhotoPreview) holder).viewLayout.clearAnimation();
        } else if(holder instanceof ViewHolder.ViewHolderState) {
            ((ViewHolder.ViewHolderState) holder).viewLayout.clearAnimation();
        } else if(holder instanceof ViewHolder.ViewHolderLoading) {
            ((ViewHolder.ViewHolderLoading) holder).viewLayout.clearAnimation();
        }
    }

    public void setDatas(ArrayList<ItemView> datas, boolean is_more) {
        this.datas = datas;
        if(!is_more) last_position = -1;
    }

    public void initScreenOrientation() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = Function.getScreenOrientation(context) == Configuration.ORIENTATION_LANDSCAPE ? displayMetrics.heightPixels : displayMetrics.widthPixels;

        notifyDataSetChanged();
    }
}