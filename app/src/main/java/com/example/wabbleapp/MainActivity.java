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
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 5000;
    //variables
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase rootnode = FirebaseDatabase.getInstance();
    DatabaseReference reference;

    Animation topAnim, bottomAnim;
    ImageView image;
    TextView slog1,slog2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        //for full screen
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //Animations
        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        //Hooks
        image = findViewById(R.id.logosplit);
        slog1 = findViewById(R.id.slogan1);
        slog2 = findViewById(R.id.slogan2);

        //set animations
        image.setAnimation(topAnim);
        slog1.setAnimation(bottomAnim);
        slog2.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {

                Context context = getApplicationContext();
                ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if(!isConnected){
                    Toast.makeText(getApplicationContext(), "Connection Error!", Toast.LENGTH_LONG).show();
                }
                if(mAuth.getCurrentUser() != null){
                    final String Email = EncoderEmail.EncodeString(mAuth.getCurrentUser().getEmail());
                    reference = rootnode.getReference("users");
                    Query check_email = reference.orderByChild("email").equalTo(Email);

                    check_email.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if(snapshot.exists()){

                                String NameFormDb     = snapshot.child(Email).child("fullname").getValue(String.class);
                                String EmailFromDb    = EncoderEmail.DecodeString(snapshot.child(Email).child("email").getValue(String.class));
                               // String UserNameFromDb = snapshot.child(Email).child("userName").getValue(String.class);
                                String PhoneFromDb    = snapshot.child(Email).child("phoneNo").getValue(String.class);

                                Intent Profile = new Intent(getApplicationContext(), profile_screen.class);



                                Profile.putExtra("fullname",NameFormDb);
                                Profile.putExtra("email", EmailFromDb);
                                //Profile.putExtra("UserName", UserNameFromDb);
                                Profile.putExtra("phoneNo", PhoneFromDb);

                               // Profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                               // finish();

                                Pair[] pairs = new Pair[2];
                                pairs[0] = new Pair<View, String>(image,"logotransition");
                                pairs[1] = new Pair<View, String>(slog1,"slogantransition");

                                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);


                                startActivity(Profile, options.toBundle());
                                finish();
                            }
                            else{
                                mAuth.signOut();
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
                else{
                    Intent intent = new Intent(getApplicationContext(),login_screen.class);

                    Pair[] pairs = new Pair[2];
                    pairs[0] = new Pair<View, String>(image,"logotransition");
                    pairs[1] = new Pair<View, String>(slog1,"slogantransition");

                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);

                    startActivity(intent, options.toBundle());
                    finish();
                }
                //Intent intent = new Intent(MainActivity.this,login_screen.class);
                //startActivity(intent);
                //finish();
            }
        },SPLASH_SCREEN);
    }
}