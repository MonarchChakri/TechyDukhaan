package com.dukhaan.techy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class RequestsAdapter extends ArrayAdapter<Request> implements Serializable {
    Context mContext;
    int mLayoutResourceId;
    View row;
    private RequestsAdapter mAdapter;
    private BaseActivity mActivity;
    private String bid, sid, pid, type;

    public FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mProductTable = FirebaseDatabase.getInstance().getReference("products");
    private DatabaseReference mRequestTable = FirebaseDatabase.getInstance().getReference("requests");
    private DatabaseReference mUserTable = FirebaseDatabase.getInstance().getReference("users");

    RequestsAdapter(Context context, String type, BaseActivity activity, int layoutResourceId) {
        super(context, layoutResourceId);

        mActivity = activity;
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mAdapter = this;
        this.type = type;
    }

    private void GetImage(final ImageView imageView, final Product currentItem) {
        if (currentItem.getImg() != null) {
            if (!currentItem.getImg().equals("")) {
                Glide.with(mContext).load(currentItem.getImg()).into(imageView);
            }
        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        row = convertView;

        final Request currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);

        assert currentItem != null;
        pid = currentItem.getPid();
        bid = currentItem.getBid();
        sid = currentItem.getSid();

        mUserTable.child(bid).addValueEventListener(new ValueEventListener() {
            final View row_loc = row;
            final String bid_loc = bid;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                if (temp != null) {
                    if (temp.getUid() != null) {
                        if (temp.getUid().equals(bid_loc)) {
                            ((TextView) row_loc.findViewById(R.id.buyer)).setText(temp.getName());
                        } else {
                            Toast.makeText(mContext, "Buyer not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mUserTable.child(sid).addValueEventListener(new ValueEventListener() {
            final String sid_loc = sid;
            final View row_loc = row;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User temp = dataSnapshot.getValue(User.class);
                if (temp != null) {
                    if (temp.getUid() != null) {
                        if (temp.getUid().equals(sid_loc)) {
                            ((TextView) row_loc.findViewById(R.id.seller)).setText(temp.getName());
                        } else {
                            Toast.makeText(mContext, "Seller not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mProductTable.child(pid).addValueEventListener(new ValueEventListener() {
            final View row_loc = row;
            final String pid_loc = pid;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product temp = dataSnapshot.getValue(Product.class);
                if (temp != null) {
                    if (temp.getPid() != null) {
                        if (temp.getPid().equals(pid_loc)) {
                            ((TextView) row_loc.findViewById(R.id.reqprodprice)).setText(temp.getPrice());
                            ((TextView) row_loc.findViewById(R.id.reqprodname)).setText(temp.getName());
                            GetImage(((ImageView) row_loc.findViewById(R.id.reqprodimg)), temp);
                        } else {
                            Toast.makeText(mContext, "Product not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        switch (type) {
            case "in":
                row.findViewById(R.id.declbutton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.showProgressDialog();
                        mRequestTable.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot request : dataSnapshot.getChildren()) {
                                    String sid = request.getKey();
                                    if (sid != null && sid.equals(mUser.getUid())) {
                                        for (DataSnapshot product : request.getChildren()) {
                                            String pid = product.getKey();
                                            assert pid != null;
                                            if (pid.endsWith(currentItem.getPid())) {
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
                                                    if (bid.equals(currentItem.getBid())) {
                                                        Toast.makeText(mContext, "Request declined.", Toast.LENGTH_SHORT).show();
                                                        mRequestTable.removeEventListener(this);
                                                        mRequestTable.child(sid).child(pid).child(buyer.getKey()).removeValue();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                mActivity.hideProgressDialog();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                row.findViewById(R.id.accbutton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.showProgressDialog();
                        mRequestTable.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot request : dataSnapshot.getChildren()) {
                                    String sid = request.getKey();
                                    if (sid != null && sid.equals(mUser.getUid())) {
                                        for (DataSnapshot product : request.getChildren()) {
                                            String pid = product.getKey();
                                            assert pid != null;
                                            if (pid.endsWith(currentItem.getPid())) {
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
                                                    if (bid.equals(currentItem.getBid())) {
                                                        Toast.makeText(mContext, "Request accepted.", Toast.LENGTH_SHORT).show();
                                                        mRequestTable.removeEventListener(this);
                                                        mRequestTable.child(sid).child(pid).child(buyer.getKey()).setValue(bid + "," + "accepted");
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                mActivity.hideProgressDialog();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                break;
            case "out":
                row.findViewById(R.id.accbutton).setVisibility(View.GONE);
                ((Button) row.findViewById(R.id.declbutton)).setText("Cancel Request");
                row.findViewById(R.id.declbutton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.showProgressDialog();
                        mRequestTable.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot request : dataSnapshot.getChildren()) {
                                    String sid = request.getKey();
                                    if (sid != null) {
                                        for (DataSnapshot product : request.getChildren()) {
                                            String pid = product.getKey();
                                            assert pid != null;
                                            if (pid.endsWith(currentItem.getPid())) {
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
                                                    if (bid.equals(mUser.getUid())) {
                                                        Toast.makeText(mContext, "Request cancelled.", Toast.LENGTH_SHORT).show();
                                                        mRequestTable.removeEventListener(this);
                                                        mRequestTable.child(sid).child(pid).child(buyer.getKey()).removeValue();
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                mActivity.hideProgressDialog();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                break;
            case "accepted":
                row.findViewById(R.id.declbutton).setVisibility(View.GONE);
                ((Button) row.findViewById(R.id.accbutton)).setText("Details");
                row.findViewById(R.id.accbutton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mActivity.showProgressDialog();
                        mRequestTable.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot request : dataSnapshot.getChildren()) {
                                    String sid = request.getKey();
                                    if (sid != null) {
                                        for (DataSnapshot product : request.getChildren()) {
                                            String pid = product.getKey();
                                            assert pid != null;
                                            if (pid.endsWith(currentItem.getPid())) {
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
                                                    if (bid.equals(mUser.getUid())) {
                                                        Intent detailsIntent = new Intent(mActivity, ProdDetailsActivity.class);
                                                        detailsIntent.putExtra("pid", currentItem.getPid());
                                                        mActivity.startActivity(detailsIntent);
                                                        mRequestTable.removeEventListener(this);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                mActivity.hideProgressDialog();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                break;
        }
        return row;

    }

}
