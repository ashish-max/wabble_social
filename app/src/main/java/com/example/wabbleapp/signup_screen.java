package com.example.wabbleapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class signup_screen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    TextInputLayout signupName, signupEmail, signupPhoneNo, signupPassword;
    Button callSignIn, signUP;
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    ProgressBar pb;
    //ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);

        callSignIn = findViewById(R.id.Logintext);
        signUP = findViewById(R.id.signupbutton);
        signupName = findViewById(R.id.signupfullname);
        signupEmail = findViewById(R.id.signupemail);
        signupPhoneNo = findViewById(R.id.signupmobilenumber);
        signupPassword = findViewById(R.id.signuppassoword);
        pb = findViewById(R.id.progressBarsignup);

        mAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();

        callSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup_screen.this, login_screen.class);
                startActivity(intent);
                finish();
            }
        });

        signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                ConnectivityManager cm =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (!isConnected) {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Connection Error!", Toast.LENGTH_LONG).show();
                } else {
                    resisterUser(view);// User defined function
                }
            }
        });
    }

    private Boolean validateName() {
        String val = signupName.getEditText().getText().toString();

        if (val.isEmpty()) {
            signupName.setError("Field cannot be empty");
            pb.setVisibility(View.GONE);
            return false;
        } else {
            signupName.setError(null);
            signupName.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatPhoneNo() {
        String val = signupPhoneNo.getEditText().getText().toString();

        if (val.isEmpty()) {
            signupPhoneNo.setError("Field cannot be empty");
            pb.setVisibility(View.GONE);
            return false;
        } else if (val.length() > 10) {
            signupPhoneNo.setError("Please Enter 10 digit mobile number");
            pb.setVisibility(View.GONE);
            return false;
        } else {
            signupPhoneNo.setError(null);
            signupPhoneNo.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validateEmail() {
        String val = signupEmail.getEditText().getText().toString();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (val.isEmpty()) {
            signupEmail.setError("Field cannot be empty");
            pb.setVisibility(View.GONE);
            return false;
        } else if (!val.matches(emailPattern)) {
            signupEmail.setError("Invalid Email Address");
            pb.setVisibility(View.GONE);
            return false;
        } else {
            signupEmail.setError(null);
            signupEmail.setErrorEnabled(false);
            return true;
        }
    }

    private Boolean validatePassword() {
        String val = signupPassword.getEditText().getText().toString();

        if (val.isEmpty()) {
            signupPassword.setError("Field cannot be empty");
            pb.setVisibility(View.GONE);
            return false;
        } else if (val.length() <= 6) {
            signupPassword.setError("Invalid Password[must contain atleast 6 characters]");
            pb.setVisibility(View.GONE);
            return false;
        }
        else {
            signupPassword.setError(null);
            signupPassword.setErrorEnabled(false);
            return true;
        }
    }

    //savedata in firebase on button click
    public void resisterUser(View view) {

        if(!validateName() | !validatPhoneNo() | !validateEmail() | !validatePassword()){
            return;
        }

        //get all values in string
        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        final String fullname = signupName.getEditText().getText().toString();
        final String signupemail = signupEmail.getEditText().getText().toString();
        final String useremail = EncoderEmail.EncodeString(signupEmail.getEditText().getText().toString());
        final String phoneNo = signupPhoneNo.getEditText().getText().toString();
        final String password = signupPassword.getEditText().getText().toString();
        // UserHelperClass helperClass = new UserHelperClass(fullname, useremail, phoneNo, password);
        // reference.child(useremail).setValue(helperClass);
        final Query checkUser = reference.orderByChild("email").equalTo(useremail);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    signupEmail.setError("Email already linked with another account");
                    signupEmail.requestFocus();
                    pb.setVisibility(View.GONE);
                }
                else{
                    UserHelperClass helperClass = new UserHelperClass(fullname, useremail, phoneNo);
                    reference.child(useremail).setValue(helperClass);
                    createUser(signupemail,password);
                    Intent intent = new Intent(getApplicationContext(),login_screen.class);
                    finish();
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void createUser (String useremail,String password){

        mAuth.createUserWithEmailAndPassword(useremail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(),"User registerd to database",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}