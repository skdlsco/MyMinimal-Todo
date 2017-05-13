package com.example.eka.myminimal_todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Context context = this;
    private ListView todo_list;
    private ArrayList<ToDoItem> toDoItems = new ArrayList<>();
    private ToDoListAdapter toDoListAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todo_list = (ListView) findViewById(R.id.todo_list);
        toDoItems=GetTodoList();
        toDoListAdapter = new ToDoListAdapter(this,toDoItems);
        todo_list.setAdapter(toDoListAdapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        todo_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context,AddTodoActivity.class);
                intent.putExtra("Add_Modi",position);
                startActivity(intent);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,AddTodoActivity.class);
                intent.putExtra("Add_Modi",-1);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        toDoItems.clear();
        toDoItems.addAll(GetTodoList());
        toDoListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this,SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public ArrayList<ToDoItem> GetTodoList(){
        SharedPreferences pref = getSharedPreferences("ToDoList",MODE_PRIVATE);
        SharedPreferences.Editor pref_edit = pref.edit();
        Gson gson = new Gson();
        String json = pref.getString("ToDoList","null");
        Log.e("json",json);
        ArrayList<ToDoItem> toDoItems_ = new ArrayList<>();
        if(json !="null") {
            toDoItems_ = gson.fromJson(json, new TypeToken<ArrayList<ToDoItem>>(){}.getType());
        }
        return toDoItems_;
    }



    public void SetTodoList(){
        SharedPreferences pref = getSharedPreferences("ToDoList",MODE_PRIVATE);
        SharedPreferences.Editor pref_edit = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(toDoItems);
        pref_edit.remove("ToDoList");
        pref_edit.putString("ToDoList",json);
        pref_edit.apply();
    }
}
