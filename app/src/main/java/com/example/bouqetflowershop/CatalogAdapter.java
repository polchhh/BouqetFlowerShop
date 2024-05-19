package com.example.bouqetflowershop;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    Context ctx;
    LayoutInflater lInFlater;
    ArrayList<BouqetCard> objects;
    private StorageReference mStorageRef;
    private Uri uploadUri;
    private String CATALOG_KEY = "Catalog";
    private String USER_KEY = "User";
    private DatabaseReference catalogDatabase;
    Dialog dialog;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    private ImageView deleteProduct;

    CatalogAdapter(Context context, ArrayList<BouqetCard> products) {
        ctx = context;
        objects = products;
        lInFlater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mStorageRef = FirebaseStorage.getInstance().getReference("CatalogImage");
        catalogDatabase = FirebaseDatabase.getInstance().getReference(CATALOG_KEY);
        dialog = new Dialog(context);
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference(USER_KEY);
    }

    public void removeItem(BouqetCard product) {
        objects.remove(product);
        notifyDataSetChanged();
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
        deletebtn.setOnClickListener(v -> showDialog(p));

        // Проверка, является ли пользователь администратором
        checkIfAdmin(isAdmin -> {
            if (isAdmin) {
                deletebtn.setVisibility(View.VISIBLE);
            } else {
                deletebtn.setVisibility(View.GONE);
            }
        });
        return view;
    }

    BouqetCard getProduct(int position) {
        return ((BouqetCard) getItem(position));
    }

    public void addProduct(BouqetCard product) {
        uploadImage(product);
    }

    private void uploadImage(BouqetCard product) {
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

    private void saveProductToDatabase(BouqetCard product) {
        String imageUrl = product.getImageUri() != null ? product.getImageUri().toString() : null;
        String productId = catalogDatabase.push().getKey();
        BouqetCard productToSave = new BouqetCard(product.getName(), productId, product.getPrice(), imageUrl);
        catalogDatabase.child(productId).setValue(productToSave)
                .addOnSuccessListener(aVoid -> {
                    // Успешно добавлено
                })
                .addOnFailureListener(e -> {
                    // Ошибка при добавлении
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

