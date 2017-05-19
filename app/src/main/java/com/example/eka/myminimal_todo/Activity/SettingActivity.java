package com.example.eka.myminimal_todo.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.eka.myminimal_todo.R;

public class SettingActivity extends AppCompatActivity{
    CheckBox mcheckBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mcheckBox = (CheckBox) findViewById(R.id.mcheckbox);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.setting_layout);
        linearLayout.setOnClickListener(checked);
    }
    View.OnClickListener checked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mcheckBox.isChecked()){
                mcheckBox.setChecked(false);
            }else {
                mcheckBox.setChecked(true);
            }
        }
    };

}
