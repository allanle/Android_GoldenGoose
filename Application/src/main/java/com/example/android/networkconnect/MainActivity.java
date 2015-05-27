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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Sample application demonstrating how to connect to the network and fetch raw
 * HTML. It uses AsyncTask to do the fetch on a background thread. To establish
 * the network connection, it uses HttpURLConnection.
 */
public class MainActivity extends FragmentActivity {
    private EditText email;
    private EditText password;
    private Button login;
    private static final String TAG_MY_APP = "MYAPP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

	    Log.d(TAG_MY_APP, "+ Starting App");

        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);

        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Must enter email", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Must enter password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Trying to login...", Toast.LENGTH_SHORT).show();
                    new LoginTask().execute();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.fetch_action:
                new LoginTask().execute();
                return true;
        }
        return false;
    }

    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class LoginTask extends AsyncTask<String, Void, JSONObject> {
        private static final String TAG_TEAMID = "teamid";
        private static final String TAG_PEOPLE_ID = "peopleid";
	    private static final String API_URL = "https://teamlockerroom.com/api/authenticate";
	    private static final String CHARSET = "UTF-8";
        private JSONObject json;

        @Override
        protected JSONObject doInBackground(String... urls) {
            boolean ok = false;
            int responseCode = 0;
            HttpURLConnection conn = null;
            String requestData = null;
            BufferedReader reader = null;
            String line = null;
            StringBuilder sb = new StringBuilder();
            OutputStream oStream;
            OutputStreamWriter wr;

            try {
                URL url = new URL(API_URL);
                //String mUsername = email.getText().toString(); //"14hhqt+2y8jbjzz3wz1s@sharklasers.com";
                //String mPassword = password.getText().toString(); //"ZuLGHDaLM9";
                String mUsername = "ale@pointstreak.com";
                String mPassword = "allan123";
                requestData = URLEncoder.encode("username", CHARSET) + "=" + URLEncoder.encode(mUsername, CHARSET);
                requestData += "&" + URLEncoder.encode("password", CHARSET) + "=" + URLEncoder.encode(mPassword, CHARSET);

                conn = (HttpURLConnection) url.openConnection();

                //setJellyBeanAuthentication(conn);

                conn.setRequestProperty("Accept-Charset", CHARSET);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + CHARSET);

                //HttpURLConnection uses the GET method by default.
                //It will use POST if setDoOutPut(true).
                //Other HTTP methods (OPTIONS, HEAD, PUT, DELETE and TRACE) can be used with setRequestMethod(String).
                conn.setDoOutput(true);

                oStream = conn.getOutputStream();
                wr = new OutputStreamWriter(oStream);
                wr.write(requestData);
                wr.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                Log.d(TAG_MY_APP, "+ Connected to URL.");
                responseCode = conn.getResponseCode();
                if(responseCode != 200) {
                    json = new JSONObject("{failed: false}");
                    json.put("failed", false);
                    return json;
                }
            } catch(Exception e) {
                try {
                    json = new JSONObject("{failed: true}");
                    json.put("message", e.getMessage());
                    return json;
                } catch(JSONException e1) {
                    e1.printStackTrace();
                }
            }
            Log.d(TAG_MY_APP," response bro " + responseCode);
            try {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                // Read Server Response and append it to a String.
                while((line = reader.readLine()) != null) {
                    sb.append(line +"\n");
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
                //reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            try {
                json = new JSONObject(sb.toString());
                json.put("failed", false);
            } catch(JSONException jse) {
                try {
                    json = new JSONObject("{failed: true}");
                    json.put("message", "There was an error reading the data.");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return json;
        }

        /**
         * This executes after the doInBackground function is done checking for a valid / invalid
         * login for the User.
         */
        @Override
        protected void onPostExecute(JSONObject json) {
            try {
				// If the failed == true, then there was a login failure. Notify the User.
	            if(json.getBoolean("failed")) {
		            Toast.makeText(MainActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
	            } else {
                    if(json.getString(TAG_TEAMID).equals(null) || json.getString(TAG_PEOPLE_ID).equals(null)) {
                        Toast.makeText(MainActivity.this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                    } else {
                        String teamId = json.getString(TAG_TEAMID).toString();
                        String peopleId = json.get(TAG_PEOPLE_ID).toString();

                        // Put the bundle of extras together for the next activity.
                        Bundle bundle = new Bundle();
                        bundle.putString(TAG_TEAMID, teamId);
                        bundle.putString(TAG_PEOPLE_ID, peopleId);

                        Intent intent = new Intent(MainActivity.this, DisplayEventsActivity.class);
                        intent.putExtras(bundle);

                        // Login was successful, notify the User.
                        Toast.makeText(MainActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();

                        // Start the DisplayEventsActivity.
                        startActivity(intent);
                    }
	            }
            } catch(JSONException e) {
	            Toast.makeText(MainActivity.this, "Incorrect email/password. Please try again.", Toast.LENGTH_SHORT).show();
            }
        }
    }
/*
    private void setJellyBeanAuthentication(HttpURLConnection httpURLConnection) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            byte[] auth = (email + ":" + password).getBytes();
            String basic = Base64.encodeToString(auth, Base64.NO_WRAP);
            httpURLConnection.setRequestProperty("Authorization", "Basic " + basic);
        }
    }*/
}
