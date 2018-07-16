package com.dukhaan.techy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Trace;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class EditProfileActivity extends BaseActivity {

    EditText name;
    Button cllg;
    Button addr;
    EditText phNum;
    EditText mail;
    Button update;
    User me = null;
    String lat = null, lng = null, my_addr = null;
    String cllg_lat = null, cllg_lng = null, cllg_addr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        init();
        refreshUser();
    }

    private void init() {
        name = (EditText) findViewById(R.id.userName);
        cllg = (Button) findViewById(R.id.college);
        addr = (Button) findViewById(R.id.address);
        phNum = (EditText) findViewById(R.id.phone);
        mail = (EditText) findViewById(R.id.mail);
        update = (Button) findViewById(R.id.btnUpdateProfile);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(EditProfileActivity.this), 1);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        cllg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(EditProfileActivity.this), 2);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
        mProductTable.addValueEventListener(pvel);
    }

    ValueEventListener pvel = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            assert mUser != null;

            for (DataSnapshot product : dataSnapshot.getChildren()) {
                Product p = product.getValue(Product.class);
                assert p != null;
                if (p.getUid().equals(mUser.getUid()))
                    mUserTable.child(mUser.getUid()).child("prod_gist")
                            .child(p.getPid()).setValue(p.getName() + "," + p.getCat());
            }
            mProductTable.removeEventListener(this);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void updateUser(View view) {

        showProgressDialog();

        if (mUser == null) {
            hideProgressDialog();
            return;
        }

        if (me != null) {

            if (lat == null || lng == null || my_addr == null) {
                hideProgressDialog();
                Toast.makeText(this, "Please select your location.", Toast.LENGTH_SHORT).show();
                return;
            }else if (cllg_addr == null || cllg_lat == null || cllg_lng == null) {
                hideProgressDialog();
                Toast.makeText(this, "Please select college location.", Toast.LENGTH_SHORT).show();
                return;
            } else {

                me.setName(name.getText().toString());
                me.setPhNum(phNum.getText().toString());
                me.setMail(mail.getText().toString());

                me.setCllg(cllg_addr);
                me.setCllg_lat(cllg_lat);
                me.setCllg_lng(cllg_lng);

                me.setLat(lat);
                me.setLng(lng);
                me.setAddr(my_addr);

                HashMap<String, Object> user = new HashMap<>();
                user.put(mUser.getUid(), me);
                mUserTable.updateChildren(user);

                mProductTable.addValueEventListener(pvel);

            }
        }
        hideProgressDialog();
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();

        finish();

    }

    private void refreshUser() {
        showProgressDialog();

        mUserTable.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                me = dataSnapshot.getValue(User.class);
                if (me != null) {
                    name.setText(me.getName());
                    phNum.setText(me.getPhNum());
                    mail.setText(me.getMail());

                    lat = me.getLat();
                    lng = me.getLng();
                    my_addr = me.getAddr();

                    cllg_addr = me.getCllg();
                    cllg_lat = me.getCllg_lat();
                    cllg_lng = me.getCllg_lng();

                    if (!TextUtils.isEmpty(me.getAddr()))
                        ((TextView) findViewById(R.id.addr)).setText(me.getAddr());
                    if (!TextUtils.isEmpty(me.getCllg()))
                        ((TextView) findViewById(R.id.cllg)).setText(me.getCllg());

                } else {
                    Toast.makeText(EditProfileActivity.this, "No user Found", Toast.LENGTH_SHORT).show();
                }
                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
                    lat = String.valueOf(place.getLatLng().latitude);
                    lng = String.valueOf(place.getLatLng().longitude);
                    my_addr = place.getName().toString();
                    ((TextView) findViewById(R.id.addr)).setText(place.getName());
                }
            }
        } else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                if (place != null) {
                    cllg_lat = String.valueOf(place.getLatLng().latitude);
                    cllg_lng = String.valueOf(place.getLatLng().longitude);
                    cllg_addr = place.getName().toString();
                    ((TextView) findViewById(R.id.cllg)).setText(place.getName());
                }
            }
        }
    }

}
