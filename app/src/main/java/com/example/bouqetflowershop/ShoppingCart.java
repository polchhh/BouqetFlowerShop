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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bouqetflowershop.databinding.FragmentFavouritesBinding;
import com.example.bouqetflowershop.databinding.FragmentShoppingCartBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
public class ShoppingCart extends Fragment {
    private FragmentShoppingCartBinding binding;
    private ArrayList<BouqetCard> bouqets= new ArrayList<>();
    private CatalogAdapter adapter;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private TextView emptyTextView;
    private LinearLayout itogCost;
    private TextView totalCost;
    // Базы данных
    private FirebaseAuth mAuth;
    private DatabaseReference userCartDatabase;
    private DatabaseReference userDatabase;
    private String USER_KEY = "User";
    private String CART_KEY = "Cart";
    private TextView numberView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize image picker launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        // Handle the selected image URI
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
        binding = FragmentShoppingCartBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Initialize the adapter
        adapter = new CatalogAdapter(getContext(), bouqets, "корзина", imagePickerLauncher, this);
        binding.gridViewCart.setAdapter(adapter);

        // Initialize Firebase references
        mAuth = FirebaseAuth.getInstance();
        userCartDatabase = FirebaseDatabase.getInstance().getReference(CART_KEY);

        emptyTextView = binding.emptyTextView;
        itogCost = binding.itogCost;
        totalCost = binding.totalCost;

        loadCart();

        return view;
    }

    public void calculateTotalCost() {
        double total = 0;
        for (BouqetCard product : bouqets) {
            double price = Double.parseDouble(product.getPrice());
            int quantity = product.getNumber();
            total += price * quantity;
        }
        totalCost.setText(String.valueOf(total));
    }

    private void loadCart() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            if (uid != null) {
                userCartDatabase.child(uid).addValueEventListener(new ValueEventListener() {
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
                        calculateTotalCost();

                        if (bouqets.isEmpty()) {
                            emptyTextView.setVisibility(View.VISIBLE);
                            itogCost.setVisibility(View.GONE);
                        } else {
                            emptyTextView.setVisibility(View.GONE);
                            itogCost.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } else {
            }
        } else {
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // HEADER
        binding.header.goBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.header.headerName.setText("Корзина");
        binding.header.goToCabinet.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_shoppingCart_to_personalCabinet));
        binding.header.goToFavoutites.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_shoppingCart_to_favourites));
        // HEADER END
        binding.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bouqets.isEmpty()){
                    Toast.makeText(getContext(), "Корзина пуста!\nСначала заполните корзину!", Toast.LENGTH_LONG).show();
                }
                else Navigation.findNavController(v).navigate(R.id.action_shoppingCart_to_order);
            }
        });
    }
}