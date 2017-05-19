package com.example.eka.myminimal_todo.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.example.eka.myminimal_todo.ItemTouchHelperClass;
import com.example.eka.myminimal_todo.R;
import com.example.eka.myminimal_todo.ToDoItem;
import com.example.eka.myminimal_todo.ToDoListAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Context context = this;
    private RecyclerView todo_list;
    private static ArrayList<ToDoItem> toDoItems = new ArrayList<>();
    private ToDoListAdapter toDoListAdapter;
    private static LinearLayout empty_view;
    private ItemTouchHelper itemTouchHelper;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        todo_list = (RecyclerView) findViewById(R.id.todo_list);
        toDoItems=GetTodoList();
        toDoListAdapter = new ToDoListAdapter(this, toDoItems);
        todo_list.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        todo_list.setAdapter(toDoListAdapter);
        empty_view = (LinearLayout) findViewById(R.id.empty_view);
        setEmpty_view();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,AddTodoActivity.class);
                intent.putExtra("Add_Modi",-1);
                startActivity(intent);
            }
        });
        ItemTouchHelper.Callback callback = new ItemTouchHelperClass(toDoListAdapter,todo_list);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(todo_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toDoItems.clear();
        toDoItems.addAll(GetTodoList());
        toDoListAdapter.notifyDataSetChanged();
        setEmpty_view();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                Intent intent= new Intent(this,SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return false;
        }
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

    public static void setEmpty_view(){
        if(toDoItems.size()==0){
            empty_view.setVisibility(View.VISIBLE);
        }else{
            empty_view.setVisibility(View.INVISIBLE);
        }
    }
}
