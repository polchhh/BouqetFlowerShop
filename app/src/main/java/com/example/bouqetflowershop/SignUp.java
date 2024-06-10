package com.example.bouqetflowershop;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bouqetflowershop.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends Fragment {
    private FragmentSignUpBinding binding;
    String namePattern = "^[A-Za-zА-Яа-яЁё-]+$";
    String phonePattern = "^(\\+?7|8)?\\s?\\(?\\d{3}\\)?\\s?\\d{3}[-\\s]?\\d{2}[-\\s]?\\d{2}$";
    String emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    String passwordPattern = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}";
    private FirebaseAuth mAuth;
    private DatabaseReference userDatabase;
    private String USER_KEY = "User";
    private String domain = "@bouqet.org";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.comeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_signUp_to_home2);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseDatabase.getInstance().getReference(USER_KEY);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText editTextPersonalName = binding.editTextPersonalName;
        EditText editTextPersonalSurname = binding.editTextPersonalSurname;
        EditText editTextPhone = binding.editTextPhone;
        EditText editTextEmailAddress = binding.editTextEmailAddress;
        EditText editTextPassword = binding.editTextPassword;

        binding.btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasError = false;
                if (editTextPersonalName.getText().toString().isEmpty()) {
                    editTextPersonalName.setError("Введите имя");
                    hasError = true;
                } else if (!editTextPersonalName.getText().toString().matches(namePattern)) {
                    editTextPersonalName.setError("Неправильный формат, используйте только буквы и '-'");
                    hasError = true;
                }
                if (editTextPersonalSurname.getText().toString().isEmpty()) {
                    editTextPersonalSurname.setError("Введите фамилию");
                    hasError = true;
                } else if (!editTextPersonalSurname.getText().toString().matches(namePattern)) {
                    editTextPersonalSurname.setError("Неправильный формат, используйте только буквы и '-'");
                    hasError = true;
                }
                if (editTextPhone.getText().toString().isEmpty()) {
                    editTextPhone.setError("Введите номер телефона");
                    hasError = true;
                } else if (!editTextPhone.getText().toString().matches(phonePattern)) {
                    editTextPhone.setError("Неправильный формат, используйте +79998887766 или 89998887766");
                    hasError = true;
                }
                if (editTextEmailAddress.getText().toString().isEmpty()) {
                    editTextEmailAddress.setError("Введите email");
                    hasError = true;
                } else if (!editTextEmailAddress.getText().toString().matches(emailPattern)) {
                    editTextEmailAddress.setError("Неправильный формат, используйте username@domain.com");
                    hasError = true;
                }
                if (editTextPassword.getText().toString().isEmpty()) {
                    editTextPassword.setError("Введите пароль");
                    hasError = true;
                } else if (!editTextPassword.getText().toString().matches(passwordPattern)) {
                    editTextPassword.setError("Неправильный формат, используйте минимум 8 символов, минимум 1 цифру, минимум 1 строчную и 1 заглавную букву");
                    hasError = true;
                }
                if (hasError) {
                    Toast.makeText(getContext(), "Заполните все поля, пожалуйста!", Toast.LENGTH_LONG).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(editTextEmailAddress.getText().toString(), editTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Получение UID
                            String uid = mAuth.getCurrentUser().getUid();
                            Boolean isAdmin = false;
                            if (editTextEmailAddress.getText().toString().endsWith(domain)) {
                                isAdmin = true;
                            }
                            User user = new User(uid, editTextPersonalName.getText().toString(), editTextPersonalSurname.getText().toString(), editTextPhone.getText().toString(), editTextEmailAddress.getText().toString(), isAdmin);
                            // Сохранение пользователя в базу данных
                            userDatabase.child(uid).setValue(user);
                            Toast.makeText(getContext(), "Вы успешно зарегистрированы!", Toast.LENGTH_LONG).show();
                            Navigation.findNavController(v).navigate(R.id.action_signUp_to_mainHomePage);
                        } else {
                            Toast.makeText(getContext(), "Пользователь с такой почтой уже существует!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }
}