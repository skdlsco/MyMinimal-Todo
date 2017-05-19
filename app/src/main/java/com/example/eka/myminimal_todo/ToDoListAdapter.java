package com.example.eka.myminimal_todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.eka.myminimal_todo.Activity.AddTodoActivity;
import com.example.eka.myminimal_todo.Activity.MainActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by eka on 2017. 5. 13..
 */

public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ToDoViewHolder> implements ItemTouchHelperClass.ItemTouchHelperAdapter{
    ArrayList<ToDoItem> toDoItems = new ArrayList<>();
    Context context;
    public ToDoListAdapter(Context context, ArrayList<ToDoItem> toDoItem) {
        this.context = context;
        this.toDoItems = toDoItem;
    }

    @Override
    public ToDoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//      View view= LayoutInflater.from(context).inflate(R.layout.todo_list_content,null);
        View view= LayoutInflater.from(context).inflate(R.layout.todo_list_content,parent,false);

//      2 view.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new ToDoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ToDoViewHolder holder, final int position) {
        //첫 글자, 알람 내용 set
        holder.todo_list_title.setText(toDoItems.get(position).getContents().charAt(0) + "");
        holder.todo_list_contents.setText(toDoItems.get(position).getContents());
        //알람 체크후 체크되있다면 날짜표시
        if (toDoItems.get(position).isToDoChecked()) {
            holder.todo_list_date.setVisibility(View.VISIBLE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("M월 dd, yyyy h:mm a");
            holder.todo_list_date.setText(dateFormat.format(toDoItems.get(position).getCalendar().getTime()));
        } else {
            holder.todo_list_date.setVisibility(View.GONE);
        }
        //아이템 클릭시 Add+TodoActivity 로 넘어가 알람 설정
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddTodoActivity.class);
                intent.putExtra("Add_Modi", position);
                context.startActivity(intent);
            }
        });
//        //롱클릭시 아이템 삭제
//        //모든 알람 삭제 -> 아이템 삭제 -> 모든 알람 설정
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                int i;
//                for (i = 0; i < toDoItems.size(); i++) {
//                    delAlarm(position);
//                }
//                toDoItems.remove(position);
//                notifyItemRemoved(position);
//
//                for (i = 0; i < toDoItems.size(); i++) {
//                    if (toDoItems.get(i).isToDoChecked()) {
//                        addAlarm(position);
//                    }
//                }
//                SetTodoList();
//                return true;
//            }
//
//        });
    }
    @Override
    public int getItemCount() {
        return  toDoItems.size();
    }

    // 알람 생성
    void add_Alarm(int pos) {
        if (toDoItems.get(pos).isToDoChecked()) {
            Intent intent = new Intent(context, TodoNotificationService.class);
            intent.putExtra("todoText", toDoItems.get(pos).getContents());
            intent.putExtra("index", pos);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getService(context, pos, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, toDoItems.get(pos).getCalendar().getTimeInMillis(), pendingIntent);
        }
    }
    // 알람 삭제
    void del_Alarm(int pos) {
            Intent intent = new Intent(context, TodoNotificationService.class);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getService(context, pos, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            pendingIntent.cancel();
            alarmManager.cancel(pendingIntent);
    }
    // 아이템 정보 가져오기
    public ArrayList<ToDoItem> GetTodoList(){
        SharedPreferences pref = context.getSharedPreferences("ToDoList",MODE_PRIVATE);
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
    // 아이템 정보 저
    public void SetTodoList(){
        SharedPreferences pref = context.getSharedPreferences("ToDoList",MODE_PRIVATE);
        SharedPreferences.Editor pref_edit = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(toDoItems);
        pref_edit.remove("ToDoList");
        pref_edit.putString("ToDoList",json);
        pref_edit.apply();
    }


    @Override
    public void onItemMoved(int fromPosition, int toPosition) {
        if(fromPosition<toPosition){
            for(int i=fromPosition; i<toPosition; i++){
                del_Alarm(i);
                del_Alarm(i+1);
                Collections.swap(toDoItems, i, i+1);
                add_Alarm(i);
                add_Alarm(i+1);
            }
        }
        else{
            for(int i=fromPosition; i > toPosition; i--){
                del_Alarm(i);
                del_Alarm(i-1);
                Collections.swap(toDoItems, i, i-1);
                add_Alarm(i);
                add_Alarm(i-1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        SetTodoList();
    }


    @Override
    public void onItemRemoved(int position ,RecyclerView rv) {
        int i;
        View view = LayoutInflater.from(context).inflate(R.layout.activity_main,null);
        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.empty_view);
        linearLayout.setVisibility(View.VISIBLE);
        final int pos = position;
        final ToDoItem todoitem=toDoItems.get(position);
        Log.e("asdf",toDoItems.size()+"");
        for(i=0;i<toDoItems.size();i++){
            del_Alarm(i);
            Log.e("asdfdel1",toDoItems.get(i).isToDoChecked()+" "+i);
        }
        toDoItems.remove(position);
        Log.e("asdf2",toDoItems.size()+"");
        for(i=0;i<toDoItems.size();i++){
            add_Alarm(i);
            Log.e("asdfadd1",toDoItems.get(i).isToDoChecked()+"");
        }
        notifyItemRemoved(position);
        SetTodoList();
        Snackbar.make(rv,"Deleted Todo",Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i;
                        for(i=0;i<toDoItems.size();i++){
                            del_Alarm(i);
                            Log.e("asdfdel2",toDoItems.get(i).isToDoChecked()+"");
                        }
                        toDoItems.add(pos,todoitem);
                        notifyItemInserted(pos);
                        for(i=0;i<toDoItems.size();i++){
                            add_Alarm(i);
                            Log.e("asdfadd2",toDoItems.get(i).isToDoChecked()+"");
                        }
                        MainActivity.setEmpty_view();
                    }
                })
                .show();
        MainActivity.setEmpty_view();
        SetTodoList();

    }

    public static class ToDoViewHolder extends RecyclerView.ViewHolder{
        TextView todo_list_title;
        TextView todo_list_contents;
        TextView todo_list_date;
        public ToDoViewHolder(View itemView) {
            super(itemView);
            todo_list_title = (TextView) itemView.findViewById(R.id.todo_list_title);
            todo_list_contents = (TextView) itemView.findViewById(R.id.todo_list_contents);
            todo_list_date = (TextView) itemView.findViewById(R.id.todo_list_date);
        }
    }
}
