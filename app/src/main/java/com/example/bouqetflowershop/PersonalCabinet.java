package com.example.bouqetflowershop;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bouqetflowershop.databinding.FragmentHeaderBinding;
import com.example.bouqetflowershop.databinding.FragmentPersonalCabinetBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class PersonalCabinet extends Fragment {
    private FragmentPersonalCabinetBinding binding;
    Dialog dialog;
    private ListView listView;
    private AddressListAdapter adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    private StorageReference mStorageRef;
    private Uri uploadUri;
    private DatabaseReference addressDatabase;
    private String ADDRESS_KEY = "Address";
    private String USER_KEY = "User";
    EditText cityEditText;
    EditText streetEditText;
    EditText numberHouseEditText;
    EditText coorpuseHouseEditText;
    EditText houseApartEditText;
    EditText housePodEditText;
    EditText houseFloorEditText;
    TextView textViewNumberOfOrders;
    TextView textViewNumberOfOrdersAdmin;
    TextView textViewSalary;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalCabinetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.header.goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
        binding.header.headerName.setText("Кабинет");
        binding.header.goToCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        binding.header.goToFavoutites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_personalCabinet_to_favourites);
            }
        });
        binding.footer.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_personalCabinet_to_shoppingCart));

        dialog = new Dialog(getContext());
        textViewNumberOfOrders = binding.textViewNumberOfOrders;
        textViewNumberOfOrdersAdmin = binding.textViewNumberOfOrdersAdmin;
        textViewSalary = binding.textViewSalary;
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference(USER_KEY);
        addressDatabase = FirebaseDatabase.getInstance().getReference(ADDRESS_KEY);
        mStorageRef = FirebaseStorage.getInstance().getReference("UserImage");
        cityEditText = dialog.findViewById(R.id.editTextCity);
        streetEditText = dialog.findViewById(R.id.editTextStreet);
        numberHouseEditText = dialog.findViewById(R.id.editTextNumberHouse);
        coorpuseHouseEditText = dialog.findViewById(R.id.editTextCoorpuseHouse);
        houseApartEditText = dialog.findViewById(R.id.editTextHouseApart);
        housePodEditText = dialog.findViewById(R.id.editTextHousePod);
        housePodEditText = dialog.findViewById(R.id.editTextHousePod);
        houseFloorEditText = dialog.findViewById(R.id.editTextHouseFloor);

        // Проверка, является ли пользователь администратором
        checkIfAdmin(new PersonalCabinet.AdminCheckCallback() {
            @Override
            public void onCheckCompleted(boolean isAdmin) {
                if (isAdmin) {
                    binding.header.goToFavoutites.setVisibility(View.INVISIBLE);
                    binding.userA.setVisibility(View.VISIBLE);
                    binding.userM.setVisibility(View.GONE);
                    binding.footer.setVisibility(View.GONE);
                }
            }
        });
        loadNumberOfOrders();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        binding.shapeableImageView.setImageURI(uri);
                        uploadImage();
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(getContext(), ImagePicker.getError(result.getData()), Toast.LENGTH_SHORT).show();
                    } else {
                        //Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

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

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(PersonalCabinet.this)
                        .crop()
                        .compress(1024)
                        .maxResultSize(1080, 1080)
                        .createIntent(intent -> {
                            imagePickerLauncher.launch(intent);
                            return null;
                        });
            }
        });

        binding.myBounse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.myEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_personalCabinet_to_calendarHoliday);
            }
        });
        binding.myOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textViewNumberOfOrders.getText().toString().equals("0")){
                    Toast.makeText(getContext(),"У вас пока нет заказов!",Toast.LENGTH_LONG).show();
                }
                else{
                    Navigation.findNavController(getView()).navigate(R.id.action_personalCabinet_to_ordersHistory);
                }
            }
        });
        binding.orderTitle.setOnClickListener(v ->Navigation.findNavController(getView()).navigate(R.id.action_personalCabinet_to_ordersHistory))
        ;
        // Получение текущего UID пользователя
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            userDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            if (user.imageUri != null) {
                                Picasso.get().load(user.getImageUri()).into(binding.shapeableImageView);
                            }
                            binding.textViewPersonalCabName.setText(user.getName());
                            binding.textViewPersonalCabEmail.setText(user.getEmail());
                            binding.textViewPersonalCabPhone.setText(user.getPhone_number());
                            binding.textViewBounces.setText(String.valueOf(user.getBonuses()));
                            }
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        loadAdressFromDatabase();
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

    private void uploadImage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            ImageView image = binding.shapeableImageView;
            Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] byteArray = baos.toByteArray();
            final StorageReference mRef = mStorageRef.child(uid);
            UploadTask up = mRef.putBytes(byteArray);
            Task<Uri> task = up.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    return mRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    uploadUri = task.getResult();
                    userDatabase.child(uid).child("imageUri").setValue(uploadUri.toString());
                }
            });
        }
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

    private void loadNumberOfOrders() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference ordersDatabase = FirebaseDatabase.getInstance().getReference("Orders");

            checkIfAdmin(new AdminCheckCallback() {
                @Override
                public void onCheckCompleted(boolean isAdmin) {
                    if (isAdmin) {
                        ordersDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long totalOrders = 0;
                                double totalCost = 0.0;
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    for (DataSnapshot orderSnapshot : userSnapshot.getChildren()) {
                                        Double itogCost = orderSnapshot.child("itogCost").getValue(Double.class);
                                        if (itogCost != null) {
                                            totalOrders++;
                                            totalCost += itogCost;
                                        }
                                    }
                                }
                                textViewNumberOfOrdersAdmin.setText(String.valueOf(totalOrders));
                                textViewSalary.setText(String.format("%.2f", totalCost));
                                userDatabase.child(uid).child("bonuses").setValue(totalCost);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Не удалось загрузить количество заказов", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        ordersDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                long numberOfOrders = snapshot.getChildrenCount();
                                textViewNumberOfOrders.setText(String.valueOf(numberOfOrders));
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(getContext(), "Не удалось загрузить количество заказов", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }


    private void checkIfAdmin(final PersonalCabinet.AdminCheckCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            userDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isAdmin = false;
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            isAdmin = user.getIs_admin();
                        }
                    }
                    callback.onCheckCompleted(isAdmin);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onCheckCompleted(false);
                }
            });
        } else {
            callback.onCheckCompleted(false);
        }
    }


    interface AdminCheckCallback {
        void onCheckCompleted(boolean isAdmin);
    }
}
