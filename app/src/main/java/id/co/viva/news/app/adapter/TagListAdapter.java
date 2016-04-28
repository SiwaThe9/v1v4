package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.model.Tag;

/**
 * Created by reza on 08/07/15.
 */
public class TagListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Tag> tags;

    public TagListAdapter(Context context, ArrayList<Tag> tags) {
        this.context = context;
        this.tags = tags;
    }

    @Override
    public int getCount() {
        return tags.size();
    }

    @Override
    public Object getItem(int position) {
        return tags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.item_tag_popular_list, null);
            holder = new ViewHolder();
            holder.tagText = (TextView) view.findViewById(R.id.item_text_tag_list);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Tag tag = tags.get(position);
        if (tag.getKey() != null) {
            if (tag.getKey().length() > 0) {
                holder.tagText.setText(tag.getKey());
            }
        }

        return view;
    }

    private static class ViewHolder {
        public TextView tagText;
    }

}
