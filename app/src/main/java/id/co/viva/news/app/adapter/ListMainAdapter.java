package id.co.viva.news.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.model.EntityMain;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by rezarachman on 01/10/14.
 */
public class ListMainAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<EntityMain> entityMains;
    private String typePopular;

    private DisplayImageOptions options;

    private int width;

    public ListMainAdapter(Context context, ArrayList<EntityMain> entityMains, String typePopular) {
        this.context = context;
        this.entityMains = entityMains;
        this.typePopular = typePopular;

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
    public int getCount() {
        return entityMains.size();
    }

    @Override
    public Object getItem(int position) {
        return entityMains.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_big_card, null);
            holder = new ViewHolder();
            holder.icon_item_news = (ImageView) view.findViewById(R.id.image_item_news);
            holder.icon_item_viva_news = (ImageView) view.findViewById(R.id.icon_headline_terbaru);
            holder.title_item_news = (TextView) view.findViewById(R.id.title_item_latest);
            holder.date_item_news = (TextView) view.findViewById(R.id.date_item_news);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        EntityMain entity = entityMains.get(position);

//        if (entity.getImage_url().length() > 0) {
        if(holder.icon_item_news.getTag() == null || !holder.icon_item_news.getTag().equals(entity.getImage_url())) {
            ImageLoader.getInstance().displayImage(entity.getImage_url(), holder.icon_item_news, options);

            holder.icon_item_news.setTag(entity.getImage_url());
        }
//        } else {
//            holder.icon_item_news.setImageResource(R.drawable.default_image);
//        }
//        if (Constant.isTablet(context)) {
            holder.icon_item_news.getLayoutParams().height = width * 9 / 16;
//            holder.icon_item_news.getLayoutParams().height = Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
//            holder.icon_item_news.requestLayout();
//        }

        holder.title_item_news.setText(entity.getTitle());

        if (entity.getKanal().equalsIgnoreCase("bola")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_bola);
        } else if (entity.getKanal().equalsIgnoreCase("vivalife")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_life);
        } else if (entity.getKanal().equalsIgnoreCase("otomotif")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_otomotif);
        } else {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_news);
        }

        try {
            if (typePopular.equals("popular")) {
                holder.date_item_news.setText(entity.getDate_publish());
            } else {
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = formatter.parse(entity.getDate_publish());
                holder.date_item_news.setText(Constant.getTimeAgo(date.getTime()));
            }
        } catch (Exception e) {
            e.getMessage();
        }

        return view;
    }

    public void initScreenOrientation() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = Function.getScreenOrientation(context) == Configuration.ORIENTATION_LANDSCAPE ? displayMetrics.heightPixels : displayMetrics.widthPixels;

        notifyDataSetChanged();
    }

    private static class ViewHolder {
        public TextView title_item_news;
        public TextView date_item_news;
        public ImageView icon_item_news;
        public ImageView icon_item_viva_news;
    }

}
