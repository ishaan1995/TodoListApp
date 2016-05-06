package com.ishaan.todolists;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

/**
 * Created by ishaan on 06/05/16.
 */
public class TodoService extends Service {

    private NotificationManager mManager;
    NotificationCompat.Builder mBuilder;
    Random random = new Random();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mManager =(NotificationManager) this.getApplicationContext().getSystemService
                (this.getApplicationContext().NOTIFICATION_SERVICE);



        /*PendingIntent pendingIntent,pendingIntent1;
        Intent myIntent = new Intent(TodoService.this, TodoService.class);
        pendingIntent1 = PendingIntent.getService(TodoService.this, 0, myIntent, 0);


        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.SECOND, 10);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent1);*/
        String msg="",place="";
        int m = random.nextInt(9999 - 1000) + 1000;
        //Log.v("todos","Intent = "+intent.toString());
        if (intent!=null){
            //Toast.makeText(TodoService.this, "here", Toast.LENGTH_SHORT).show();
            Bundle b = intent.getExtras();
            msg= b.getString("todoitem");
            place = b.getString("place");
            if (place.equals("Pick a Place")){
                place="Home";
            }
        }
        Intent myintent = new Intent(this,TodoBroadcast.class);
        Bundle mybundle = new Bundle();
        Bundle mybundle1 = new Bundle();
        mybundle.putString("todoitem",msg);
        mybundle.putString("place",place);
        mybundle.putBoolean("flag",true);
        mybundle.putInt("notid",m);
        myintent.putExtras(mybundle);

        mybundle1.putBoolean("flag",false);
        mybundle1.putString("todoitem",msg);
        mybundle1.putString("place",place);
        mybundle1.putInt("notid",m);
        Intent myintent1 = new Intent(this,TodoBroadcast.class);
        myintent1.putExtras(mybundle1);
        PendingIntent pendingIntent= PendingIntent.getBroadcast(this,m,myintent,0);
        PendingIntent pendingIntent1= PendingIntent.getBroadcast(this,m+1,myintent1,0);;
        mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_logo)
                        .setAutoCancel(true)
                        .addAction(R.drawable.cancel,"Cancel",pendingIntent)
                        .addAction(R.drawable.snooze,"Snooze",pendingIntent1)
                        .setContentTitle("TodoList Reminder")
                        .setContentText(msg+" & "+"Place: "+place);

        Intent intent1 = new Intent(this.getApplicationContext(),MainActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0,
                intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingNotificationIntent);

        mManager.notify(m,mBuilder.build());
        stopSelf(startId);
        return super.onStartCommand(intent, flags, startId);

    }



}
