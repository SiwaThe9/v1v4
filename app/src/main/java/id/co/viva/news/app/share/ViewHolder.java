package id.co.viva.news.app.share;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import id.co.viva.news.app.R;
import id.co.viva.news.app.adapter.PhotoListAdapter;

public class ViewHolder {

    public static class ViewHolderNone extends RecyclerView.ViewHolder {
        public ViewHolderNone(View view) {
            super(view);
        }
    }

    public static class ViewHolderPhotoPreview extends RecyclerView.ViewHolder {
        public CardView viewLayout;
        public ImageView viewImage;
        public TextView viewNum;
        public ImageView viewLogo;
        public TextView viewTitle;
        public TextView viewPublishDate;
        public View viewLayer;

        public ViewHolderPhotoPreview(View view) {
            super(view);

            viewLayout = (CardView) view.findViewById(R.id.layout);
            viewImage = (ImageView) view.findViewById(R.id.image);
            viewNum = (TextView) view.findViewById(R.id.num);
            viewLogo = (ImageView) view.findViewById(R.id.logo);
            viewTitle = (TextView) view.findViewById(R.id.title);
            viewPublishDate = (TextView) view.findViewById(R.id.publish_date);
            viewLayer = view.findViewById(R.id.layer);
        }
    }

    public static class ViewHolderVideoPreview extends RecyclerView.ViewHolder {
        public CardView viewLayout;
        public ImageView viewImage;
        public ImageView viewLogo;
        public TextView viewTitle;
        public TextView viewPublishDate;
        public View viewLayer;

        public ViewHolderVideoPreview(View view) {
            super(view);

            viewLayout = (CardView) view.findViewById(R.id.layout);
            viewImage = (ImageView) view.findViewById(R.id.image);
            viewLogo = (ImageView) view.findViewById(R.id.logo);
            viewTitle = (TextView) view.findViewById(R.id.title);
            viewPublishDate = (TextView) view.findViewById(R.id.publish_date);
            viewLayer = view.findViewById(R.id.layer);
        }
    }

    public static class ViewHolderState extends RecyclerView.ViewHolder {
        public CardView viewLayout;
        public TextView viewTitle;

        public ViewHolderState(View view) {
            super(view);

            viewLayout = (CardView) view.findViewById(R.id.layout);
            viewTitle = (TextView) view.findViewById(R.id.title);
        }
    }

    public static class ViewHolderLoading extends RecyclerView.ViewHolder {
        public CardView viewLayout;

        public ViewHolderLoading(View view) {
            super(view);

            viewLayout = (CardView) view.findViewById(R.id.layout);
        }
    }

    public static class ViewHolderHeader extends RecyclerView.ViewHolder {
        public CardView viewLayout;
        public TextView viewTitle;

        public ViewHolderHeader(View view) {
            super(view);

            viewLayout = (CardView) view.findViewById(R.id.layout);
            viewTitle = (TextView) view.findViewById(R.id.title);
        }
    }

    public static class ViewHolderItemText extends ViewHolderHeader {
        public ViewHolderItemText(View view) {
            super(view);
        }
    }

    public static class ViewHolderPhotoPreviewSmall extends RecyclerView.ViewHolder {
        public CardView viewLayout;
        public ImageView viewImage;
        public TextView viewTitle;
        public View viewLayer;

        public ViewHolderPhotoPreviewSmall(View view) {
            super(view);

            viewLayout = (CardView) view.findViewById(R.id.layout);
            viewImage = (ImageView) view.findViewById(R.id.image);
            viewTitle = (TextView) view.findViewById(R.id.title);
            viewLayer = view.findViewById(R.id.layer);
        }
    }

    public static class ViewHolderVideoPreviewSmall extends RecyclerView.ViewHolder {
        public CardView viewLayout;
        public ImageView viewImage;
        public TextView viewTitle;
        public View viewLayer;

        public ViewHolderVideoPreviewSmall(View view) {
            super(view);

            viewLayout = (CardView) view.findViewById(R.id.layout);
            viewImage = (ImageView) view.findViewById(R.id.image);
            viewTitle = (TextView) view.findViewById(R.id.title);
            viewLayer = view.findViewById(R.id.layer);
        }
    }

    public static class ViewHolderPhotoDetail extends RecyclerView.ViewHolder {
        public CardView viewLayout;
        public TextView viewTitle;
        public TextView viewPublishDate;
        public TextView viewDescription;

        public ViewHolderPhotoDetail(View view) {
            super(view);

            viewLayout = (CardView) view.findViewById(R.id.layout);
            viewTitle = (TextView) view.findViewById(R.id.title);
            viewPublishDate = (TextView) view.findViewById(R.id.publish_date);
            viewDescription = (TextView) view.findViewById(R.id.description);
        }
    }

    public static class ViewHolderVideoDetail extends RecyclerView.ViewHolder {
        public CardView viewLayout;
        public TextView viewTitle;
        public TextView viewPublishDate;
        public TextView viewDescription;

        public ViewHolderVideoDetail(View view) {
            super(view);

            viewLayout = (CardView) view.findViewById(R.id.layout);
            viewTitle = (TextView) view.findViewById(R.id.title);
            viewPublishDate = (TextView) view.findViewById(R.id.publish_date);
            viewDescription = (TextView) view.findViewById(R.id.description);
        }
    }
}