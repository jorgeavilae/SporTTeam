package com.usal.jorgeav.sportapp.data.calendarevent;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.tibolte.agendacalendarview.render.EventRenderer;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Se encarga de renderizar cada partido en su celda correspondiente del calendario
 * <p></p>
 * Mas informacion en (Github) {@link
 * <a href= "https://github.com/Tibolte/AgendaCalendarView/tree/master/agendacalendarview/src/main/java/com/github/tibolte/agendacalendarview">
 *     AgendaCalendarView implementation
 * </a>}
 *  y {@link
 * <a href= "https://github.com/Tibolte/AgendaCalendarView/blob/master/agendacalendarview/src/main/java/com/github/tibolte/agendacalendarview/render/EventRenderer.java">
 *     EventRenderer implementation
 * </a>}
 */
public class MyEventRenderer extends EventRenderer<MyCalendarEvent> {

    /**
     * Utiliza el {@link MyCalendarEvent} proporcionado para emplazar sus datos en los elementos
     * de la View
     * @param view celda del calendario
     * @param event partido representado
     */
    @Override
    public void render(View view, MyCalendarEvent event) {
        ImageView imageViewEventSport = (ImageView) view.findViewById(R.id.event_item_calendar_sport);
        TextView textViewEventPlace = (TextView) view.findViewById(R.id.event_item_calendar_place);
        TextView textViewEventName = (TextView) view.findViewById(R.id.event_item_calendar_name);
        ImageView imageViewEventPlayer = (ImageView) view.findViewById(R.id.event_item_calendar_player);

        imageViewEventSport.setImageResource(Utiles.getSportIconFromResource(event.getSport_id()));
        textViewEventPlace.setText(event.getField_name());
        textViewEventName.setText(event.getTitle());
        int playerIcon = Utiles.getPlayerIconFromResource(event.getEmpty_players(), event.getTotal_players());
        if (playerIcon != -1) imageViewEventPlayer.setImageResource(playerIcon);
    }

    /**
     *
     * @return archivo de layout que representa cada celda
     */
    @Override
    public int getEventLayout() {
        return R.layout.event_item_calendar;
    }

    /**
     *
     * @return tipo de partido que se representa en la celda ({@link MyCalendarEvent})
     */
    @Override
    public Class<MyCalendarEvent> getRenderType() {
        return MyCalendarEvent.class;
    }
}
