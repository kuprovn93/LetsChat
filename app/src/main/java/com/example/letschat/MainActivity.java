package com.example.letschat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import model.UserAdapter;
import model.Users;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth fAuth;
    RecyclerView mainUserRecyclerView;
    UserAdapter adapter;
    FirebaseDatabase fDatabase;
    ArrayList<Users> userArrayList;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance();
        userArrayList = new ArrayList<>();

        DatabaseReference dbReference = fDatabase.getReference().child("Users");
        dbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Users users = dataSnapshot.getValue(Users.class);
                    if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(users.getUid())){
                        continue;
                    }
                    userArrayList.add(users);
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
        adapter  = new UserAdapter(MainActivity.this, userArrayList);

        mainUserRecyclerView = findViewById(R.id.main_user_recycler_view);
        mainUserRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mainUserRecyclerView.setAdapter(adapter);


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.optionmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuItemLogout){
            // adding builder for popup to logout start
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            // Setting message manually and performing action on button click
            builder.setMessage("Do you want to Logout ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (dialog, id) -> {
                        FirebaseAuth.getInstance().signOut();
                        GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build())
                                .signOut();
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish();
                        Toast.makeText(getApplicationContext(),"Successfully Logged Out",
                                Toast.LENGTH_SHORT).show();

                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Logout Confirmation");
            alert.show();
            // adding builder for popup to logout start
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(getApplicationContext(), Login.class));
//            finish();

        }

        return super.onOptionsItemSelected(item);
    }
//    @Override
//    protected void onStart() {
//        super.onStart();
//        adapter.startListening();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        if(adapter != null){
//            adapter.stopListening();
//        }
//    }

}