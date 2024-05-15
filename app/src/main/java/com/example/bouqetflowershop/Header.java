package com.example.bouqetflowershop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bouqetflowershop.databinding.FragmentHeaderBinding;
import com.example.bouqetflowershop.databinding.FragmentHomeBinding;


public class Header extends Fragment {
    private FragmentHeaderBinding binding;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHeaderBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

}