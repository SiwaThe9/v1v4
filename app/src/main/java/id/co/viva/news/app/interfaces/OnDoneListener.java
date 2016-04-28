package id.co.viva.news.app.interfaces;

import org.json.JSONObject;

/**
 * Created by reza on 09/12/14.
 */
public interface OnDoneListener {
    void onCompleteListComment(JSONObject jsonObject);
    void onFailedListComment();
    void onErrorListComment();
}
