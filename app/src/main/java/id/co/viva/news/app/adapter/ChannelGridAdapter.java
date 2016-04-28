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

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.Channel;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 22/10/14.
 */
public class ChannelGridAdapter extends BaseAdapter {

    private ArrayList<Channel> channelArrayList;
    private Context context;

    private DisplayImageOptions options;

    public ChannelGridAdapter(Context context, ArrayList<Channel> channelArrayList) {
        this.context = context;
        this.channelArrayList = channelArrayList;

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
        return channelArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return channelArrayList.get(position);
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
            view = inflater.inflate(R.layout.item_grid, null);
            holder = new ViewHolder();
            holder.thumb_featured = (ImageView) view.findViewById(R.id.item_thumb);
            holder.title_channel = (TextView) view.findViewById(R.id.item_title_kanal);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Channel channel = channelArrayList.get(position);

        holder.title_channel.setText(channel.getChannel_title().toUpperCase());
//        if (channel.getImage_url().length() > 0) {
        if(holder.thumb_featured.getTag() == null || !holder.thumb_featured.getTag().equals(channel.getImage_url())) {
            ImageLoader.getInstance().displayImage(channel.getImage_url(), holder.thumb_featured, options);

            holder.thumb_featured.setTag(channel.getImage_url());
        }
//        } else {
//            holder.thumb_featured.setImageResource(R.drawable.default_image);
//        }

        return view;
    }

    private static class ViewHolder {
        public TextView title_channel;
        public ImageView thumb_featured;
    }

}
