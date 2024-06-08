package com.example.bouqetflowershop;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bouqetflowershop.databinding.FragmentOrderBinding;
import com.example.bouqetflowershop.databinding.FragmentPersonalCabinetBinding;
import com.example.bouqetflowershop.databinding.FragmentShoppingCartBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

public class Order extends Fragment {
    private FragmentOrderBinding binding;
    private ArrayList<BouqetCard> bouqets = new ArrayList<>();
    private CatalogAdapter catalogAdapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Dialog dialog;
    private TextView totalCost;
    private LinearLayout selectedTimeSlot = null;
    private ListView listView;
    private AddressListAdapter adapter;
    private Uri uploadUri;
    private EditText cityEditText;
    private EditText streetEditText;
    private EditText numberHouseEditText;
    private EditText coorpuseHouseEditText;
    private EditText houseApartEditText;
    private EditText housePodEditText;
    private EditText houseFloorEditText;
    private EditText cardNumber;
    private EditText monthYearNumber;
    private EditText cvcNumber;
    private LinearLayout date;
    private TextView selectedDateTextView;
    private LinearLayout footer;
    private Switch bonusSwitch;
    private Integer prev_bounces;
    private Double prev_total;
    //Базы данных
    private FirebaseAuth mAuth;
    private DatabaseReference userCartDatabase;
    private DatabaseReference userDatabase;
    private DatabaseReference addressDatabase;
    private DatabaseReference ordersDatabase;
    private String USER_KEY = "User";
    private String CART_KEY = "Cart";
    private String ADDRESS_KEY = "Address";
    private String ORDERS_KEY = "Orders";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(getContext(), ImagePicker.getError(result.getData()), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Запрос отменен", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // HEADER
        binding.header.goBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.header.headerName.setText("Заказ");
        binding.header.goToCabinet.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_order_to_personalCabinet));
        binding.header.goToFavoutites.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_order_to_favourites));
        // HEADER END

        catalogAdapter = new CatalogAdapter(getContext(), bouqets, "корзина", imagePickerLauncher, this);
        GridView gridView = binding.gridViewCart;
        gridView.setAdapter(catalogAdapter);

        dialog = new Dialog(getContext());
        cityEditText = dialog.findViewById(R.id.editTextCity);
        streetEditText = dialog.findViewById(R.id.editTextStreet);
        numberHouseEditText = dialog.findViewById(R.id.editTextNumberHouse);
        coorpuseHouseEditText = dialog.findViewById(R.id.editTextCoorpuseHouse);
        houseApartEditText = dialog.findViewById(R.id.editTextHouseApart);
        housePodEditText = dialog.findViewById(R.id.editTextHousePod);
        houseFloorEditText = dialog.findViewById(R.id.editTextHouseFloor);
        totalCost = binding.totalCost;

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference(USER_KEY);
        userCartDatabase = FirebaseDatabase.getInstance().getReference(CART_KEY);
        addressDatabase = FirebaseDatabase.getInstance().getReference(ADDRESS_KEY);
        ordersDatabase = FirebaseDatabase.getInstance().getReference(ORDERS_KEY);

        setUpTimeSlotClickListener(binding.time1);
        setUpTimeSlotClickListener(binding.time2);
        setUpTimeSlotClickListener(binding.time3);
        setUpTimeSlotClickListener(binding.time4);

        binding.adressTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAdress();
            }
        });

        listView = view.findViewById(R.id.listViewAdresses);
        ArrayList<ListDataAdreses> addressList = new ArrayList<>();
        adapter = new AddressListAdapter(requireContext(), addressList, listView, this);
        listView.setAdapter(adapter);

        cardNumber = binding.editTextNumberCard;
        monthYearNumber = binding.editTextNumberMonthYear;
        cvcNumber = binding.editTextCVC;

        date = binding.changeDate;
        selectedDateTextView = binding.textViewDate;
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        bonusSwitch = binding.bonusSwitch;
        // Получние текущего UID пользователя
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            userDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            prev_bounces = Integer.parseInt(String.valueOf(user.getBonuses()));
                            binding.textViewBounces.setText(String.valueOf(user.getBonuses()));
                        }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        bonusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    prev_total = Double.parseDouble(totalCost.getText().toString());
                    int bonuses = Integer.parseInt(binding.textViewBounces.getText().toString());
                    double total = Double.parseDouble(totalCost.getText().toString());
                    // Проверяем, достаточно ли бонусов для списания
                    if (bonuses >= prev_total) {
                        // Вычитаем бонусы из общей суммы
                        total = 0;
                        bonuses -= prev_total; // Исправлено на prev_total
                    } else {
                        // Если бонусов недостаточно, вычитаем только их и остаток добавляем к общей сумме
                        total -= bonuses;
                        bonuses = 0;
                    }
                    // Обновляем отображение общей суммы и бонусов
                    totalCost.setText(String.valueOf(total));
                    binding.textViewBounces.setText(String.valueOf(bonuses));
                }
                else {
                    totalCost.setText(String.valueOf(prev_total));
                    binding.textViewBounces.setText(String.valueOf(prev_bounces));
                }
            }
        });

        footer = binding.footer;
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bonusSwitch.isChecked()) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String uid = currentUser.getUid();
                        userDatabase.child(uid).child("bonuses").setValue(Integer.parseInt(binding.textViewBounces.getText().toString()));
                    }
                }
                if (isOrderDataValid()) {
                    OrderData orderData = collectOrderData();
                    uploadOrder(orderData);
                    Navigation.findNavController(v).navigate(R.id.action_order_to_successfulPayment);
                } else {
                    //Toast.makeText(getContext(), "Выберите и заполните все поля!", Toast.LENGTH_LONG).show();
                }
            }
        });


        loadCart();
        loadAdressFromDatabase();
    }


    private void uploadOrder(OrderData orderData) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference newOrderRef = ordersDatabase.child(uid).push();
            String orderId = newOrderRef.getKey();
            orderData.setId(orderId);
            orderData.setItogCost(Double.parseDouble(totalCost.getText().toString()));
            newOrderRef.setValue(orderData)
                    .addOnSuccessListener(aVoid -> {
                        addBonusesToUser(uid, orderData.getItogCost());
                        clearCart(uid);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Ошибка. Повторите снова", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void addBonusesToUser(String uid, double totalCost) {
        double bonus = totalCost * 0.03;

        DatabaseReference userRef = userDatabase.child(uid);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        double newBonuses = user.getBonuses() + bonus;
                        userRef.child("bonuses").setValue(newBonuses);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Ошибка базы данных", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearCart(String uid) {
        userCartDatabase.child(uid).removeValue()
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {

                });
    }

    private boolean isOrderDataValid() {
        if (selectedDateTextView.getText().toString().equals("Выбрать дату")) {
            Toast.makeText(getContext(), "Выберите дату!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (selectedTimeSlot == null) {
            Toast.makeText(getContext(), "Выберите время!", Toast.LENGTH_LONG).show();
            return false;
        }
        if (adapter.getSelectedPosition() == -1) {
            Toast.makeText(getContext(), "Выберите адрес!", Toast.LENGTH_LONG).show();
            return false;
        }
        String cardNumberText = cardNumber.getText().toString().trim();
        if ( cardNumberText.isEmpty() || !cardNumberText.matches("^\\d{16}$")) {
            cardNumber.setError("Введите номер карты (16 цифр)");
            return false;
        }
        String monthYearText = monthYearNumber.getText().toString().trim();
        if (monthYearText.isEmpty() || !monthYearText.matches("^(0[1-9]|1[0-2])/(2[4-9]|[3-9][0-9])$")) {
            monthYearNumber.setError("Введите реальный срок действия карты (ММ/ГГ)");
            return false;
        }
        String cvcText = cvcNumber.getText().toString().trim();
        if (cvcText.isEmpty() || !cvcText.matches("^\\d{3}$")) {
            cvcNumber.setError("Введите код защиты (3 цифры на обороте)");
            return false;
        }
        return true;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        date.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.rounded_selected));
                        String formattedDate = String.format("%02d.%02d.%d", dayOfMonth, (month + 1), year);
                        selectedDateTextView.setText(formattedDate);
                    }
                },
                year, month, day
        );

        // Установка минимальной даты на сегодняшний день
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        // Установка максимальной даты на 2 недели от сегодняшнего дня
        calendar.add(Calendar.DAY_OF_MONTH, 14);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void showDialogAdress() {
        dialog.setContentView(R.layout.dialog_adresses);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        cityEditText = dialog.findViewById(R.id.editTextCity);
        streetEditText = dialog.findViewById(R.id.editTextStreet);
        numberHouseEditText = dialog.findViewById(R.id.editTextNumberHouse);
        coorpuseHouseEditText = dialog.findViewById(R.id.editTextCoorpuseHouse);
        houseApartEditText = dialog.findViewById(R.id.editTextHouseApart);
        housePodEditText = dialog.findViewById(R.id.editTextHousePod);
        houseFloorEditText = dialog.findViewById(R.id.editTextHouseFloor);

        Button ok = dialog.findViewById(R.id.btn_yes);
        Button cancel = dialog.findViewById(R.id.btn_no);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cityEditText.getText().toString().isEmpty() ||
                        streetEditText.getText().toString().isEmpty() ||
                        numberHouseEditText.getText().toString().isEmpty() ||
                        houseApartEditText.getText().toString().isEmpty() ||
                        housePodEditText.getText().toString().isEmpty() ||
                        houseFloorEditText.getText().toString().isEmpty()) {
                    if (cityEditText.getText().toString().isEmpty()) {
                        cityEditText.setError("Введите город");
                    }
                    if (streetEditText.getText().toString().isEmpty()) {
                        streetEditText.setError("Введите улицу");
                    }
                    if (numberHouseEditText.getText().toString().isEmpty()) {
                        numberHouseEditText.setError("Введите номер дома");
                    }
                    if (houseApartEditText.getText().toString().isEmpty()) {
                        houseApartEditText.setError("Введите номер квартиры");
                    }
                    if (housePodEditText.getText().toString().isEmpty()) {
                        housePodEditText.setError("Введите номер подъезда");
                    }
                    if (houseFloorEditText.getText().toString().isEmpty()) {
                        houseFloorEditText.setError("Введите номер этажа");
                    }
                    Toast.makeText(getContext(), "Заполните все поля, пожалуйста!", Toast.LENGTH_LONG).show();
                    return;
                }

                ListDataAdreses newAddress = new ListDataAdreses(
                        cityEditText.getText().toString(),
                        streetEditText.getText().toString(),
                        numberHouseEditText.getText().toString(),
                        coorpuseHouseEditText.getText().toString(),
                        houseApartEditText.getText().toString(),
                        housePodEditText.getText().toString(),
                        houseFloorEditText.getText().toString()
                );
                adapter.addAddress(newAddress);
                uploadAdress();
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

    private void uploadAdress() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference newAddressRef = addressDatabase.child(uid).push();
            String id = newAddressRef.getKey();
            ListDataAdreses newAddress = new ListDataAdreses(cityEditText.getText().toString(), streetEditText.getText().toString(), numberHouseEditText.getText().toString(), coorpuseHouseEditText.getText().toString(), housePodEditText.getText().toString(), houseFloorEditText.getText().toString(), houseApartEditText.getText().toString(), id);
            newAddressRef.setValue(newAddress);
        }
    }

    private void loadAdressFromDatabase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            addressDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                        ListDataAdreses adress = dateSnapshot.getValue(ListDataAdreses.class);
                        if (adress != null) {
                            adapter.addAddress(adress);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void setUpTimeSlotClickListener(LinearLayout timeSlot) {
        timeSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedTimeSlot != null) {
                    selectedTimeSlot.setSelected(false);
                }
                timeSlot.setSelected(true);
                selectedTimeSlot = timeSlot;
                // Получаем текст времени из выбранного временного слота
                TextView timeTextView = (TextView) timeSlot.getChildAt(0);
                String selectedTime = timeTextView.getText().toString();
            }
        });
    }

    public void calculateTotalCost() {
        double total = 0;
        for (BouqetCard product : bouqets) {
            double price = Double.parseDouble(product.getPrice());
            int quantity = product.getNumber();
            total += price * quantity;
        }
        totalCost.setText(String.valueOf(total));
    }


    private void loadCart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if (uid != null) {
                userCartDatabase.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bouqets.clear();
                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            BouqetCard bouqet = itemSnapshot.getValue(BouqetCard.class);
                            if (bouqet != null) {
                                bouqets.add(bouqet);
                            }
                        }
                        catalogAdapter.notifyDataSetChanged();
                        calculateTotalCost();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
    }

    public OrderData collectOrderData() {
        OrderData orderData = new OrderData();

        orderData.setBouqets(bouqets);

        String selectedDate = selectedDateTextView.getText().toString();
        orderData.setSelectedDate(selectedDate);

        if (selectedTimeSlot != null) {
            TextView timeTextView = (TextView) selectedTimeSlot.getChildAt(0);
            String selectedTime = timeTextView.getText().toString();
            orderData.setSelectedTime(selectedTime);
        }

        // Получение выбранного адреса из адаптера
        ListDataAdreses selectedAddress = adapter.getSelectedAddress();
        if (selectedAddress != null) {
            OrderData.Address address = new OrderData.Address();
            address.setCity(selectedAddress.getCity());
            address.setStreet(selectedAddress.getStreet());
            address.setNumberHouse(selectedAddress.getHouseNumber());
            address.setCoorpuseHouse(selectedAddress.getHouseCourpose());
            address.setHouseApart(selectedAddress.getHouseApart());
            address.setHousePod(selectedAddress.getHousePod());
            address.setHouseFloor(selectedAddress.getHouseFloor());
            orderData.setAddress(address);
        }

        /*
        OrderData.CardDetails cardDetails = new OrderData.CardDetails();
        cardDetails.setCardNumber(cardNumber.getText().toString());
        cardDetails.setMonthYear(monthYearNumber.getText().toString());
        cardDetails.setCvc(cvcNumber.getText().toString());
        orderData.setCardDetails(cardDetails);
        */
        return orderData;
    }
}