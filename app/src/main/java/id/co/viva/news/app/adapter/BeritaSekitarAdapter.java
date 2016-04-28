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

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.BeritaSekitar;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 23/02/15.
 */
public class BeritaSekitarAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<BeritaSekitar> beritaSekitarArrayList;

    private DisplayImageOptions options;

    private int width;

    public BeritaSekitarAdapter(Context context, ArrayList<BeritaSekitar> beritaSekitarArrayList) {
        this.context = context;
        this.beritaSekitarArrayList = beritaSekitarArrayList;

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(context));
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

        initScreenOrientation();
    }

    @Override
    public int getCount() {
        return beritaSekitarArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return beritaSekitarArrayList.get(position);
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

        BeritaSekitar beritaSekitar = beritaSekitarArrayList.get(position);

//        if (beritaSekitar.getImage_url().length() > 0) {
        if(holder.icon_item_news.getTag() == null || !holder.icon_item_news.getTag().equals(beritaSekitar.getImage_url())) {
            ImageLoader.getInstance().displayImage(beritaSekitar.getImage_url(), holder.icon_item_news, options);

            holder.icon_item_news.setTag(beritaSekitar.getImage_url());
        }
//        } else {
//            holder.icon_item_news.setImageResource(R.drawable.default_image);
//        }
        holder.icon_item_news.getLayoutParams().height = width * 9 / 16;
//        if (Constant.isTablet(context)) {
//            holder.icon_item_news.getLayoutParams().height = Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
//            holder.icon_item_news.requestLayout();
//        }

        holder.title_item_news.setText(beritaSekitar.getTitle());
        holder.date_item_news.setText(beritaSekitar.getDate_publish());

        if (beritaSekitar.getKanal().equalsIgnoreCase("bola")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_bola);
        } else if (beritaSekitar.getKanal().equalsIgnoreCase("vivalife")) {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_life);
        } else {
            holder.icon_item_viva_news.setImageResource(R.drawable.icon_viva_news);
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
