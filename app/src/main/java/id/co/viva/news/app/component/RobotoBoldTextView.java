package id.co.viva.news.app.component;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by reza on 16/01/15.
 */
public class RobotoBoldTextView extends TextView {

    private Drawable[] drawables;

    public RobotoBoldTextView(Context context, AttributeSet as) {
        super(context, as);
    }

    public RobotoBoldTextView(Context context) {
        super(context);
    }

    @Override
    public void setTypeface(Typeface tf, int style) {
        try {
            Typeface helveticaTF = Typeface.createFromAsset(getContext().getAssets(), "fonts/Roboto-Regular.ttf");
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
