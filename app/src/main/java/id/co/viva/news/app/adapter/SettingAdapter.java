package id.co.viva.news.app.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

import id.co.viva.news.app.R;
import id.co.viva.news.app.fragment.SettingFragment;
import id.co.viva.news.app.object.ItemView;
import id.co.viva.news.app.share.ViewHolder;

public class SettingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public final static int TYPE_HEADER = 0;
    public final static int TYPE_ITEM_TEXT = 1;

    private int last_position = -1;

    private Context context;
    private Fragment fragment;
    private ArrayList<ItemView> datas = new ArrayList<ItemView>();

    public SettingAdapter(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).type;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ItemView data = datas.get(position);

        switch (data.type) {
            case TYPE_HEADER:
                ViewHolder.ViewHolderHeader holderHeader = (ViewHolder.ViewHolderHeader) holder;

                holderHeader.viewTitle.setText(data.string_1);
                break;
            case TYPE_ITEM_TEXT:
                ViewHolder.ViewHolderItemText holderItemText = (ViewHolder.ViewHolderItemText) holder;

                holderItemText.viewTitle.setText(data.string_2);
                holderItemText.viewTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(fragment instanceof SettingFragment) {
                            ((SettingFragment) fragment).openItem(data.string_1);
                        }
                    }
                });
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HEADER:
                View viewHeader = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_setting, parent, false);
                return new ViewHolder.ViewHolderHeader(viewHeader);
            case TYPE_ITEM_TEXT:
                View viewItemText = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text, parent, false);
                return new ViewHolder.ViewHolderItemText(viewItemText);
            default:
                return null;
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        int position = holder.getPosition();
        if (position > last_position) {
            last_position = position;

            if (holder instanceof ViewHolder.ViewHolderHeader) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_left_enter);

                ((ViewHolder.ViewHolderHeader) holder).viewLayout.startAnimation(animation);
            } else if(holder instanceof ViewHolder.ViewHolderItemText) {
                Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_enter);

                ((ViewHolder.ViewHolderItemText) holder).viewLayout.startAnimation(animation);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);

        if (holder instanceof ViewHolder.ViewHolderHeader) {
            ((ViewHolder.ViewHolderHeader) holder).viewLayout.clearAnimation();
        } else if(holder instanceof ViewHolder.ViewHolderItemText) {
            ((ViewHolder.ViewHolderItemText) holder).viewLayout.clearAnimation();
        }
    }

    public void setDatas(ArrayList<ItemView> datas, boolean is_more) {
        this.datas = datas;
        if(!is_more) last_position = -1;
    }
}