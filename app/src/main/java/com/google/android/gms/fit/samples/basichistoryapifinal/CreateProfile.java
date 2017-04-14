package com.google.android.gms.fit.samples.basichistoryapifinal;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.fit.samples.DBLayout.FriendsDB;
import com.google.android.gms.fit.samples.DBLayout.ProxyDB;
import com.google.android.gms.fit.samples.ExceptionHandler.ExceptionHandling;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
Used for creating a profile
 */
public class CreateProfile extends AppCompatActivity {

    /*
    Initializing buttons, database, calendar, and strings
     */

    Button savebutton;
    ProxyDB profiledb;
    EditText age, weight, stepgoal, calgoal;
    Calendar calendar;
    SimpleDateFormat mdformat;
    String username;
    ExceptionHandling exceptionHandling = new ExceptionHandling();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        profiledb = new ProxyDB(this);


        Intent in = getIntent();
        username= in.getExtras().getString("usernamesignup");

        /*
        Saving userinput to textfields
         */

        savebutton = (Button) findViewById(R.id.savebutton);
        age = (EditText)findViewById(R.id.editText3);
        weight = (EditText)findViewById(R.id.editText4);
        stepgoal = (EditText)findViewById(R.id.editText5);
        calgoal = (EditText)findViewById(R.id.editText6);
        calendar = Calendar.getInstance();
        mdformat = new SimpleDateFormat("yyyy / MM / dd ");

        /*
        Upon clicking this button the goals and other information of the user get saved into the local database
         */


        savebutton.setOnClickListener(new View.OnClickListener() {

                @Override

                public void onClick(View v) {
                    if (!age.getText().toString().isEmpty()) {
                        Response.Listener<String> response = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                try {
                                    Log.i("LOGGING2: ", response);
                                    JSONObject jsonObject = new JSONObject(response);
                                    boolean success = jsonObject.getBoolean("success");

                                    if (success){
                                        Log.i("Retrieving to DB2:", "SUCCESSS");
                                        AddtoDB();
                                        Intent intent = new Intent(getApplicationContext(), Update.class);
                                        intent.putExtra("usernamesignin", username);
                                        startActivity(intent);
                                    } else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(CreateProfile.this);
                                        builder.setMessage("Login Failed :( ")
                                                .setNegativeButton("Retry", null)
                                                .create()
                                                .show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            }
                        };

                        /*
                        Adding new user to the leaderboard table on the remote database
                         */


                        FriendsDB fdb = new FriendsDB(username, 0, response);
                        RequestQueue queue = Volley.newRequestQueue(CreateProfile.this);
                        queue.add(fdb);

                    }
                    else {
                        exceptionHandling.displayErrorMessage(CreateProfile.this);
                    }


                }
            });




    }
    /*
    Adding user info to the local database
     */

    public  void AddtoDB() {

        boolean isInserted = profiledb.insertDataT1(username,
                Integer.parseInt(age.getText().toString()),
                Integer.parseInt(weight.getText().toString()),
                mdformat.format(calendar.getTime()).toString(),
                Integer.parseInt(stepgoal.getText().toString()),
                Integer.parseInt(calgoal.getText().toString()));
    }



    //When we setup the data base there's going to be a Toast message that says "incorrect password/email" incase
    //the password or email entered by the user is incorrect.


}
