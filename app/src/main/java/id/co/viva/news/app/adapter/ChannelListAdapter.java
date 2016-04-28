package id.co.viva.news.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
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
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 23/10/14.
 */
public class ChannelListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ChannelList> channelListArrayList;

    private DisplayImageOptions options;

    public ChannelListAdapter(Context context, ArrayList<ChannelList> channelListArrayList) {
        this.channelListArrayList = channelListArrayList;
        this.context = context;

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
    }

    @Override
    public int getCount() {
        return channelListArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelListArrayList.get(position);
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
            view = inflater.inflate(R.layout.item_channel_list, null);
            holder = new ViewHolder();
            holder.image_item_channel = (ImageView) view.findViewById(R.id.image_item_channel);
            holder.title_item_channel = (TextView) view.findViewById(R.id.title_item_channel);
            holder.date_item_channel = (TextView) view.findViewById(R.id.date_item_channel);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        ChannelList channelList = channelListArrayList.get(position);

        holder.title_item_channel.setText(channelList.getTitle());
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(channelList.getDate_publish());
            holder.date_item_channel.setText(Constant.getTimeAgo(date.getTime()));
        } catch (Exception e) {
            e.getMessage();
        }

//        if (channelList.getImage_url().length() > 0) {
        if(holder.image_item_channel.getTag() == null || !holder.image_item_channel.getTag().equals(channelList.getImage_url())) {
            ImageLoader.getInstance().displayImage(channelList.getImage_url(), holder.image_item_channel, options);

            holder.image_item_channel.setTag(channelList.getImage_url());
        }
//        } else {
//            holder.image_item_channel.setImageResource(R.drawable.default_image);
//        }

        return view;
    }

    private static class ViewHolder {
        public ImageView image_item_channel;
        public TextView title_item_channel;
        public TextView date_item_channel;
    }

}
