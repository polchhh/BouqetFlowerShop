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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.bouqetflowershop.databinding.FragmentCatalogBinding;
import com.example.bouqetflowershop.databinding.FragmentCatalogIntBinding;
import com.example.bouqetflowershop.databinding.FragmentPersonalCabinetBinding;

import java.util.ArrayList;


public class CatalogInt extends Fragment {
    private FragmentCatalogIntBinding binding;
    ArrayList<BouqetCard> bouqets= new ArrayList<>();
    CatalogAdapter catalogAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCatalogIntBinding.inflate(inflater, container, false);
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
            BouqetCard bouqet = new BouqetCard("Цветы №" + (i + 1), 16430 + i, (i + 1) * 1000,
                    R.drawable.c1, false);
            bouqets.add(bouqet);
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
        binding.header.headerName.setText("Цветы");
        binding.header.goToCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_catalogInt_to_personalCabinet);
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