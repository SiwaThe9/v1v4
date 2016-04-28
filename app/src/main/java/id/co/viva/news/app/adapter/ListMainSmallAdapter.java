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
import id.co.viva.news.app.model.BeritaSekitar;
import id.co.viva.news.app.model.EntityMain;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by rezarachman on 01/10/14.
 */
public class ListMainSmallAdapter extends BaseAdapter {

    private Context context;
    private int type;
    private ArrayList<BeritaSekitar> beritaSekitarArrayList;
    private ArrayList<EntityMain> entityMains;

    private DisplayImageOptions options;

    public ListMainSmallAdapter(Context context, int type,
                                ArrayList<BeritaSekitar> beritaSekitarArrayList,
                                ArrayList<EntityMain> entityMains) {
        this.context = context;
        this.type = type;
        this.beritaSekitarArrayList = beritaSekitarArrayList;
        this.entityMains = entityMains;

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
        if (type == Constant.NEWS_AROUND_LIST) {
            return beritaSekitarArrayList.size();
        } else {
            return entityMains.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (type == Constant.NEWS_AROUND_LIST) {
            return beritaSekitarArrayList.get(position);
        } else {
            return entityMains.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolderSmallCard holderSmall;
        //Checking view
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            //Checking list type
            view = inflater.inflate(R.layout.item_channel_list, null);
            holderSmall = new ViewHolderSmallCard();
            holderSmall.icon_item_news = (ImageView) view.findViewById(R.id.image_item_channel);
            holderSmall.title_item_news = (TextView) view.findViewById(R.id.title_item_channel);
            holderSmall.date_item_news = (TextView) view.findViewById(R.id.date_item_channel);
            view.setTag(holderSmall);
        } else {
            holderSmall = (ViewHolderSmallCard) view.getTag();
        }
        //Choose type
        switch (type) {
            case Constant.NEWS_AROUND_LIST:
                //Get position each item
                BeritaSekitar beritaSekitar = beritaSekitarArrayList.get(position);
                //Set image
//                if (beritaSekitar.getImage_url().length() > 0) {
                if(holderSmall.icon_item_news.getTag() == null || !holderSmall.icon_item_news.getTag().equals(beritaSekitar.getImage_url())) {
                    ImageLoader.getInstance().displayImage(beritaSekitar.getImage_url(), holderSmall.icon_item_news, options);

                    holderSmall.icon_item_news.setTag(beritaSekitar.getImage_url());
                }
//                } else {
//					holderSmall.icon_item_news.setImageResource(R.drawable.default_image);
//				}
				//Check is tablet or not
				if (Constant.isTablet(context)) {
					holderSmall.icon_item_news.getLayoutParams().height =
							Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
					holderSmall.icon_item_news.requestLayout();
				}
				
                //Set title
                holderSmall.title_item_news.setText(beritaSekitar.getTitle());
                //Set time
                holderSmall.date_item_news.setText(beritaSekitar.getDate_publish());
                break;
            default:
                //Get position each item
                EntityMain entity = entityMains.get(position);
                //Set image
//                if (entity.getImage_url().length() > 0) {
                if(holderSmall.icon_item_news.getTag() == null || !holderSmall.icon_item_news.getTag().equals(entity.getImage_url())) {
                    ImageLoader.getInstance().displayImage(entity.getImage_url(), holderSmall.icon_item_news, options);

                    holderSmall.icon_item_news.setTag(entity.getImage_url());
                }
//                } else {
//					holderSmall.icon_item_news.setImageResource(R.drawable.default_image);
//				}
				//Check is tablet or not
				if (Constant.isTablet(context)) {
					holderSmall.icon_item_news.getLayoutParams().height =
							Constant.getDynamicImageSize(context, Constant.DYNAMIC_SIZE_LIST_TYPE);
					holderSmall.icon_item_news.requestLayout();
				}
				
                //Set title
                holderSmall.title_item_news.setText(entity.getTitle());
                //Set time
                if (type == Constant.TAG_POPULAR_RESULT_LIST) {
                    holderSmall.date_item_news.setText(entity.getDate_publish());
                } else {
                    try {
                        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = formatter.parse(entity.getDate_publish());
                        holderSmall.date_item_news.setText(Constant.getTimeAgo(date.getTime()));
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
                break;
        }
        return view;
    }

    private static class ViewHolderSmallCard {
        private TextView title_item_news;
        private TextView date_item_news;
        private ImageView icon_item_news;
    }

}
