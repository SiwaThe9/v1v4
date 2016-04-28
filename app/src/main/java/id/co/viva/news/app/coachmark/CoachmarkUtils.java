package id.co.viva.news.app.coachmark;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by reza on 19/11/14.
 */
public class CoachmarkUtils {

    private Context context;

    public CoachmarkUtils(Context context) {
        this.context = context;
    }

    boolean hasShown(int id) {
        return context.getSharedPreferences("showtips", Context.MODE_PRIVATE).getBoolean("id" + id, false);
    }

    void storeShownId(int id) {
        SharedPreferences internal = context.getSharedPreferences("showtips", Context.MODE_PRIVATE);
        internal.edit().putBoolean("id" + id, true).apply();
    }

}
