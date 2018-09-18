package com.usal.jorgeav.sportapp.data.calendarevent;

import com.github.tibolte.agendacalendarview.models.CalendarEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase envoltorio para la lista de partidos del calendario.
 *
 * <p>Mantiene la lista de los partidos que deben mostrarse en el calendario y metodos para
 * consultarla y controlarla
 *
 * @see
 * <a href= "https://github.com/Tibolte/AgendaCalendarView/tree/master/agendacalendarview/src/main/java/com/github/tibolte/agendacalendarview">
 *     AgendaCalendarView implementation
 * </a>
 */
public class MyCalendarEventList {
    /**
     * Coleccion de partidos del calendario
     */
    private List<MyCalendarEvent> mEventList;

    /**
     * Inicializacion de la lista
     *
     * @param eventList lista inicial de partidos
     */
    public MyCalendarEventList(List<MyCalendarEvent> eventList) {
        this.mEventList = eventList;
        notifyListChange();
    }

    /**
     * Getter para la lista de partidos
     * @return lista de partidos
     */
    public List<CalendarEvent> getAsCalendarEventList() {
        List<CalendarEvent> result = new ArrayList<>();
        if (this.mEventList != null)
                result.addAll(this.mEventList);
        return result;
    }

    /**
     * Devuelve el partido situado en una posicion especifica
     * @param position posicion indicada
     * @return item de la lista de partidos en esa posicion
     */
    public MyCalendarEvent getItemAtPosition(int position) {
        if (mEventList != null && position >= 0 && position < mEventList.size())
            return mEventList.get(position);
        return null;
    }

    /**
     * De todos los partidos de la lista, busca la fecha mas baja
     * @return la fecha mas baja en milisegundos
     */
    public Long getMinDateInMillis() {
        Long min = null;
        if (mEventList != null)
            for(MyCalendarEvent event : mEventList)
                min = (min == null || event.getStartTime().getTimeInMillis() < min) ?
                        event.getStartTime().getTimeInMillis() : min;
        return min;
    }

    /**
     * De todos los partidos de la lista, busca la fecha mas alta
     * @return la fecha mas alta en milisegundos
     */
    public Long getMaxDateInMillis() {
        Long max = null;
        if (mEventList != null)
            for(MyCalendarEvent event : mEventList)
                max = (max == null || event.getStartTime().getTimeInMillis() > max) ?
                        event.getStartTime().getTimeInMillis() : max;
        return max;
    }

    /**
     * Actualiza la lista de partidos con otra lista de partidos
     * @param list partidos nuevos
     */
    public void replaceEvents(List<MyCalendarEvent> list) {
        this.mEventList = new ArrayList<>(list);
        notifyListChange();
    }

    /**
     * Limpia la lista
     */
    public void clear(){
        if (this.mEventList != null)
            this.mEventList.clear();
    }

    /**
     * Para cada {@link MyCalendarEvent} de la lista, asocia a su {@link MyCalendarEvent#mId}
     * el numero correspondiente a la posicion que ocupa en la lista
     */
    private void notifyListChange() {
        if (mEventList != null)
            for (int i = 0; i < mEventList.size(); i++)
                mEventList.get(i).setId(i);
    }

    @Override
    public String toString() {
        return "MyCalendarEventList{" +
                "mEventList=" + mEventList +
                '}';
    }
}
