package id.co.viva.news.app.services;

import android.os.AsyncTask;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import id.co.viva.news.app.interfaces.OnFacebookInfoListener;

/**
 * Created by reza on 11/12/14.
 */
public class GetFacebookInfo extends AsyncTask {

    private OnFacebookInfoListener mOnFacebookInfoListener;
    private Session session;

    public GetFacebookInfo(OnFacebookInfoListener mOnFacebookInfoListener, Session session) {
        this.mOnFacebookInfoListener = mOnFacebookInfoListener;
        this.session = session;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        session = Session.getActiveSession();
        if(session != null) {
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser graphUser, Response response) {
                    if(graphUser != null) {
                        mOnFacebookInfoListener.onCompleteGetInfo(graphUser, response);
                    }
                }
            }).executeAndWait();
        }
        session.closeAndClearTokenInformation();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        mOnFacebookInfoListener.onSuccessGetInfo();
    }

}
