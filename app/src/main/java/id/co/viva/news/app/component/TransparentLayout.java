package id.co.viva.news.app.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by reza on 30/10/14.
 */
public class TransparentLayout extends RelativeLayout {

    public TransparentLayout(Context context) {
        super(context);
    }

    public TransparentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TransparentLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

}
