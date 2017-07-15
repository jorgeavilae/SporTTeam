package com.usal.jorgeav.sportapp.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.usal.jorgeav.sportapp.network.firebase.FirebaseDBContract;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jorge Avila on 23/04/2017.
 */

public class Event implements Parcelable {
    String event_id;
    String sport_id;
    String field_id;
    String name;
    String city;
    Double coord_latitude;
    Double coord_longitude;
    Long date;
    String owner;
    int total_players;
    int empty_players;
    HashMap<String, Boolean> participants;
    HashMap<String, SimulatedUser> simulated_participants;

    public Event() {
        // Default constructor required for calls to DataSnapshot.getValue(Event.class)
    }

    public Event(String mId, String mSport, String mField, String mName, String mCity, LatLng coord,
                 Long mDate, String mOwner, int mTotalPlayers, int mEmptyPlayers,
                 HashMap<String, Boolean> participants, HashMap<String, SimulatedUser> simulated_participants) {
        this.event_id = mId;
        this.sport_id = mSport;
        this.field_id = mField;
        this.name = mName;
        this.city = mCity;
        this.coord_latitude = coord.latitude;
        this.coord_longitude = coord.longitude;
        this.date = mDate;
        this.owner = mOwner;
        this.total_players = mTotalPlayers;
        this.empty_players = mEmptyPlayers;
        this.participants = participants;
        this.simulated_participants = simulated_participants;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getEvent_id() {
        return event_id;
    }

    public String getSport_id() {
        return sport_id;
    }

    public String getField_id() {
        return field_id;
    }

    public String getCity() {
        return city;
    }

    public Double getCoord_latitude() {
        return coord_latitude;
    }

    public Double getCoord_longitude() {
        return coord_longitude;
    }

    public String getName() {
        return (name!=null?name:"");
    }

    public Long getDate() {
        return date;
    }

    public String getOwner() {
        return owner;
    }

    public int getTotal_players() {
        return total_players;
    }

    public int getEmpty_players() {
        return empty_players;
    }

    public void setEmpty_players(int empty_players) {
        this.empty_players = empty_players;
    }

    public HashMap<String, Boolean> getParticipants() {
        return participants;
    }

    public HashMap<String, SimulatedUser> getSimulated_participants() {
        return simulated_participants;
    }

    public void addToParticipants(String userId, Boolean participates) {
        if (this.participants == null) this.participants = new HashMap<String, Boolean>();
        this.participants.put(userId, participates);
    }

    public void deleteParticipant(String userId) {
        if (this.participants != null)
            this.participants.remove(userId);
    }

    public void addToSimulatedParticipants(String key, SimulatedUser participant) {
        if (this.simulated_participants == null) this.simulated_participants = new HashMap<String, SimulatedUser>();
        this.simulated_participants.put(key, participant);
    }

    public void deleteSimulatedParticipant(String key) {
        if (this.simulated_participants != null)
            this.simulated_participants.remove(key);
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.DATA, dataToMap());
        result.put(FirebaseDBContract.Event.INVITATIONS, new HashMap<String, Long>());
        result.put(FirebaseDBContract.Event.USER_REQUESTS, new HashMap<String, Long>());
        return result;
    }

    private Map<String, Object> dataToMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(FirebaseDBContract.Event.SPORT, this.sport_id);
        result.put(FirebaseDBContract.Event.FIELD, this.field_id);
        result.put(FirebaseDBContract.Event.CITY, this.city);
        result.put(FirebaseDBContract.Event.COORD_LATITUDE, this.coord_latitude);
        result.put(FirebaseDBContract.Event.COORD_LONGITUDE, this.coord_longitude);
        result.put(FirebaseDBContract.Event.NAME, this.name);
        result.put(FirebaseDBContract.Event.OWNER, this.owner);
        result.put(FirebaseDBContract.Event.DATE, this.date);
        result.put(FirebaseDBContract.Event.TOTAL_PLAYERS, this.total_players);
        result.put(FirebaseDBContract.Event.EMPTY_PLAYERS, this.empty_players);
        result.put(FirebaseDBContract.Event.PARTICIPANTS, this.participants);
        result.put(FirebaseDBContract.Event.SIMULATED_PARTICIPANTS, this.simulated_participants);
        return result;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Event event = (Event) o;

        if (total_players != event.total_players) return false;
        if (empty_players != event.empty_players) return false;
        if (event_id != null ? !event_id.equals(event.event_id) : event.event_id != null) return false;
        if (sport_id != null ? !sport_id.equals(event.sport_id) : event.sport_id != null) return false;
        if (field_id != null ? !field_id.equals(event.field_id) : event.field_id != null) return false;
        if (city != null ? !city.equals(event.city) : event.city != null) return false;
        if (date != null ? !date.equals(event.date) : event.date != null) return false;
        return owner != null ? owner.equals(event.owner) : event.owner == null;

    }

    @Override
    public int hashCode() {
        int result = event_id != null ? event_id.hashCode() : 0;
        result = 31 * result + (sport_id != null ? sport_id.hashCode() : 0);
        result = 31 * result + (field_id != null ? field_id.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + total_players;
        result = 31 * result + empty_players;
        return result;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.event_id);
        dest.writeString(this.sport_id);
        dest.writeString(this.field_id);
        dest.writeString(this.city);
        dest.writeValue(this.date);
        dest.writeString(this.owner);
        dest.writeInt(this.total_players);
        dest.writeInt(this.empty_players);
    }

    protected Event(Parcel in) {
        this.event_id = in.readString();
        this.sport_id = in.readString();
        this.field_id = in.readString();
        this.city = in.readString();
        this.date = (Long) in.readValue(Long.class.getClassLoader());
        this.owner = in.readString();
        this.total_players = in.readInt();
        this.empty_players = in.readInt();
    }

    public static final Creator<Event> CREATOR = new Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };

    @Override
    public String toString() {
        return "Event{" +
                "event_id='" + event_id + '\'' +
                ", sport_id='" + sport_id + '\'' +
                ", field_id='" + field_id + '\'' +
                ", city='" + city + '\'' +
                ", date=" + date +
                ", owner='" + owner + '\'' +
                ", total_players=" + total_players +
                ", empty_players=" + empty_players +
                ", participants=" + (participants!=null?participants.toString():"null") +
                ", simulated_participants=" + (simulated_participants!=null?simulated_participants.toString():"null") +
                '}';
    }
}


