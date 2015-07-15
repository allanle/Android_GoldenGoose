package com.pointstreak.goldengoose.classes;

import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.android.networkconnect.R;

public class ViewHolder {
    private TextView played;
    private TextView eventId;
    private TextView title;
    private TextView arenaName;
    private TextView rinkName;
    private TextView eventDate;
    public Button yes;
    public Button no;
    private TextView attendance;
    private boolean yesClicked = false;
    private boolean noClicked = false;
    private Event event;
    private TextView unixTimeStamp;
    private static final String TAG_MY_APP = "MyApp";

    /**
     * Look for a child view with the given tag.
     * @param convertView
     */
    public ViewHolder(View convertView) {
        // Set the tags for the record.
        this.title = (TextView)convertView.findViewById(R.id.title);
        this.arenaName = (TextView)convertView.findViewById(R.id.arenaname);
        this.rinkName = (TextView)convertView.findViewById(R.id.rinkname);
        this.eventDate = (TextView)convertView.findViewById(R.id.eventdate);
        this.attendance = (TextView)convertView.findViewById(R.id.attendance);
        this.eventId = (TextView)convertView.findViewById(R.id.eventid);
        this.played = (TextView)convertView.findViewById(R.id.played);
        this.no = (Button)convertView.findViewById(R.id.no);
        this.yes = (Button)convertView.findViewById(R.id.yes);
        this.unixTimeStamp = (TextView)convertView.findViewById(R.id.unixtimestamp);
    }

    /**
     *
     * @param event - passing in event
     */
    public void setEvent(Event event) {
        this.event = event;
        this.setStrikeoutText();
        this.setText();
        this.setButtonClicking();
    }

    /**
     * Used date comparison for events to strike out past events. If the older date is less than
     * the current date, then strike out the text.
     */
    public void setStrikeoutText() {
        long currentUnixTime = System.currentTimeMillis() / 1000;
        long eventUnixTime = 0;

        try {
            eventUnixTime = Long.parseLong(event.getUnixTimeStamp());
        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG_MY_APP, "cur  time : " + currentUnixTime);
        Log.d(TAG_MY_APP, event.getTitle() + " : " + eventUnixTime);

        if(eventUnixTime < currentUnixTime) {
            this.title.setPaintFlags(this.title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.rinkName.setPaintFlags(this.rinkName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.arenaName.setPaintFlags(this.arenaName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.eventDate.setPaintFlags(this.eventDate.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.attendance.setPaintFlags(this.attendance.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.yes.setPaintFlags(this.yes.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            this.no.setPaintFlags(this.no.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            // Don't strike text if future event
            this.title.setPaintFlags(0);
            this.rinkName.setPaintFlags(0);
            this.arenaName.setPaintFlags(0);
            this.eventDate.setPaintFlags(0);
            this.attendance.setPaintFlags(0);
            this.yes.setPaintFlags(0);
            this.no.setPaintFlags(0);
        }
    }

    /**
     * Setting the text to the current JSON feed.
     */
    public void setText() {
        this.title.setText(event.getTitle());
        this.rinkName.setText(event.getRinkName());
        this.arenaName.setText(event.getArenaName());
        this.eventDate.setText(event.getEventDate());
        this.attendance.setText(event.getAttendance());
        this.eventId.setText(event.getEventId());
        this.played.setText(event.getPlayed());
        this.unixTimeStamp.setText(event.getUnixTimeStamp());
    }

    /**
     * Flags for setting attendance to an event.
     */
    public void setButtonClicking() {
        if(event.isYesClicked()) {
            this.yes.setEnabled(false);
        } else {
            this.yes.setEnabled(true);
        }

        if(event.isNoClicked()) {
            this.no.setEnabled(false);
        } else {
            this.no.setEnabled(true);
        }
    }
}
