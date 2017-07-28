package com.usal.jorgeav.sportapp.data.calendarevent;

import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge Avila on 28/07/2017.
 */

//TODO dos listas una para mis eventos y otra para los participo. No addAll si no replaceAll
public class MyCalendarEventList {
    private List<MyCalendarEvent> mList;

    public MyCalendarEventList(List<MyCalendarEvent> mList) {
        this.mList = mList;
        notifyListChange();
    }

    public List<CalendarEvent> getAsCalendarEventList() {
        List<CalendarEvent> result = new ArrayList<>();
        if (mList != null)
            for (MyCalendarEvent event : mList)
                result.add(event);
        return result;
    }

    public MyCalendarEvent getItemAtPosition(int position) {
        if (mList == null || position < 0 || mList.size() <= position) return null;
        return mList.get(position);
    }

    public void addAll(List<MyCalendarEvent> addList) {
        if (mList == null) this.mList = new ArrayList<>();

        List<String> eventsId = getAllEventId();
        if (addList != null)
            for (MyCalendarEvent event : addList)
                if (!eventsId.contains(event.getEvent_id()))
                    this.mList.add(event);

        notifyListChange();
    }

    public void clear(){
        this.mList.clear();
    }

    private void notifyListChange() {
        if (mList != null)
            for (int i = 0; i < mList.size(); i++)
                mList.get(i).setId(i);
    }

    private List<String> getAllEventId() {
        List<String> result = new ArrayList<>();
        if (mList != null)
            for (MyCalendarEvent event : mList)
                result.add(event.getEvent_id());
        return result;
    }

    @Override
    public String toString() {
        return "MyCalendarEventList{" +
                "mList=" + mList +
                '}';
    }
}
