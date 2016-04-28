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
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.RelatedArticle;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 21/10/14.
 */
public class RelatedAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RelatedArticle> relatedArticleArrayList;

    private DisplayImageOptions options;

    public RelatedAdapter(Context context, ArrayList<RelatedArticle> relatedArticleArrayList) {
        this.relatedArticleArrayList = relatedArticleArrayList;
        this.context = context;

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
    }

    @Override
    public int getCount() {
        return relatedArticleArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return relatedArticleArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if(view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_related_article, null);
            holder = new ViewHolder();
            holder.title_item_related = (TextView) view.findViewById(R.id.title_related_article);
            holder.image_item_related = (ImageView) view.findViewById(R.id.image_related_article);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        RelatedArticle relatedArticle = relatedArticleArrayList.get(position);
        holder.title_item_related.setText(relatedArticle.getRelated_title());
//        if(relatedArticle.getImage().length() > 0) {
//            try {
        if(holder.image_item_related.getTag() == null || !holder.image_item_related.getTag().equals(relatedArticle.getImage())) {
            ImageLoader.getInstance().displayImage(relatedArticle.getImage(), holder.image_item_related, options);

            holder.image_item_related.setTag(relatedArticle.getImage());
        }
//            } catch(NullPointerException e) {
//                e.printStackTrace();
//            }
//        } else {
//			holder.image_item_related.setImageResource(R.drawable.default_image);
//		}
        return view;
    }

    private static class ViewHolder {
        public TextView title_item_related;
        public ImageView image_item_related;
    }

}
