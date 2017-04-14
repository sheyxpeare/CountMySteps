package com.google.android.gms.fit.samples.basichistoryapifinal;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.fit.samples.DBLayout.ProxyDB;
import com.google.android.gms.fit.samples.DBLayout.SignInProxyDB;
import com.google.android.gms.fit.samples.DBLayout.SignUpProxyDB;
import com.google.android.gms.fit.samples.ExceptionHandler.ExceptionHandling;

import org.json.JSONException;
import org.json.JSONObject;
/*
LOGIN PAGE
 */

/*
Used for the login page
 */
public class Login extends AppCompatActivity {
    /*
    initializing buttons, databases, and edit texts
     */

    Button loginbutton, signupbutton;
    EditText usernamesignup, passwordsignup, usernamesignin, passwordsignin;
    ProxyDB profiledb;
    CheckBox keeplog;
    Intent intent;
    boolean isChecked;

    ExceptionHandling exceptionHandling = new ExceptionHandling();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        profiledb = new ProxyDB(this);

        loginbutton = (Button) findViewById(R.id.loginbutton);
        signupbutton = (Button) findViewById(R.id.signupbutton);
        usernamesignin = (EditText)findViewById(R.id.UsernameID);
        passwordsignin = (EditText)findViewById(R.id.PasswordID);
        usernamesignup = (EditText)findViewById(R.id.editText);
        passwordsignup = (EditText)findViewById(R.id.editText2);
        intent = getIntent();
        keeplog = (CheckBox) findViewById(R.id.keepMeLoggedInID);

        /*
        Check to see if user had chosen the "keep me logged in" option, if yes then don't load this page
         */

        if(intent.getExtras() != null){
            Log.i("TEST","TEST");
            keeplog.setChecked(false);
            isChecked= intent.getExtras().getBoolean("ischecked");

        } else {
            isChecked = false;

            keeplog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    SharedPreferences settings = getSharedPreferences("PREFS_NAME", 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putBoolean("isChecked", isChecked);
                    editor.commit();
                }
            });


            SharedPreferences settings1 = getSharedPreferences("PREFS_NAME", 0);
            isChecked = settings1.getBoolean("isChecked", false);
        }

        if (isChecked) {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("usernamesignin", usernamesignin.getText().toString());
            i.putExtra("ischecked", isChecked);
            startActivity(i);
        }

        /*
        Checks with remote database to find username and password and load the profile
        Loads error if username or password is incorrect
         */


        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = usernamesignin.getText().toString();
                final String password = passwordsignin.getText().toString();
                Response.Listener<String> response = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            Log.i("LOGGING: ", response);
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");

                            if (success){
                                Log.i("Retrieving to DB:" , "SUCCESSS");
                                Intent intent = new Intent(getApplicationContext(),Update.class);
                                intent.putExtra("usernamesignin", usernamesignin.getText().toString());
                                startActivity(intent);
                            } else {
                                exceptionHandling.displayErrorMessage(Login.this);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };



                RequestQueue queue = Volley.newRequestQueue(Login.this);
                SignInProxyDB rpdb = new SignInProxyDB(username, password, response);
                queue.add(rpdb);



            }
        });

        /*
        Checks with remote database to see if username is taken, if not the new username and password
        get added to the remote database, if username is taken shows an error saying "username taken"
        and gives the option to sign up for another one
         */

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    final String username = usernamesignup.getText().toString();
                    final String password = passwordsignup.getText().toString();

                    Response.Listener<String> response = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                Log.i("LOGGING: ", response);
                                JSONObject jsonObject = new JSONObject(response);
                                boolean success = jsonObject.getBoolean("success");

                                if (success){
                                    Log.i("Adding to DB:" , "SUCCESSS");
                                    Intent intent = new Intent(getApplicationContext(), CreateProfile.class);
                                    intent.putExtra("usernamesignup", usernamesignup.getText().toString());
                                    startActivity(intent);
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                    builder.setMessage("Username already taken :( ")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    };

                    SignUpProxyDB rpdb = new SignUpProxyDB(username, password, response);
                    RequestQueue queue = Volley.newRequestQueue(Login.this);
                queue.add(rpdb);

            }
        });

    }


}
