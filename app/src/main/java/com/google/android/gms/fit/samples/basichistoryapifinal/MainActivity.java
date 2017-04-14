package com.google.android.gms.fit.samples.basichistoryapifinal;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fit.samples.DBLayout.ProxyDB;
import com.google.android.gms.fit.samples.DBLayout.UpdateFriendsDB;
import com.google.android.gms.fit.samples.common.logger.Log;
import com.google.android.gms.fit.samples.common.logger.LogView;
import com.google.android.gms.fit.samples.common.logger.LogWrapper;
import com.google.android.gms.fit.samples.common.logger.MessageOnlyLogFilter;
import com.google.android.gms.fit.samples.entities.Statistics;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataDeleteRequest;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;
import com.hookedonplay.decoviewlib.DecoView;
import com.hookedonplay.decoviewlib.charts.SeriesItem;
import com.hookedonplay.decoviewlib.events.DecoEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;
import static java.text.DateFormat.getTimeInstance;

/*
This activity shows the step count and the active days
 */
public class MainActivity extends AppCompatActivity {
    Button logoutbutton;
    boolean isChecked;
    public static final String TAG = "BasicHistoryApi";
    private static final int REQUEST_OAUTH = 1;
    private static final String DATE_FORMAT = "yyyy.MM.dd HH:mm:ss";
    public static int scount;
    public HashMap<String, String> map;

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private static boolean authInProgress = false;

    public static GoogleApiClient mClient = null;

    /*
    ArrayList of StepCount
     */
    public static ArrayList<Integer> stepsTakenArray = null;

    /*
    DecoView
     */
    private DecoView mDecoView;

    private int mBackIndex;
    private int mFrontIndex;
    private int mFrontIndex1;
    String username;
    ProxyDB profiledb;
    int sum;

    private final float mSeriesMax = 50f;

    /*
    TextView
     */
    private static TextView stepCount;
    public static void setStepCount(int val) {
        stepCount.setText(Integer.toString(val));
    }

    private static TextView activeTimeDaily;
    public static void setActiveTimeDaily(int val) {
        activeTimeDaily.setText(Integer.toString(val));
    }

    /*
    Variable to hold the number of steps taken
     */
    private static int numberOfStepsTaken;
    public static void setNumberOfStepsTaken(int val) {
        numberOfStepsTaken = val;
    }
    public static int getNumberOfStepsTaken() {
        return numberOfStepsTaken;
    }

    /*
    Variable to hold the the goal for number of steps
     */
    private static int stepGoal;
    public static void setStepGoal(int val) {
        stepGoal = val;
    }
    public static int getStepGoal() {
        return stepGoal;
    }

    /*
    Variable for holding the active time
     */
    private static int activeTime;
    public static int getActiveTime() {
        return activeTime;
    }
    public static void setActiveTime (int val) {
        activeTime = val;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logoutbutton = (Button) findViewById(R.id.logoutbutton);



        stepCount = (TextView)findViewById(R.id.stepCount);
        activeTimeDaily = (TextView) findViewById((R.id.activeTime));
        // This method sets up our custom logger, which will print all log messages to the device
        // screen, as well as to adb logcat.
        //initializeLogging();

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        buildFitnessClient();

        mDecoView = (DecoView) findViewById(R.id.dynamicArcView);

        createBackSeries();

        createFrontSeries();

        createFrontSeries1();

        // Step goal obtained from the database
        profiledb = new ProxyDB(this);
        map = profiledb.fetchdata();

        int stepGoalFromDB = Integer.parseInt(map.get("Step_Goal"));

        MainActivity.setStepGoal(stepGoalFromDB);

        logoutbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent in = getIntent();
                isChecked= in.getExtras().getBoolean("ischecked");
                if (isChecked) {
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    i.putExtra("usernamesignin", username);
                    isChecked = false;
                    i.putExtra("ischecked", isChecked);
                    startActivity(i);
                } else{
                    Intent i = new Intent(getApplicationContext(), Login.class);
                    i.putExtra("usernamesignin", username);
                    startActivity(i);

                }
            }
        });
    }

    // When the statistics button is clicked
    public void statistics(View view) {
        Intent intent = new Intent(this, Statistics.class);
        intent.putExtra("usernamesignin", username);
        startActivity(intent);
    }

    // When the leaderboard button is clicked
    public void friends(View view) {
        Intent intent = new Intent(this, Leaderboard.class);
        intent.putExtra("usernamesignin", username);
        startActivity(intent);
    }

    // Create the grey back series
    private void createBackSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFE2E2E2"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mBackIndex = mDecoView.addSeries(seriesItem);
    }

    // Create the Front series
    private void createFrontSeries() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FF6699FF"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mFrontIndex = mDecoView.addSeries(seriesItem);
    }

    // Create the second front series
    private void createFrontSeries1() {
        SeriesItem seriesItem = new SeriesItem.Builder(Color.parseColor("#FFFF8800"))
                .setRange(0, mSeriesMax, 0)
                .setInitialVisibility(true)
                .build();

        mFrontIndex1 = mDecoView.addSeries(seriesItem);
    }

    // Annimate the decoview
    private void createEvents() {
        mDecoView.executeReset();

        mDecoView.addEvent(new DecoEvent.Builder(mSeriesMax)
                .setIndex(mBackIndex)
                .setDuration(3000)
                .setDelay(100)
                .build());

        float numberOfStepsTakenFloat = (float)MainActivity.getNumberOfStepsTaken() + scount - sum;
        float stepGoalFloat = (float)MainActivity.getStepGoal();

        float percentageOfGoalCompleted = numberOfStepsTakenFloat / stepGoalFloat;
        percentageOfGoalCompleted *= 100;
        percentageOfGoalCompleted = percentageOfGoalCompleted / 2f;

        mDecoView.addEvent(new DecoEvent.Builder(percentageOfGoalCompleted)
                .setIndex(mFrontIndex)
                .setDuration(3000)
                .setDelay(100)
                .build());

        float activeTime = (float) stepsTakenArray.size();


        float percentageOfActiveTime = activeTime / Integer.parseInt(map.get("act_goal"));
        percentageOfActiveTime *= 100;
        percentageOfActiveTime = percentageOfActiveTime / 2f;

        mDecoView.addEvent(new DecoEvent.Builder(percentageOfActiveTime)
                .setIndex(mFrontIndex1)
                .setDuration(3000)
                .setDelay(100)
                .build());
    }

    /*
    Start buildong the fitness client. This consists of inserting the data onto Google Fit and then
    retrieving that data for calculation purposes.
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Look at some data!!
                                new InsertAndVerifyDataTask().execute();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i(TAG, "Google Play services connection failed. Cause: " +
                                result.toString());
                        Snackbar.make(
                                MainActivity.this.findViewById(R.id.main_activity_view),
                                "Exception while connecting to Google Play services: " +
                                        result.getErrorMessage(),
                                Snackbar.LENGTH_INDEFINITE).show();
                    }
                })
                .build();
    }

   /*
   Method for inserting and verifying that the data insertion is taking place
    */
    private class InsertAndVerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            // Create a new dataset and insertion request.
            DataSet dataSetFitness = insertFitnessData();

            // Then, invoke the History API to insert the data. Include a timeout when calling
            // await() to prevent hanging.
            Log.i(TAG, "Inserting the dataset in the History API. FITNESS");
            com.google.android.gms.common.api.Status insertStatusFitness =
                    Fitness.HistoryApi.insertData(mClient, dataSetFitness)
                            .await(1, TimeUnit.MINUTES);

            // Before querying the data, check to see if the insertion succeeded.
            if (!insertStatusFitness.isSuccess()) {
                Log.i(TAG, "There was a problem inserting the dataset.FITNESS");
                return null;
            }

            // At this point, the data has been inserted and can be read.
            Log.i(TAG, "Data insert was successful! FITNESS");
            // [END insert_dataset]

            // Begin by creating the query.
            DataReadRequest readRequestFitnessData = queryFitnessData();
            // Invoke the History API to fetch the data with the query and await the result of
            // the read request.
            DataReadResult dataReadResultFitness =
                    Fitness.HistoryApi.readData(mClient, readRequestFitnessData).await(1, TimeUnit.MINUTES);

            printDataFitness(dataReadResultFitness);

            // Create a new dataset and insertion request.
            DataSet dataSetActive = insertActiveData();

            // Then, invoke the History API to insert the data. Include a timeout when calling
            // await() to prevent hanging.
            Log.i(TAG, "Inserting the dataset in the History API. ACTIVE");
            com.google.android.gms.common.api.Status insertStatusActive =
                    Fitness.HistoryApi.insertData(mClient, dataSetActive)
                            .await(1, TimeUnit.MINUTES);

            // Before querying the data, check to see if the insertion succeeded.
            if (!insertStatusActive.isSuccess()) {
                Log.i(TAG, "There was a problem inserting the dataset. ACTIVE");
                return null;
            }

            // At this point, the data has been inserted and can be read.
            Log.i(TAG, "Data insert was successful! ACTIVE");

            // Begin by creating the query.
            DataReadRequest readRequestActiveData = queryActiveData();
            // [START read_dataset]
            // Invoke the History API to fetch the data with the query and await the result of
            // the read request.
            DataReadResult dataReadResultActive =
                    Fitness.HistoryApi.readData(mClient, readRequestActiveData).await(1, TimeUnit.MINUTES);

            printDataActive(dataReadResultActive);

            return null;
        }



        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MainActivity.setStepCount(MainActivity.getNumberOfStepsTaken() + scount - sum);

            Log.i("ACTIVE TIME", "ON POST EXECUTE ACTIVE TIME " + Integer.toString(MainActivity.getActiveTime()));

            int numberOfActiveDays = stepsTakenArray.size();

            MainActivity.setActiveTimeDaily(numberOfActiveDays);
            createEvents();

            Intent in = getIntent();
            username= in.getExtras().getString("usernamesignin");

            sum = 0;
            for (int i =0 ; i < stepsTakenArray.size(); i++) {
                sum += stepsTakenArray.get(i);
            }

            Response.Listener<String> response = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        Log.i("LOGGING2: ", response);
                        JSONObject jsonObject = new JSONObject(response);
                        boolean success = jsonObject.getBoolean("success");

                        if (success){
                            android.util.Log.i("Retrieving to DB2:", "SUCCESSS");
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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


            UpdateFriendsDB fdb = new UpdateFriendsDB(username, numberOfStepsTaken + scount - sum, response);
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            queue.add(fdb);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        stepsTakenArray = new ArrayList<Integer>();
    }

    private DataSet insertActiveData() {
        Log.i(TAG, "Creating a new data insert request for active data.");
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                .setStreamName(TAG + " - active time")
                .setType(DataSource.TYPE_RAW)
                .build();

        // Create a data set
        int activeTime = 0;
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MINUTES);
        dataPoint.getValue(Field.FIELD_ACTIVITY).setInt(activeTime);
        dataSet.add(dataPoint);

        return dataSet;

    }

    /*
    Create and return a DataSet of step count data for insertion using the History API.
     */
    private DataSet insertFitnessData() {
        Log.i(TAG, "Creating a new data insert request.");

        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.HOUR_OF_DAY, -1);
        long startTime = cal.getTimeInMillis();

        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setStreamName(TAG + " - step count")
                .setType(DataSource.TYPE_RAW)
                .build();

        // Create a data set
        int stepCountDelta = 0;
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint = dataSet.createDataPoint()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS);
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);

        return dataSet;
    }

    public static DataReadRequest queryActiveData() {
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return readRequest;
    }


    /*
    Return a DataReadRequest for all step count changes in the past week.
     */
    public static DataReadRequest queryFitnessData() {
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));

        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        return readRequest;
    }

    /*
    Used for printing fitness data
     */
    public static void printDataFitness(DataReadResult dataReadResult) {
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSetFitness(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSetFitness(dataSet);
            }
        }
    }

    // START parse_dataset
    private static void dumpDataSetFitness(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        Log.i(TAG, dataSet.toString());
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                MainActivity.setNumberOfStepsTaken(dp.getValue(field).asInt());

                Log.i(TAG, "INITIALIZING STEPS TAKEN ARRAY");

                stepsTakenArray.add(dp.getValue(field).asInt());
            }
        }
    }

    public static void printDataActive(DataReadResult dataReadResult) {
        if (dataReadResult.getBuckets().size() > 0) {
            Log.i(TAG, "Number of returned buckets of DataSets is: "
                    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    dumpDataSetActive(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {
            Log.i(TAG, "Number of returned DataSets is: "
                    + dataReadResult.getDataSets().size());
            for (DataSet dataSet : dataReadResult.getDataSets()) {
                dumpDataSetActive(dataSet);
            }
        }
    }

    private static void dumpDataSetActive(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        Log.i(TAG, dataSet.toString());
        DateFormat dateFormat = getTimeInstance();

        for (DataPoint dp : dataSet.getDataPoints()) {
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
            for(Field field : dp.getDataType().getFields()) {
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
                MainActivity.setActiveTime(dp.getValue(field).asInt());
            }
        }
    }

    /*
    Used for deleting a data set from the Google Fit store
     */
    private void deleteData() {
        Log.i(TAG, "Deleting today's step count data.");

        // Set a start and end time for our data, using a start time of 1 day before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.DAY_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        //  Create a delete request object, providing a data type and a time interval
        DataDeleteRequest request = new DataDeleteRequest.Builder()
                .setTimeInterval(startTime, endTime, TimeUnit.MILLISECONDS)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .build();

        // Invoke the History API with the Google API client object and delete request, and then
        // specify a callback that will check the result.
        Fitness.HistoryApi.deleteData(mClient, request)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Successfully deleted today's step count data.");
                        } else {
                            Log.i(TAG, "Failed to delete today's step count data.");
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_update_data){
            Intent intent = new Intent(MainActivity.this, Update.class);
            intent.putExtra("usernamesignin", username);
            MainActivity.this.startActivity(intent);
        }
        else if (id == R.id.action_settings) {
            Intent intent1 = new Intent(MainActivity.this, EditProfile.class);
            intent1.putExtra("usernamesignin", username);
            MainActivity.this.startActivity(intent1);
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    /*
    Initialize a custom log class
     */
    private void initializeLogging() {
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);
        LogView logView = (LogView) findViewById(R.id.sample_logview);
        logView.setTextAppearance(this, R.style.Log);
        logView.setBackgroundColor(Color.WHITE);
        msgFilter.setNext(logView);
        Log.i(TAG, "Ready.");
    }
}