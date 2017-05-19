package com.example.eka.myminimal_todo;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.eka.myminimal_todo.Activity.ReminderActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by eka on 2017. 5. 13..
 */

public class TodoNotificationService extends IntentService {

    private SharedPreferences pref;
    private SharedPreferences.Editor pref_edit;
    private Gson gson = new Gson();
    private String json;

    private ArrayList<ToDoItem> toDoItems = new ArrayList<>();

    private int index;
    private String todoText;
    private String todoTitle="Remind";

    public TodoNotificationService() {
        super("");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        index = intent.getIntExtra("index",-1);
        todoText = intent.getStringExtra("todoText");
        Context context = getApplicationContext();
        Intent intent1 = new Intent(context,ReminderActivity.class);
        intent1.putExtra("index",index);
        Log.e("noti index",index+"");
        //상단바에띄우기
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification.Builder(this)
                .setContentTitle(todoTitle)
                .setSmallIcon(R.drawable.ic_check_24dp)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentText(todoText)
                .setContentIntent(PendingIntent.getActivity(context,index,intent1,PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
        manager.notify(index,notification);

        //알람 제거된 정보 저장
        pref = getSharedPreferences("ToDoList",MODE_PRIVATE);
        pref_edit = pref.edit();
        json=pref.getString("ToDoList",null);
        toDoItems = gson.fromJson(json,new TypeToken<ArrayList<ToDoItem>>(){}.getType());
        toDoItems.get(index).setToDoChecked(false);
        json=gson.toJson(toDoItems);
        pref_edit.putString("ToDoList",json);
        pref_edit.apply();
    }
}
