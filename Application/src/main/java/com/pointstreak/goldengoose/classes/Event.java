package com.pointstreak.goldengoose.classes;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

public class Event implements Parcelable {
	private String eventId;
    private String title;
    private String arenaName;
    private String rinkName;
    private String eventDate;
    private String attendance;
    private JSONObject jsonObject;
    private String teamId;
    private String playerId;
    private String result;
    private int id;
    private String played;
    private boolean yesClicked;
    private boolean noClicked;

    private static final String TAG_EVENT_ID = "eventid";
    private static final String TAG_TITLE = "title";
    private static final String TAG_ARENA_NAME = "arenaname";
    private static final String TAG_RINK_NAME = "rinkname";
    private static final String TAG_EVENT_DATE = "eventdate";
    private static final String TAG_ATTENDANCE_STATUS = "attstatus";
    private static final String TAG_PLAYED = "played";

    private static final String ATTENDANCE_NULL = "You haven't decided yet";
    private static final String ATTENDANCE_YES = "I am attending this event";
    private static final String ATTENDANCE_NO = "I am not attending this event";

    public Event(JSONObject jsonObject) {
        try {
            if(jsonObject.getString(TAG_RINK_NAME).equalsIgnoreCase("null")) {
                this.setRinkName(jsonObject.getString(TAG_RINK_NAME));
            } else if(jsonObject.getString(TAG_ARENA_NAME).equalsIgnoreCase("null")) {
                this.setArenaName(jsonObject.getString(TAG_ARENA_NAME));
            } else if(jsonObject.getString(TAG_EVENT_DATE).equalsIgnoreCase("null")) {
                this.setEventDate(jsonObject.getString(TAG_EVENT_DATE));
            } else if(jsonObject.getString(TAG_EVENT_DATE).equalsIgnoreCase("null")) {
                this.setEventDate(jsonObject.getString(TAG_EVENT_DATE));
            }

            if(jsonObject.getString(TAG_ATTENDANCE_STATUS).equalsIgnoreCase("null")) {
                this.setAttendance(ATTENDANCE_NULL);
            } else if(jsonObject.getString(TAG_ATTENDANCE_STATUS).equalsIgnoreCase("0")) {
                this.setAttendance(ATTENDANCE_NO);
            } else if(jsonObject.getString(TAG_ATTENDANCE_STATUS).equalsIgnoreCase("1")) {
                this.setAttendance(ATTENDANCE_YES);
            }

            // Set the eventId.
            this.setPlayed(jsonObject.getString(TAG_PLAYED));
            this.setEventId(jsonObject.getString(TAG_EVENT_ID));
            this.setTitle(jsonObject.getString(TAG_TITLE));
            this.setArenaName(jsonObject.getString(TAG_ARENA_NAME));
            this.setRinkName(jsonObject.getString(TAG_RINK_NAME));
            this.setEventDate(jsonObject.getString(TAG_EVENT_DATE));

        }catch(JSONException e) {
            e.printStackTrace();
        }
    }

    public Event(Parcel in) {
	    this.eventId = in.readString();
        this.title = in.readString();
        this.arenaName = in.readString();
        this.rinkName = in.readString();
        this.eventDate = in.readString();
        this.attendance = in.readString();
        this.teamId = in.readString();
        this.playerId = in.readString();
        this.result = in.readString();
        this.played = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArenaName() {
        return arenaName;
    }

    public void setArenaName(String arenaName) {
        this.arenaName = arenaName;
    }

    public String getRinkName() {
        return rinkName;
    }

    public void setRinkName(String rinkName) {
        this.rinkName = rinkName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getAttendance() {
        return attendance;
    }

    public void setAttendance(String attendance) {
        this.attendance = attendance;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPlayed() {
        return this.played;
    }

    public void setPlayed(String played) {
        this.played = played;
    }

    public boolean isYesClicked() {
        return yesClicked;
    }

    public void setYesClicked(boolean yesClicked) {
        this.yesClicked = yesClicked;
    }

    public boolean isNoClicked() {
        return noClicked;
    }
    public void setNoClicked(boolean noClicked) {
        this.noClicked = noClicked;
    }

    @Override
    public String toString() {
        return this.id + this.title;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.playerId);
        dest.writeString(this.teamId);
	    dest.writeString(this.eventId);
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {

        @Override
        public Event createFromParcel(Parcel in) {
            return new Event(in); // RECREATE VENUE GIVEN SOURCE
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size]; // CREATING AN ARRAY OF VENUES
        }

    };
}
