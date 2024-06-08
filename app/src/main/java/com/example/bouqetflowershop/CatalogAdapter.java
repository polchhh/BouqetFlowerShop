package com.example.bouqetflowershop;

import android.app.Dialog;

import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CatalogAdapter extends BaseAdapter {
    private Context ctx;
    private LayoutInflater lInFlater;
    private ArrayList<BouqetCard> objects;
    private Dialog dialog;
    private Fragment fragment;
    private OnImageSelectedListener onImageSelectedListener;
    private Uri uploadUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String typeC;
    // Базы данных
    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private DatabaseReference userFavDatabase;
    private DatabaseReference userCartDatabase;
    private DatabaseReference catalogDatabase;
    private DatabaseReference userDatabase;
    private String CATALOG_KEY = "Catalog";
    private String CATALOGINT_KEY = "CatalogInt";
    private String USER_KEY = "User";
    private String FAV_KEY = "Favourites";
    private String CART_KEY = "Cart";


    CatalogAdapter(Context context, ArrayList<BouqetCard> products, String type, ActivityResultLauncher<Intent> launcher, Fragment fragment) { // Добавляем Catalog в конструктор
        ctx = context;
        objects = products;
        this.fragment = fragment;

        if (type.equals("букет")) {
            mStorageRef = FirebaseStorage.getInstance().getReference("CatalogImage");
            catalogDatabase = FirebaseDatabase.getInstance().getReference(CATALOG_KEY);
            typeC = "букет";
        } else if (type.equals("цветок")) {
            mStorageRef = FirebaseStorage.getInstance().getReference("CatalogIntImage");
            catalogDatabase = FirebaseDatabase.getInstance().getReference(CATALOGINT_KEY);
            typeC = "цветок";
        }
        lInFlater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialog = new Dialog(context);
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference(USER_KEY);
        userFavDatabase = FirebaseDatabase.getInstance().getReference(FAV_KEY);
        userCartDatabase = FirebaseDatabase.getInstance().getReference(CART_KEY);
        this.imagePickerLauncher = launcher;

    }

    public void setUri(Uri uri) {
        this.uploadUri = uri;
    }

    public void setOnImageSelectedListener(OnImageSelectedListener listener) {
        this.onImageSelectedListener = listener;
    }

    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInFlater.inflate(R.layout.item_catalog, parent, false);
        }
        BouqetCard p = getProduct(position);
        ((TextView) view.findViewById(R.id.textViewName)).setText(p.name);
        ((TextView) view.findViewById(R.id.textViewPrice)).setText(p.price + "₽");

        ShapeableImageView imageView = view.findViewById(R.id.imageViewImage);
        if (p.imageUri != null && !p.imageUri.isEmpty()) {
            Picasso.get().load(p.imageUri).into(imageView);
        } else {
            imageView.setImageResource(R.drawable.b1); // Используйте placeholder изображение
        }

        ImageView deletebtn = view.findViewById(R.id.deleteProduct);
        LinearLayout PlusMinus = view.findViewById(R.id.plusminus);
        ImageView deletebtnCart = view.findViewById(R.id.deleteProductFromCart);
        ImageView favbtn = view.findViewById(R.id.addToFavoutites);
        ImageView cartbtn = view.findViewById(R.id.addToCart);
        ImageView editbtn = view.findViewById(R.id.showMenuPr);
        TextView numberView = view.findViewById(R.id.textViewNumber);
        ImageView plus = view.findViewById(R.id.imageViewPlus);
        ImageView minus = view.findViewById(R.id.imageViewMinus);

        numberView.setText(String.valueOf(p.getNumber()));

        // Проверка, находится ли продукт в избранном, и установка иконки
        checkIfFavorite(p, favbtn);

        // Устанавливаем слушатель для добавления в избранное
        favbtn.setOnClickListener(v -> toggleFavorite(p, favbtn));

        // Устанавливаем слушатель для добавления в корзину
        cartbtn.setOnClickListener(v -> toggleCart(p));

        // Устанавливаем слушатель для удаления из корзины
        deletebtnCart.setOnClickListener(v -> removeFromCart(p));

        // Устанавливаем слушатель для удаления продукта
        deletebtn.setOnClickListener(v -> showDialog(p));

        // Устанавливаем слушатель для редактирования продукта
        editbtn.setOnClickListener(v -> showEditDialog(p));

        // Устанавливаем слушатели событий для кнопок
        plus.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                DatabaseReference productRef = userCartDatabase.child(uid).child(p.getId());

                // Читаем текущее значение количества товара из базы данных
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentNumber = snapshot.child("number").getValue(Integer.class);
                        int newNumber = currentNumber + 1;

                        // Обновляем количество товара в объекте
                        p.setNumber(newNumber);
                        numberView.setText(String.valueOf(p.getNumber()));

                        // Обновляем количество товара в базе данных
                        productRef.child("number").setValue(newNumber)
                                .addOnSuccessListener(aVoid -> {
                                    // Успешно обновлено
                                    notifyDataSetChanged();
                                    if (fragment instanceof ShoppingCart) {
                                        ((ShoppingCart) fragment).calculateTotalCost();
                                    }
                                    if (fragment instanceof Order) {
                                        ((Order) fragment).calculateTotalCost();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Обработка ошибки при обновлении
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        minus.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String uid = currentUser.getUid();
                DatabaseReference productRef = userCartDatabase.child(uid).child(p.getId());

                // Читаем текущее значение количества товара из базы данных
                productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int currentNumber = snapshot.child("number").getValue(Integer.class);

                        if (currentNumber > 1) {
                            int newNumber = currentNumber - 1;

                            // Обновляем количество товара в объекте
                            p.setNumber(newNumber);
                            numberView.setText(String.valueOf(p.getNumber()));

                            // Обновляем количество товара в базе данных
                            productRef.child("number").setValue(newNumber)
                                    .addOnSuccessListener(aVoid -> {
                                        // Успешно обновлено
                                        notifyDataSetChanged();
                                        if (fragment instanceof ShoppingCart) {
                                            ((ShoppingCart) fragment).calculateTotalCost();
                                        }
                                        if (fragment instanceof Order) {
                                            ((Order) fragment).calculateTotalCost();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Обработка ошибки при обновлении
                                    });
                        } else {
                            // Если количество равно 1, удаляем товар из корзины
                            removeFromCart(p);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Обработка ошибки при чтении данных
                    }
                });
            }
        });

        // Проверка на администратора
        checkIfAdmin(isAdmin -> {
            if (isAdmin) {
                editbtn.setVisibility(View.VISIBLE);
                favbtn.setVisibility(View.GONE);
                cartbtn.setVisibility(View.GONE);
                deletebtn.setVisibility(View.VISIBLE);
            } else {
                deletebtn.setVisibility(View.GONE);
                if (fragment.getClass().getSimpleName().equals("ShoppingCart") || fragment.getClass().getSimpleName().equals("Order")) {
                    cartbtn.setVisibility(View.GONE);
                    deletebtnCart.setVisibility(View.VISIBLE);
                    PlusMinus.setVisibility(View.VISIBLE);
                }
            }
        });
        return view;
    }

    // метод для проверки, находится ли продукт в избранном
    private void checkIfFavorite(BouqetCard product, ImageView favbtn) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            userFavDatabase.child(uid).child(product.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        favbtn.setImageResource(R.drawable.player_heart_fill);
                    } else {
                        favbtn.setImageResource(R.drawable.player_heart);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Обработка ошибки
                }
            });
        }
    }

    // метод удаления из корзины
    private void removeFromCart(BouqetCard product) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference productRef = userCartDatabase.child(uid).child(product.getId());
            productRef.removeValue()
                    .addOnSuccessListener(aVoid -> {
                        objects.remove(product);
                        notifyDataSetChanged();
                        if (fragment instanceof ShoppingCart) {
                            ((ShoppingCart) fragment).calculateTotalCost();
                        }
                        if (fragment instanceof Order) {
                            ((Order) fragment).calculateTotalCost();
                        }
                        if (objects.isEmpty()) {
                        }
                    })
                    .addOnFailureListener(e -> {
                    });
        }
    }

    // метод добавления в корзину
    private void toggleCart(BouqetCard product) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference productRef = userCartDatabase.child(uid).child(product.getId());
            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Товар уже в корзине, увеличиваем количество
                        int currentNumber = snapshot.child("number").getValue(Integer.class);
                        int newNumber = currentNumber + 1;
                        productRef.child("number").setValue(newNumber)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(fragment.getContext(), "Количество товара '" + product.getName() + "' увеличено", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Обработка ошибки
                                });
                    } else {
                        // Товара еще нет в корзине, добавляем его
                        BouqetCard cartItem = new BouqetCard(product.getName(), product.getId(), product.getPrice(), product.getImageUri(), product.getType(), 1);
                        productRef.setValue(cartItem)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(fragment.getContext(), "Товар '" + product.getName() + "' добавлен в корзину", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    // Обработка ошибки
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Обработка ошибки
                }
            });
        }
    }

    // метод добавления/удаления из избранного
    private void toggleFavorite(BouqetCard product, ImageView favbtn) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DatabaseReference productRef = userFavDatabase.child(uid).child(product.getId());

            productRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        productRef.removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    favbtn.setImageResource(R.drawable.player_heart);
                                    if (fragment.getClass().getSimpleName().equals("Favourites")) {
                                        objects.remove(product);
                                        notifyDataSetChanged(); // обновляем список
                                    }
                                    if (objects.isEmpty()) {
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Обработка ошибки удаления из избранного
                                });
                    } else {
                        // Товара еще нет в избранном, добавляем его
                        BouqetCard cartItem = new BouqetCard(product.getName(), product.getId(), product.getPrice(), product.getImageUri(), product.getType());
                        productRef.setValue(cartItem)
                                .addOnSuccessListener(aVoid -> {
                                    favbtn.setImageResource(R.drawable.player_heart_fill);
                                })
                                .addOnFailureListener(e -> {
                                    // Обработка ошибки
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Обработка ошибки
                }
            });
        }
    }

    BouqetCard getProduct(int position) {
        return ((BouqetCard) getItem(position));
    }

    // Загрузка нового товара
    public void uploadItem(BouqetCard product) {
        if (product.imageUri != null) {
            StorageReference imageRef = mStorageRef.child(product.getName());
            UploadTask uploadTask = imageRef.putFile(Uri.parse(product.imageUri));
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    product.setImageUri(String.valueOf(uri));
                    saveProductToDatabase(product);
                }).addOnFailureListener(e -> {
                });
            }).addOnFailureListener(e -> {
            });
        } else {
            saveProductToDatabase(product);
        }
    }

    // Сохранение товара в бд
    private void saveProductToDatabase(BouqetCard product) {
        String imageUrl = product.getImageUri() != null ? product.getImageUri().toString() : null;
        String productId = catalogDatabase.push().getKey();
        BouqetCard productToSave = new BouqetCard(product.getName(), productId, product.getPrice(), imageUrl, typeC);
        catalogDatabase.child(productId).setValue(productToSave)
                .addOnSuccessListener(aVoid -> {
                })
                .addOnFailureListener(e -> {
                });
    }

    // Метод для показа диалога
    private void showDialog(BouqetCard bouqetCard) {
        dialog.setContentView(R.layout.dialog_yes_no);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        Button ok = dialog.findViewById(R.id.btn_yes);
        Button cancel = dialog.findViewById(R.id.btn_no);
        dialog.show();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String productId = bouqetCard.getId();
                if (productId != null) {

                    catalogDatabase.child(productId).removeValue()
                            .addOnSuccessListener(aVoid -> {
                                objects.remove(bouqetCard);
                                notifyDataSetChanged();
                            })
                            .addOnFailureListener(e -> {
                                // Ошибка при удалении
                            });
                }
                deleteImageFromStorage(bouqetCard.getName());
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // Метод удаления изображения из хранилища FireBase
    private void deleteImageFromStorage(String imageName) {
        StorageReference imageRef = mStorageRef.child(imageName);
        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    // Метод показа диалога редактирования товара
    private void showEditDialog(BouqetCard product) {
        Dialog dialog = new Dialog(fragment.getContext());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_edit_product);
        EditText editTextName = dialog.findViewById(R.id.editTextProductNameEd);
        EditText editTextPrice = dialog.findViewById(R.id.editTextProductPriceEd);
        Button buttonChoiceImage = dialog.findViewById(R.id.buttonChangeImage);
        Button btnSave = dialog.findViewById(R.id.btn_yes);
        Button btnCancel = dialog.findViewById(R.id.btn_no);
        editTextName.setText(product.getName());
        editTextPrice.setText(String.valueOf(product.getPrice()));

        buttonChoiceImage.setOnClickListener(v -> {
            onImageSelectedListener.onImageSelected();
        });

        btnSave.setOnClickListener(v -> {
            String newName = editTextName.getText().toString();
            String newPrice = editTextPrice.getText().toString();
            if (String.valueOf(uploadUri) != null) {
                uploadImageAndUpdateProduct(product, newName, newPrice, uploadUri);
            } else {
                uploadImageAndUpdateProduct(product, newName, newPrice, Uri.parse(product.getImageUri()));
            }
            dialog.dismiss();
        });
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    // Метод загрузки нового товара
    private void uploadImageAndUpdateProduct(BouqetCard product, String newName, String newPrice, Uri uploadUri) {
        if (uploadUri != null) {
            StorageReference imageRef = mStorageRef.child(product.getName());
            UploadTask uploadTask = imageRef.putFile(uploadUri);

            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                return imageRef.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    String newImageUrl = downloadUri.toString();
                    product.setName(newName);
                    product.setPrice(newPrice);
                    product.setImageUri(newImageUrl);
                    catalogDatabase.child(product.getId()).setValue(product);
                    updateUserProductsInDatabase(userCartDatabase, product, newName, newPrice, newImageUrl );
                    updateUserProductsInDatabase(userFavDatabase, product, newName, newPrice, newImageUrl);
                } else {
                }
            });
        } else {
            product.setName(newName);
            product.setPrice(newPrice);
            catalogDatabase.child(product.getId()).setValue(product);
            updateUserProductsInDatabase(userCartDatabase, product, newName, newPrice, product.getImageUri());
            updateUserProductsInDatabase(userFavDatabase, product, newName, newPrice, product.getImageUri());
        }
    }

    // метод обновление товара в пользовательских бд
    private void updateUserProductsInDatabase(DatabaseReference userDatabaseRef, BouqetCard product, String newName, String newPrice, String uploadUri) {
        userDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userId = userSnapshot.getKey();
                    userDatabaseRef.child(userId).child(product.getId()).child("name").setValue(newName);
                    userDatabaseRef.child(userId).child(product.getId()).child("price").setValue(newPrice);
                    userDatabaseRef.child(userId).child(product.getId()).child("imageUri").setValue(uploadUri);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // проверка на администратора
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

    public interface OnImageSelectedListener {
        void onImageSelected();
    }
}

