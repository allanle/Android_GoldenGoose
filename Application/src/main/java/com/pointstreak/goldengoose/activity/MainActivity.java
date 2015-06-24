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

package com.pointstreak.goldengoose.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.networkconnect.R;
import com.pointstreak.goldengoose.encryption.ObscuredSharedPreferences;

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

public class MainActivity extends FragmentActivity {
    private EditText email;
    private EditText password;
    private Button login;
    private SharedPreferences.Editor editor;
    private ObscuredSharedPreferences obscuredSharedPreferences;
    private JSONObject json;
    private ProgressDialog dialog;

    private static final String TAG_MY_APP = "MYAPP";
    private static final String SHARED_PREFS = "SharedPrefs";
    private static final String SHARED_EMAIL = "SharedEmail";
    private static final String SHARED_PASSWORD = "SharedPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_main);

	    Log.d(TAG_MY_APP, "+ Starting App");

        displayUserCredentials();

        if(password.getText().toString().length() != 0) {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading, please wait");
            dialog.setTitle("Connecting to server");
            dialog.show();
            dialog.setCancelable(true);
            Toast.makeText(getApplicationContext(), "Trying to login...", Toast.LENGTH_SHORT).show();
            new LoginTask().execute();
        }

        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Must enter email", Toast.LENGTH_SHORT).show();
                } else if(password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Must enter password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Trying to login...", Toast.LENGTH_SHORT).show();
                    new LoginTask().execute();
                }
            }
        });
    }

    private void setSharedPreferencesEmail() {
        String sharedEmail = email.getText().toString();

        obscuredSharedPreferences = ObscuredSharedPreferences.getPrefs(this, SHARED_PREFS, MODE_PRIVATE);
        editor = obscuredSharedPreferences.edit();
        editor.putString(SHARED_EMAIL, sharedEmail);
        editor.commit();
    }

    private void setSharedPreferencesPassword() {
        String sharedPassword = password.getText().toString();

        obscuredSharedPreferences = ObscuredSharedPreferences.getPrefs(this, SHARED_PREFS, MODE_PRIVATE);
        editor = obscuredSharedPreferences.edit();
        editor.putString(SHARED_PASSWORD, sharedPassword);
        editor.commit();
    }

    private String getSharedPreferencesEmail() {
        obscuredSharedPreferences = ObscuredSharedPreferences.getPrefs(this, SHARED_PREFS, MODE_PRIVATE);
        String extractedEmail = obscuredSharedPreferences.getString(SHARED_EMAIL, null);
        Log.d(TAG_MY_APP, "extracted email: " + extractedEmail);
        return extractedEmail;
    }

    private String getSharedPreferencesPassword() {
        obscuredSharedPreferences = ObscuredSharedPreferences.getPrefs(this, SHARED_PREFS, MODE_PRIVATE);
        String extractedPassword = obscuredSharedPreferences.getString(SHARED_PASSWORD, null);
        Log.d(TAG_MY_APP, "extracted Password: " + extractedPassword);
        return extractedPassword;
    }

    private void removeSharedPreferencesPassword() {
        obscuredSharedPreferences = ObscuredSharedPreferences.getPrefs(this, SHARED_PREFS, MODE_PRIVATE);
        editor = obscuredSharedPreferences.edit();
        editor.remove(SHARED_PASSWORD);
        editor.commit();
    }

    private void removeSharedPreferencesEmail() {
        obscuredSharedPreferences = ObscuredSharedPreferences.getPrefs(this, SHARED_PREFS, MODE_PRIVATE);
        editor = obscuredSharedPreferences.edit();
        editor.remove(SHARED_EMAIL);
        editor.commit();
    }

    private void displayUserCredentials() {
        String mEmail = getSharedPreferencesEmail();
        String mPassword = getSharedPreferencesPassword();
        email = (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        email.setText(mEmail);
        password.setText(mPassword);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.fetch_action:
                new LoginTask().execute();
                return true;
        }
        return false;
    }
*/
    /**
     * Implementation of AsyncTask, to fetch the data in the background away from
     * the UI thread.
     */
    private class LoginTask extends AsyncTask<String, Void, JSONObject> {
        private static final String TAG_TEAMID = "teamid";
        private static final String TAG_PEOPLE_ID = "peopleid";
	    private static final String API_URL = "https://teamlockerroom.com/api/authenticate";
	    private static final String CHARSET = "UTF-8";

        @Override
        protected JSONObject doInBackground(String... urls) {
            boolean ok = false;
            int responseCode = 0;
            HttpURLConnection conn = null;
            String requestData;
            BufferedReader reader;
            String line;
            StringBuilder sb = new StringBuilder();
            OutputStream oStream;
            OutputStreamWriter wr;

            try {
                URL url = new URL(API_URL);
                String mUsername = email.getText().toString();
                String mPassword = password.getText().toString();
                //String mUsername = "allan_911@hotmail.com";
                //String mPassword = "123456";
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
                if (json.getBoolean("failed")) {
                    Toast.makeText(MainActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                } else {
                    if (json.getString(TAG_TEAMID).equals(null) || json.getString(TAG_PEOPLE_ID).equals(null)) {
                        Toast.makeText(MainActivity.this, "Something went wrong. Try again.", Toast.LENGTH_SHORT).show();
                    } else {

                        setSharedPreferencesPassword();
                        setSharedPreferencesEmail();

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
            } catch (JSONException e) {
                removeSharedPreferencesPassword();
//                removeSharedPreferencesEmail();
                Toast.makeText(MainActivity.this, "Incorrect email/password. Please try again.", Toast.LENGTH_SHORT).show();
            }
            try {
                dialog.dismiss();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
