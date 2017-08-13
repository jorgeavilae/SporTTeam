package com.usal.jorgeav.sportapp.data.calendarevent;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.tibolte.agendacalendarview.render.EventRenderer;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.utils.Utiles;

public class MyEventRenderer extends EventRenderer<MyCalendarEvent> {

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

    @Override
    public int getEventLayout() {
        return R.layout.event_item_calendar;
    }

    @Override
    public Class<MyCalendarEvent> getRenderType() {
        return MyCalendarEvent.class;
    }
}
