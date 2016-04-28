package id.co.viva.news.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.model.Comment;

/**
 * Created by reza on 09/12/14.
 */
public class CommentAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Comment> commentArrayList;

    public CommentAdapter(Context context, ArrayList<Comment> commentArrayList) {
        this.commentArrayList = commentArrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return commentArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentArrayList.get(position);
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
            view = inflater.inflate(R.layout.item_comment_list, null);
            holder = new ViewHolder();
            holder.name_item_comment = (TextView) view.findViewById(R.id.name_item_comment);
            holder.date_item_comment = (TextView) view.findViewById(R.id.date_item_comment);
            holder.comment_item_content = (TextView) view.findViewById(R.id.content_item_comment);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        Comment comment = commentArrayList.get(position);
        holder.name_item_comment.setText(comment.getUsername());
        if (comment.getComment_text().length() > 0 ) {
            holder.comment_item_content.setText(comment.getComment_text());
        }
        try {
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = formatter.parse(comment.getSubmitted_date());
            holder.date_item_comment.setText(Constant.getTimeAgo(date.getTime()));
        } catch (Exception e) {
            e.getMessage();
        }

        return view;
    }

    private static class ViewHolder {
        public TextView name_item_comment;
        public TextView date_item_comment;
        public TextView comment_item_content;
    }

}
