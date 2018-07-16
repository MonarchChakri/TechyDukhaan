package com.dukhaan.techy;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RequestsActivity extends BaseActivity {

    private RequestsAdapter mAdapter;
    List<Request> results = new ArrayList<>();
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        ListView reqlv = findViewById(R.id.lvReqs);
        type = (String) getIntent().getExtras().get("type");
        mAdapter = new RequestsAdapter(RequestsActivity.this, type, RequestsActivity.this, R.layout.request_item);
        reqlv.setAdapter(mAdapter);

        mRequestsTable.addValueEventListener(rvel);
        mAdapter.clear();
    }

    ValueEventListener rvel = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mUser = FirebaseAuth.getInstance().getCurrentUser();
            results.clear();
            mAdapter.clear();

            for (DataSnapshot request : dataSnapshot.getChildren()) {
                String sid = request.getKey();
                if (type.equals("in") && sid.equals(mUser.getUid())) {
                    for (DataSnapshot product : request.getChildren()) {
                        String pid = product.getKey();
                        for (DataSnapshot buyer : product.getChildren()) {
                            String bidStatus = buyer.getValue(String.class);
                            assert bidStatus != null;
                            int comma = bidStatus.indexOf(',');
                            String bid = bidStatus;
                            String status = "";
                            if (comma > 0) {
                                bid = bidStatus.substring(0, comma);
                                status = bidStatus.substring(comma + 1, bidStatus.length());
                            }
                            if (status.equals("requested")) {
                                Request r = new Request(bid, pid, sid, status);
                                results.add(r);
                            }
                        }
                    }
                }
            }

            for (DataSnapshot request : dataSnapshot.getChildren()) {
                String sid = request.getKey();
                for (DataSnapshot product : request.getChildren()) {
                    String pid = product.getKey();
                    for (DataSnapshot buyer : product.getChildren()) {
                        String bidStatus = buyer.getValue(String.class);
                        assert bidStatus != null;
                        int comma = bidStatus.indexOf(',');
                        String bid = bidStatus;
                        String status = "";
                        if (comma > 0) {
                            bid = bidStatus.substring(0, comma);
                            status = bidStatus.substring(comma + 1, bidStatus.length());
                        }
                        if (type.equals("out") && bid.equals(mUser.getUid())) {
                            if (status.equals("requested")) {
                                Request r = new Request(bid, pid, sid, status);
                                results.add(r);
                            }
                        }
                    }
                }
            }

            for (DataSnapshot request : dataSnapshot.getChildren()) {
                String sid = request.getKey();
                for (DataSnapshot product : request.getChildren()) {
                    String pid = product.getKey();
                    for (DataSnapshot buyer : product.getChildren()) {
                        String bidStatus = buyer.getValue(String.class);
                        assert bidStatus != null;
                        int comma = bidStatus.indexOf(',');
                        String bid = bidStatus;
                        String status = "";
                        if (comma > 0) {
                            bid = bidStatus.substring(0, comma);
                            status = bidStatus.substring(comma + 1, bidStatus.length());
                        }
                        if (type.equals("accepted") && bid.equals(mUser.getUid())) {
                            if (status.equals("accepted")) {
                                Request r = new Request(bid, pid, sid, status);
                                results.add(r);
                            }
                        }
                    }
                }
            }

            for (Request item : results) {
                mAdapter.add(item);
            }

            if (results.size() == 0) {
                Toast.makeText(RequestsActivity.this, "No Requests Found!", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    protected void onRestart() {
        super.onRestart();
        mRequestsTable.addValueEventListener(rvel);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRequestsTable.addValueEventListener(rvel);
    }

    @Override
    public void onStop() {
        super.onStop();
        mRequestsTable.removeEventListener(rvel);
        mAdapter.clear();
        results.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestsTable.removeEventListener(rvel);
        mAdapter.clear();
        results.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mRequestsTable.removeEventListener(rvel);
        mAdapter.clear();
        results.clear();
    }
}
