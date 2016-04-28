package id.co.viva.news.app.component;

import android.content.Context;
import android.content.res.Configuration;
import android.view.OrientationEventListener;

/**
 * Created by viva on 11/02/2016.
 */
public class OrientationManager extends OrientationEventListener {
    private static final String TAG = OrientationManager.class.getName();

    private int previousAngle;
    private int previousOrientation;
    private Context context;
    private OrientationChangeListener orientationChangeListener;
    private static OrientationManager instance;
    private OrientationManager(Context context) {
        super(context);
        this.context = context;
    }

    public static OrientationManager  getInstance(Context context){
        if (instance == null){
            instance = new OrientationManager(context);
        }
        return instance;
    }

    public int getOrientation(){
        return previousOrientation;
    }

    public void setOrientation(int orientation){
        this.previousOrientation = orientation;
    }


    @Override
    public void onOrientationChanged(int orientation) {
        if (orientation == -1)
            return;
        if(previousOrientation == 0){
            previousOrientation = context.getResources().getConfiguration().orientation;
            if (orientationChangeListener != null){
                orientationChangeListener.onOriChanged(previousOrientation);
            }
        }
        if (previousOrientation == Configuration.ORIENTATION_LANDSCAPE &&
                ((previousAngle > 10 && orientation <= 10) ||
                        (previousAngle < 350 && previousAngle > 270 && orientation >= 350)) ){
            if (orientationChangeListener != null){
                orientationChangeListener.onOriChanged(Configuration.ORIENTATION_PORTRAIT);
            }
            previousOrientation = Configuration.ORIENTATION_PORTRAIT;
        }

        if (previousOrientation == Configuration.ORIENTATION_PORTRAIT &&
                ((previousAngle <90 && orientation >= 90 && orientation <270) ||
                        (previousAngle > 280 && orientation <= 280 && orientation > 180))   ){
            if (orientationChangeListener != null){
                orientationChangeListener.onOriChanged(Configuration.ORIENTATION_LANDSCAPE);
            }
            previousOrientation = Configuration.ORIENTATION_LANDSCAPE;
        }
        previousAngle = orientation;
    }

    public void setOrientationChangedListener(OrientationChangeListener l){
        this.orientationChangeListener = l;
    }

    public interface OrientationChangeListener{
        public void onOriChanged(int newOrientation);
    }
}