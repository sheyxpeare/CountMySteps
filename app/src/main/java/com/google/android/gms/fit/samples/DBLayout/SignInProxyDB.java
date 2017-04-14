package com.google.android.gms.fit.samples.DBLayout;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * Pulling data from the username/password table on the remote database using an http reuqest
 * so the user can sign into the application
 */
public class SignInProxyDB extends StringRequest {
    public static final String LOGIN_REQUEST_URL = "http://countmysteps.x10host.com/login.php";
    public Map<String, String> params;



    public SignInProxyDB(String username, String password, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener,null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
