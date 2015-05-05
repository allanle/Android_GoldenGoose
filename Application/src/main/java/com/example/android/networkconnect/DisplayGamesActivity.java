package com.example.android.networkconnect;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.common.logger.Log;

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

public class DisplayGamesActivity extends Activity {
    private ArrayList<Game> gamesList;
    private ListView list;
    private CustomListAdapter adapter;
    private Button yes;
    private Button no;

    private Calendar calendar = Calendar.getInstance();
    private int getMonth = calendar.get(Calendar.MONTH) + 1;
    private int getYear = calendar.get(Calendar.YEAR);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_games);

        //getting json data from login activity to pass into api calendar
        String playerId;
        String teamId;
        Bundle bundle = getIntent().getExtras();
        playerId = bundle.getString("playerid");
        teamId = bundle.getString("teamid");
        Log.d("MyApp", " Bundle in display games activity " + bundle);
        Log.d("MyApp", " DisplayActivity " + playerId + " " + teamId);

        /*
        //SharedPreferences sharedPreerences = getSharedPreferences("app", MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        peopleId = sharedPreferences.getString("PeopleId", peopleId);
        teamId = sharedPreferences.getString("TeamId", teamId);
        Log.d("MyApp" + " DisplayGamesActivity", peopleId);
        Log.d("MyApp" + " DisplayGamesActivity", teamId);
        */

        //new JSONAsyncTask().execute("https://teamlockerroom.com/api/calendar/408330/17786870/5/2015");
        new JSONAsyncTask().execute("https://teamlockerroom.com/api/calendar/" + teamId + "/" + playerId + "/" + getMonth + "/" + getYear);
        //new JSONAsyncTask().execute("https://teamlockerroom.com/api/calendar/408330/17786870/" + getMonth + "/" + getYear);

        Log.d("MyApp", "MONTH " + getMonth + " YEAR " + getYear);

        //new JSONAsyncTask().execute("https://teamlockerroom.com/api/calendar/408330/17786870");

        gamesList = new ArrayList<Game>();
        list = (ListView) findViewById(R.id.listView);
        adapter = new CustomListAdapter(getApplicationContext(), R.layout.custom_list_adapter, gamesList);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Context context = view.getContext();
                String a = list.getTag().toString();
                Toast.makeText(getApplication(), gamesList.get(position).getTitle().toString(), Toast.LENGTH_SHORT).show();
                Toast.makeText(context, a, Toast.LENGTH_SHORT).show();
            }
            /*@Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DisplayGamesActivity.this, CustomListAdapter.class);
                startActivity(intent);
            }*/

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_games, menu);
        return true;
    }

    private class JSONAsyncTask extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;
        private static final String TAG_TITLE = "title";
        private static final String TAG_ARENA_NAME = "arenaname";
        private static final String TAG_RINK_NAME = "rinkname";
        private static final String TAG_EVENT_DATE = "eventdate";
        private static final String TAG_ATTENDANCE_STATUS = "attstatus";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(DisplayGamesActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting to server");
            dialog.show();
            dialog.setCancelable(false);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {

                HttpGet httpPost = new HttpGet(urls[0]);
                HttpClient httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpPost);

                // StatusLine stat = response.getStatusLine();
                int status = response.getStatusLine().getStatusCode();

                if(status == 200) {
                    HttpEntity entity = response.getEntity();
                    String data = EntityUtils.toString(entity);

                    JSONArray jsonArray = new JSONArray(data);

                    Log.d("MyApp" + " JSON Array in DisplayGamesActivity", jsonArray.toString());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);

                        Log.d("MyApp", "IF ATTENDANCE IS NULL THEN DO SOMETHING HURRRRR " + object.get(TAG_ATTENDANCE_STATUS));

                        Log.d("MyApp" + " JSON object in DisplayGamesActivity", object.toString());
                        //Log.d("MyApp" + " Title: ", object.getString(TAG_TITLE).toString());
                        //Log.d("MyApp" + " Arena: ", object.getString(TAG_ARENA_NAME).toString());
                        //Log.d("MyApp" + " Rink: ", object.getString(TAG_RINK_NAME).toString());
                        //Log.d("MyApp" + " Date: ", object.getString(TAG_EVENT_DATE).toString());
                        //Log.d("MyApp" + " Attendance: ", object.getString(TAG_ATTENDANCE_STATUS).toString());

                        Game game = new Game();

                        if(object.isNull(TAG_ARENA_NAME) || object.isNull(TAG_RINK_NAME) || object.isNull(TAG_ATTENDANCE_STATUS)) {
                            game.setTitle(object.getString(TAG_TITLE));
                            game.setArenaName("");
                            game.setRinkName("");
                            game.setEventDate(object.getString(TAG_EVENT_DATE));
                            game.setAttendance("Bro, you haven't set your attendance to this event yet");
                            if(!object.isNull(TAG_TITLE) && !object.isNull(TAG_ARENA_NAME) && !object.isNull(TAG_RINK_NAME) && !object.isNull(TAG_EVENT_DATE)
                                    /*&& object.getString(TAG_ATTENDANCE_STATUS).toString().equals("0")*/) {
                                game.setTitle(object.getString(TAG_TITLE));
                                game.setArenaName(object.getString(TAG_ARENA_NAME));
                                game.setRinkName(object.getString(TAG_RINK_NAME));
                                game.setEventDate(object.getString(TAG_EVENT_DATE));
                                game.setAttendance("Not attending this event bro");
                            }
                        } else {
                            game.setTitle(object.getString(TAG_TITLE));
                            game.setArenaName(object.getString(TAG_ARENA_NAME));
                            game.setRinkName(object.getString(TAG_RINK_NAME));
                            game.setEventDate(object.getString(TAG_EVENT_DATE));
                            game.setAttendance("I am attending this event bro");
                        }
                        gamesList.add(game);
                        Log.d("MyApp", gamesList.toString());
                        Log.d("MyApp", game.toString());
                    }
                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {
            dialog.cancel();
            adapter.notifyDataSetChanged();
            if (result == false) {
                Toast.makeText(getApplicationContext(), "Unable to fetch data from server", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

