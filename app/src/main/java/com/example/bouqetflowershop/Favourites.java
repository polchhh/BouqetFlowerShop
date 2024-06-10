package com.example.bouqetflowershop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bouqetflowershop.databinding.FragmentFavouritesBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Favourites extends Fragment {
    private FragmentFavouritesBinding binding;
    private ArrayList<BouqetCard> bouqets = new ArrayList<>();
    private CatalogAdapter adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private DatabaseReference userFavDatabase;
    private FirebaseAuth mAuth;
    private TextView emptyTextView;
    private static final String USER_FAV_KEY = "Favourites";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        adapter = new CatalogAdapter(getContext(), bouqets, "избранное", imagePickerLauncher, this);
        GridView gridView = binding.gridViewFavourites;
        gridView.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        userFavDatabase = FirebaseDatabase.getInstance().getReference(USER_FAV_KEY);
        emptyTextView = binding.emptyTextView;

        loadFavourites();

        return view;
    }


    private void loadFavourites() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if (uid != null) {
                userFavDatabase.child(uid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        bouqets.clear();
                        for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                            BouqetCard bouqet = itemSnapshot.getValue(BouqetCard.class);
                            if (bouqet != null) {
                                bouqets.add(bouqet);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (bouqets.isEmpty()) {
                            emptyTextView.setVisibility(View.VISIBLE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // HEADER
        binding.header.goBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.header.headerName.setText("Избранное");
        binding.header.goToCabinet.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_favourites_to_personalCabinet));
        binding.header.goToFavoutites.setOnClickListener(v -> {
        });
        // HEADER END
        binding.footer.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_favourites_to_shoppingCart));
    }
}