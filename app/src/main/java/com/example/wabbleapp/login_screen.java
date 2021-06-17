package com.example.wabbleapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
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
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Arrays;

public class login_screen extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    Button callSignUp,loginbutton,forgotPass;
    TextInputLayout email, password;
    LoginButton facebook_login_button; // Type is LoginButton not Button
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    ProgressBar pb;
    TextView sloganlogin;
    ImageView logologin;
    private CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callbackManager = CallbackManager.Factory.create();

        setContentView(R.layout.activity_login_screen);

        facebook_login_button = (LoginButton) findViewById(R.id.facebook_login_button);
        facebook_login_button.setPermissions(Arrays.asList(EMAIL, "public_profile"));

        sloganlogin = findViewById(R.id.signintocontinue);
        logologin = findViewById(R.id.signinlogo);
        callSignUp = findViewById(R.id.Signuptext);
        loginbutton = findViewById(R.id.signinbutton);
        email = findViewById(R.id.signinemail);
        password = findViewById(R.id.signinpassword);
        forgotPass = findViewById(R.id.forgetpassword);
        pb = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
        rootNode = FirebaseDatabase.getInstance();


        // This code is to check if the person has access token for facebook or not.
        // If the access token is missing then don't login else proceed to login method
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        // Login can be done with following code
        //LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        // The code returns a user ig. Print to see what the user returns before proceeding.
        // Also you can setup a Access Token tracker to implement the login method
        // The exact usage depends on your code

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        SignInButton signInButton = findViewById(R.id.googlesignin);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.googlesignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GooglesignIn();
            }
        });

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (login_screen.this,signup_screen.class);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(logologin,"logotransition");
                pairs[1] = new Pair<View, String>(sloganlogin,"slogantransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(login_screen.this,pairs);

                startActivity(intent,options.toBundle());
                finish();
            }
        });


        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                ConnectivityManager cm =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (!isConnected) {
                    Toast.makeText(getApplicationContext(), "Connection Error!", Toast.LENGTH_LONG).show();
                } else {
                    pb.setVisibility(View.VISIBLE);
                    LoginUser(view);
                }
            }
        });

        facebook_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResults) {
                GraphRequest request = GraphRequest.newMeRequest(loginResults.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            Log.v("LoginActivity", object.toString());
                            final Intent intent = new Intent(getApplicationContext(), profile_screen.class);
                            intent.putExtra("fullname", object.getString("name"));
                            intent.putExtra("phoneNo", "9777100189");
                            intent.putExtra("email", object.getString("email"));
                            intent.putExtra("isFbLogin", "true");
                            intent.putExtra("isGLlogin","false");
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
                // App code
            }
            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });


        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = email.getEditText().getText().toString();

                if(emailAddress.isEmpty()){
                    email.setError("No Email Provided");
                    email.requestFocus();
                }
                else if (! Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
                    email.setError("Invalid Email Address");
                    email.requestFocus();
                }
                else{
                    // For checking internet connection
                    Context context = getApplicationContext();
                    ConnectivityManager cm =
                            (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

                    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                    boolean isConnected = activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting();
                    if(!isConnected){
                        pb.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Connection Error!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        auth.sendPasswordResetEmail(emailAddress)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Password link reset link sent to currently provided email address", Toast.LENGTH_LONG).show();
                                        }
                                        else{
                                            pb.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }
            }
        });


    }

    private void GooglesignIn() {
        Log.v("Main_Login", "Signin");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount acct = completedTask.getResult(ApiException.class);
            Log.v("Main_Login", acct.toString());
//            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
            final Intent intent = new Intent(getApplicationContext(), profile_screen.class);
            String personName = acct.getDisplayName();
//                String personGivenName = acct.getGivenName();
//                String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
//                String personId = acct.getId();
//                Uri personPhoto = acct.getPhotoUrl();
            intent.putExtra("fullname",personName);
            intent.putExtra("email",personEmail);
            intent.putExtra("phoneNo","9777100189");
            intent.putExtra("isFbLogin","false");
            intent.putExtra("isGLlogin","true");
            Log.v("Main_Login","Running_till_now");
            startActivity(intent);
            finish();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.v("Main Error",e.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        else
        {
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    private Boolean validateEmail(){
        String val = email.getEditText().getText().toString();

        if(val.isEmpty()){
            pb.setVisibility(View.GONE);
            email.setError("Field cannot be empty");
            return false;
        }else
            email.setError(null);
        email.setErrorEnabled(false);
        return true;
    }

    private  Boolean validatePassword(){
        String val = password.getEditText().getText().toString();

        if(val.isEmpty()){
            pb.setVisibility(View.GONE);
            password.setError("Field cannot be empty");
            return false;
        }else
            password.setError(null);
        password.setErrorEnabled(false);
        return true;
    }

    public void LoginUser(View view){
        //validate login info
        if (!validateEmail() | !validatePassword()){
            return;
        }else
            isUser(view);
    }

    private void isUser(View view){

        rootNode = FirebaseDatabase.getInstance();
        reference = rootNode.getReference("users");
        final String Email = EncoderEmail.EncodeString(email.getEditText().getText().toString());
        //Email = EncoderEmail.EncodeString(Email);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUser = reference.orderByChild("email").equalTo(Email);

        final String userEnteredEmail = EncoderEmail.DecodeString(Email);
        final String userEnteredPassword = password.getEditText().getText().toString().trim();


        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    email.setError(null);
                    email.setErrorEnabled(false);

                    final String nameFromDataBase = snapshot.child(Email).child("fullname").getValue(String.class);
                    final String emailFromDataBase = snapshot.child(Email).child("email").getValue(String.class);
                    final String phoneNoFromDataBase = snapshot.child(Email).child("phoneNo").getValue(String.class);

                    UserHelperClass helperClass = new UserHelperClass();
                    helperClass.setFullname(nameFromDataBase);
                    helperClass.setEmail(emailFromDataBase);
                    helperClass.setPhoneNo(phoneNoFromDataBase);

                    mAuth.signInWithEmailAndPassword(userEnteredEmail,userEnteredPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                final  Intent intent = new Intent(getApplicationContext(),profile_screen.class);
                                intent.putExtra("fullname",nameFromDataBase);
                                intent.putExtra("phoneNo",phoneNoFromDataBase);
                                intent.putExtra("email",emailFromDataBase);
                                intent.putExtra("isFbLogin", "false");
                                intent.putExtra("isGLlogin", "false");


                                Pair[] pairs = new Pair[2];
                                pairs[0] = new Pair<View, String>(logologin,"logotransition");
                                pairs[1] = new Pair<View, String>(sloganlogin,"slogantransition");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(login_screen.this,pairs);

                                startActivity(intent,options.toBundle());
                                finish();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                                pb.setVisibility(View.GONE);

                            }
                        }
                    });

                }
                else
                    email.setError("No Such User Exists");
                email.requestFocus();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}