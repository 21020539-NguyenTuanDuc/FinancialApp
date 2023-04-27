package com.example.financialapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.financialapp.Model.UserModel;
import com.example.financialapp.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SignupActivity extends AppCompatActivity {
    SweetAlertDialog sweetAlertDialog;
    ActivitySignupBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        sweetAlertDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setCancelable(false);

        binding.signup.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                String fullName = binding.fullName.getText().toString();
                String number = binding.mobileNumber.getText().toString();
                String email = binding.email.getText().toString().trim();
                String password = binding.password.getText().toString();
                String confirmPassword = binding.confirmPassword.getText().toString();

                sweetAlertDialog.show();
                if (!password.equals("") && !email.equals("") && !confirmPassword.equals("") && !fullName.equals("") && !number.equals("")) {
                    if (password.equals(confirmPassword)) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {

                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        Toast.makeText(SignupActivity.this, "Sign up successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                        sweetAlertDialog.dismissWithAnimation();

                                        firebaseFirestore.collection("User")
                                                .document(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()))
                                                .set(new UserModel(fullName, number, email));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {

                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                });
                    } else {
                        sweetAlertDialog.dismissWithAnimation();
                        Toast.makeText(SignupActivity.this, "Reconfirm your password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    sweetAlertDialog.dismissWithAnimation();
                    Toast.makeText(SignupActivity.this, "Please fill in all the information", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.toLoginActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }
}