package com.usal.jorgeav.sportapp.data.calendarevent;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.tibolte.agendacalendarview.render.EventRenderer;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.utils.Utiles;

/**
 * Created by Jorge Avila on 28/07/2017.
 */

public class MyEventRenderer extends EventRenderer<MyCalendarEvent> {

    @Override
    public void render(View view, MyCalendarEvent event) {
        ConstraintLayout container = (ConstraintLayout) view.findViewById(R.id.event_item_calendar_container);
        ImageView imageViewEventSport = (ImageView) view.findViewById(R.id.event_item_calendar_sport);
        TextView textViewEventPlace = (TextView) view.findViewById(R.id.event_item_calendar_place);
        TextView textViewEventName = (TextView) view.findViewById(R.id.event_item_calendar_name);
        ImageView imageViewEventPlayer = (ImageView) view.findViewById(R.id.event_item_calendar_player);

        int sportIcon = MyApplication.getAppContext().getResources()
                .getIdentifier(event.getSport_id(), "drawable", MyApplication.getAppContext().getPackageName());
        imageViewEventSport.setImageResource(sportIcon);
        textViewEventPlace.setText(event.getField_name());
        textViewEventName.setText(event.getTitle());
        int playerIcon = Utiles.getPlayerIconFromResource(event.getEmpty_players(), event.getTotal_players());
        if (playerIcon != -1)
            imageViewEventPlayer.setImageResource(playerIcon);
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
