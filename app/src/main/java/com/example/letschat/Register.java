package com.example.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Users;

public class Register extends AppCompatActivity {

    EditText mFirstName, mLastName, mEmail, mPassword;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseStorage fStorage;
    FirebaseDatabase fDatabase;

    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirstName = findViewById(R.id.firstName);
        mLastName = findViewById(R.id.lastName);
        mEmail = findViewById(R.id.emailRegister);
        mPassword = findViewById(R.id.passwordRegister);
        mRegisterBtn = findViewById(R.id.registerBtn);
        mLoginBtn = findViewById(R.id.goToLoginBtn);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fStorage = FirebaseStorage.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if (fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        // register button event start
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                String fname = mFirstName.getText().toString();
                String lname = mLastName.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                String status = "Hey there! I am using LetsChat.";

                if (fname.isEmpty()){
                    mFirstName.setError("First Name is Mandatory");
                    return;
                }
                if (lname.isEmpty()){
                    mLastName.setError("Last Name is Mandatory");
                    return;
                }

                if (email.isEmpty()){
                    mEmail.setError("Email is Mandatory");
                    return;
                }
                if (password.isEmpty()){
                    mPassword.setError("Password is Mandatory");
                    return;
                }
                if (password.length() < 6 ){
                    mPassword.setError("Password must be >= 6 characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        FirebaseUser user = fAuth.getCurrentUser();

                        Toast.makeText(Register.this,"User Created", Toast.LENGTH_SHORT).show();
                        // for cloud firestore start

//                        DocumentReference df = fStore.collection("Users").document(user.getUid());
//                        Map<String, Object> userInfo = new HashMap<>();
//                        userInfo.put("FirstName", fname);
//                        userInfo.put("LastName", lname);
//                        userInfo.put("Email", email);
//                        userInfo.put("UserRole", "Customer");
//                        userInfo.put("RegisteredOn", new Date());
//                        df.set(userInfo);
                        // for  cloud firestore end

                        DatabaseReference dbReference = fDatabase.getReference().child("Users").child(fAuth.getUid());
                        StorageReference storeReference = fStorage.getReference().child("Upload").child(fAuth.getUid());

                        Users newUser = new Users(fAuth.getUid(), fname, lname, email, status);
                        dbReference.setValue(newUser);

                        progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(Register.this,"Failed to create account", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        // register button event end

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));

            }
        });



    }
}