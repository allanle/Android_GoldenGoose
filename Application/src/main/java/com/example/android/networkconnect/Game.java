package com.example.android.networkconnect;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by pointstreak on 15-04-14.
 */
public class Game implements Parcelable {
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

    public Game(Parcel in) {
	    eventId = in.readString();
        title = in.readString();
        arenaName = in.readString();
        rinkName = in.readString();
        eventDate = in.readString();
        attendance = in.readString();
        teamId = in.readString();
        playerId = in.readString();
        result = in.readString();
    }

	public Game() {}

	public String getEventId() { return eventId; }

	public void setEventId(String eventId) { this.eventId = eventId; }

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

    public static final Parcelable.Creator<Game> CREATOR = new Parcelable.Creator<Game>() {

        @Override
        public Game createFromParcel(Parcel in) {
            return new Game(in); // RECREATE VENUE GIVEN SOURCE
        }

        @Override
        public Game[] newArray(int size) {
            return new Game[size]; // CREATING AN ARRAY OF VENUES
        }

    };
}
