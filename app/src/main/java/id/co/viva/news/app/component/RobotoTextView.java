package id.co.viva.news.app.component;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by reza on 5/22/14.
 */
public class RobotoTextView extends TextView {

    private Drawable[] drawables;

    public RobotoTextView(Context context, AttributeSet as) {
        super(context, as);
    }

    public RobotoTextView(Context context) {
        super(context);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        try {
            Typeface helveticaTF = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Light.ttf");
            super.setTypeface(helveticaTF);
        } catch (Exception e) {
            super.setTypeface(tf);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        drawables = getCompoundDrawables();
    }

    @Override
    public void setError(CharSequence error) {
        super.setError(error);
        if (error == null) {
            restoreDrawables();
        }
    }

    private void restoreDrawables() {
        if (drawables != null) {
            setCompoundDrawables(drawables[0], drawables[1], drawables[2], drawables[3]);
        }
    }

}
