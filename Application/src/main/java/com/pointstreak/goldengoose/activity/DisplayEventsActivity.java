package com.pointstreak.goldengoose.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.android.networkconnect.R;
import com.pointstreak.goldengoose.adapter.CustomListAdapter;
import com.pointstreak.goldengoose.classes.Event;
import com.pointstreak.goldengoose.classes.ViewHolder;
import com.pointstreak.goldengoose.encryption.ObscuredSharedPreferences;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DisplayEventsActivity extends Activity {
    private ArrayList<Event> eventList;
    private CustomListAdapter adapter;
    private ObscuredSharedPreferences obscuredSharedPreferences;
    private SharedPreferences.Editor editor;
    private Calendar calendar = Calendar.getInstance();
    private int getMonth = calendar.get(Calendar.MONTH) + 1;
    private int getYear = calendar.get(Calendar.YEAR);
    private ViewHolder viewHolder;
    private static final String TAG_MY_APP = "MyApp";
    private static final String TAG_PEOPLE_ID = "peopleid";
    private static final String TAG_TEAM_ID = "teamid";
    private static final String TAG_EVENT_DATE = "eventdate";
    private static final String SHARED_PREFS = "SharedPrefs";
    private static final String SHARED_EMAIL = "SharedEmail";
    private static final String SHARED_PASSWORD = "SharedPassword";
    private int pivotCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_games);

        //getting json data from login activity to pass into api calendar
	    Bundle bundle = getIntent().getExtras();
        String peopleId = bundle.getString(TAG_PEOPLE_ID);
        String teamId = bundle.getString(TAG_TEAM_ID);

        Log.d(TAG_MY_APP, " DisplayActivity " + peopleId + " " + teamId);

        eventList = new ArrayList<Event>();

        // execute calendar api
        new ProcessCalendarAsync().execute("https://teamlockerroom.com/api/calendar/" + teamId + "/" + peopleId);

//        new ProcessCalendarAsync().execute("https://teamlockerroom.com/api/calendar/" + teamId + "/" + peopleId + "/" + getMonth + "/" + getYear);
//        new ProcessCalendarAsync().execute("https://teamlockerroom.com/api/calendar/410281/17802742/7/2015");

        final ListView listView = (ListView)findViewById(R.id.listView);
        adapter = new CustomListAdapter(getApplicationContext(), R.layout.custom_list_adapter, peopleId, teamId, eventList);

//        listView.setSelection(20);

//        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(adapter);
/*
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(adapter.getCount() - 1);
            }
        });*/

        // the pivot count is passed into smoothScrollToPosition() to keep
        // track the amount of past games of the current date.
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
//                listView.smoothScrollToPosition(20);
                listView.setSelection(pivotCount);
            }
        });
/*
        listView.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    listView.smoothScrollToPosition(pivotCount);
//                listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
//                listView.setSelection(15);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },100);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_events, menu);
        return true;
    }

    /**
     * action_logout allows the users to log out of their account. When the user logs out, the email / password
     * text fields will be cleared.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_logout:
                obscuredSharedPreferences = ObscuredSharedPreferences.getPrefs(this, SHARED_PREFS, MODE_PRIVATE);
                editor = obscuredSharedPreferences.edit();
                editor.remove(SHARED_EMAIL);
                editor.remove(SHARED_PASSWORD);
                editor.commit();

                Intent intent = new Intent(this, MainActivity.class);
                // this makes it so the app doesn't have to create a new activity and will go back to
                // the activity that was paused.
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
        }
        return false;
    }

    private class ProcessCalendarAsync extends AsyncTask<String, Void, JSONArray> {
        private ProgressDialog dialog;
        private JSONObject jsonObject;
        private JSONArray jsonArray;

        /**
         * A dialog will appear when trying to make an API GET.
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(DisplayEventsActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting to server");
            dialog.show();
            dialog.setCancelable(false);
        }

        /**
         *
         * @param urls - takes in a url address (API).
         * @return
         */
        @Override
        protected JSONArray doInBackground(String... urls) {
            HttpGet httpGet = null;
            HttpClient httpClient = null;
            Event event = null;
            Date oldEventDate = null;
            DateFormat yearFormat = null;
            DateFormat pivotFormat = null;
            Date currentDate = Calendar.getInstance().getTime();
            int count = 0;
            int count2014 = 0;
            int count2015 = 0;

            try {
                // http GET request.
                httpGet = new HttpGet(urls[0]);
                httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);

                // getting the server response.
                int status = httpResponse.getStatusLine().getStatusCode();

                if(status == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    String data = EntityUtils.toString(entity);
                    jsonArray = new JSONArray(data);

                    pivotFormat = new SimpleDateFormat("EEE, MMM dd, yyyy");

                    // year format
                    yearFormat = new SimpleDateFormat("yyyy");

                    // convert year format to a String to compare
                    String yearString = yearFormat.format(currentDate);

                    Log.d(TAG_MY_APP, "the year is: " + yearString);

                    for(int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);

                        try {
                            // parse the json date format to simple date format.
                            oldEventDate = pivotFormat.parse(jsonObject.getString(TAG_EVENT_DATE));

                            // keeping track of how many past events are before the current date.
                            if(oldEventDate.before(currentDate)) {
                                pivotCount++;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if(jsonObject.getString(TAG_EVENT_DATE).contains("2014")) {
                            count2014++;
                        }

                        // takes in a formatted date for the current year.
                        if(jsonObject.getString(TAG_EVENT_DATE).contains(yearString)) {
                            count2015++;

                            event = new Event(jsonObject);
                            eventList.add(event);
                        }
                        count++;
                    }

                    Log.d(TAG_MY_APP, "old event date counter: " + oldEventDate.toString());
                    Log.d(TAG_MY_APP, "pivot counter: " + pivotCount);
                    Log.d(TAG_MY_APP, jsonObject.toString());
                    Log.d(TAG_MY_APP, "count " + count);
                    Log.d(TAG_MY_APP, "2014 " + count2014);
                    Log.d(TAG_MY_APP, "2015 " + count2015);
                    return jsonArray;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonArray;
        }

        /**
         * This will notifiy the changes that will be made to the list view adapter.
         * @param jsonArray
         */
        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            dialog.cancel();
            adapter.notifyDataSetChanged();
        }
    }
}
