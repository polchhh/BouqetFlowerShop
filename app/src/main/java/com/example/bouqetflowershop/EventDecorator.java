package com.example.bouqetflowershop;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

public class EventDecorator implements DayViewDecorator {
    private Drawable drawable;
    private final CalendarDay date;

    public EventDecorator(Activity context, CalendarDay date, int resId) {
        this.date = date;
        drawable = ContextCompat.getDrawable(context, resId);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return date != null && day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setBackgroundDrawable(drawable);
    }
}
