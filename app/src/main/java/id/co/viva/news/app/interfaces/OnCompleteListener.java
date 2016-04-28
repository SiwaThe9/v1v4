package id.co.viva.news.app.interfaces;

/**
 * Created by reza on 28/11/14.
 */
public interface OnCompleteListener {
    void onComplete(String message);
    void onFailed(String message);
    void onError(String message);
    void onDelay(String message);
}
