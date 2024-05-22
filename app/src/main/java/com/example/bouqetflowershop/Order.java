package com.example.bouqetflowershop;

import android.app.Activity;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
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

import java.util.ArrayList;

public class Order extends Fragment {
    private FragmentOrderBinding binding;
    private ArrayList<BouqetCard> bouqets= new ArrayList<>();
    private CatalogAdapter  catalogAdapter;
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
    //Базы данных
    private FirebaseAuth mAuth;
    private DatabaseReference userCartDatabase;
    private DatabaseReference userDatabase;
    private DatabaseReference addressDatabase;
    private String USER_KEY = "User";
    private String CART_KEY = "Cart";
    private String ADDRESS_KEY = "Address";

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        //binding.footer.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_shoppingCart_to_order));

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
        adapter = new AddressListAdapter(requireContext(), addressList, listView);
        listView.setAdapter(adapter);

        loadCart();
        loadAdressFromDatabase();
        setUpAddressClickListener();
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
                Toast.makeText(getContext(), "Выбранное время: " + selectedTime, Toast.LENGTH_SHORT).show();
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
                        Log.e("ShoppingCart", "Failed to load cart items", error.toException());
                        Toast.makeText(getContext(), "Failed to load cart items", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Log.e("ShoppingCart", "UID is null");
                Toast.makeText(getContext(), "Failed to get user UID", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("ShoppingCart", "Current user is null");
            Toast.makeText(getContext(), "User is not signed in", Toast.LENGTH_SHORT).show();
        }
    }



}