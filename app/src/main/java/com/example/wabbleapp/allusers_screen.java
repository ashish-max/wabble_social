package com.example.wabbleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class allusers_screen extends AppCompatActivity {

    private DatabaseReference rootnode;
    private RecyclerView RView;
    private ArrayList<User_details> UserList;
    private CustomAdapter customAdapter;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allusers_screen);

        // Hooks
        RView = findViewById(R.id.recycler);
        RView.setLayoutManager(new LinearLayoutManager(this));
        UserList = new ArrayList<>();
        rootnode= FirebaseDatabase.getInstance().getReference().child("users");


        rootnode.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User_details ud = dataSnapshot.getValue(User_details.class);
                    UserList.add(ud);
                }
                customAdapter = new CustomAdapter(allusers_screen.this, UserList);
                RView.setAdapter(customAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}