package com.dukhaan.techy;

        import android.os.Bundle;
        import android.widget.ImageView;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.bumptech.glide.Glide;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.ValueEventListener;

public class ProdDetailsActivity extends BaseActivity {

    String pid = null;
    TextView name, age, price, type, desc, ownrname, ownrcntct, ownradd, ownrinstn;
    ImageView photo;

    private void GetImage(final ImageView imageView, final Product currentItem) {
        if (currentItem.getImg() != null) {
            if (!currentItem.getImg().equals("")) {
                Glide.with(ProdDetailsActivity.this).load(currentItem.getImg()).into(imageView);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        name = (TextView) findViewById(R.id.tvProdName);
        age = (TextView) findViewById(R.id.tvProdAge);
        price = (TextView) findViewById(R.id.tvProdPrice);
        type = (TextView) findViewById(R.id.tvProdType);
        desc = (TextView) findViewById(R.id.tvProdDsc);
        ownrname = (TextView) findViewById(R.id.ProdOwner);
        ownrcntct = (TextView) findViewById(R.id.ProdCntct);
        ownradd = (TextView) findViewById(R.id.ProdAdd);
        ownrinstn = (TextView) findViewById(R.id.ProdInstn);
        photo = (ImageView) findViewById(R.id.imgProd);

        pid = (String) getIntent().getExtras().get("pid");

        assert pid != null;
        refreshProduct(pid);

    }

    private void refreshUser(final String sid) {
        mUserTable.child(sid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User seller = dataSnapshot.getValue(User.class);
                assert seller != null;
                if (seller.getUid().equals(sid)) {
                    ownrname.setText(seller.getName());
                    ownrcntct.setText(seller.getPhNum());
                    ownradd.setText(seller.getAddr());
                    ownrinstn.setText(seller.getCllg());
                } else {
                    Toast.makeText(ProdDetailsActivity.this, "Seller not found.", Toast.LENGTH_SHORT).show();
                }
                mUserTable.child(sid).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void refreshProduct(final String pid) {
        mProductTable.child(pid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Product product = dataSnapshot.getValue(Product.class);
                assert product != null;
                if (product.getPid().equals(pid)) {
                    name.setText(product.getName());
                    age.setText(product.getAge());
                    price.setText(product.getPrice());
                    type.setText(product.getCat());
                    desc.setText(product.getDesc());
                    GetImage(photo, product);
                    refreshUser(product.getUid());
                } else {
                    Toast.makeText(ProdDetailsActivity.this, "Product not found.", Toast.LENGTH_SHORT).show();
                }
                mProductTable.child(pid).removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
