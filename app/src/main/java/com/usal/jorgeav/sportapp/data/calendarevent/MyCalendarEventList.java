package com.usal.jorgeav.sportapp.data.calendarevent;

import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import java.util.ArrayList;
import java.util.List;

public class MyCalendarEventList {
    private List<MyCalendarEvent> mEventList;

    public MyCalendarEventList(List<MyCalendarEvent> eventList) {
        this.mEventList = eventList;
        notifyListChange();
    }

    public List<CalendarEvent> getAsCalendarEventList() {
        List<CalendarEvent> result = new ArrayList<>();
        if (this.mEventList != null)
                result.addAll(this.mEventList);
        return result;
    }

    public MyCalendarEvent getItemAtPosition(int position) {
        if (mEventList != null && position >= 0 && position < mEventList.size())
            return mEventList.get(position);
        return null;
    }

    public Long getMinDateInMillis() {
        Long min = null;
        if (mEventList != null)
            for(MyCalendarEvent event : mEventList)
                min = (min == null || event.getStartTime().getTimeInMillis() < min) ?
                        event.getStartTime().getTimeInMillis() : min;
        return min;
    }

    public Long getMaxDateInMillis() {
        Long max = null;
        if (mEventList != null)
            for(MyCalendarEvent event : mEventList)
                max = (max == null || event.getStartTime().getTimeInMillis() > max) ?
                        event.getStartTime().getTimeInMillis() : max;
        return max;
    }

    public void replaceEvents(List<MyCalendarEvent> addList) {
        this.mEventList = new ArrayList<>();
        if (addList != null)
            this.mEventList.addAll(addList);

        notifyListChange();
    }

    public void clear(){
        if (this.mEventList != null)
            this.mEventList.clear();
    }

    /* Set ID as the position number in List */
    private void notifyListChange() {
        if (mEventList != null)
            for (int i = 0; i < mEventList.size(); i++)
                mEventList.get(i).setId(i);
    }

    private List<String> getAllEventId(List<MyCalendarEvent> list) {
        List<String> result = new ArrayList<>();
        if (list != null)
            for (MyCalendarEvent event : list)
                result.add(event.getEvent_id());
        return result;
    }

    @Override
    public String toString() {
        return "MyCalendarEventList{" +
                "mEventList=" + mEventList +
                '}';
    }
}
