package id.co.viva.news.app.interfaces;

/**
 * Created by reza on 23/12/14.
 */
public interface OnSpinnerListener {
    void onSuccessLoadDataSpinner(String response, String type);
    void onErrorLoadDataSpinner(String error, String type);
}
