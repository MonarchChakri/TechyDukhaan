package com.dukhaan.techy;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ResultsActivity extends BaseActivity {

    private ResultAdapter mAdapter;
    List<Product> products = new ArrayList<>();
    List<User> users = new ArrayList<>();
    String src = null;
    int sort = 0;
    Set<Product> hs = new HashSet<>();
    User me = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        final String[] type = {"Location", "College"};
        final Spinner sortType = findViewById(R.id.spinnerSortType);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(ResultsActivity.this, android.R.layout.simple_dropdown_item_1line, type);
        sortType.setAdapter(adapter);

        ListView prodlv = findViewById(R.id.lvResultProducts);
        mAdapter = new ResultAdapter(this, ResultsActivity.this, R.layout.resultprodslayout);
        prodlv.setAdapter(mAdapter);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        src = (String) getIntent().getExtras().get("src");
        src = src.trim();
        src = src.toLowerCase();

        mUserTable.addValueEventListener(mevel);
        mProductTable.addValueEventListener(pvel);
        mUserTable.addValueEventListener(uvel);

        sortType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showProgressDialog();
                if (sortType.getSelectedItemPosition() == 0) {
                    sort = 0;
                } else {
                    sort = 1;
                }
                refreshItemsFromTable();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

    }

    private void refreshItemsFromTable() {
        hs.clear();
        hs.addAll(products);
        products.clear();
        products.addAll(hs);

        for (int i = 0, n = products.size(); i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (dist(products.get(j).getUid()) > dist(products.get(j + 1).getUid())) {
                    Product temp = products.get(j);
                    products.set(j, products.get(j + 1));
                    products.set(j + 1, temp);
                }
            }
        }



        mAdapter.clear();
        for (Product item : products) {
            mAdapter.add(item);
        }

        hideProgressDialog();
    }

    private double dist(String uid) {
        User other = null;
        for (int i = 0; i < users.size(); i++){
            if (users.get(i).getUid().equals(uid)){
                other = users.get(i);
            }
        }
        if (sort == 0) {
            assert other != null;
            return Math.abs((Double.parseDouble(other.getLat()) - Double.parseDouble(me.getLat())) * 111.2);
        }
        assert other != null;
        return Math.abs((Double.parseDouble(other.getCllg_lat()) - Double.parseDouble(me.getCllg_lat())) * 111.2);
    }

    ValueEventListener uvel = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            for (DataSnapshot user : dataSnapshot.getChildren()) {
                User u = user.getValue(User.class);
                assert u != null;
                if (!u.getUid().equals(mUser.getUid())) {
                    if (!users.contains(u)) {
                        users.add(u);
                    }
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ValueEventListener mevel = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            for (DataSnapshot user : dataSnapshot.getChildren()) {
                User u = user.getValue(User.class);
                assert u != null;
                if (u.getUid().equals(mUser.getUid())) {
                    me = u;
                    mUserTable.removeEventListener(this);
                    break;
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    ValueEventListener pvel = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            for (DataSnapshot product : dataSnapshot.getChildren()) {
                Product p = product.getValue(Product.class);
                assert p != null;
                if ((p.getName().toLowerCase().contains(src) || p.getCat().toLowerCase().contains(src)) && !p.getUid().equals(mUser.getUid())) {
                    if (!products.contains(p)) {
                        products.add(p);
                    }
                }
            }
            if (products.size() == 0)
                Toast.makeText(ResultsActivity.this, "No Products Found!", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

}
