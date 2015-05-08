package com.example.android.networkconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.common.logger.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;


public class CustomListAdapter extends ArrayAdapter<Game> {
    private ArrayList<Game> games;
    private LayoutInflater layoutInflater;
    private int mResource;
    private ViewHolder viewHolder;
    Game game = new Game();
    private Context ctx;

    public CustomListAdapter(Context context, int resource, ArrayList<Game> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
        games = objects;
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(mResource, null);

            viewHolder.title = (TextView)convertView.findViewById(R.id.title);
            viewHolder.arenaName = (TextView)convertView.findViewById(R.id.arenaname);
            viewHolder.rinkName = (TextView)convertView.findViewById(R.id.rinkname);
            viewHolder.eventDate = (TextView)convertView.findViewById(R.id.eventdate);
            viewHolder.attendance = (TextView)convertView.findViewById(R.id.attendance);
            convertView.setTag(viewHolder);

            viewHolder.no = (Button)convertView.findViewById(R.id.no);
            viewHolder.yes = (Button)convertView.findViewById(R.id.yes);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(v.getId()) {
                        case R.id.yes:
                            new UpdateAttendanceEvent().execute("https://teamlockerroom.com/api/setattendance");
                            Toast.makeText(getContext(), "Attending Game Bro", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.no:
                            new UpdateAttendanceEvent().execute("https://teamlockerroom.com/api/setattendance");
                            Toast.makeText(getContext(), "Not Attending Game Bro", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

            viewHolder.no.setOnClickListener(listener);
            viewHolder.yes.setOnClickListener(listener);
/*
            viewHolder.no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UpdateAttendanceEvent().execute("https://teamlockerroom.com/api/setattendance");
                    Toast.makeText(getContext(), "Not Attending Game Bro", Toast.LENGTH_SHORT).show();
                }
            });
            viewHolder.yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UpdateAttendanceEvent().execute("https://teamlockerroom.com/api/setattendance");
                    Toast.makeText(getContext(), "Attending Game Bro", Toast.LENGTH_SHORT).show();
                }
            });*/
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.title.setText(games.get(position).getTitle());
        viewHolder.rinkName.setText(games.get(position).getRinkName());
        viewHolder.arenaName.setText(games.get(position).getArenaName());
        viewHolder.eventDate.setText(games.get(position).getEventDate());
        viewHolder.attendance.setText(games.get(position).getAttendance());
        return convertView;
    }

    static class ViewHolder {
        public TextView title;
        public TextView arenaName;
        public TextView rinkName;
        public TextView eventDate;
        public Button yes;
        public Button no;
        public TextView attendance;
    }

    private class UpdateAttendanceEvent extends AsyncTask<String, Boolean, String> {
        private static final String TAG_EVENT_ID = "eventid";
        private static final String TAG_STATUS = "status";
        private static final String TAG_PEOPLE_ID = "peopleid";
        private static final String TAG_TEAM_ID = "teamid";
        private static final String TAG_PERM_LEVEL = "permlevel";
        private static final String TAG_FLAGS = "flags";

        @Override
        protected void onPreExecute() {
            SharedPreferences sharedPreferences = ctx.getSharedPreferences("data", Context.MODE_PRIVATE);

            String eventid = sharedPreferences.getString("event", "");
            String status = sharedPreferences.getString("status", "");
            String peopleid = sharedPreferences.getString("people", "");
            String teamid = sharedPreferences.getString("team", "");
            String permlevel = sharedPreferences.getString("perm", "");
            String flag = sharedPreferences.getString("flag", "");

            Log.d("MyApp" + " IS THIS WORKING BRO ", eventid + "---" + status + "---" + peopleid + "---" + teamid + "---" + permlevel + "---" + flag);
        }

        @Override
        protected String doInBackground(String... urls) {
            if(!viewHolder.yes.isSelected()) {
                try {
                    HttpPost httpPost = new HttpPost(urls[0]);

                    HttpClient httpClient = new DefaultHttpClient();
                    String json = "";

                    JSONObject jsonObject = new JSONObject();
                    String a = game.getArenaName();
                    String b = game.getAttendance();
                    String c = game.getEventDate();
                    Log.d("MyApp", a + b + c);

                    jsonObject.put(TAG_EVENT_ID, "1895772");
                    jsonObject.put(TAG_STATUS, "in");
                    jsonObject.put(TAG_PEOPLE_ID, "17786870"); //17786870 17788045
                    jsonObject.put(TAG_TEAM_ID, "408330");
                    jsonObject.put(TAG_PERM_LEVEL, "");
                    jsonObject.put(TAG_FLAGS, "");



                    json = jsonObject.toString();

                    Log.d("MyApp" + " Update Attendance Event", json);

                    StringEntity stringEntity = new StringEntity(json);
                    httpPost.setEntity(stringEntity);
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    int responseStatus = httpResponse.getStatusLine().getStatusCode();
                    if (responseStatus == 200) {
                        Log.d("MyApp", "Attendance Success 200");
                    } else {
                        Log.d("MyApp", "Attendance Fail 500");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "attendance set to yes";
            } else if(viewHolder.no.isSelected()) {
                try {
                    HttpPost httpPost = new HttpPost(urls[0]);

                    HttpClient httpClient = new DefaultHttpClient();
                    String json = "";

                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put(TAG_EVENT_ID, "1895772");
                    jsonObject.put(TAG_STATUS, "out");
                    jsonObject.put(TAG_PEOPLE_ID, "17786870"); //17786870 17788045
                    jsonObject.put(TAG_TEAM_ID, "408330");
                    jsonObject.put(TAG_PERM_LEVEL, "");
                    jsonObject.put(TAG_FLAGS, "");

                    json = jsonObject.toString();

                    Log.d("MyApp" + " Update Attendance Event", json);

                    StringEntity stringEntity = new StringEntity(json);
                    httpPost.setEntity(stringEntity);
                    httpPost.setHeader("Accept", "application/json");
                    httpPost.setHeader("Content-type", "application/json");

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    int responseStatus = httpResponse.getStatusLine().getStatusCode();
                    if (responseStatus == 200) {
                        Log.d("MyApp", "Attendance Success 200");
                    } else {
                        Log.d("MyApp", "Attendance Fail 500");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return "attendance set to no";
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("MyApp", result);
        }
    }
}
