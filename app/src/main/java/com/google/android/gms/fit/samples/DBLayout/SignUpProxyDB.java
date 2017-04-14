package com.google.android.gms.fit.samples.DBLayout;


import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Inserting data into the user table on the remote database using an http request
 */
public class SignUpProxyDB extends StringRequest {

    public static final String REGISTER_REQUEST_URL = "http://countmysteps.x10host.com/register.php";
    public Map<String, String> params;



    public SignUpProxyDB(String username, String password, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener,null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);

    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }


}
