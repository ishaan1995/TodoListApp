package com.ishaan.todolists;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by ishaan on 06/05/16.
 */
public class TodoBroadcast extends BroadcastReceiver {

    PendingIntent pendingIntent;
    private NotificationManager mManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        //Toast.makeText(context, "Here", Toast.LENGTH_SHORT).show();
        Boolean b = intent.getExtras().getBoolean("flag");
        String msg =intent.getExtras().getString("todoitem");
        String place =intent.getExtras().getString("place");
        int id = intent.getExtras().getInt("notid");
        mManager =(NotificationManager) context.getSystemService
                (context.NOTIFICATION_SERVICE);
        mManager.cancel(id);
        //mManager.cancelAll();
        if (b){
            Intent i = new Intent(context,TodoService.class);
            context.stopService(i);

            //Toast.makeText(context, "Service stopped!!", Toast.LENGTH_SHORT).show();
        }else{
            Intent i = new Intent(context,TodoService.class);
            context.stopService(i);//close previous service
            //context.startService(i);
            Intent myIntent = new Intent(context, TodoService.class);
            Bundle bundle = new Bundle();
            bundle.putString("todoitem",msg);
            bundle.putString("place",place);
            myIntent.putExtras(bundle);
            pendingIntent = PendingIntent.getService(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            //snooze notification till 30 mins
            calendar.add(Calendar.MINUTE,30);
            calendar.add(Calendar.SECOND, 10);
            //calendar.add(Calendar.MINUTE,1);
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
