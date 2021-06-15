package com.example.wabbleapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class profile_screen extends AppCompatActivity {

    TextInputLayout fullname,emailp,phoneNo,password;
    DatabaseReference reference;
    FirebaseAuth mAuth;
    FirebaseDatabase rootnode;
    Button Update , Delete, AllUsers, logOut;
    boolean update_name,update_number,update_email,update_password;
    String  or_name, or_number,or_email;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);

        //hooks
        fullname = findViewById(R.id.fullnameprofile);
        phoneNo = findViewById(R.id.mobilenumberprofile);
        emailp = findViewById(R.id.emailaddressprofile);
        password = findViewById(R.id.currentpasswordprofile);
        pb = findViewById(R.id.progressBarprofile);

        Update = findViewById(R.id.updatebutton);
        logOut = findViewById(R.id.logoutbutton);
        Delete = findViewById(R.id.deletebutton);
        AllUsers = findViewById(R.id.friendsbutton);

        mAuth = FirebaseAuth.getInstance();

        rootnode = FirebaseDatabase.getInstance();
        reference = rootnode.getReference("users");

        //show all data
        showAllData();

        AllUsers.setOnClickListener(new View.OnClickListener() {//friends
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent allusers = new Intent(getApplicationContext(),allusers_screen.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(AllUsers,"slogantransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(profile_screen.this,pairs);
                //do
                startActivity(allusers,options.toBundle());
            }
       });



        Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(profile_screen.this);

                alertDialogBuilder.setTitle("Delete Account?");
                alertDialogBuilder.setMessage("Are you sure you want to delete your account?");

                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pb.setVisibility(View.VISIBLE);
                        reference.child(EncoderEmail.EncodeString(mAuth.getCurrentUser().getEmail())).removeValue();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.delete();
                        Intent intent = new Intent(getApplicationContext(), login_screen.class);
                        //do
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(), "User deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(profile_screen.this);

                alertDialogBuilder.setTitle("Log Out?");
                alertDialogBuilder.setMessage("You need to login again if you log out!");
                alertDialogBuilder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { // Redirects to login_page.xml upon click/tap
                        finish();
                        mAuth.signOut();
                        startActivity(new Intent(profile_screen.this, login_screen.class));
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });

        Update.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                Updateinfo();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void Updateinfo() {

        String name     = Objects.requireNonNull(fullname.getEditText().getText()).toString().trim();
        String email    = Objects.requireNonNull(emailp.getEditText().getText()).toString().trim();
        String mobile   = Objects.requireNonNull(phoneNo.getEditText().getText()).toString().trim();
        String pass     = Objects.requireNonNull(password.getEditText().getText()).toString().trim();
       // String username = Objects.requireNonNull(UserName.getText()).toString().trim();



        // Error when Name is Not provided
        if(name.isEmpty()){
            fullname.setError("Name is required");
            fullname.requestFocus();
            pb.setVisibility(View.GONE);
        }
        else{
            chkName(name);
        }

        // Error when Email is not provided
        if(email.isEmpty()){
            emailp.setError("Email address is required");
            emailp.requestFocus();
            pb.setVisibility(View.GONE);
        }

        // Error when invalid email format is given
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailp.setError("Please Enter valid Email Address");
            emailp.requestFocus();
            pb.setVisibility(View.GONE);
        }
        else {
            ChkEmail(email);
        }

        // Error when mobile number is not provided
        if (mobile.isEmpty()){
            phoneNo.setError("Contact Number is required");
            phoneNo.requestFocus();
            pb.setVisibility(View.GONE);

        }
        // Error when invalid mobile number id given
        else if (mobile.length() != 10){
            phoneNo.setError("Enter valid 10-digit phone Number");
            phoneNo.requestFocus();
            pb.setVisibility(View.GONE);
        }
        else {
            ChkMobile(mobile);
        }

        if (!pass.isEmpty()){
            if (pass.length() < 6){
                password.setError("Password is too short. Minimum 6 characters required");
                password.requestFocus();
                //pb.setVisibility(View.GONE);
            }
            else {
                ChkPass(pass);
            }
        }

        if (update_password && (update_email || update_name || update_number )){
            Toast.makeText(getApplicationContext(), "Password and data updated", Toast.LENGTH_LONG).show();
            pb.setVisibility(View.GONE);
        }
        else if (update_password){
            Toast.makeText(getApplicationContext(), "Password Updated successfully", Toast.LENGTH_LONG).show();
            pb.setVisibility(View.GONE);
        }
        else if (!update_email || update_name || update_number ){//here ! is added only for toast msg debugging the actual logic is only updated is true...and it's working
            Toast.makeText(getApplicationContext(), "Data Updated",Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(getApplicationContext(), "Are you sure you want to change something?", Toast.LENGTH_SHORT).show();
            pb.setVisibility(View.GONE);
        }

        //pb.setVisibility(View.GONE);
    }

    private void ChkPass(String pass) {
        mAuth.getCurrentUser().updatePassword(pass);
        update_password = true;
    }

    // Error to be fixed where entire database gets deleted (try removing On completion listener)
    private void ChkMobile(String mobile) {
        if(! mobile.equals(or_number)){
            reference.child(EncoderEmail.EncodeString(mAuth.getCurrentUser().getEmail())).child("phoneNo").setValue(mobile);
            update_number= true;
            or_number = mobile;
        }
        else {
            update_number = false;
        }

    }


    private void chkName(String name) {
        if (! name.equals(or_name)){
            reference.child(EncoderEmail.EncodeString(mAuth.getCurrentUser().getEmail())).child("fullname").setValue(name);
            or_name = name;
            update_name = true;
        }
        else{
            update_name = false;
        }

    }

    private void ChkEmail(final String email) {
        if (!email.equals(or_email)){
            Query check_user = reference.orderByChild("email").equalTo(EncoderEmail.EncodeString(email));
            check_user.addListenerForSingleValueEvent(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
                        String name     = Objects.requireNonNull(fullname.getEditText().getText()).toString().trim();
                        String email    = Objects.requireNonNull(emailp.getEditText().getText()).toString().trim();
                        String mobile   = Objects.requireNonNull(phoneNo.getEditText().getText()).toString().trim();
                        reference.child(EncoderEmail.EncodeString(mAuth.getCurrentUser().getEmail())).removeValue(); // deletes node with old email address

                        UserHelperClass obj2 = new UserHelperClass(name, EncoderEmail.EncodeString(email), mobile);

                        reference.child(EncoderEmail.EncodeString(email)).setValue(obj2); // Creates an new node with updated email address
                        mAuth.getCurrentUser().updateEmail(email);

                        or_email = email;
                        update_email = true;
                    }
                    else {
                        emailp.setError("Email linked with another account");
                        emailp.requestFocus();
                        update_email = false;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else{
            update_email = false;
        }
    }
    private void showAllData() {

        Intent intent = getIntent();
        String user_fullname = intent.getStringExtra("fullname");
        String user_phoneNo = intent.getStringExtra("phoneNo");
        String user_email = intent.getStringExtra("email");
        String user_password = intent.getStringExtra("password");

        user_email = EncoderEmail.DecodeString(user_email);

        fullname.getEditText().setText(user_fullname);
        phoneNo.getEditText().setText(user_phoneNo);
        emailp.getEditText().setText(user_email);
        password.getEditText().setText(user_password);

        or_name = fullname.getEditText().getText().toString();
        or_email = emailp.getEditText().getText().toString();
        or_number = phoneNo.getEditText().getText().toString();

    }
}