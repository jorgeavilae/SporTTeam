package com.usal.jorgeav.sportapp.data.calendarevent;

import com.github.tibolte.agendacalendarview.models.BaseCalendarEvent;
import com.github.tibolte.agendacalendarview.models.CalendarEvent;
import com.usal.jorgeav.sportapp.data.Event;
import com.usal.jorgeav.sportapp.data.Field;

import java.util.Calendar;

/**
 * Created by Jorge Avila on 28/07/2017.
 */

public class MyCalendarEvent extends BaseCalendarEvent {
    private String event_id;
    private String sport_id;
    private String field_name;
    private String city;
    private Double coord_latitude;
    private Double coord_longitude;
    private String owner;
    private Long total_players;
    private Long empty_players;

    private int mDrawableId;

    /**
     * Initializes the event
     *
     * @param title       The title of the event.
     * @param description The description of the event.
     * @param location    The location of the event.
     * @param color       The color of the event (for display in the app).
     * @param startTime   The start time of the event.
     * @param endTime     The end time of the event.
     * @param allDay      Indicates if the event lasts the whole day.
     */
    private MyCalendarEvent(String title, String description, String location, int color, Calendar startTime, Calendar endTime, boolean allDay) {
        super(title, description, location, color, startTime, endTime, allDay);
    }

    /**
     * Initializes the event
     *
     * @param id          The id of the event.
     * @param color       The color of the event.
     * @param title       The title of the event.
     * @param description The description of the event.
     * @param location    The location of the event.
     * @param dateStart   The start date of the event.
     * @param dateEnd     The end date of the event.
     * @param allDay      Int that can be equal to 0 or 1.
     * @param duration    The duration of the event in RFC2445 format.
     */
    public MyCalendarEvent(long id, int color, String title, String description, String location, long dateStart, long dateEnd, int allDay, String duration) {
        super(id, color, title, description, location, dateStart, dateEnd, allDay, duration);
    }



    private void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    private void setSport_id(String sport_id) {
        this.sport_id = sport_id;
    }

    private void setField_name(String field_name) {
        this.field_name = field_name;
    }

    private void setCity(String city) {
        this.city = city;
    }

    private void setCoord_latitude(Double coord_latitude) {
        this.coord_latitude = coord_latitude;
    }

    private void setCoord_longitude(Double coord_longitude) {
        this.coord_longitude = coord_longitude;
    }

    private void setOwner(String owner) {
        this.owner = owner;
    }

    private void setTotal_players(Long total_players) {
        this.total_players = total_players;
    }

    private void setEmpty_players(Long empty_players) {
        this.empty_players = empty_players;
    }

    public String getEvent_id() {

        return event_id;
    }

    public String getSport_id() {
        return sport_id;
    }

    public String getField_name() {
        return field_name;
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

    public String getOwner() {
        return owner;
    }

    public Long getTotal_players() {
        return total_players;
    }

    public Long getEmpty_players() {
        return empty_players;
    }

    @Override
    public CalendarEvent copy() {
        return Builder.copyInstance(this);
    }

    public static class Builder {
        public static MyCalendarEvent newInstance(Event event, Field field, int color) {

            // Set field name or event address
            String place = "";
            if (field == null) place = event.getAddress();
            else place = field.getName();

            Calendar startTime = Calendar.getInstance();
            startTime.setTimeInMillis(event.getDate());

            long twoHoursInMillis = 1000 * 60 * 60 * 2;
            Calendar endTime = Calendar.getInstance();
            endTime.setTimeInMillis(event.getDate() + twoHoursInMillis);

            MyCalendarEvent myCalendarEvent = new MyCalendarEvent(-1, color, event.getName(),
                    event.getAddress(), place, startTime.getTimeInMillis(), endTime.getTimeInMillis(), 0, "");

            myCalendarEvent.setEvent_id(event.getEvent_id());
            myCalendarEvent.setSport_id(event.getSport_id());
            myCalendarEvent.setField_name(place);
            myCalendarEvent.setCity(event.getCity());
            myCalendarEvent.setCoord_latitude(event.getCoord_latitude());
            myCalendarEvent.setCoord_longitude(event.getCoord_longitude());
            myCalendarEvent.setOwner(event.getOwner());
            myCalendarEvent.setTotal_players(event.getTotal_players());
            myCalendarEvent.setEmpty_players(event.getEmpty_players());

            return myCalendarEvent;
        }

        private static MyCalendarEvent copyInstance(MyCalendarEvent myCalendarEvent) {
            MyCalendarEvent result = new MyCalendarEvent(myCalendarEvent.getId(), myCalendarEvent.getColor(),
                    myCalendarEvent.getTitle(), myCalendarEvent.getDescription(), myCalendarEvent.getLocation(),
                    myCalendarEvent.getStartTime().getTimeInMillis(), myCalendarEvent.getEndTime().getTimeInMillis(), 0, "");

            result.setEvent_id(myCalendarEvent.getEvent_id());
            result.setSport_id(myCalendarEvent.getSport_id());
            result.setField_name(myCalendarEvent.getField_name());
            result.setCity(myCalendarEvent.getCity());
            result.setCoord_latitude(myCalendarEvent.getCoord_latitude());
            result.setCoord_longitude(myCalendarEvent.getCoord_longitude());
            result.setOwner(myCalendarEvent.getOwner());
            result.setTotal_players(myCalendarEvent.getTotal_players());
            result.setEmpty_players(myCalendarEvent.getEmpty_players());

            return myCalendarEvent;
        }
    }

    @Override
    public String toString() {
        return "MyCalendarEvent{" +
                "event_id='" + event_id + '\'' +
                ", sport_id='" + sport_id + '\'' +
                ", field_name='" + field_name + '\'' +
                ", name='" + getTitle() + '\'' +
                ", address='" + getDescription() + '\'' +
                ", city='" + city + '\'' +
                ", coord_latitude=" + coord_latitude +
                ", coord_longitude=" + coord_longitude +
                ", date=" + getStartTime() +
                ", owner='" + owner + '\'' +
                ", total_players=" + total_players +
                ", empty_players=" + empty_players +
                '}';
    }
}
