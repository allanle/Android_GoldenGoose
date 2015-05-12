/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.networkconnect;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.common.logger.Log;
import com.example.android.common.logger.LogFragment;
import com.example.android.common.logger.LogWrapper;
import com.example.android.common.logger.MessageOnlyLogFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;

/**
 * Sample application demonstrating how to connect to the network and fetch raw
 * HTML. It uses AsyncTask to do the fetch on a background thread. To establish
 * the network connection, it uses HttpURLConnection.
 *
 * This sample uses the logging framework to display log output in the log
 * fragment (LogFragment).
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    public static final String TAG = "Network Connect";
    private EditText email;
    private EditText password;
    private Button login;
    private Calendar calendar = Calendar.getInstance();
    private int month = calendar.get(Calendar.MONTH) + 1;
    private int year = calendar.get(Calendar.YEAR);
    private static final String TAG_MY_APP = "TAG_MY_APP";

    // Reference to the fragment showing events, so we can clear it with a button
    // as necessary.
    private LogFragment mLogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

        // Initialize text fragment that displays intro text.
        //SimpleTextFragment introFragment = (SimpleTextFragment) getSupportFragmentManager().findFragmentById(R.id.intro_fragment);
        //introFragment.setText(R.string.welcome_message);
        //introFragment.getTextView().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16.0f);

        email = (EditText)findViewById(R.id.email);
        email.setOnClickListener(this);

        password = (EditText)findViewById(R.id.password);
        password.setOnClickListener(this);

        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Must enter email", Toast.LENGTH_SHORT).show();
                } else if(password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Must enter password", Toast.LENGTH_SHORT).show();
                } else {
                    new LoginTask().execute();
                    Toast.makeText(getApplicationContext(), " Login Successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // Initialize the logging framework.
        initializeLogging();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // When the user clicks FETCH, fetch the first 500 characters of
            // raw HTML from www.google.com.
            case R.id.fetch_action:
                new LoginTask().execute();
                return true;
            // Clear the log view fragment.
            case R.id.clear_action:
              mLogFragment.getLogView().setText("");
              return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v == email) {
            email.setText("");
        } else if(v == password) {
            password.setText("");
        }
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class LoginTask extends AsyncTask<String, Void, JSONObject> {
        private static final String TAG_TEAMNAME = "teamname";
        private static final String TAG_TEAMLIST = "teamlist";
        private static final String TAG_FIRSTNAME = "firstname";
        private static final String TAG_LASTNAME = "lastname";
        private static final String TAG_TEAMID = "teamid";
        private static final String TAG_EVENT_GAMEID = "gameid";
        private static final String TAG_EVENT_RINKNAME = "rinkname";
        private static final String TAG_EVENT_DATE = "eventdate";
        private static final String TAG_EVENT_TITLE = "title";
        private static final String TAG_EVENT_ATTIN = "attin";
        private static final String TAG_EVENT_ATTOUT = "attout";
        private static final String TAG_EVENT_ATTSTATUS = "attstatus";
        private static final String TAG_PEOPLE_ID = "peopleid";
        private JSONObject json;

        @Override
        protected JSONObject doInBackground(String... urls) {
            try {
                Log.d(TAG_MY_APP, "+ Starting App");

                URL url = new URL("https://teamlockerroom.com/api/authenticate");
                String charset = "UTF-8";
                String username = email.getText().toString(); //"14hhqt+2y8jbjzz3wz1s@sharklasers.com";
                String mpassword = password.getText().toString(); //"ZuLGHDaLM9";
                String data = URLEncoder.encode("username", charset)
                        + "=" + URLEncoder.encode(username, charset);

                data += "&" + URLEncoder.encode("password", charset)
                        + "=" + URLEncoder.encode(mpassword, charset);

                Log.d(TAG_MY_APP, "+ Here is the data");
                Log.d(TAG_MY_APP, data);

                BufferedReader reader = null;
                String line = null;
                StringBuilder sb = new StringBuilder();

                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Accept-Charset", charset);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + charset);
                conn.setDoOutput(true);
                Log.d(TAG_MY_APP, "+ Connected to URL.");

                OutputStream ostream = conn.getOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(ostream);
                wr.write(data);
                wr.flush();

                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                Log.d(TAG_MY_APP, "+ Dumping Output stream");

                // Read Server Response
                while((line = reader.readLine()) != null) {
                    // Append server response in string
                    sb.append(line +"\n");
                }
                reader.close();

	            try {
                    json = new JSONObject(sb.toString());
		            JSONArray teamList = json.getJSONArray(TAG_TEAMLIST);

		            Log.d(TAG_MY_APP, "Welcome " +json.getString(TAG_FIRSTNAME).toString() + " " +json.getString(TAG_LASTNAME).toString());
                    Log.d(TAG_MY_APP, "TEAM ID: " + json.get(TAG_TEAMID).toString() + " PEOPLE ID: " + json.get(TAG_PEOPLE_ID).toString());

		            // Number of teams.
		            int numTeams = teamList.length();

		            if(numTeams > 1) {
			            Log.d(TAG_MY_APP, "+ Multiple teams detected. Number of Teams: " +teamList.length());
			            Log.d(TAG_MY_APP, "+ Team info: " +teamList.toString());
		            } else {
			            String teamId = json.getString(TAG_TEAMID);
			            String teamName = json.getString(TAG_TEAMNAME);

			            Log.d(TAG_MY_APP, "+ There is only one team with id: " +teamId);
			            Log.d(TAG_MY_APP, "+ There is only one team with name: " +teamName);

			            // Get the schedule for this team.
			            URL eventURL = new URL("https://teamlockerroom.com/api/calendar/" + TAG_TEAMID + TAG_PEOPLE_ID + month + year);

			            BufferedReader eventReader = null;
			            String eventLine = null;
			            StringBuilder eventSB = new StringBuilder();
			            URLConnection eventConn = eventURL.openConnection();
			            eventReader = new BufferedReader(new InputStreamReader(eventConn.getInputStream()));

			            // Read Server Response
			            while((eventLine = eventReader.readLine()) != null) {
				            // Append server response in string
				            eventSB.append(eventLine +"\n");
			            }
			            eventReader.close();

			            // The fucking thing is an array already.
			            JSONArray eventsJSON = new JSONArray(eventSB.toString());
			            int numEvents = eventsJSON.length();
			            Log.d(TAG_MY_APP, "+ Number of Events: " +Integer.toString(numEvents));

			            // Loop through the JSON and look for the next game.
			            for(int i = 0; i < numEvents; i++) {
				            JSONObject tempEvent = eventsJSON.getJSONObject(i);
				            int tempGameId = Integer.parseInt(tempEvent.getString(TAG_EVENT_GAMEID));
                            Log.d(TAG_MY_APP, "   " + tempEvent);
				            if(tempGameId != 0) {
					            Log.d(TAG_MY_APP, "GameID: " +tempEvent.getString(TAG_EVENT_GAMEID).toString());
					            Log.d(TAG_MY_APP, "Game Date: " +tempEvent.getString(TAG_EVENT_DATE).toString());
					            Log.d(TAG_MY_APP, "Rink Name: " +tempEvent.getString(TAG_EVENT_RINKNAME).toString());
					            Log.d(TAG_MY_APP, "Attending: " +tempEvent.getString(TAG_EVENT_ATTIN).toString());
					            Log.d(TAG_MY_APP, "Not Attending: " +tempEvent.getString(TAG_EVENT_ATTOUT).toString());
					            Log.d(TAG_MY_APP, "Attending Status: " +tempEvent.getString(TAG_EVENT_ATTSTATUS).toString());
					            Log.d(TAG_MY_APP, "I am attending button here");
					            Log.d(TAG_MY_APP, "I am not attending button here");
				            }
			            }
		            }
	            } catch(JSONException e) {
		            Log.e("JSON Parser", "Error parsing data: " + e.toString());
	            }

            } catch (Exception ex) {
                Log.d(TAG_MY_APP, ex.getMessage());
                ex.printStackTrace();
            }
            return json;
        }

        /**
         * Uses the logging framework to display the output of the fetch
         * operation in the log fragment.
         */
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                String teamId = json.getString(TAG_TEAMID).toString();
                String peopleId = json.get(TAG_PEOPLE_ID).toString();

                Bundle bundle = new Bundle();
                bundle.putString(TAG_TEAMID, teamId);
                bundle.putString(TAG_PEOPLE_ID, peopleId);
                Log.d(TAG_MY_APP, " BUNDLE" + bundle);

                Intent intent = new Intent(MainActivity.this, DisplayGamesActivity.class);
                intent.putExtras(bundle);
                //start next activity
                startActivity(intent);
                Log.i(TAG_MY_APP, json.toString());
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /** Initiates the fetch operation. */
    private String loadFromNetwork(String urlString) throws IOException {
        InputStream stream = null;
        String str ="";

        try {
            stream = downloadUrl(urlString);
            str = readIt(stream, 500);
       } finally {
           if (stream != null) {
               stream.close();
            }
        }
        return str;
    }

    /**
     * Given a string representation of a URL, sets up a connection and gets
     * an input stream.
     * @param urlString A string representation of a URL.
     * @return An InputStream retrieved from a successful HttpURLConnection.
     * @throws java.io.IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        // BEGIN_INCLUDE(get_inputstream)
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Start the query
        conn.connect();
        InputStream stream = conn.getInputStream();
        return stream;
        // END_INCLUDE(get_inputstream)
    }

    /** Reads an InputStream and converts it to a String.
     * @param stream InputStream containing HTML from targeted site.
     * @param len Length of string that this method returns.
     * @return String concatenated according to len parameter.
     * @throws java.io.IOException
     * @throws java.io.UnsupportedEncodingException
     */
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    /** Create a chain of targets that will receive log data */
    public void initializeLogging() {

        // Using Log, front-end to the logging chain, emulates
        // android.util.log method signatures.

        // Wraps Android's native log framework
        LogWrapper logWrapper = new LogWrapper();
        Log.setLogNode(logWrapper);

        // A filter that strips out everything except the message text.
        MessageOnlyLogFilter msgFilter = new MessageOnlyLogFilter();
        logWrapper.setNext(msgFilter);

        // On screen logging via a fragment with a TextView.
        mLogFragment = (LogFragment) getSupportFragmentManager().findFragmentById(R.id.log_fragment);
        msgFilter.setNext(mLogFragment.getLogView());
    }

}
