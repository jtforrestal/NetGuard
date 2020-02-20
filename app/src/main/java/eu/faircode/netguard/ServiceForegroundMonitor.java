package eu.faircode.netguard;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.TypedValue;
import android.view.accessibility.AccessibilityEvent;


import androidx.core.app.NotificationCompat;


public class ServiceForegroundMonitor extends AccessibilityService {
    private static final int NOTIFY_MONITORING =100;

    private NotificationManager nm;
    @Override
    public void onServiceConnected(){
        Notification notification = getInteractiveNotification("Home");
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFY_MONITORING, notification);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        CharSequence pkgName = event.getPackageName();
        if(pkgName == "com.android.systemui"){
            Notification notification = getInteractiveNotification("E");
            nm.notify(NOTIFY_MONITORING, notification);
            return;
        }else {
            Notification notification = getInteractiveNotification(pkgName);
            nm.notify(NOTIFY_MONITORING, notification);
        }
    }

    @Override
    public void onInterrupt(){
    }




    private Notification getInteractiveNotification(CharSequence name ){
        Intent main = new Intent(this, ActivityMain.class); // intent to go to main
        PendingIntent pi = PendingIntent.getActivity(this, 0, main, PendingIntent.FLAG_UPDATE_CURRENT); // attach main intent to notification
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, tv, true);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "foreground");
        builder.setSmallIcon(R.drawable.ic_security_white_24dp)
                .setContentIntent(pi)
                .setColor(tv.data)
                .setOngoing(true)
                .setAutoCancel(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            builder.setContentTitle(name);

        else
            builder.setContentTitle(getString(R.string.app_name))
                    .setContentText(name);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setCategory(NotificationCompat.CATEGORY_STATUS)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setPriority(NotificationCompat.PRIORITY_MAX);

        return builder.build();
    }


}

