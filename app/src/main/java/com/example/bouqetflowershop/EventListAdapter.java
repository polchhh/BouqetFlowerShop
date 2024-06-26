package com.example.bouqetflowershop;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.util.ArrayList;
import java.util.Map;

public class EventListAdapter extends ArrayAdapter<ListDataEvent> {
    private ArrayList<ListDataEvent> eventList;
    private Context context;
    private ListView listView;
    Dialog dialog;
    private CalendarHoliday calendarHoliday;
    private int selectedPosition = -1;
    private Map<CalendarDay, EventDecorator> decoratorsMap;
    private MaterialCalendarView calendarView;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final String EVENT_KEY = "Event";
    private final DatabaseReference eventsDatabase = FirebaseDatabase.getInstance().getReference(EVENT_KEY);

    public EventListAdapter(Context context, ArrayList<ListDataEvent> eventList, ListView listView, CalendarHoliday calendarHoliday) {
        super(context, R.layout.event_item, eventList);
        this.context = context;
        this.eventList = eventList;
        this.listView = listView;
        this.calendarHoliday = calendarHoliday;
    }

    public void addEvent(ListDataEvent newEvent) {
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height += convertDpToPx(context, 70);
        listView.setLayoutParams(params);
        eventList.add(newEvent);
        notifyDataSetChanged();
    }

    private int convertDpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.event_item, parent, false);

        TextView dateTextView = view.findViewById(R.id.dateV);
        TextView eventTextView = view.findViewById(R.id.eventV);

        ListDataEvent currentItem = eventList.get(position);

        dateTextView.setText(currentItem.date);
        eventTextView.setText(currentItem.event);

        ImageView showMoreButton = view.findViewById(R.id.deleteEvent);
        dialog = new Dialog(getContext());
        showMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position;
                showDialog();
            }
        });
        notifyDataSetChanged();
        return view;
    }

    private void showDialog() {
        dialog.setContentView(R.layout.dialog_yes_no);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button ok = dialog.findViewById(R.id.btn_yes);
        Button cancel = dialog.findViewById(R.id.btn_no);
        dialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    ListDataEvent eventToRemove = eventList.get(selectedPosition);
                    String dateToRemove = eventList.get(selectedPosition).date;
                    String eventIdToRemove = eventToRemove.id;

                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        if (uid != null && eventIdToRemove != null) {
                            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference("Event").child(uid).child(eventIdToRemove);
                            eventRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        eventList.remove(selectedPosition);
                                        notifyDataSetChanged();
                                        String[] dateParts = dateToRemove.split("[/.]");
                                        int day = Integer.parseInt(dateParts[0]);
                                        int month = Integer.parseInt(dateParts[1]);
                                        int year = Integer.parseInt(dateParts[2]);
                                        CalendarDay mydate = CalendarDay.from(year, month, day);
                                        calendarHoliday.removeDecorator(mydate);
                                        dialog.dismiss();
                                        updateListViewHeight();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void updateListViewHeight() {
        int newHeight = 0;
        for (int i = 0; i < eventList.size(); i++) {
            View listItem = getView(i, null, listView);
            listItem.measure(0, 0);
            newHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = newHeight;
        listView.setLayoutParams(params);
    }

    public void sendImmediateNotification(ListDataEvent event) {
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "EVENT_REMINDER_CHANNEL_ID")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Новое событие добавлено")
                .setContentText(event.getEvent())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("EVENT_REMINDER_CHANNEL_ID", "Event Reminders", NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }
            notificationManager.notify(event.getId().hashCode(), builder.build());
        }
    }
}