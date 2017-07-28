package com.usal.jorgeav.sportapp.data.calendarevent;

import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.tibolte.agendacalendarview.render.EventRenderer;
import com.usal.jorgeav.sportapp.MyApplication;
import com.usal.jorgeav.sportapp.R;

import java.util.Locale;

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
        TextView textViewEventTotal = (TextView) view.findViewById(R.id.event_item_calendar_total);
        TextView textViewEventEmpty = (TextView) view.findViewById(R.id.event_item_calendar_empty);

        int sportIcon = MyApplication.getAppContext().getResources()
                .getIdentifier(event.getSport_id(), "drawable", MyApplication.getAppContext().getPackageName());
        imageViewEventSport.setImageResource(sportIcon);
        textViewEventPlace.setText(event.getField_name());
        textViewEventName.setText(event.getTitle());
        textViewEventTotal.setText(String.format(Locale.getDefault(), "%2d", event.getTotal_players()));
        textViewEventEmpty.setText(String.format(Locale.getDefault(), "%2d", event.getEmpty_players()));
    }

    @Override
    public int getEventLayout() {
        /* This layout should set marginLeft to not be overlap by dayView */
        /* https://github.com/Tibolte/AgendaCalendarView/issues/3
        * "In AgendaEventView's layout, the marginLeft is 75dp(paddingLeft 15dp + marginLeft 55dp + marginLeft 5dp)."
        */
        return R.layout.event_item_calendar;
    }

    @Override
    public Class<MyCalendarEvent> getRenderType() {
        return MyCalendarEvent.class;
    }
}
