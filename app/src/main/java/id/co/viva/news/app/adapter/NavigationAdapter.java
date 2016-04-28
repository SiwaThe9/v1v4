package id.co.viva.news.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActLanding;
import id.co.viva.news.app.component.ProgressWheel;
import id.co.viva.news.app.model.NavigationItem;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by rezarachman on 30/09/14.
 */
public class NavigationAdapter extends BaseAdapter {
    private final static int MENU_LIST_ITEM = 0;
    private final static int MENU_LIST_SECTION = 1;
    private final static int NUMBER_OF_TYPE = 2;

    public int selected_position = -1;

    private Context context;
    private ArrayList<NavigationItem> navItems;
    private LayoutInflater viewInflater;

    private DisplayImageOptions options;

    public NavigationAdapter(Context context, ArrayList<NavigationItem> navItems) {
        this.context = context;
        this.navItems = navItems;

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(context));
        }

        options = new DisplayImageOptions.Builder()
                .preProcessor(new BitmapProcessorCenterCrop())
//                .displayer(new RoundedBitmapDisplayer(512))
                .showImageOnLoading(0)
                .showImageForEmptyUri(0)
                .showImageOnFail(0)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .resetViewBeforeLoading(true)
                .build();
    }

    @Override
    public int getCount() {
        return navItems.size();
    }

    @Override
    public Object getItem(int position) {
        return navItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return NUMBER_OF_TYPE;
    }

    @Override
    public int getItemViewType(int position) {
        if (navItems.get(position).type == MENU_LIST_ITEM) {
            return MENU_LIST_ITEM;
        } else {
            return MENU_LIST_SECTION;
        }
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        //Holder for component
        final ViewHolder holder;
        //Get each item
        NavigationItem navigationItem = navItems.get(position);
        //Identify kind of view
        int type = getItemViewType(position);
        //Checking view process
        if (view == null) {
            viewInflater = LayoutInflater.from(context);
            holder = new ViewHolder();
            view = viewInflater.inflate(R.layout.item_navigation_list, null);
            holder.background = (FrameLayout) view.findViewById(R.id.background);
            holder.layout = (RelativeLayout) view.findViewById(R.id.layout);
            holder.title = (TextView) view.findViewById(R.id.text_navigation_list);
            holder.image = (ImageView) view.findViewById(R.id.list_item_entry_drawable);
            holder.progress = (ProgressWheel) view.findViewById(R.id.progress_wheel_item_list);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        //Put it into component
        holder.background.setBackgroundResource(position != selected_position ? R.color.menu_item_bg : R.color.menu_item_pressed_bg);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ActLanding) context).displayView(position);
            }
        });
        holder.title.setText(navigationItem.title);
//        if (navigationItem.icon_url.length() > 0) {
        if(holder.image.getTag() == null || !holder.image.getTag().equals(navigationItem.icon_url)) {
            ImageLoader.getInstance().displayImage(navigationItem.icon_url, holder.image, options,
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            holder.progress.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            holder.progress.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            holder.progress.setVisibility(View.GONE);
                        }
                    });

            holder.image.setTag(navigationItem.icon_url);
        }
//        } else {
//            holder.image.setImageResource(0);
//        }

        return view;
    }

    private static class ViewHolder {
        public FrameLayout background;
        public RelativeLayout layout;
        public TextView title;
        public ImageView image;
        public ProgressWheel progress;
    }
}