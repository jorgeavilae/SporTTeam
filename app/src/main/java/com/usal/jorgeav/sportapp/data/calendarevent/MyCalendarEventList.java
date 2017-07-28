package com.usal.jorgeav.sportapp.data.calendarevent;

import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge Avila on 28/07/2017.
 */

public class MyCalendarEventList {
    private List<MyCalendarEvent> mOwnEventList;
    private List<MyCalendarEvent> mParticipationEventList;

    public MyCalendarEventList(List<MyCalendarEvent> list) {
        this.mOwnEventList = list;
        this.mParticipationEventList = list;
        notifyListChange();
    }

    public List<CalendarEvent> getAsCalendarEventList() {
        List<CalendarEvent> result = new ArrayList<>();
        if (mOwnEventList != null)
            for (MyCalendarEvent event : mOwnEventList)
                result.add(event);
        if (mParticipationEventList != null)
            for (MyCalendarEvent event : mParticipationEventList)
                result.add(event);
        return result;
    }

    public MyCalendarEvent getItemAtPosition(int position) {
        int sizeOwn = 0, sizeParticipation = 0;
        if (mOwnEventList != null) sizeOwn = mOwnEventList.size();
        if (mParticipationEventList != null) sizeParticipation = mParticipationEventList.size();
        if (position < 0 || position >= sizeOwn + sizeParticipation) return null;

        List<MyCalendarEvent> result = new ArrayList<>();
        if (mOwnEventList != null)
            for (MyCalendarEvent event : mOwnEventList)
                result.add(event);
        if (mParticipationEventList != null)
            for (MyCalendarEvent event : mParticipationEventList)
                result.add(event);
        return result.get(position);
    }

    public void replaceOwnEvents(List<MyCalendarEvent> addList) {
        if (mOwnEventList == null) this.mOwnEventList = new ArrayList<>();

        List<String> eventsId = getAllEventId(mOwnEventList);
        if (addList != null)
            for (MyCalendarEvent event : addList)
                if (!eventsId.contains(event.getEvent_id()))
                    this.mOwnEventList.add(event);

        notifyListChange();
    }

    public void replaceParticipationEvents(List<MyCalendarEvent> addList) {
        if (mParticipationEventList == null) this.mParticipationEventList = new ArrayList<>();

        List<String> eventsId = getAllEventId(mParticipationEventList);
        if (addList != null)
            for (MyCalendarEvent event : addList)
                if (!eventsId.contains(event.getEvent_id()))
                    this.mParticipationEventList.add(event);

        notifyListChange();
    }

    public void clear(){
        this.mOwnEventList.clear();
        this.mParticipationEventList.clear();
    }

    /* Set ID as the position number in List returned by getAsCalendarEventList() */
    private void notifyListChange() {
        int i, j = 0;
        if (mOwnEventList != null) {
            j = mOwnEventList.size();
            for (i = 0; i < mOwnEventList.size(); i++)
                mOwnEventList.get(i).setId(i);
        }

        if (mParticipationEventList != null)
            for (i = 0; i < mParticipationEventList.size(); i++, j++)
                mParticipationEventList.get(i).setId(j);
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
                "mOwnEventList=" + mOwnEventList +
                ", mParticipationEventList=" + mParticipationEventList +
                '}';
    }
}
