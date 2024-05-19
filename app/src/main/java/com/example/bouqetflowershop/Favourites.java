package com.example.bouqetflowershop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;


public class Favourites extends Fragment {
    private com.example.bouqetflowershop.databinding.FragmentCatalogBinding binding;
    ArrayList<BouqetCard> bouqets= new ArrayList<>();
    CatalogAdapter catalogAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.example.bouqetflowershop.databinding.FragmentCatalogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        fillData();
        catalogAdapter = new CatalogAdapter(getContext(), bouqets);
        GridView gridView = binding.gridViewCatalog;
        gridView.setAdapter(catalogAdapter);
        return view;
    }


    void fillData() {
        for (int i = 0; i < 6; i++) {
            // Создание экземпляра BouqetCard

        }
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
        binding.header.headerName.setText("Букеты");
        binding.header.goToCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_catalog_to_personalCabinet);
            }
        });
        binding.header.goToFavoutites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        // HEADER END


    }
}