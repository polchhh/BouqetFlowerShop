package com.example.bouqetflowershop;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.bouqetflowershop.databinding.FragmentHeaderBinding;

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