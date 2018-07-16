package com.dukhaan.techy;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SharedPref extends AppCompatActivity {

    public FirebaseUser mUser;

    public DatabaseReference mProductTable = FirebaseDatabase.getInstance().getReference("products");
    public DatabaseReference mRequestsTable = FirebaseDatabase.getInstance().getReference("requests");
    public DatabaseReference mUserTable = FirebaseDatabase.getInstance().getReference("users");

    public static final int RC_SIGN_IN = 9001;
    public FirebaseAuth mAuth = null;
    public GoogleSignInClient mGoogleSignInClient;


    public String[] types = {"Reference Books", "Stationery", "Equipments", "Guide Books", "Written Notes", "Others"};
    public String[] ages = {"< 2 months", "2 months", "4 months", "8 months", "1 year", "> 1 year"};


    @Override
    protected void onStart() {
        super.onStart();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_pref);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}
