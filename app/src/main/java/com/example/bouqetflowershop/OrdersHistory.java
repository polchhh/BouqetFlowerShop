package com.example.bouqetflowershop;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.bouqetflowershop.databinding.FragmentOrdersHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class OrdersHistory extends Fragment {
    private FragmentOrdersHistoryBinding binding;
    private ListView listView;
    // Базы данных
    private DatabaseReference ordersDatabase;
    private String ORDERS_KEY = "Orders";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrdersHistoryBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // HEADER
        binding.header.goBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.header.headerName.setText("Заказы");
        binding.header.goToCabinet.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_ordersHistory_to_personalCabinet));
        binding.header.goToFavoutites.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_ordersHistory_to_favourites));
        // HEADER END
        binding.footer.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_ordersHistory_to_shoppingCart));

        listView = binding.listViewOrders;
        ArrayList<OrderData> orderDataArrayList = new ArrayList<>();

        // Получаем ссылку на базу данных заказов
        ordersDatabase = FirebaseDatabase.getInstance().getReference(ORDERS_KEY);

        // Проверяем, является ли текущий пользователь администратором
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("User");
            userDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Boolean isAdmin = snapshot.child("is_admin").getValue(Boolean.class);
                        if (isAdmin != null && isAdmin) {
                            // Если текущий пользователь администратор, загружаем все заказы всех пользователей
                            ordersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                        for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                                            OrderData orderData = orderSnapshot.getValue(OrderData.class);
                                            if (orderData != null) {
                                                orderDataArrayList.add(orderData);
                                            }
                                        }
                                    }
                                    // Создаем адаптер и устанавливаем его для ListView
                                    OrdersAdapter adapter = new OrdersAdapter(getContext(), orderDataArrayList);
                                    listView.setAdapter(adapter);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        } else {
                            // Если текущий пользователь не администратор, загружаем только его заказы
                            ordersDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot orderSnapshot : snapshot.getChildren()) {
                                        OrderData orderData = orderSnapshot.getValue(OrderData.class);
                                        if (orderData != null) {
                                            orderDataArrayList.add(orderData);
                                        }
                                    }
                                    // Создаем адаптер и устанавливаем его для ListView
                                    OrdersAdapter adapter = new OrdersAdapter(getContext(), orderDataArrayList);
                                    listView.setAdapter(adapter);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}