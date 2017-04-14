package com.google.android.gms.fit.samples.entities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.android.gms.fit.samples.basichistoryapifinal.EditProfile;
import com.google.android.gms.fit.samples.basichistoryapifinal.Leaderboard;
import com.google.android.gms.fit.samples.basichistoryapifinal.MainActivity;
import com.google.android.gms.fit.samples.basichistoryapifinal.R;
import com.google.android.gms.fit.samples.basichistoryapifinal.Update;

import java.util.ArrayList;

public class Statistics extends AppCompatActivity {

    LineChart mChart;
    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mChart = (LineChart) findViewById(R.id.statistics);

        Intent in = getIntent();
        username= in.getExtras().getString("usernamesignin");

        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setTextSize(10f);


        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.setAxisMaxValue(11000f);
        leftAxis.setAxisMinValue(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        ArrayList<Integer> temp = MainActivity.stepsTakenArray;

        int numberOfDays = temp.size();

        setData(numberOfDays, 10000, temp);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);


    }

    private void setData(int count, float range, ArrayList<Integer> arr) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < arr.size(); i++) {
            yVals.add(new Entry(arr.get(i), i));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "");

            set1.enableDashedLine(10f, 5f, 0f);
            set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.BLACK);
            set1.setCircleColor(Color.BLACK);
            set1.setLineWidth(1f);
            set1.setCircleRadius(3f);
            set1.setDrawCircleHole(false);
            set1.setValueTextSize(9f);
            set1.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            // set data
            mChart.setData(data);
        }
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
            Intent intent = new Intent(Statistics.this, Update.class);
            intent.putExtra("usernamesignin", username);
            Statistics.this.startActivity(intent);
        }
        else if (id == R.id.action_settings) {
            Intent intent1 = new Intent(Statistics.this, EditProfile.class);
            intent1.putExtra("usernamesignin", username);
            Statistics.this.startActivity(intent1);
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    public void profile(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("usernamesignin", username);
        startActivity(intent);
    }

    public void friends(View view) {
        Intent intent = new Intent(this, Leaderboard.class);
        intent.putExtra("usernamesignin", username);
        startActivity(intent);
    }
}


