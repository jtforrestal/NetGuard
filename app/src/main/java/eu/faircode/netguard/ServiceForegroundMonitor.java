package eu.faircode.netguard;

import android.accessibilityservice.AccessibilityService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.util.TypedValue;
import android.view.accessibility.AccessibilityEvent;


import java.util.ArrayList;
import java.util.List;

import androidx.core.app.NotificationCompat;


public class ServiceForegroundMonitor extends AccessibilityService {
    private static final String TAG = "NetGuard.Accesibility";
    private static final int NOTIFY_MONITORING =100;
    private List<Rule> listRule  = new ArrayList<>();
    private NotificationManager nm;
    @Override
    public void onServiceConnected(){
        Notification notification = getInteractiveNotification("Home");
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFY_MONITORING, notification);
        listRule = Rule.getRules(true, ServiceForegroundMonitor.this);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        CharSequence pkgName = event.getPackageName();

        if(pkgName != null) {
            Log.d(TAG,"Event, Package Name =" + pkgName.toString());
            Rule rule = findRuleByPackageName(pkgName.toString(), listRule);
            if (rule != null && !(rule.system)) {
                Notification notification = getInteractiveNotification(rule.name);
                nm.notify(NOTIFY_MONITORING, notification);

            }
        }
    }

    @Override
    public void onInterrupt(){
    }

    @Override
    public boolean onUnbind(Intent intent){
        nm.cancel(NOTIFY_MONITORING);
        return false;
    }


    private Notification getInteractiveNotification(CharSequence name ){
        Intent main = new Intent(this, ActivityMain.class); // intent to go to main
        PendingIntent pi = PendingIntent.getActivity(this, 0, main, PendingIntent.FLAG_UPDATE_CURRENT); // attach main intent to notification
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, tv, true);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "foreground");
        builder.setSmallIcon(R.drawable.ic_equalizer_white_24dp)
                .setContentIntent(pi)
                .setColor(tv.data)
                .setOngoing(true)
                .setAutoCancel(false)
                .addAction(R.drawable.ic_security_white_24dp, getString(R.string.foreground_rule_update), pi);

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

    private Rule findRuleByPackageName(String pck, List<Rule> lst){
        for(Rule rule: lst){
            if((rule.packageName != null) && rule.packageName.toLowerCase().contains(pck.toLowerCase())){
                return rule;
            }
        }
        return null;
    }
}

