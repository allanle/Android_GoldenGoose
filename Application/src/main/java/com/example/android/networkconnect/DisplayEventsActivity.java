package com.example.android.networkconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;

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
import java.util.ArrayList;
import java.util.Calendar;

public class DisplayEventsActivity extends Activity {
    private ArrayList<Event> eventList;
    private CustomListAdapter adapter;
    private Calendar calendar = Calendar.getInstance();
    private int getMonth = calendar.get(Calendar.MONTH) + 1;
    private int getYear = calendar.get(Calendar.YEAR);
    private static final String TAG_MY_APP = "MyApp";
    private static final String TAG_PEOPLE_ID = "peopleid";
    private static final String TAG_TEAM_ID = "teamid";

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

        new ProcessCalendarAsync().execute("https://teamlockerroom.com/api/calendar/" + teamId + "/" + peopleId + "/" + getMonth + "/" + getYear);

//        new ProcessCalendarAsync().execute("https://teamlockerroom.com/api/calendar/410281/17802742/5/2015");
        ListView listView = (ListView)findViewById(R.id.listView);
        adapter = new CustomListAdapter(getApplicationContext(), R.layout.custom_list_adapter, peopleId, teamId, eventList);

        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_games, menu);
        return true;
    }

    private class ProcessCalendarAsync extends AsyncTask<String, Void, JSONArray> {
        private ProgressDialog dialog;
        private JSONObject jsonObject;
        private JSONArray jsonArray;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(DisplayEventsActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting to server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected JSONArray doInBackground(String... urls) {
            HttpGet httpGet = null;
            HttpClient httpClient = null;
            Event event = null;
            try {
                httpGet = new HttpGet(urls[0]);
                httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);

                int status = httpResponse.getStatusLine().getStatusCode();

                if(status == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    String data = EntityUtils.toString(entity);
                    jsonArray = new JSONArray(data);

                    for(int i = 0; i < jsonArray.length(); i++) {
	                    jsonObject = jsonArray.getJSONObject(i);

                        event = new Event(jsonObject);

                        eventList.add(event);
                    }
                    return jsonArray;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            dialog.cancel();
            adapter.notifyDataSetChanged();
        }
    }
}
