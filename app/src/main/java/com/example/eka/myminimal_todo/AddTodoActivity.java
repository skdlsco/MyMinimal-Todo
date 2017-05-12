package com.example.eka.myminimal_todo;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


@RequiresApi(api = Build.VERSION_CODES.N)
public class AddTodoActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Calendar calendar = Calendar.getInstance();

    private LinearLayout set_remind_layout;
    private TextView remind_text;
    private Button remind_setdate,remind_setTime;
    private ImageButton back_btn;
    private SwitchCompat remind_switch;
    private FloatingActionButton remind_set;
    private EditText remind_title;

    private ArrayList<ToDoItem> toDoItems = new ArrayList<>();

    private SimpleDateFormat dateFormat;
    private Date date= new Date();

    private boolean isSaveTodo=true;
    private boolean isRemind=false;
    private int Add_Modi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        Intent intent = getIntent();
        Add_Modi = intent.getIntExtra("Add_Modi",-1);
        GetTodoList();
        remind_title = (EditText) findViewById(R.id.remind_title);
        remind_text= (TextView) findViewById(R.id.remind_text);
        remind_setdate = (Button) findViewById(R.id.set_date_btn);
        remind_setTime = (Button) findViewById(R.id.set_time_btn);
        remind_switch = (SwitchCompat) findViewById(R.id.remind_swtich);
        set_remind_layout = (LinearLayout) findViewById(R.id.remind_layout);
        back_btn = (ImageButton) findViewById(R.id.back_todo);
        remind_set = (FloatingActionButton) findViewById(R.id.todo_set);

        set_remind_layout.setVisibility(View.GONE);
        if (Add_Modi!=-1){
            calendar= toDoItems.get(Add_Modi).getCalendar();
            remind_title.setText(toDoItems.get(Add_Modi).getContents());
            if(toDoItems.get(Add_Modi).isToDoChecked()) {
                remind_switch.setChecked(toDoItems.get(Add_Modi).isToDoChecked());
                set_remind_layout.setVisibility(View.VISIBLE);
                set_time_text();
                set_date_text();
                set_remind_text();
            }
        }



        remind_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        remind_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    animateSetRemindLayout(isChecked);
                    isRemind=true;
                    set_remind_text();
                }else{
                    isRemind=false;
                    animateSetRemindLayout(isChecked);
                }
            }
        });

        remind_setdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(AddTodoActivity.this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setThemeDark(true);
                datePickerDialog.show(getFragmentManager(),"");
            }
        });

        remind_setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(AddTodoActivity.this,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false);
                timePickerDialog.setThemeDark(true);
                timePickerDialog.show(getFragmentManager(),"");
            }
        });
        }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(remind_title.getText().toString().equals("")) isSaveTodo=false;

        if (isSaveTodo) {
            if (Add_Modi == -1) {
                toDoItems.add(new ToDoItem(remind_title.getText().toString(), calendar, remind_switch.isChecked()));
            } else {
                toDoItems.set(Add_Modi, new ToDoItem(remind_title.getText().toString(), calendar, remind_switch.isChecked()));
            }
        }
        SetTodoList();
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        calendar.set(year,monthOfYear,dayOfMonth);
        set_date_text();
    }


    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),hourOfDay,minute);
        set_time_text();
    }



    void set_date_text(){
        date=calendar.getTime();
        dateFormat = new SimpleDateFormat("d M월, yyyy ",new Locale("en"));
        remind_setdate.setText(dateFormat.format(calendar.getTime()));
    }

    void set_time_text(){
        date=calendar.getTime();
        dateFormat = new SimpleDateFormat("h:mm a");
        remind_setTime.setText(dateFormat.format(calendar.getTime()));
    }

    void set_remind_text(){
        dateFormat = new SimpleDateFormat("d M월, yyyy, h:mm a");
        remind_text.setText("Reminder set for "+dateFormat.format(calendar.getTime()));
    }

    public void GetTodoList(){
        SharedPreferences pref = getSharedPreferences("ToDoList",MODE_PRIVATE);
        SharedPreferences.Editor pref_edit = pref.edit();
        Gson gson = new Gson();
        String json = pref.getString("ToDoList","null");
        Log.e("json",json);
        if(json !="null") {
            toDoItems = gson.fromJson(json, new TypeToken<ArrayList<ToDoItem>>(){}.getType());
        }
    }

    public void SetTodoList(){
        SharedPreferences pref = getSharedPreferences("ToDoList",MODE_PRIVATE);
        SharedPreferences.Editor pref_edit = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(toDoItems);
        pref_edit.remove("ToDoList");
        pref_edit.putString("ToDoList",json);
        pref_edit.apply();
        Log.e("asdf",json);
    }

    void animateSetRemindLayout(boolean checked){
        if (checked){
            set_remind_layout.animate().alpha(1.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    set_remind_layout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else{
            set_remind_layout.animate().alpha(0.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    set_remind_layout.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }



}
