package com.google.android.gms.fit.samples.basichistoryapifinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.fit.samples.DBLayout.ProxyDB;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/*
Used for editing an already existing profile
 */
public class EditProfile extends AppCompatActivity {
    /*
    Initiliazing buttons/edit texts/databases
     */

    Button updatebutton;
    ProxyDB profiledb;
    EditText age, weight, stepgoal, calgoal;
    Calendar calendar;
    SimpleDateFormat mdformat;
    String username;
    public HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profiledb = new ProxyDB(this);
        map = profiledb.fetchdata();

        updatebutton = (Button) findViewById(R.id.updatebutton);
        /*
        setting texts from user input, also loading previous inputs from the database
         */


        age = (EditText)findViewById(R.id.editText9);
        age.setText(map.get("Age"), TextView.BufferType.EDITABLE);
        weight = (EditText)findViewById(R.id.editText10);
        weight.setText(map.get("Weight"), TextView.BufferType.EDITABLE);
        stepgoal = (EditText)findViewById(R.id.editText11);
        stepgoal.setText(map.get("Step_Goal"), TextView.BufferType.EDITABLE);
        calgoal = (EditText)findViewById(R.id.editText12);
        calgoal.setText(map.get("act_goal"), TextView.BufferType.EDITABLE);
        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("yyyy / MM / dd ");

        Intent in = getIntent();
        username = in.getExtras().getString("usernamesignin");




        /*
        Upon clicking this button the information that's saved in the user table on the local db gets updated
         */
        updatebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddtoDB();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("usernamesignin", username);
                startActivity(intent);

                Toast.makeText(EditProfile.this, "Updated!", Toast.LENGTH_LONG).show();
            }
        });



    }

    /*
    Updating user info on the local database
     */


    public  void AddtoDB() {
        boolean isInserted = profiledb.updateDataT1(username,
                Integer.parseInt(age.getText().toString()),
                Integer.parseInt(weight.getText().toString()),
                mdformat.format(calendar.getTime()).toString(),
                Integer.parseInt(stepgoal.getText().toString()),
                Integer.parseInt(calgoal.getText().toString()));
    }


}
