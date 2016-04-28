package id.co.viva.news.app.interfaces;

/**
 * Created by reza on 11/12/14.
 */
public interface OnPathListener {
    void onSavePathAttributes(String access_token, String user_id);
    void onErrorGetAttributes(String error);
}
