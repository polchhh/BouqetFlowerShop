package com.example.bouqetflowershop;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.bouqetflowershop.databinding.FragmentCatalogBinding;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Catalog extends Fragment implements CatalogAdapter.OnImageSelectedListener {
    private FragmentCatalogBinding binding;
    private ArrayList<BouqetCard> bouqets = new ArrayList<>();
    private CatalogAdapter adapter;
    private Dialog dialog;
    private EditText editTextNameProduct;
    private EditText editTextCostProduct;
    private ShapeableImageView imageViewImage;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private GridView gridView;
    private boolean imageDone = false;
    private Uri uploadUri;
    //Базы данных
    private FirebaseAuth mAuth;
    private DatabaseReference catalogDatabase;
    private DatabaseReference userDatabase;
    private String CATALOG_KEY = "Catalog";
    private String USER_KEY = "User";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCatalogBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        uploadUri = uri;
                        imageDone = true;
                        if (imageViewImage != null) {
                            imageViewImage.setImageURI(uploadUri);
                        }
                        if (adapter != null) {
                            adapter.setUri(uri);
                        }
                    } else if (result.getResultCode() == ImagePicker.RESULT_ERROR) {
                        Toast.makeText(getContext(), ImagePicker.getError(result.getData()), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Запрос отменен", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // HEADER
        binding.header.goBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        binding.header.headerName.setText("Букеты");
        binding.header.goToCabinet.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_catalog_to_personalCabinet));
        binding.header.goToFavoutites.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_catalog_to_favourites));
        // HEADER END
        binding.footer.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_catalog_to_shoppingCart));

        dialog = new Dialog(getContext());

        mAuth = FirebaseAuth.getInstance();
        catalogDatabase = FirebaseDatabase.getInstance().getReference(CATALOG_KEY);
        userDatabase = FirebaseDatabase.getInstance().getReference(USER_KEY);

        adapter = new CatalogAdapter(getContext(), bouqets, "букет", imagePickerLauncher, ((Fragment) this));
        adapter.setOnImageSelectedListener(this);
        gridView = binding.gridViewCatalog;
        gridView.setAdapter(adapter);

        binding.addProductToCatalog.setOnClickListener(v -> showDialogAddProduct());

        catalogDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bouqets.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    BouqetCard product = dataSnapshot.getValue(BouqetCard.class);
                    bouqets.add(product);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // Проверка, является ли пользователь администратором
        checkIfAdmin(new AdminCheckCallback() {
            @Override
            public void onCheckCompleted(boolean isAdmin) {
                if (isAdmin) {
                    binding.header.goToFavoutites.setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.addProductToCatalog).setVisibility(View.VISIBLE);
                    binding.footer.setVisibility(View.GONE);
                } else {
                    view.findViewById(R.id.addProductToCatalog).setVisibility(View.GONE);
                }
            }
        });
    }

    // метод показа диалога добавления товара
    private void showDialogAddProduct() {
        dialog.setContentView(R.layout.dialog_add_product);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        imageViewImage = dialog.findViewById(R.id.imageViewImage);
        Button ok = dialog.findViewById(R.id.btn_yes);
        Button cancel = dialog.findViewById(R.id.btn_no);

        dialog.findViewById(R.id.buttonChoiceImage).setOnClickListener(v -> ImagePicker.with(Catalog.this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    imagePickerLauncher.launch(intent);
                    return null;
                }));

        ok.setOnClickListener(v -> {
            editTextNameProduct = dialog.findViewById(R.id.editTextNameProduct);
            editTextCostProduct = dialog.findViewById(R.id.editTextCostProduct);
            boolean hasError = false;

            if (editTextNameProduct.getText().toString().isEmpty()) {
                editTextNameProduct.setError("Введите название");
                hasError = true;
            }

            if (editTextCostProduct.getText().toString().isEmpty()) {
                editTextCostProduct.setError("Введите стоимость");
                hasError = true;
            }

            if (!imageDone) {
                Toast.makeText(getContext(), "Выберите изображение", Toast.LENGTH_LONG).show();
                hasError = true;
            }

            if (hasError) {
                Toast.makeText(getContext(), "Заполните все поля и выберите изображение, пожалуйста!", Toast.LENGTH_LONG).show();
                return;
            }
            BouqetCard newBouqet = new BouqetCard(editTextNameProduct.getText().toString(), editTextCostProduct.getText().toString(), uploadUri.toString());
            adapter.uploadItem(newBouqet);
            dialog.dismiss();
        });
        cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Реализация метода интерфейса для загрузки изображения в адаптер
    @Override
    public void onImageSelected() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .createIntent(intent -> {
                    imagePickerLauncher.launch(intent);
                    return null;
                });
    }

    // Проверка типа пользователя
    private void checkIfAdmin(final AdminCheckCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            userDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean isAdmin = false;
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            isAdmin = user.getIs_admin();
                        }
                    }
                    callback.onCheckCompleted(isAdmin);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    callback.onCheckCompleted(false);
                }
            });
        } else {
            callback.onCheckCompleted(false);
        }
    }

    interface AdminCheckCallback {
        void onCheckCompleted(boolean isAdmin);
    }
}