package com.google.android.gms.fit.samples.DBLayout;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Inputs data into the leaderboard database using an http request
 */
public class FriendsDB extends StringRequest {

    public static final String REGISTER_REQUEST_URL = "http://countmysteps.x10host.com/friends.php";
    public Map<String, String> params;

    public FriendsDB(String username, int stepcount, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener,null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("stepcount", stepcount + "");


    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
