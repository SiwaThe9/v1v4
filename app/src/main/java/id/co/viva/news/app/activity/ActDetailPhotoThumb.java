package id.co.viva.news.app.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import id.co.viva.news.app.R;
import id.co.viva.news.app.component.ZoomImageView;
import id.co.viva.news.app.util.BitmapProcessorCenterCrop;
import id.co.viva.news.app.util.Function;

/**
 * Created by reza on 31/12/14.
 */
public class ActDetailPhotoThumb extends ActionBarActivity {
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().init(Function.getImageLoaderConfiguration(ActDetailPhotoThumb.this));
        }

        options = new DisplayImageOptions.Builder()
//                .preProcessor(new BitmapProcessorCenterCrop())
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

        getHeaderActionBar();

        Bundle bundle = getIntent().getExtras();
        String photoUrl = bundle.getString("photoUrl");
        String image_caption = bundle.getString("image_caption");
        setContentView(R.layout.act_detail_photo_thumb);

        ZoomImageView imageView = (ZoomImageView) findViewById(R.id.img_thumb_content_dialog);
        imageView.setMaxZoom(4f);
        TextView textView = (TextView) findViewById(R.id.title_thumb_content_dialog);

//        if (photoUrl.length() > 0) {
            ImageLoader.getInstance().displayImage(photoUrl, imageView, options);
//        } else {
//			imageView.setImageResource(R.drawable.default_image);
//		}

        if (image_caption.length() > 0) {
            textView.setText(image_caption);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getHeaderActionBar() {
        ColorDrawable colorDrawable = new ColorDrawable();
        colorDrawable.setColor(getResources().getColor(R.color.black));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Detail Foto");
    }

}
