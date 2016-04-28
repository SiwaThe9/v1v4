package id.co.viva.news.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.SearchResult;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 16/10/14.
 */
public class SearchResultAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<SearchResult> searchResultArrayList;

    private DisplayImageOptions options;

    public SearchResultAdapter(Context context, ArrayList<SearchResult> searchResultArrayList) {
        this.context = context;
        this.searchResultArrayList = searchResultArrayList;

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
        return searchResultArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return searchResultArrayList.get(position);
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
            view = inflater.inflate(R.layout.item_search_result, null);
            holder = new ViewHolder();
            holder.title_item_search_result = (TextView) view.findViewById(R.id.title_item_search_result);
            holder.date_item_search_result = (TextView) view.findViewById(R.id.date_item_search_result);
            holder.subkanal_item_search_result = (TextView) view.findViewById(R.id.subkanal_item_search_result);
            holder.subkanal_item_search_result_layout = (RelativeLayout) view.findViewById(R.id.subkanal_item_search_result_layout);
            holder.icon_item_search_result = (ImageView) view.findViewById(R.id.image_item_search_result);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        SearchResult result = searchResultArrayList.get(position);
//        if (result.getImage_url().length() > 0) {
        if(holder.icon_item_search_result.getTag() == null || !holder.icon_item_search_result.getTag().equals(result.getImage_url())) {
            ImageLoader.getInstance().displayImage(result.getImage_url(), holder.icon_item_search_result, options);

            holder.icon_item_search_result.setTag(result.getImage_url());
        }
//        } else {
//			holder.icon_item_search_result.setImageResource(R.drawable.default_image);
//		}
        holder.title_item_search_result.setText(result.getTitle());
        holder.date_item_search_result.setText(result.getDate_publish());
        holder.subkanal_item_search_result.setText(result.getKanal());

        if (result.getKanal().equalsIgnoreCase("bola")) {
            holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_bola);
        } else if (result.getKanal().equalsIgnoreCase("vivalife")) {
            holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_life);
        } else if (result.getKanal().equalsIgnoreCase("otomotif")) {
            holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_auto);
        } else  {
            holder.subkanal_item_search_result_layout.setBackgroundResource(R.color.color_news);
        }

        return view;
    }

    private static class ViewHolder {
        public TextView title_item_search_result;
        public TextView date_item_search_result;
        public TextView subkanal_item_search_result;
        public RelativeLayout subkanal_item_search_result_layout;
        public ImageView icon_item_search_result;
    }

}
