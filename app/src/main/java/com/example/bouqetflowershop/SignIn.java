package com.example.bouqetflowershop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bouqetflowershop.databinding.FragmentHomeBinding;
import com.example.bouqetflowershop.databinding.FragmentSignInBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignIn extends Fragment {
    private FragmentSignInBinding binding;
    private FirebaseAuth mAuth;
    private Boolean signIn = false;
    private Dialog dialog;
    String emailPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        binding.comeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_signIn_to_home2);
            }
        });
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText editTextEmailAddress = binding.editTextEmailAddress;
        EditText editTextPassword = binding.editTextPassword;

        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasError = false;
                if (editTextEmailAddress.getText().toString().isEmpty()) {
                    editTextEmailAddress.setError("Введите почту");
                    hasError = true;
                } else if (!editTextEmailAddress.getText().toString().matches(emailPattern)) {
                    editTextEmailAddress.setError("Неправильный формат, используйте username@domain.com");
                    hasError = true;
                }
                if (editTextPassword.getText().toString().isEmpty()) {
                    editTextPassword.setError("Введите пароль");
                    hasError = true;
                }
                if (hasError) {
                    Toast.makeText(getContext(), "Заполните все поля, пожалуйста!", Toast.LENGTH_LONG).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(editTextEmailAddress.getText().toString(),editTextPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            signIn = true;
                            Navigation.findNavController(v).navigate(R.id.action_signIn_to_mainHomePage);
                        }
                        else{
                            signIn = false;
                            Toast.makeText(getContext(), "Неверное имя пользователя или пароль", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        dialog = new Dialog(getContext());
        binding.forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogForgotPassword();
            }
        });
    }

    private void showDialogForgotPassword() {
        dialog.setContentView(R.layout.dialog_forgot_password);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        EditText email = dialog.findViewById(R.id.editTextEmailForgot);
        Button ok = dialog.findViewById(R.id.btn_yes);
        Button cancel = dialog.findViewById(R.id.btn_no);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasError = false;
                if (email.getText().toString().isEmpty()) {
                    email.setError("Введите почту");
                    hasError = true;
                } else if (!email.getText().toString().matches(emailPattern)) {
                    email.setError("Неправильный формат, используйте username@domain.com");
                    hasError = true;
                }
                if (hasError) {
                    Toast.makeText(getContext(), "Заполните все поля, пожалуйста!", Toast.LENGTH_LONG).show();
                    return;
                }
                mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "Письмо с инструкцией отправлено на email!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                        else{
                            Toast.makeText(getContext(), "Пользователь с таким email не найден!", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}