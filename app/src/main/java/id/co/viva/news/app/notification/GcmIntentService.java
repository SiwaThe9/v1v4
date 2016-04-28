package id.co.viva.news.app.notification;

import android.app.IntentService;
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
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.R;
import id.co.viva.news.app.activity.ActNotification;

/**
 * Created by reza on 19/01/15.
 */
public class GcmIntentService extends IntentService {

    private Intent intent;

    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!bundle.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e(Constant.TAG, "Send error: " + bundle.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e(Constant.TAG, "Deleted messages on server: " + bundle.toString());
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(Constant.TAG, "Received: " + bundle.toString());
                getContentFromNotification(bundle);
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String id, String title, String kanal, int notification_id, String type,
                                  String message, String image, String url) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (type.equalsIgnoreCase("browse")) {
            Uri uri = Uri.parse(url);
            intent = new Intent(Intent.ACTION_VIEW, uri);
        } else if (type.equalsIgnoreCase("open")) {
            PackageManager manager = getPackageManager();
            try {
                intent = manager.getLaunchIntentForPackage(getPackageName());
                if (intent == null) {
                    throw new PackageManager.NameNotFoundException();
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.getMessage();
            }
        } else {
            intent = new Intent(this, ActNotification.class);
            intent.putExtra("id", id);
            intent.putExtra("kanal", kanal);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        final NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        if (image != null) {
            if (image.length() > 0) {
                try {
                    Bitmap remote_picture = BitmapFactory.decodeStream(
                            (InputStream) new URL(image).getContent());
                    builder.setLargeIcon(remote_picture);
                    builder.setStyle(new NotificationCompat
                            .BigPictureStyle()
                            .bigPicture(remote_picture)
                            .setBigContentTitle(title)
                            .setSummaryText(message));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if (kanal.equals("vivalife")) {
            builder.setSmallIcon(R.drawable.icon_viva_life);
        } else if (kanal.equals("bola")) {
            builder.setSmallIcon(R.drawable.icon_viva_bola);
        } else {
            builder.setSmallIcon(R.drawable.icon_viva_news);
        }

        builder.setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true)
                .setTicker(title + "\n" + message)
                .setSound(sound)
                .setContentIntent(contentIntent);
        mNotificationManager.notify(notification_id, builder.build());
    }

    private void getContentFromNotification(Bundle extras) {
        String id = extras.containsKey("id") ? extras.getString("id") : "";
        String title = extras.containsKey("title") ? extras.getString("title") : "";
        String message = extras.containsKey("msg") ? extras.getString("msg") : "";
        String image = extras.containsKey("img") ? extras.getString("img") : "";
        String channel = extras.containsKey("cat") ? extras.getString("cat") : "";
        String type = extras.containsKey("act") ? extras.getString("act") : "";
        String nid = extras.containsKey("nid") ? extras.getString("nid") : "0";
        String url = extras.containsKey("url") ? extras.getString("url") : "";
        int notification_id = Integer.parseInt(nid);
        if (notification_id != 0) {
            sendNotification(id, title, channel, notification_id, type, message, image, url);
        }
    }

}
