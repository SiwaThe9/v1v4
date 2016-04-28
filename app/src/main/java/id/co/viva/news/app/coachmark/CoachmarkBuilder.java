package id.co.viva.news.app.coachmark;

import android.app.Activity;
import android.view.View;

import id.co.viva.news.app.interfaces.CoachmarkListener;

/**
 * Created by reza on 19/11/14.
 */
public class CoachmarkBuilder {

    CoachMarkView showtipsView;
    public CoachmarkBuilder(Activity activity) {
        this.showtipsView = new CoachMarkView(activity);
    }

    /**
     * Set highlight view. All view will be highlighted
     *
     * @param v
     * Target view
     * @return CoachmarkBuilder
     */
    public CoachmarkBuilder setTarget(View v) {
        this.showtipsView.setTarget(v);
        return this;
    }

    /**
     * Set highlighted view with custom center and radius
     *
     * @param v
     * Target View
     * @param x
     * circle center x according target
     * @param y
     * circle center y according target
     * @param radius
     * @return
     */
    public CoachmarkBuilder setTarget(View v, int x, int y, int radius) {
        showtipsView.setTarget(v, x, y, radius);
        return this;
    }

    public CoachMarkView build() {
        return showtipsView;
    }

    public CoachmarkBuilder setTitle(String text) {
        this.showtipsView.setTitle(text);
        return this;
    }

    public CoachmarkBuilder setDescription(String text) {
        this.showtipsView.setDescription(text);
        return this;
    }

    public CoachmarkBuilder displayOneTime(int showtipId) {
        this.showtipsView.setDisplayOneTime(true);
        this.showtipsView.setDisplayOneTimeID(showtipId);
        return this;
    }

    public CoachmarkBuilder setCallback(CoachmarkListener callback) {
        this.showtipsView.setCallback(callback);
        return this;
    }

    public CoachmarkBuilder setDelay(int delay) {
        showtipsView.setDelay(delay);
        return this;
    }

    public CoachmarkBuilder setTitleColor(int color) {
        showtipsView.setTitle_color(color);
        return this;
    }

    public CoachmarkBuilder setDescriptionColor(int color) {
        showtipsView.setDescription_color(color);
        return this;
    }

    public CoachmarkBuilder setBackgroundColor(int color) {
        showtipsView.setBackground_color(color);
        return this;
    }

    public CoachmarkBuilder setCircleColor(int color) {
        showtipsView.setCircleColor(color);
        return this;
    }

}
