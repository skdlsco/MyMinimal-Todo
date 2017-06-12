package com.example.eka.myminimal_todo;

import java.util.Calendar;

/**
 * Created by eka on 2017. 5. 13..
 */

public class ToDoItem {
    private String contents;
    private Calendar calendar;
    private boolean isToDoChecked;

    public ToDoItem(String contents, Calendar calendar, boolean isToDoChecked) {
        this.contents = contents;
        this.calendar = calendar;
        this.isToDoChecked = isToDoChecked;
        String first_char = contents.charAt(0) + "";
        this.contents = first_char.toUpperCase() + contents.substring(1);
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public boolean isToDoChecked() {
        return isToDoChecked;
    }

    public void setToDoChecked(boolean toDoChecked) {
        isToDoChecked = toDoChecked;
    }
}
