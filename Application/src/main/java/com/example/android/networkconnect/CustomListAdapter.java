package com.example.android.networkconnect;

import android.content.Context;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.util.ArrayList;


public class CustomListAdapter extends ArrayAdapter<Events> {
    private ArrayList<Events> events;
	private String peopleId;
	private String teamId;
    private LayoutInflater layoutInflater;
    private int mResource;
    private ViewHolder viewHolder;
	private static final String TAG_MY_APP = "MyApp";

	public CustomListAdapter() {
		super(null, Integer.parseInt(null));
	}

    public CustomListAdapter(Context context, int resource, ArrayList<Events> events) {
        super(context, resource, events);
    }

    public CustomListAdapter(Context context, int resource, String peopleId, String teamId, ArrayList<Events> events) {
        super(context, resource, events);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = resource;

	    // Set the passed in variables.
        this.events = events;
	    this.peopleId = peopleId;
	    this.teamId = teamId;
    }

	/**
	 * The list view is not in charge of instantiating or modifying the underlying state of the views it contains.
	 * That's the job of the adapter. When you scroll a ListView, it calls the adapter method getView()
	 * and the adapter will hand it a row (possibly a recycle view from the rows that scrolled off-screen).
	 *
	 * Inside the getView() method, the adapter should consult the underlying data set and modify the row
	 * as needed - that would include crossing out text, or setting any visual properties of the row content.
	 *
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            // Get the eventId for this record.

            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(mResource, null);

            // Set the tags for the record.
            viewHolder.title = (TextView)convertView.findViewById(R.id.title);
            viewHolder.arenaName = (TextView)convertView.findViewById(R.id.arenaname);
            viewHolder.rinkName = (TextView)convertView.findViewById(R.id.rinkname);
            viewHolder.eventDate = (TextView)convertView.findViewById(R.id.eventdate);
            viewHolder.attendance = (TextView)convertView.findViewById(R.id.attendance);
            viewHolder.eventId = (TextView)convertView.findViewById(R.id.eventid);
            viewHolder.played = (TextView)convertView.findViewById(R.id.played);
            viewHolder.no = (Button)convertView.findViewById(R.id.no);
            viewHolder.yes = (Button)convertView.findViewById(R.id.yes);

            convertView.setTag(viewHolder);

            // Click listeners for the attendance buttons.
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }

        String eventId = events.get(position).getEventId();
        viewHolder.no.setOnClickListener(new AttendanceClickListener(eventId, peopleId, teamId, position));
        viewHolder.yes.setOnClickListener(new AttendanceClickListener(eventId, peopleId, teamId, position));

        // Strike through text for past events
        if(events.get(position).getPlayed().equals("1")) {
            viewHolder.title.setPaintFlags(viewHolder.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.rinkName.setPaintFlags(viewHolder.rinkName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.arenaName.setPaintFlags(viewHolder.arenaName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.eventDate.setPaintFlags(viewHolder.eventDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.attendance.setPaintFlags(viewHolder.attendance.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.yes.setPaintFlags(viewHolder.yes.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.no.setPaintFlags(viewHolder.no.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            // Don't strike text if future events
            viewHolder.title.setPaintFlags(0);
            viewHolder.rinkName.setPaintFlags(0);
            viewHolder.arenaName.setPaintFlags(0);
            viewHolder.eventDate.setPaintFlags(0);
            viewHolder.attendance.setPaintFlags(0);
            viewHolder.yes.setPaintFlags(0);
			viewHolder.no.setPaintFlags(0);
		}

		viewHolder.title.setText(events.get(position).getTitle());
		viewHolder.rinkName.setText(events.get(position).getRinkName());
		viewHolder.arenaName.setText(events.get(position).getArenaName());
		viewHolder.eventDate.setText(events.get(position).getEventDate());
		viewHolder.attendance.setText(events.get(position).getAttendance());
		viewHolder.eventId.setText(events.get(position).getEventId());
		viewHolder.played.setText(events.get(position).getPlayed());

        if(events.get(position).isYesClicked()) {
            viewHolder.yes.setEnabled(false);
        } else {
            viewHolder.yes.setEnabled(true);
        }

        if(events.get(position).isNoClicked()) {
            viewHolder.no.setEnabled(false);
        } else {
            viewHolder.no.setEnabled(true);
        }

		convertView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getContext(), events.get(position).getTitle(), Toast.LENGTH_SHORT).show();
			}
		});

        return convertView;
	}

    class ViewHolder {
		public TextView played;
	    public TextView eventId;
        public TextView title;
        public TextView arenaName;
        public TextView rinkName;
        public TextView eventDate;
        public Button yes;
        public Button no;
        public TextView attendance;
        public boolean yesClicked = false;
        public boolean noClicked = false;
    }

	public class AttendanceClickListener implements View.OnClickListener {
		private final String eventId;
		private final String peopleId;
		private final String teamId;
		private final int position;

		public AttendanceClickListener(final String eventId, final String peopleId, final String teamId,
									   final int position) {
			this.eventId = eventId;
			this.peopleId = peopleId;
			this.teamId = teamId;
			this.position = position;
		}

		@Override
		// Instead of creating one instance of a listener for everything and trying to get the eventid,
		// this will make sure that each button has a unique event ID to call the UpdateAttendanceEvent
		// function.
		public void onClick(View v) {
            switch(v.getId()) {
                case R.id.yes:
                    // The eventId, peopleId and teamId.
                    //new UpdateAttendanceEvent().execute("in", eventId, "17786870", "408330");
                    new UpdateAttendanceEvent(v, position).execute("in", eventId, peopleId, teamId);
                    events.get(position).setAttendance("I am attending this event");
                    Toast.makeText(getContext(), "Attending Event", Toast.LENGTH_SHORT).show();
                    events.get(position).setYesClicked(true);
                    break;
				case R.id.no:
                    // The eventId, peopleId and teamId.
                    //new UpdateAttendanceEvent().execute("out", eventId, "17786870", "408330");
                    new UpdateAttendanceEvent(v, position).execute("out", eventId, peopleId, teamId);
                    events.get(position).setAttendance("I am not attending this event");
                    Toast.makeText(getContext(), "Not Attending Event", Toast.LENGTH_SHORT).show();
                    events.get(position).setNoClicked(true);
                    break;
			}
		}
	}

	// This is called by the onClick handler for a YES or NO button.
	// The handler should pass "true" or "false" to this update attendance function.
	// The handler should also pass the eventId, peopleId, teamId to this function.
	// <String> gets sent to doInBackground.
	// <Void> get sent to process function (if needed).
	// <String> get sent to onPostExecute.
    private class UpdateAttendanceEvent extends AsyncTask<String, Void, String> {
        private static final String TAG_EVENT_ID = "eventid";
        private static final String TAG_STATUS = "status";
        private static final String TAG_PEOPLE_ID = "peopleid";
        private static final String TAG_TEAM_ID = "teamid";
        private static final String TAG_PERM_LEVEL = "permlevel";
        private static final String TAG_FLAGS = "flags";
	    private static final String API_URL = "https://teamlockerroom.com/api/setattendance";
        private View rowView;
        private int position;

        public UpdateAttendanceEvent(View v, int position) {
            super();
            this.rowView = v;
            this.position = position;
        }

        @Override
        // "params" should have 0 = String "in"/"out", 1 = eventid, 2 = peopleid, 3 = teamid.
        protected String doInBackground(String... params) {
	        String attendance = "not attending";
            HttpPost httpPost = null;
            HttpClient httpClient = null;
            JSONObject jsonObject = null;

	        if(params[0].equals("in")) {
		        attendance = "attending";
	        }

	        // Try and set the attendance.
	        try {
		        httpPost = new HttpPost(API_URL);
		        httpClient = new DefaultHttpClient();
		        jsonObject = new JSONObject();
		        String json = "";

		        jsonObject.put(TAG_STATUS, params[0]);
		        jsonObject.put(TAG_EVENT_ID, params[1]);
		        jsonObject.put(TAG_PEOPLE_ID, params[2]);
		        jsonObject.put(TAG_TEAM_ID, params[3]);
		        jsonObject.put(TAG_PERM_LEVEL, "");
		        jsonObject.put(TAG_FLAGS, "");

		        json = jsonObject.toString();

		        StringEntity stringEntity = new StringEntity(json);
		        httpPost.setEntity(stringEntity);
		        httpPost.setHeader("Accept", "application/json");
		        httpPost.setHeader("Content-type", "application/json");

		        Log.d(TAG_MY_APP + " attendance ", params[0]);
		        Log.d(TAG_MY_APP + " event id ", params[1]);
		        Log.d(TAG_MY_APP + " people id ", params[2]);
		        Log.d(TAG_MY_APP + " team id ", params[3]);

		        HttpResponse httpResponse = httpClient.execute(httpPost);
		        if(httpResponse.getStatusLine().getStatusCode() == 200) {
                    Log.d(TAG_MY_APP, attendance);
                    //return "I am " +attendance + " Event " +params[1];
					return attendance;
                }
	        } catch(Exception e) {
		        e.printStackTrace();
	        }
	        return "There was an issue setting my attendance";
        }

        @Override
        // After you're done updating the attendance, return true / false
        // to the UI so that it can properly update the text field with
        // "I am attending this event" or "I am not attending this event"
        // or it can highlight the yes/no button and disable the other.
        protected void onPostExecute(String result) {
            TextView attendance = (TextView)((View)rowView.getParent().getParent()).findViewById(R.id.attendance);
            Button buttonYes = (Button)(((View)rowView.getParent().getParent()).findViewById(R.id.yes));
            Button buttonNo = (Button)(((View)rowView.getParent().getParent()).findViewById(R.id.no));

            if(result.equals("attending")) {
                attendance.setText("I am attending this event");
                buttonYes.setEnabled(false);
            } else {
                attendance.setText("I am not attending this event");
                buttonNo.setEnabled(false);
            }
        }
    }
}
