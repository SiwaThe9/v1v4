package id.co.viva.news.app.services;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class MyInstanceIDListenerService extends InstanceIDListenerService {
	public static final String TAG = MyInstanceIDListenerService.class.getSimpleName();

	@Override
	public void onTokenRefresh() {
		Intent intent = new Intent(this, RegistrationIntentService.class);
		startService(intent);
	}
}