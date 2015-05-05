package com.example.android.networkconnect;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;


public class CustomListAdapter extends ArrayAdapter<Game> {
    private ArrayList<Game> games;
    private LayoutInflater layoutInflater;
    private int mResource;
    private ViewHolder viewHolder;

    public CustomListAdapter(Context context, int resource, ArrayList<Game> objects) {
        super(context, resource, objects);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;
        games = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //View v = convertView;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(mResource, null);

            viewHolder.title = (TextView)convertView.findViewById(R.id.title);
            viewHolder.arenaName = (TextView)convertView.findViewById(R.id.arenaname);
            viewHolder.rinkName = (TextView)convertView.findViewById(R.id.rinkname);
            viewHolder.eventDate = (TextView)convertView.findViewById(R.id.eventdate);
            viewHolder.attendance = (TextView)convertView.findViewById(R.id.attendance);

            viewHolder.title.setText(games.get(position).getTitle());
            viewHolder.rinkName.setText(games.get(position).getRinkName());
            viewHolder.arenaName.setText(games.get(position).getArenaName());
            viewHolder.eventDate.setText(games.get(position).getEventDate());
            viewHolder.attendance.setText(games.get(position).getAttendance());

            viewHolder.no = (Button)convertView.findViewById(R.id.no);
            viewHolder.yes = (Button)convertView.findViewById(R.id.yes);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch(v.getId()) {
                        case R.id.yes:

                            Toast.makeText(getContext(), "Attending Game", Toast.LENGTH_SHORT).show();
                            break;
                        case R.id.no:
                            Toast.makeText(getContext(), "Not Attending Game", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            };

            viewHolder.no.setOnClickListener(listener);
            viewHolder.yes.setOnClickListener(listener);

            /*viewHolder.no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(),"Not Attending Game", Toast.LENGTH_LONG).show();
                }
            });*/
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
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

    private void updateJson() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("TAG_ATTENDANCE_STATUS", 1);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }



        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }
    }
}
