package com.example.bouqetflowershop;

import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.graphics.drawable.AnimatedStateListDrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.bouqetflowershop.databinding.FragmentSignInBinding;
import com.example.bouqetflowershop.databinding.FragmentSignUpBinding;
import com.example.bouqetflowershop.databinding.FragmentSuccessfulPaymentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SuccessfulPayment extends Fragment {
    private FragmentSuccessfulPaymentBinding binding;
    ImageView checkImageView;
    Drawable drawable;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSuccessfulPaymentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.comeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_successfulPayment_to_mainHomePage);
            }
        });

        checkImageView = binding.checkImageView;
        drawable = checkImageView.getDrawable();
        if (drawable instanceof AnimatedVectorDrawableCompat){
            avd = (AnimatedVectorDrawableCompat) drawable;
            avd.start();
        } else if (drawable instanceof AnimatedVectorDrawable){
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }

        return view;
    }
}