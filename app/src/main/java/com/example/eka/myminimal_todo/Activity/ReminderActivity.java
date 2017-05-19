package com.example.eka.myminimal_todo.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eka.myminimal_todo.R;
import com.example.eka.myminimal_todo.ToDoItem;
import com.example.eka.myminimal_todo.TodoNotificationService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fr.ganfra.materialspinner.MaterialSpinner;

public class ReminderActivity extends AppCompatActivity {

    private MaterialSpinner spinner;
    private Button remove;
    private ImageButton done;
    private TextView contents;

    private int alarm_turm;
    private int index;

    private SharedPreferences pref;
    private SharedPreferences.Editor pref_edit;
    private String json;
    private Gson gson = new Gson();
    private ArrayList<ToDoItem> toDoItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        pref = getSharedPreferences("ToDoList",MODE_PRIVATE);
        pref_edit = pref.edit();
        getTodoList();

        Intent intent= getIntent();
        index = intent.getIntExtra("index",-1);
        if(index<0){
            Toast.makeText(this, "왜알람이없지?..;", Toast.LENGTH_LONG).show();
            finish();
        }
        spinner = (MaterialSpinner) findViewById(R.id.reminder_spinner);
        remove = (Button) findViewById(R.id.reminder_remove);
        done = (ImageButton) findViewById(R.id.reminder_done);
        contents = (TextView) findViewById(R.id.reminder_contents);

        // 재알람 설정
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date date = new Date();
                date.setTime(date.getTime()+alarm_turm*1000);
                Calendar calendar= Calendar.getInstance();
                calendar.setTime(date);
                toDoItems.get(index).setCalendar(calendar);
                toDoItems.get(index).setToDoChecked(true);
                setTodoList();
                add_Alarm(index);
                finish();
            }
        });

        // 아이템 삭제
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i;
                for(i=0;i< toDoItems.size();i++){
                    del_Alarm(i);
                }
                toDoItems.remove(index);
                for(i=0;i< toDoItems.size();i++){
                    if(toDoItems.get(i).isToDoChecked()) {
                        add_Alarm(i);
                    }
                }
                setTodoList();
                finish();
            }
        });

        //스피너
        String[] snooze ={"10 Minutes","30 Minutes","1 Hours"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.snooze_text,snooze);
        arrayAdapter.setDropDownViewResource(R.layout.snooze_text);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        alarm_turm = 60*10;
                        break;
                    case 1:
                        alarm_turm = 60*30;
                        break;
                    case 2:
                        alarm_turm = 60*60;
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    // 알람 설정
    private void add_Alarm(int index) {
            Intent intent = new Intent(this, TodoNotificationService.class);
            intent.putExtra("todoText", toDoItems.get(index).getContents());
            intent.putExtra("index",index);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getService(this, index, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, toDoItems.get(index).getCalendar().getTimeInMillis()+alarm_turm*1000, pendingIntent);
    }
    //알람 삭제
    void del_Alarm(int index) {
            Intent intent = new Intent(this, MainActivity.class);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getService(this, index, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.cancel(pendingIntent);
    }
    //아이템 정보 가져오기
    public void getTodoList(){
        pref = getSharedPreferences("ToDoList",MODE_PRIVATE);
        pref_edit = pref.edit();
        json = pref.getString("ToDoList","null");
        Log.e("json",json);
        if(json !="null") {
            toDoItems = gson.fromJson(json, new TypeToken<ArrayList<ToDoItem>>(){}.getType());
        }
    }
    //아이템 정보 저장하기
    public void setTodoList(){
        pref = getSharedPreferences("ToDoList",MODE_PRIVATE);
        pref_edit = pref.edit();
        json = gson.toJson(toDoItems);
        pref_edit.remove("ToDoList");
        pref_edit.putString("ToDoList",json);
        pref_edit.apply();
        Log.e("json",json);
    }
}
