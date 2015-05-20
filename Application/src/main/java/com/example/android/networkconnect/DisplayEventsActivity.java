package com.example.android.networkconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.widget.ListView;
import android.util.Log;

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
    private ArrayList<Events> eventsList;
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

        eventsList = new ArrayList<Events>();

        new ProcessCalendarAsync().execute("https://teamlockerroom.com/api/calendar/" + teamId + "/" + peopleId + "/" + getMonth + "/" + getYear);

        ListView listView = (ListView)findViewById(R.id.listView);
        adapter = new CustomListAdapter(getApplicationContext(), R.layout.custom_list_adapter, peopleId, teamId, eventsList);

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
        private static final String TAG_EVENT_ID = "eventid";
        private static final String TAG_TITLE = "title";
        private static final String TAG_ARENA_NAME = "arenaname";
        private static final String TAG_RINK_NAME = "rinkname";
        private static final String TAG_EVENT_DATE = "eventdate";
        private static final String TAG_ATTENDANCE_STATUS = "attstatus";
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
            try {
                HttpGet httpGet = new HttpGet(urls[0]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse httpResponse = httpClient.execute(httpGet);

                // StatusLine stat = response.getStatusLine();
                int status = httpResponse.getStatusLine().getStatusCode();

                if(status == 200) {
                    HttpEntity entity = httpResponse.getEntity();
                    String data = EntityUtils.toString(entity);
                    jsonArray = new JSONArray(data);

                    for(int i = 0; i < jsonArray.length(); i++) {
	                    jsonObject = jsonArray.getJSONObject(i);

                        Events event = new Events();

	                    // Set the eventId.
	                    event.setEventId(jsonObject.getString(TAG_EVENT_ID));
                        event.setTitle(jsonObject.getString(TAG_TITLE));
                        event.setArenaName(jsonObject.getString(TAG_ARENA_NAME));
                        event.setRinkName(jsonObject.getString(TAG_RINK_NAME));
                        event.setEventDate(jsonObject.getString(TAG_EVENT_DATE));

                        if(jsonObject.getString(TAG_ATTENDANCE_STATUS).equalsIgnoreCase("null")) {
                            event.setAttendance("You haven't decided yet");
                        } else if(jsonObject.getString(TAG_ATTENDANCE_STATUS).equalsIgnoreCase("0")) {
                            event.setAttendance("I am not attending this event");
                        } else if(jsonObject.getString(TAG_ATTENDANCE_STATUS).equalsIgnoreCase("1")) {
                            event.setAttendance("I am attending this event");
                        }
                        eventsList.add(event);
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
            super.onPostExecute(jsonArray);

            final Message message = new Message();
            message.obj = jsonArray;
            Log.d(TAG_MY_APP + " onPostExecute DisplayEventsActivity", jsonArray.toString());
        }
    }
}
