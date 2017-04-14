package com.google.android.gms.fit.samples.ExceptionHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;


/**
 * Exception handler for the situation where the password or username is incorrect
 */
public class ExceptionHandling extends Exception {

    public ExceptionHandling(){
        return;
    }


    public static void displayErrorMessage(Context context) {
        //Toast.makeText(new Login().getLoginCtx(),"Login Failed"), Toast.LENGTH_LONG).show();

        Log.i("TAG", "Login Failed" + context);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Incorrect username or password ")
                .setNegativeButton("Retry", null)
                .create()
                .show();



    }
}
