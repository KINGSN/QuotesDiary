package com.royal.attitude.status.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import android.widget.Toast;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationReceivedResult;
import com.royal.attitude.status.R;
import com.royal.attitude.status.SplashActivity;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class NotificationExtenderExample extends NotificationExtenderService {

    public static final int NOTIFICATION_ID = 1;
    public static final String CHANNEL_ID = "quotes_channel";
    private String bigpicture;
    String title;
    String message;

    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {

        title = receivedResult.payload.title;
        message = receivedResult.payload.body;
        bigpicture = receivedResult.payload.bigPicture;

        String url = "";

        try {
            Constants.pushCID = receivedResult.payload.additionalData.getString("cat_id");
            Constants.pushCName = receivedResult.payload.additionalData.getString("cat_name");
            url = receivedResult.payload.additionalData.getString("external_link");
        } catch (Exception e) {
            e.printStackTrace();
        }

        sendNotification(title, message, url);
        return true;
    }

    private void sendNotification(String title, String msg, String url) {
        NotificationManager mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent;
        if (!Constants.pushCID.equals("0")) {
            intent = new Intent(this, SplashActivity.class);
            intent.putExtra("ispushnoti", true);
        } else if (url != null && !url.equals("")) {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
        } else {
            intent = new Intent(this, SplashActivity.class);
        }
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationChannel mChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "QuotesPush";// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setAutoCancel(true)
                .setSound(uri)
                .setAutoCancel(true)
                .setLights(Color.RED, 800, 800)
                .setContentText(msg)
                .setChannelId(CHANNEL_ID);

        mBuilder.setSmallIcon(getNotificationIcon(mBuilder));
        try {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.app_icon));
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "errror large- " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        if (title.trim().isEmpty()) {
            mBuilder.setContentTitle(getString(R.string.app_name));
            mBuilder.setTicker(getString(R.string.app_name));
        } else {
            mBuilder.setContentTitle(title);
            mBuilder.setTicker(title);
        }

        if (bigpicture != null) {
            mBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(getBitmapFromURL(bigpicture)));
        }

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setColor(getColour());
            return R.drawable.ic_noti;
        } else {
            return R.mipmap.app_icon;
        }
    }

    private int getColour() {
        return 0x8b5630;
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            InputStream input;
            if(src.contains("https://")) {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            }
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            return null;
        }
    }
}
