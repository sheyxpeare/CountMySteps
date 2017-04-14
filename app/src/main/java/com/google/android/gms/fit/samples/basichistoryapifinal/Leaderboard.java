package com.google.android.gms.fit.samples.basichistoryapifinal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.fit.samples.DBLayout.PullFriendsDB;
import com.google.android.gms.fit.samples.DBLayout.SignUpProxyDB;
import com.google.android.gms.fit.samples.entities.Statistics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Leaderboard extends AppCompatActivity {

    SignUpProxyDB remotedb;
    String username;

    ListView friendsList;

    ArrayList<SingleRowData> singleRowList;

    ArrayList<HashMap<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent in = getIntent();
        username= in.getExtras().getString("usernamesignin");

        singleRowList = new ArrayList<SingleRowData>();

        friendsList = (ListView) findViewById(R.id.friendList);
        list = new ArrayList<HashMap<String, String>>();

        Response.Listener<String> response = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Log.i("LOGGING: ", response);
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray JSONresponse = jsonObject.getJSONArray("response");

                    for (int i = 0; i < JSONresponse.length(); i++) {


                        JSONObject responseObject = JSONresponse.getJSONObject(i);
                        String username = responseObject.optString("username");

                        int stepCount = Integer.parseInt(responseObject.optString("stepcount"));
                        Log.i("TAG", Integer.toString(stepCount));
                        SingleRowData singleRowData = new SingleRowData((i + 1), username, stepCount);
                        singleRowList.add(singleRowData);
                    }

                    for (int i = 0; i < singleRowList.size(); i++) {
                        HashMap<String, String> temp = new HashMap<String, String>();
                        temp.put(ListConstants.FIRST_COLUMN, Integer.toString(singleRowList.get(i).getRank()));
                        temp.put(ListConstants.SECOND_COLUMN, singleRowList.get(i).getUsername());
                        temp.put(ListConstants.THIRD_COLUMN, Integer.toString(singleRowList.get(i).getStepCount()));
                        list.add(temp);
                    }

                    ListViewAdaptors listViewAdaptors = new ListViewAdaptors(Leaderboard.this, list);
                    friendsList.setAdapter(listViewAdaptors);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        };

        PullFriendsDB rpdb = new PullFriendsDB(response);
        RequestQueue queue = Volley.newRequestQueue(Leaderboard.this);
        queue.add(rpdb);
    }

    public void statistics(View view) {
        Intent intent = new Intent(this, Statistics.class);
        intent.putExtra("usernamesignin", username);
        startActivity(intent);
    }

    public void profile(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("usernamesignin", username);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_update_data){
            Intent intent = new Intent(Leaderboard.this, Update.class);
            intent.putExtra("usernamesignin", username);
            Leaderboard.this.startActivity(intent);
        }
        else if (id == R.id.action_settings) {
            Intent intent1 = new Intent(Leaderboard.this, EditProfile.class);
            intent1.putExtra("usernamesignin", username);
            Leaderboard.this.startActivity(intent1);
            return super.onOptionsItemSelected(item);
        }
        return true;
    }
}
