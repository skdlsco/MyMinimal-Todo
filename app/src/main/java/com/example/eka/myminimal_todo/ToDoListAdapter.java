package com.example.eka.myminimal_todo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by eka on 2017. 5. 13..
 */

public class ToDoListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ToDoItem> toDoItems;

    public ToDoListAdapter(Context context, ArrayList<ToDoItem> toDoItems) {
        this.context = context;
        this.toDoItems = toDoItems;
    }

    @Override
    public int getCount() {
        return toDoItems.size();
    }

    @Override
    public ToDoItem getItem(int position) {
        return toDoItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.todo_list_content,null);

        TextView title = (TextView) view.findViewById(R.id.todo_list_title);
        TextView contents = (TextView) view.findViewById(R.id.todo_list_contents);
        TextView date = (TextView) view.findViewById(R.id.todo_list_date);
        if (getItem(position).isToDoChecked()){
            date.setVisibility(View.VISIBLE);
            SimpleDateFormat dateFormat = new SimpleDateFormat("M월 dd, yyyy h:mm a");
            date.setText(dateFormat.format(getItem(position).getCalendar().getTime()));
        }else{
            date.setVisibility(View.GONE);
        }
        title.setText(getItem(position).getContents().charAt(0)+"");
        contents.setText(getItem(position).getContents());
        return view;
    }
}
