package com.example.bouqetflowershop;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bouqetflowershop.databinding.FragmentAboutAuthorBinding;
import com.example.bouqetflowershop.databinding.FragmentSignInBinding;


public class AboutAuthor extends Fragment {
    private FragmentAboutAuthorBinding binding;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutAuthorBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.comeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigateUp();
            }
        });
        return view;
    }
}