package com.ws.notes.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ws.notes.CalendarActivity;
import com.ws.notes.R;
import com.ws.notes.utils.TimeAid;

import java.io.File;
import java.util.Objects;

/**
 * 接受通知
 * Created by KanModel on 2017/12/30.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "NOTE.NOTIFICATION")) {
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent2 = new Intent(context, CalendarActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent2, 0);
            Notification notify = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.appwidget_preview)
                    .setContentTitle(context.getString(R.string.app_name) + "提醒")
                    .setSound(Uri.fromFile(new File("/system/media/audio/alarms/wr.ogg")))
                    .setVibrate(new long[]{0, 1000, 1000, 1000})
                    .setLights(Color.GREEN, 1000, 1000)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(intent.getStringExtra("title")))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setNumber(1).build();
            manager.notify(intent.getIntExtra("id", 0), notify);
        }
//        }
    }

    public static Boolean CancelAlarm(Context context, int notificationID) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancel(notificationID);
        }
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setAlarm(Context context, int id, long minute, String title) {
        Log.d(TAG, "setAlarm: minute: " + minute);
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.setAction("NOTE.NOTIFICATION");
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("min", minute);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int type = AlarmManager.RTC_WAKEUP;
        long triggerAtMillis = TimeAid.getNowTime() + 1000 * 60 * minute;
        if (manager != null) {
            if (Build.VERSION.SDK_INT >= 19) {
                manager.setExact(type, triggerAtMillis, pi);
            } else {
                manager.set(type, triggerAtMillis, pi);
            }
        }
    }
}
