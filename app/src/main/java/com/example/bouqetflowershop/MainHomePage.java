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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bouqetflowershop.databinding.FragmentHomeBinding;
import com.example.bouqetflowershop.databinding.FragmentMainHomePageBinding;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MainHomePage extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private FragmentMainHomePageBinding binding;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    private TextView textViewNameUserNavigation;
    private ShapeableImageView shapeableImageViewNav;

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
        binding.goToFavoutites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_mainHomePage_to_favourites);
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

        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference("User");

        View headerView = navigationView.getHeaderView(0);
        textViewNameUserNavigation = headerView.findViewById(R.id.textViewNameUserNavigation);
        shapeableImageViewNav = headerView.findViewById(R.id.shapeableImageViewNav);

        // Проверка, является ли пользователь администратором
        checkIfAdmin(new Catalog.AdminCheckCallback() {
            @Override
            public void onCheckCompleted(boolean isAdmin) {
                if (isAdmin) {
                    hideMenuItemsForAdmin();
                    binding.goToFavoutites.setVisibility(View.INVISIBLE);
                    binding.myImageViewTextCal.setText("Календарь\nнапоминаний");
                }
            }
        });
        return view;
    }

    private void hideMenuItemsForAdmin() {
        Menu menu = navigationView.getMenu();
        MenuItem favouritesNav = menu.findItem(R.id.favouritesNav);
        MenuItem cartNav = menu.findItem(R.id.cartNav);
        MenuItem historyNav = menu.findItem(R.id.historyNav);
        favouritesNav.setVisible(false);
        cartNav.setVisible(false);
        historyNav.setVisible(false);
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
        binding.myImageViewHoliday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_calendarHoliday);
            }
        });
        binding.myImageViewTextGoToInstruction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_aboutInstruction);
            }
        });
        binding.footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_shoppingCart);
            }
        });

        // Получение текущего UID пользователя
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            userDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            if (user.imageUri != null) {
                                Picasso.get().load(user.getImageUri()).into(shapeableImageViewNav);
                            }
                            binding.textViewHelloUser.setText(user.getName());
                            textViewNameUserNavigation.setText(user.getName());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.personalCabinetNav) {
            Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_personalCabinet);
            drawerLayout.close();
        }
        if (item.getItemId() == R.id.catalogNav) {
            Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_catalog);
            drawerLayout.close();
        }
        if (item.getItemId() == R.id.cartNav) {
            Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_shoppingCart);
            drawerLayout.close();
        }
        if (item.getItemId() == R.id.favouritesNav) {
            Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_favourites);
            drawerLayout.close();
        }
        if (item.getItemId() == R.id.historyNav) {
            Log.d("Mylog", "Cab");
        }
        if (item.getItemId() == R.id.aboutAuthorNav) {
            Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_aboutAuthor);
            drawerLayout.close();
        }
        if (item.getItemId() == R.id.aboutProgramNav) {
            Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_aboutProgram);
            drawerLayout.close();
        }
        if (item.getItemId() == R.id.instructionNav) {
            Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_aboutInstruction);
            drawerLayout.close();
        }
        if (item.getItemId() == R.id.logOutNav) {
            FirebaseAuth.getInstance().signOut();
            Navigation.findNavController(getView()).navigate(R.id.action_mainHomePage_to_home2);
        }
        return false;
    }

    private void checkIfAdmin(final Catalog.AdminCheckCallback callback) {
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

