package com.example.bouqetflowershop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.bouqetflowershop.databinding.FragmentHomeBinding;
import com.example.bouqetflowershop.databinding.FragmentMainHomePageBinding;
import com.google.android.material.navigation.NavigationView;


public class MainHomePage extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentMainHomePageBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainHomePageBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.goToCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_mainHomePage_to_personalCabinet);
            }
        });
        drawerLayout = view.findViewById(R.id.drawerLayout);
        navigationView = view.findViewById(R.id.navView);
        binding.showMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout != null) {
                    drawerLayout.open();
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.myImageViewTextCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_catalog);
            }
        });
        binding.interiorFlowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_catalogInt);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.personalCabinetNav){
            Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_personalCabinet);
            drawerLayout.close();
        }
        if (item.getItemId() == R.id.catalogNav){
            Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_catalog);
            drawerLayout.close();
        }
        if (item.getItemId() == R.id.cartNav){
            Log.d("Mylog","Cab");
        }
        if (item.getItemId() == R.id.favouritesNav){
            Log.d("Mylog","Cab");
        }
        if (item.getItemId() == R.id.historyNav){
            Log.d("Mylog","Cab");
        }
        if (item.getItemId() == R.id.logOutNav){
            Log.d("Mylog","Cab");
        }
        return false;
    }
}
