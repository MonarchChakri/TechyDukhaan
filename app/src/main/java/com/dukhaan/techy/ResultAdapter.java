package com.dukhaan.techy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;

public class ResultAdapter extends ArrayAdapter<Product> implements Serializable {
    private Context mContext;
    private int mLayoutResourceId;
    private BaseActivity mActivity;

    private DatabaseReference mUserTable = FirebaseDatabase.getInstance().getReference("users");

    private AlertDialog d;
    private boolean isListenerAlreadyAttached = false;

    public FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference mRequestTable = FirebaseDatabase.getInstance().getReference("requests");


    ResultAdapter(Context context, BaseActivity activity, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mActivity = activity;
        mLayoutResourceId = layoutResourceId;

        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext)
                .setCancelable(false).setMessage("Update Profile before requesting details.\nEdit Profile?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent editProfile = new Intent(mContext, EditProfileActivity.class);
                        mContext.startActivity(editProfile);
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        d = dialog.create();


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
        View row = convertView;

        final Product currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);

        assert currentItem != null;
        ((TextView) row.findViewById(R.id.resprodname)).setText(currentItem.getName());
        ((TextView) row.findViewById(R.id.resprodprice)).setText(currentItem.getPrice());

        GetImage(((ImageView) row.findViewById(R.id.resprodimg)), currentItem);

        row.findViewById(R.id.btnRequest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isListenerAlreadyAttached) {
                    refreshUser(currentItem);
                    isListenerAlreadyAttached = true;
                }
            }
        });

        return row;
    }

    private void addListener(final Request request) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            return;
        }
        mRequestTable.child(request.getSid()).child(request.getPid())
                .addValueEventListener(new myVel(null, request, null) {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() == null) {
                            mRequestTable.child(request.getSid()).child(request.getPid()).push().setValue(request.getBid() + "," + request.getStatus())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(mContext, "Your request has been forwarded to owner", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            boolean nobreak = true;
                            for (DataSnapshot buyer: dataSnapshot.getChildren()) {
                                if (buyer.getValue().equals(request.getBid() + "," + request.getStatus())) {
                                    Toast.makeText(mContext, "Already Requested.", Toast.LENGTH_SHORT).show();
                                    nobreak = false;
                                    break;
                                } else if (buyer.getValue().equals(request.getBid() + ",accepted")) {
                                    Toast.makeText(mContext, "Request accepted\nCheck in accepted requests tab.", Toast.LENGTH_SHORT).show();
                                    nobreak = false;
                                    break;
                                }
                            }
                            if (nobreak) {
                                mRequestTable.child(request.getSid()).child(request.getPid()).push().setValue(request.getBid() + "," + request.getStatus())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(mContext, "Your request has been forwarded to owner", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        }
                        mRequestTable.child(request.getSid()).child(request.getPid()).removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void refreshUser(final Product temp) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mActivity.showProgressDialog();
        mUserTable.child(mUser.getUid()).addValueEventListener(new myVel() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUser = FirebaseAuth.getInstance().getCurrentUser();
                User me = dataSnapshot.getValue(User.class);
                if (me != null) {
                    boolean isProfileComplete = !TextUtils.isEmpty(me.getName()) &&
                            !TextUtils.isEmpty(me.getLat()) && !TextUtils.isEmpty(me.getLng()) && !TextUtils.isEmpty(me.getAddr()) &&
                            !TextUtils.isEmpty(me.getCllg_lat()) && !TextUtils.isEmpty(me.getCllg_lng()) && !TextUtils.isEmpty(me.getCllg())
                            && !TextUtils.isEmpty(me.getPhNum());
                    if (isProfileComplete && isListenerAlreadyAttached) {
                        mActivity.hideProgressDialog();
                        Request request = new Request();
                        request.setPid(temp.getPid());
                        request.setBid(mUser.getUid());
                        request.setSid(temp.getUid());
                        request.setStatus("requested");
                        addListener(request);
                    } else {
                        mActivity.hideProgressDialog();
                        d.show();
                    }
                    isListenerAlreadyAttached = false;
                } else {
                    mActivity.hideProgressDialog();
                    Toast.makeText(mContext, "No user Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}