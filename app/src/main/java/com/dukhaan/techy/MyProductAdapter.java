package com.dukhaan.techy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.Serializable;

public class MyProductAdapter extends ArrayAdapter<Product> implements Serializable {
    Context mContext;
    int mLayoutResourceId;
    View row;
    private MyProductAdapter mAdapter;

    public FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
    public DatabaseReference mProductTable = FirebaseDatabase.getInstance().getReference("products");
    public DatabaseReference mRequestTable = FirebaseDatabase.getInstance().getReference("requests");
    public DatabaseReference mUserTable = FirebaseDatabase.getInstance().getReference("users");


    public MyProductAdapter(Context context, int layoutResourceId) {
        super(context, layoutResourceId);

        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mAdapter = this;
    }

    public void GetImage(final ImageView imageView, final Product currentItem) {
        if (currentItem.getImg() != null) {
            if (!currentItem.getImg().equals("")) {
                Glide.with(mContext).load(currentItem.getImg()).into(imageView);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        row = convertView;

        final Product currentItem = getItem(position);

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
        }

        row.setTag(currentItem);

        ((TextView) row.findViewById(R.id.myprodname)).setText(currentItem.getName());
        ((TextView) row.findViewById(R.id.myprodprice)).setText(currentItem.getPrice());

        GetImage(((ImageView) row.findViewById(R.id.myprodimg)), currentItem);

        row.findViewById(R.id.delbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(currentItem);
            }
        });

        row.findViewById(R.id.editbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(mContext, UpdateProductActivity.class);
                i.putExtra("item", currentItem);
                getContext().startActivity(i);
            }
        });

        return row;
    }

    public void deleteItem(final Product item) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            return;
        }
        mProductTable.child(item.getPid()).removeValue();
        mRequestTable.child(item.getUid()).child(item.getPid()).removeValue();

        FirebaseStorage.getInstance().getReference()
                .child("images/users/" + mUser.getUid() + "/" + item.getName() + ".jpg").delete();
        mAdapter.remove(item);
    }

}
