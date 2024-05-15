package com.example.bouqetflowershop;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class AddressListAdapter extends ArrayAdapter<ListDataAdreses> {

    private ArrayList<ListDataAdreses> addressList;
    private Context context;
    private ListView listView;
    Dialog dialog;
    private int selectedPosition = -1; // Переменная для хранения выбранной позиции


    public AddressListAdapter(Context context, ArrayList<ListDataAdreses> addressList,  ListView listView) {
        super(context, R.layout.adress_item, addressList);
        this.context = context;
        this.addressList = addressList;
        this.listView = listView;
    }

    public void addAddress(ListDataAdreses newAddress) {
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height += convertDpToPx(context, 70);
        listView.setLayoutParams(params);
        addressList.add(newAddress);
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
        View view = inflater.inflate(R.layout.adress_item, parent, false);

        TextView cityTextView = view.findViewById(R.id.adressCity);
        TextView streetTextView = view.findViewById(R.id.adressStreet);
        TextView houseNumberTextView = view.findViewById(R.id.adressHouseNumber);
        TextView houseCourposeTextView = view.findViewById(R.id.adressHouseCourpose);
        TextView housePodTextView = view.findViewById(R.id.adressHousePod);
        TextView houseFloorTextView = view.findViewById(R.id.adressHouseFloor);
        TextView houseApartTextView = view.findViewById(R.id.adressHouseApart);

        ListDataAdreses currentItem = addressList.get(position);

        cityTextView.setText(currentItem.city);
        streetTextView.setText(currentItem.street);
        houseNumberTextView.setText(String.valueOf(currentItem.houseNumber));
        houseCourposeTextView.setText("/" + String.valueOf(currentItem.houseCourpose));
        housePodTextView.setText(String.valueOf(currentItem.housePod));
        houseFloorTextView.setText(String.valueOf(currentItem.houseFloor));
        houseApartTextView.setText(String.valueOf(currentItem.houseApart));

        ImageView showMoreButton = view.findViewById(R.id.showMore);
        dialog = new Dialog(getContext());
        showMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = position; // Устанавливаем выбранную позицию
                showDialog();
            }
        });
        notifyDataSetChanged();
        return view;
    }

    // Метод для показа диалога
    private void showDialog() {
        dialog.setContentView(R.layout.dialog_yes_no);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false); // Set dialog to not cancelable
        Button ok = dialog.findViewById(R.id.btn_yes);
        Button cancel = dialog.findViewById(R.id.btn_no);
        dialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPosition != -1) {
                    addressList.remove(selectedPosition);
                    notifyDataSetChanged();
                }
                dialog.dismiss();
                updateListViewHeight(); // Обновить высоту ListView
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
        for (int i = 0; i < addressList.size(); i++) {
            View listItem = getView(i, null, listView);
            listItem.measure(0, 0);
            newHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = newHeight;
        listView.setLayoutParams(params);
    }


}