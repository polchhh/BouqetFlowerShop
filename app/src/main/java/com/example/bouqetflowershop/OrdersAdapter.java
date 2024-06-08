package com.example.bouqetflowershop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class OrdersAdapter extends ArrayAdapter<OrderData> {
    private Context mContext;
    private ArrayList<OrderData> mOrders;

    public OrdersAdapter(@NonNull Context context, ArrayList<OrderData> orders) {
        super(context, 0, orders);
        mContext = context;
        mOrders = orders;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.order_item, parent, false);
        }

        OrderData currentOrder = mOrders.get(position);

        TextView textViewDate = listItem.findViewById(R.id.textViewDate);
        textViewDate.setText(currentOrder.getSelectedDate());

        TextView textViewTime = listItem.findViewById(R.id.textViewTime);
        textViewTime.setText(currentOrder.getSelectedTime());

        TextView textViewNames = listItem.findViewById(R.id.textViewNames);
        textViewNames.setText(currentOrder.getBouquetNames());


        TextView adressCity1 = listItem.findViewById(R.id.adressCity1);
        TextView adressStreet1 = listItem.findViewById(R.id.adressStreet1);
        TextView adressHouseNumber1 = listItem.findViewById(R.id.adressHouseNumber1);
        TextView adressHouseCourpose1 = listItem.findViewById(R.id.adressHouseCourpose1);
        TextView adressHousePod1 = listItem.findViewById(R.id.adressHousePod1);
        TextView adressHouseFloor1 = listItem.findViewById(R.id.adressHouseFloor1);
        TextView adressHouseApart1 = listItem.findViewById(R.id.adressHouseApart1);

        adressCity1.setText(currentOrder.getAddress().getCity());
        adressStreet1.setText(currentOrder.getAddress().getStreet());
        adressHouseNumber1.setText(currentOrder.getAddress().getNumberHouse());
        adressHouseCourpose1.setText(currentOrder.getAddress().getCoorpuseHouse());
        adressHousePod1.setText(currentOrder.getAddress().getHousePod());
        adressHouseFloor1.setText(currentOrder.getAddress().getHouseFloor());
        adressHouseApart1.setText(currentOrder.getAddress().getHouseApart());

        TextView textViewCost = listItem.findViewById(R.id.textViewCost1);
        textViewCost.setText(String.valueOf(currentOrder.getItogCost()));

        return listItem;
    }
}