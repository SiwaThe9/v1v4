package id.co.viva.news.app.services;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import id.co.viva.news.app.R;
import id.co.viva.news.app.share.Prefs;
import id.co.viva.news.app.util.GCM;

public class RegistrationIntentService extends IntentService {
	public static final String TAG = RegistrationIntentService.class.getSimpleName();

	public RegistrationIntentService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			Prefs prefs = Prefs.getInstance(this);
			prefs.setGCMRegGCMServer(false);
			prefs.setGCMRegBackendServer(false);
			prefs.storeGCM();

			InstanceID instanceID = InstanceID.getInstance(this);
			String token = instanceID.getToken(getString(R.string.gcm_sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

			if(token != null) {
				if(!token.isEmpty()) {
					GcmPubSub pubSub = GcmPubSub.getInstance(this);
					pubSub.subscribe(token, "/topics/all", null);

					prefs.setGCMRegAppVersion(prefs.getAppVersionCode());
					prefs.setGCMToken(token);
					prefs.setGCMRegGCMServer(true);
					prefs.setGCMRegBackendServer(false);
					prefs.storeGCM();
				}
			}

			if(prefs.isGCMRegGCMServer() && !prefs.isGCMRegBackendServer()) {
				GCM gcm = GCM.getInstance(this);
				gcm.startRequestSaveGCM();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}