package id.co.viva.news.app.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActNotification;
import id.co.viva.news.app.share.Prefs;

public class MyGcmListenerService extends GcmListenerService {
    public static final String TAG = MyGcmListenerService.class.getSimpleName();

    public static final String KEY_ACTION = "act";
    public static final String KEY_NOTIFICATION_ID = "nid";
    public static final String KEY_CATEGORY = "cat";
    public static final String KEY_ID = "id";
    public static final String KEY_URL = "url";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "msg";
    public static final String KEY_IMAGE = "img";

    private String action = "";
    private int notification_id = 0;
    private String category = "";
    private String id = "";
    private String url = "";
    private String title = "";
    private String message = "";
    private String image = "";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (data != null) {
            if (data.containsKey(KEY_ACTION)) action = data.getString(KEY_ACTION);
            if (data.containsKey(KEY_NOTIFICATION_ID))
                notification_id = Integer.parseInt(data.getString(KEY_NOTIFICATION_ID));
            if (data.containsKey(KEY_CATEGORY)) category = data.getString(KEY_CATEGORY);
            if (data.containsKey(KEY_ID)) id = data.getString(KEY_ID);
            if (data.containsKey(KEY_URL)) url = data.getString(KEY_URL);
            if (data.containsKey(KEY_TITLE)) title = data.getString(KEY_TITLE);
            if (data.containsKey(KEY_MESSAGE)) message = data.getString(KEY_MESSAGE);
            if (data.containsKey(KEY_IMAGE)) image = data.getString(KEY_IMAGE);
        }

        if (notification_id != 0) {
            sendNotification();
        }
    }

    private void sendNotification() {
        Prefs prefs = Prefs.getInstance(this);

        Intent intent = null;
        if (action.equals(Constant.ACTION_BROWSE)) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        } else if (action.equals(Constant.ACTION_CONTENT)) {
            intent = new Intent(this, ActNotification.class);
            intent.putExtra("id", id);
            intent.putExtra("kanal", category);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else { //Constant.ACTION_OPEN
            PackageManager manager = getPackageManager();
            try {
                intent = manager.getLaunchIntentForPackage(getPackageName());
                if (intent == null) {
                    throw new PackageManager.NameNotFoundException();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.getMessage();
            }
        }

        PendingIntent pendingIntent = null;
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pendingIntent = PendingIntent.getActivity(this, notification_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);

//        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
//        inboxStyle.setBigContentTitle("Event tracker details:");
//        for(int i = 1; i <= 10; i++) {
//            inboxStyle.addLine(i + ". Test Berita Kita");
//        }

//		switch (category) {
//			case Constant.CATEGORY_BOLA:
//				notificationBuilder.setSmallIcon(R.drawable.icon_viva_bola);
//				break;
//			case Constant.CATEGORY_LIFE:
//				notificationBuilder.setSmallIcon(R.drawable.icon_viva_life);
//				break;
//			case Constant.CATEGORY_OTOMOTIF:
//				notificationBuilder.setSmallIcon(R.drawable.icon_viva_otomotif);
//				break;
//			default:
//				notificationBuilder.setSmallIcon(R.drawable.icon_viva_news);
//		}
        notificationBuilder.setSmallIcon(R.drawable.ic_notification);
        notificationBuilder.setTicker(title + "\n" + message);
        notificationBuilder.setContentTitle(title);
        notificationBuilder.setContentText(message);
//        notificationBuilder.setNumber(i);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        notificationBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        notificationBuilder.setVibrate(new long[]{0, 500, 100, 500});
        if(pendingIntent != null) notificationBuilder.setContentIntent(pendingIntent);
        if(image.startsWith("http")) {
            try {
                Bitmap remote_picture = BitmapFactory.decodeStream((InputStream) new URL(image).getContent());
                notificationBuilder.setLargeIcon(remote_picture);
                notificationBuilder.setStyle(new NotificationCompat
                        .BigPictureStyle()
                        .bigPicture(remote_picture)
                        .setBigContentTitle(title)
                        .setSummaryText(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notification_id, notificationBuilder.build());
    }
}