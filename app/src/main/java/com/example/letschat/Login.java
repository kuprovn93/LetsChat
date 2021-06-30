package com.example.letschat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

//import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
//import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicMarkableReference;

import model.Users;

public class Login extends AppCompatActivity {

    private static final String TAG = "Error:";
    private static final int RC_SIGN_IN = 123;
    EditText mEmail, mPassword;
    Button createAccountBtn, loginBtn;
    SignInButton googleLoginBtn;

    private FirebaseAuth fAuth;
    private FirebaseFirestore fStore;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);



        createAccountBtn = findViewById(R.id.goToRegisterBtn);
        loginBtn = findViewById(R.id.loginBtn);
        mEmail = findViewById(R.id.emailLogin);
        mPassword = findViewById(R.id.passwordLogin);
        googleLoginBtn = findViewById(R.id.googleSignInButton);
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

//         Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null || fAuth.getCurrentUser() != null){
            Toast.makeText(this, "User is already loggedIn", Toast.LENGTH_SHORT).show();
        }



        // login btn event listener start
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
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

                fAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Login.this,"Login Success!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Login.this,"User is not Registered!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
        // login btn event listener end


        // register for new account button event start
        createAccountBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);
                finish();
            }
        });
        // register for new account button event end

        // signin using google start
        googleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();
            }
        });
        // signin using google start

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d(TAG, "signIn: Here is the end of signInIntent");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: REQUEST CODE == RC SIGN IN");
            System.out.println("request code: "+requestCode);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            System.out.println("Method Inside handlesigninresult: ");

            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);
            Toast.makeText(this, "Google SignIn Success",Toast.LENGTH_SHORT).show();
             firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            e.printStackTrace();
            Toast.makeText(this, "Google sign in failed",Toast.LENGTH_SHORT).show();
            Log.w(TAG, "Google sign in failed"+e.getMessage(), e);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        System.out.println("idToken: "+idToken);
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = fAuth.getCurrentUser();
//                            DocumentReference df = fStore.collection("Users").document(user.getUid());
//                            Map<String, Object> userInfo = new HashMap<>();
//                            userInfo.put("FirstName", user.getDisplayName().split(" ")[0]);
//                            userInfo.put("LastName", user.getDisplayName().split(" ")[1]);
//                            userInfo.put("Email", user.getEmail());
//                            userInfo.put("RegisteredOn", new Date());
//                            df.set(userInfo);
                            String fname = user.getDisplayName().split(" ")[0];
                            String lname = user.getDisplayName().split(" ")[1];
                            String email = user.getEmail();
                            String status = "Hey there! I am using LetsChat.";
                            System.out.println(status);

                            FirebaseDatabase fDatabase = FirebaseDatabase.getInstance();
                            DatabaseReference dbReference = fDatabase.getReference().child("Users").child(fAuth.getUid());
                            FirebaseStorage fStorage = FirebaseStorage.getInstance();
                            Users newUser = new Users(fAuth.getUid(),fname, lname, email, status);
                            dbReference.setValue(newUser);
                            Log.d(TAG, "signInWithCredential:success");
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Login.this,"Failed to Login", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }
}