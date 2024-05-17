package com.example.bouqetflowershop;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import java.util.HashMap;
import java.util.Map;
import com.example.bouqetflowershop.databinding.FragmentCalendarHolidayBinding;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;

public class CalendarHoliday extends Fragment {
    private Map<CalendarDay, EventDecorator> decoratorsMap = new HashMap<>();
    private Map<CalendarDay, String> eventsMap = new HashMap<>();
    private Dialog dialog;
    private ListView listView;
    private EventListAdapter adapter;
    private ArrayList<ListDataEvent> eventList;
    private EventDecorator currentDecorator;
    private FragmentCalendarHolidayBinding binding;
    String datePattern = "((0[1-9]|[12][0-9]|3[01])[/.](0[1-9]|1[0-2])[/.](19[0-9]{2}|20[0-4][0-9]|2050))";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCalendarHolidayBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // HEADER
        binding.header.goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
        binding.header.headerName.setText("События");
        binding.header.goToCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_calendarHoliday_to_personalCabinet);
            }
        });
        binding.header.goToFavoutites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement action for favorites
            }
        });
        // HEADER END

        dialog = new Dialog(getContext());
        binding.adressTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogEvent();
            }
        });

        listView = view.findViewById(R.id.listViewEvent);
        ArrayList<ListDataEvent> eventArrayList = new ArrayList<>();
        adapter = new EventListAdapter(requireContext(), eventArrayList, listView, this);
        listView.setAdapter(adapter);

        binding.calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String event = eventsMap.get(date);
                if (event != null) {
                    Toast.makeText(getContext(), event, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showDialogEvent() {
        dialog.setContentView(R.layout.dialog_event);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false); // Set dialog to not cancelable

        Button ok = dialog.findViewById(R.id.btn_yes);
        Button cancel = dialog.findViewById(R.id.btn_no);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextData = dialog.findViewById(R.id.editTextData);
                EditText editTextEvent = dialog.findViewById(R.id.editTextEvent);

                boolean hasError = false;

                if (editTextData.getText().toString().isEmpty()) {
                    editTextData.setError("Введите дату dd/MM/yyyy или dd.MM.yyyy");
                    hasError = true;
                } else if (!editTextData.getText().toString().matches(datePattern)) {
                    editTextData.setError("Неправильная дата или формат даты, используйте dd/MM/yyyy или dd.MM.yyyy");
                    hasError = true;
                }

                if (editTextEvent.getText().toString().isEmpty()) {
                    editTextEvent.setError("Введите название");
                    hasError = true;
                }

                if (hasError) {
                    Toast.makeText(getContext(), "Заполните все поля, пожалуйста!", Toast.LENGTH_LONG).show();
                    return;
                }

                ListDataEvent newEvent = new ListDataEvent(
                        editTextData.getText().toString(),
                        editTextEvent.getText().toString()
                );
                adapter.addEvent(newEvent);

                String[] dateParts = editTextData.getText().toString().split("[/.]");
                int day = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]);
                int year = Integer.parseInt(dateParts[2]);
                Log.d("MyLog", String.valueOf(day) + String.valueOf(month) + String.valueOf(year));
                CalendarDay mydate = CalendarDay.from(year, month, day);
                EventDecorator eventDecorator = new EventDecorator(getActivity(), mydate, R.drawable.ellipse); // R.drawable.circle_background - это ваш ресурс для отображения точки
                currentDecorator = eventDecorator;
                binding.calendarView.addDecorator(eventDecorator);
                decoratorsMap.put(mydate, eventDecorator);
                eventsMap.put(mydate, editTextEvent.getText().toString());
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void removeDecorator(CalendarDay date) {
        EventDecorator decorator = decoratorsMap.remove(date);
        if (decorator != null) {
            binding.calendarView.removeDecorator(decorator);
            eventsMap.remove(date);
        }
    }
}
