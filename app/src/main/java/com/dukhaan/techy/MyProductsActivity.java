package com.dukhaan.techy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyProductsActivity extends BaseActivity {

    private MyProductAdapter mAdapter;
    List<Product> results = new ArrayList<>();
    AlertDialog.Builder dialog;
    AlertDialog d;
    boolean isListenerAlreadyAttached = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_products);

        ListView prodlv = findViewById(R.id.lvMyProducts);
        mAdapter = new MyProductAdapter(MyProductsActivity.this, R.layout.myproductslist_itemview);
        prodlv.setAdapter(mAdapter);

        (findViewById(R.id.btnAddProd)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isListenerAlreadyAttached) {
                    refreshUser();
                    isListenerAlreadyAttached = true;
                }
            }
        });

        dialog = new AlertDialog.Builder(MyProductsActivity.this)
                .setCancelable(false).setMessage("Update Profile before adding products.\nEdit Profile?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent editProfile = new Intent(MyProductsActivity.this, EditProfileActivity.class);
                        startActivity(editProfile);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

        d = dialog.create();

        mProductTable.addValueEventListener(pvel);
        mAdapter.clear();
    }

    ValueEventListener pvel = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            results.clear();
            mAdapter.clear();
            for (DataSnapshot product : dataSnapshot.getChildren()) {
                Product p = product.getValue(Product.class);
                assert p != null;
                if (p.getUid().equals(mUser.getUid()))
                    results.add(p);
            }
            for (Product item : results) {
                mAdapter.add(item);
            }
            if (results.size() == 0) {
                Toast.makeText(MyProductsActivity.this, "No Products Found!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private void refreshUser() {
        showProgressDialog();
        mUserTable.child(mUser.getUid()).addValueEventListener(uvel);
    }

    ValueEventListener uvel = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            User me = dataSnapshot.getValue(User.class);
            if (me != null) {
                boolean isProfileComplete = !TextUtils.isEmpty(me.getName()) &&
                        !TextUtils.isEmpty(me.getLat()) && !TextUtils.isEmpty(me.getLng()) && !TextUtils.isEmpty(me.getAddr()) &&
                        !TextUtils.isEmpty(me.getCllg_lat()) && !TextUtils.isEmpty(me.getCllg_lng()) && !TextUtils.isEmpty(me.getCllg())
                        && !TextUtils.isEmpty(me.getPhNum());
                if (isProfileComplete && isListenerAlreadyAttached) {
                    hideProgressDialog();
                    mUserTable.child(mUser.getUid()).removeEventListener(uvel);
                    isListenerAlreadyAttached = false;
                    startActivity(new Intent(MyProductsActivity.this, ProdAddActivity.class));
                } else {
                    hideProgressDialog();
                    d.show();
                }
            } else {
                hideProgressDialog();
                Toast.makeText(MyProductsActivity.this, "No user Found", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}
