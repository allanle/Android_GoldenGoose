package com.example.android.networkconnect;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by pointstreak on 15-04-21.
 */
public class JSONObjectParser implements Parcelable{

    private JSONObject jsonObject;
    private String teamId;
    private String playerId;
    private String attendanceStatus;
    private String result;

    public JSONObjectParser() {

    }

    public JSONObjectParser(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JSONObjectParser(JSONObject jsonObject, String teamId, String playerId, String attendanceStatus, String result) {
        this.jsonObject = jsonObject;
        this.teamId = teamId;
        this.playerId = playerId;
        this.attendanceStatus = attendanceStatus;
        this.result = result;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonArary(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
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

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // We just need to write each field into the
        // parcel. When we read from parcel, they
        // will come back in the same order
        // dest.writeString(strValue);
        // dest.writeInt(intValue);
    }

    /**
     * CAlled form the constructor to create this object from a parcel
     * @param in parcel from which to re-crete object
     */
    private void readFromParce(Parcel in) {
        //we just need to read back each filed in the order that it was written to the parcel
        // value1 = in.readString();
        // value 2 = in.readString();
    }
/*
    public static Creator<JSONArrayParser> CREATOR = new Creator<JSONArrayParser>() {
        public JSONArrayParser createFromParcel(Parcel source) {
            return new JSONArrayParser(source);
        }

        public JSONArrayParser[] newArray(int size) {
            return new JSONArrayParser(size);
        }
    }*/
}
