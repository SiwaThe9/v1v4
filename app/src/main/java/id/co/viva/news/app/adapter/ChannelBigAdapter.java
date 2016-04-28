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
import id.co.viva.news.app.model.ChannelList;
import id.co.viva.news.app.model.SearchResult;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 09/06/15.
 */
public class ChannelBigAdapter extends BaseAdapter {

    private Context context;
    private int typeList;
    private ArrayList<ChannelList> channelListArrayList;
    private ArrayList<SearchResult> searchResults;

    private DisplayImageOptions options;

    private int width;

    public final static String CHANNEL_BOLA = "bola";
    public final static String CHANNEL_LIFE = "vivalife";
    public final static String CHANNEL_AUTO = "otomotif";

    public ChannelBigAdapter(Context context, int typeList,
                             ArrayList<ChannelList> channelListArrayList,
                             ArrayList<SearchResult> searchResults) {
        this.context = context;
        this.typeList = typeList;
        this.channelListArrayList = channelListArrayList;
        this.searchResults = searchResults;

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
        switch (typeList) {
            case Constant.BIG_CARD_CHANNEL_LIST:
                return channelListArrayList.size();
            case Constant.BIG_CARD_SEARCH_RESULT:
                return searchResults.size();
            default:
                break;
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        switch (typeList) {
            case Constant.BIG_CARD_CHANNEL_LIST:
                return channelListArrayList.get(position);
            case Constant.BIG_CARD_SEARCH_RESULT:
                return  searchResults.get(position);
            default:
                break;
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;
        //Checking view
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            //Checking list type
            view = inflater.inflate(R.layout.item_big_card, null);
            holder = new ViewHolder();
            holder.icon_item_channel = (ImageView) view.findViewById(R.id.image_item_news);
            holder.icon_item_viva_channel = (ImageView) view.findViewById(R.id.icon_headline_terbaru);
            holder.title_item_channel = (TextView) view.findViewById(R.id.title_item_latest);
            holder.date_item_channel = (TextView) view.findViewById(R.id.date_item_news);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //Checking type of list
        switch (typeList) {
            case Constant.BIG_CARD_CHANNEL_LIST:
                //Get position each item
                ChannelList channelList = channelListArrayList.get(position);
                //Set image
//                if (channelList.getImage_url().length() > 0) {
                if(holder.icon_item_channel.getTag() == null || !holder.icon_item_channel.getTag().equals(channelList.getImage_url())) {
                    ImageLoader.getInstance().displayImage(channelList.getImage_url(), holder.icon_item_channel, options);

                    holder.icon_item_channel.setTag(channelList.getImage_url());
                }
//                } else {
//                    holder.icon_item_channel.setImageResource(R.drawable.default_image);
//                }
                //Check is tablet or not
//                if (Constant.isTablet(context)) {
                    holder.icon_item_channel.getLayoutParams().height = width * 9 / 16;
//                    holder.icon_item_channel.getLayoutParams().height = Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
//                    holder.icon_item_channel.requestLayout();
//                }

                //Set title
                holder.title_item_channel.setText(channelList.getTitle());
                //Set icon
                switch (channelList.getKanal()) {
                    case CHANNEL_BOLA:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_bola);
                        break;
                    case CHANNEL_LIFE:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_life);
                        break;
                    case CHANNEL_AUTO:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_otomotif);
                        break;
                    default:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_news);
                        break;
                }
                //Set time
                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = formatter.parse(channelList.getDate_publish());
                    holder.date_item_channel.setText(Constant.getTimeAgo(date.getTime()));
                } catch (Exception e) {
                    e.getMessage();
                }
                break;
            case Constant.BIG_CARD_SEARCH_RESULT:
                SearchResult result = searchResults.get(position);
                //Image result
//                if (result.getImage_url().length() > 0) {
                if(holder.icon_item_channel.getTag() == null || !holder.icon_item_channel.getTag().equals(result.getImage_url())) {
                    ImageLoader.getInstance().displayImage(result.getImage_url(), holder.icon_item_channel, options);

                    holder.icon_item_channel.setTag(result.getImage_url());
                }
//                } else {
//                    holder.icon_item_channel.setImageResource(R.drawable.default_image);
//                }

                //Set title result
                holder.title_item_channel.setText(result.getTitle());
                //Set date result
                holder.date_item_channel.setText(result.getDate_publish());
                //Check channel result
                switch (result.getKanal()) {
                    case CHANNEL_BOLA:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_bola);
                        break;
                    case CHANNEL_LIFE:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_life);
                        break;
                    case CHANNEL_AUTO:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_otomotif);
                        break;
                    default:
                        holder.icon_item_viva_channel.setImageResource(R.drawable.icon_viva_news);
                        break;
                }
                break;
            default:
                break;
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
        public TextView title_item_channel;
        public TextView date_item_channel;
        public ImageView icon_item_channel;
        public ImageView icon_item_viva_channel;
    }

}
