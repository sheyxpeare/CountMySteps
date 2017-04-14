package com.google.android.gms.fit.samples.DBLayout;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Pulling data From the leaderboard database using an http request from the server
 */
public class PullFriendsDB extends StringRequest {

    public static final String REGISTER_REQUEST_URL = "http://countmysteps.x10host.com/pullfriends.php";
    public Map<String, String> params;

    public PullFriendsDB(Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener,null);
        params = new HashMap<>();

    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }


}
