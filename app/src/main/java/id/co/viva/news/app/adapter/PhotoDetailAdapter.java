package id.co.viva.news.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import id.co.viva.news.app.R;
import id.co.viva.news.app.fragment.PhotoDetailFragment;
import id.co.viva.news.app.object.ItemView;
import id.co.viva.news.app.share.ViewHolder;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

public class PhotoDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static String TAG = PhotoDetailAdapter.class.getSimpleName();

    public final static int TYPE_NONE = 0;
    public final static int TYPE_PHOTO_DETAIL = 1;
    public final static int TYPE_HEADER = 2;
    public final static int TYPE_PHOTO_PREVIEW_SMALL = 3;

    private int last_position = -1;
    private int width;

    private Context context;
    private Fragment fragment;
    private ArrayList<ItemView> datas = new ArrayList<ItemView>();

    private DisplayImageOptions options;

    public PhotoDetailAdapter(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(context));
        }

        options = new DisplayImageOptions.Builder()
                .preProcessor(new BitmapProcessorCenterCrop())
                .displayer(new RoundedBitmapDisplayer(512))
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
        ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        width = displayMetrics.widthPixels - (Math.round((float) 2 * displayMetrics.density) * 2);
        width = displayMetrics.widthPixels < displayMetrics.heightPixels ? displayMetrics.widthPixels : displayMetrics.heightPixels;
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
            case TYPE_PHOTO_DETAIL:
                ViewHolder.ViewHolderPhotoDetail holderPhotoDetail = (ViewHolder.ViewHolderPhotoDetail) holder;

                Date date;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.getDefault()).parse(data.string_2);
                } catch (ParseException e) {
                    date = new Date();
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("cccc, dd-MM-yyyy | kk:mm");

                holderPhotoDetail.viewTitle.setText(data.string_1);
                holderPhotoDetail.viewPublishDate.setText(simpleDateFormat.format(date) + " WIB");
                holderPhotoDetail.viewDescription.setText(Html.fromHtml(data.string_3));
                break;
            case TYPE_HEADER:
                ViewHolder.ViewHolderHeader holderHeader = (ViewHolder.ViewHolderHeader) holder;

                holderHeader.viewTitle.setText(data.string_1);
                break;
            case TYPE_PHOTO_PREVIEW_SMALL:
                ViewHolder.ViewHolderPhotoPreviewSmall holderPhotoPreviewSmall = (ViewHolder.ViewHolderPhotoPreviewSmall) holder;

                holderPhotoPreviewSmall.viewTitle.setText(data.string_2);

//                try {
//                    if (!data.string_3.isEmpty()) {
                if(holderPhotoPreviewSmall.viewImage.getTag() == null || !holderPhotoPreviewSmall.viewImage.getTag().equals(data.string_3)) {
                    ImageLoader.getInstance().displayImage(data.string_3, holderPhotoPreviewSmall.viewImage, options);

                    holderPhotoPreviewSmall.viewImage.setTag(data.string_3);
                }
//                    } else {
//                        holderPhotoPreviewSmall.viewImage.setImageResource(R.drawable.default_image);
//                    }
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }

                holderPhotoPreviewSmall.viewLayer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((PhotoDetailFragment) fragment).openPhotoDetail(data.string_1);
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
            case TYPE_PHOTO_DETAIL:
                View viewPhotoDetail = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_detail, parent, false);
                return new ViewHolder.ViewHolderPhotoDetail(viewPhotoDetail);
            case TYPE_HEADER:
                View viewHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header, parent, false);
                return new ViewHolder.ViewHolderHeader(viewHeader);
            case TYPE_PHOTO_PREVIEW_SMALL:
                View viewPhotoPreviewSmall = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_photo_preview_small, parent, false);
                return new ViewHolder.ViewHolderPhotoPreviewSmall(viewPhotoPreviewSmall);
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

            if (holder instanceof ViewHolder.ViewHolderPhotoDetail) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_enter);

                ((ViewHolder.ViewHolderPhotoDetail) holder).viewLayout.startAnimation(animation);
            } else if (holder instanceof ViewHolder.ViewHolderHeader) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_enter);

                ((ViewHolder.ViewHolderHeader) holder).viewLayout.startAnimation(animation);
            } else if (holder instanceof ViewHolder.ViewHolderPhotoPreviewSmall) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_enter);

                ((ViewHolder.ViewHolderPhotoPreviewSmall) holder).viewLayout.startAnimation(animation);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (holder instanceof ViewHolder.ViewHolderPhotoDetail) {
            ((ViewHolder.ViewHolderPhotoDetail) holder).viewLayout.clearAnimation();
        } else if (holder instanceof ViewHolder.ViewHolderHeader) {
            ((ViewHolder.ViewHolderHeader) holder).viewLayout.clearAnimation();
        } else if (holder instanceof ViewHolder.ViewHolderPhotoPreviewSmall) {
            ((ViewHolder.ViewHolderPhotoPreviewSmall) holder).viewLayout.clearAnimation();
        }
    }

    public void setDatas(ArrayList<ItemView> datas) {
        this.datas = datas;
        last_position = -1;
    }
}