package com.example.bouqetflowershop;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.bouqetflowershop.databinding.FragmentHeaderBinding;
import com.example.bouqetflowershop.databinding.FragmentPersonalCabinetBinding;

import java.util.ArrayList;

public class PersonalCabinet extends Fragment {
    private FragmentPersonalCabinetBinding binding;
    Dialog dialog;
    private ListView listView;
    private AddressListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPersonalCabinetBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
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
        binding.header.headerName.setText("Кабинет");
        binding.header.goToCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.header.goToFavoutites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // HEADER END

        dialog = new Dialog(getContext());
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
    }

    private void showDialogAdress() {
        dialog.setContentView(R.layout.dialog_adresses);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false); // Set dialog to not cancelable

        Button ok = dialog.findViewById(R.id.btn_yes);
        Button cancel = dialog.findViewById(R.id.btn_no);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText cityEditText = dialog.findViewById(R.id.editTextCity);
                EditText streetEditText = dialog.findViewById(R.id.editTextStreet);
                EditText numberHouseEditText = dialog.findViewById(R.id.editTextNumberHouse);
                EditText coorpuseHouseEditText = dialog.findViewById(R.id.editTextCoorpuseHouse);
                EditText houseApartEditText = dialog.findViewById(R.id.editTextHouseApart);
                EditText housePodEditText = dialog.findViewById(R.id.editTextHousePod);
                EditText houseFloorEditText = dialog.findViewById(R.id.editTextHouseFloor);

                if (cityEditText.getText().toString().isEmpty() ||
                        streetEditText.getText().toString().isEmpty() ||
                        numberHouseEditText.getText().toString().isEmpty() ||
                        houseApartEditText.getText().toString().isEmpty() ||
                        housePodEditText.getText().toString().isEmpty() ||
                        houseFloorEditText.getText().toString().isEmpty()) {
                    if (cityEditText.getText().toString().isEmpty()){
                        cityEditText.setError("Введите город");
                    }
                    if (streetEditText.getText().toString().isEmpty()){
                        streetEditText.setError("Введите улицу");
                    }
                    if (numberHouseEditText.getText().toString().isEmpty()){
                        numberHouseEditText.setError("Введите номер дома");
                    }
                    if (houseApartEditText.getText().toString().isEmpty()){
                        houseApartEditText.setError("Введите номер квартиры");
                    }
                    if (housePodEditText.getText().toString().isEmpty()){
                        housePodEditText.setError("Введите номер подъезда");
                    }
                    if (houseFloorEditText.getText().toString().isEmpty()){
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
}