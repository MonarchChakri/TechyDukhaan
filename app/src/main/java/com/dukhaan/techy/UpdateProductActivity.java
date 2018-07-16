package com.dukhaan.techy;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateProductActivity extends BaseActivity {
    Spinner protype, sage;
    Button upload, edit;
    EditText descript, price, name;
    ImageView pic;
    Product item;
    private Uri imageUri = null;
    String imageUrl = null;
    private StorageReference mStorageRef;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        item = (Product) getIntent().getExtras().get("item");

        protype = (Spinner) findViewById(R.id.editCat);
        sage = (Spinner) findViewById(R.id.editsage);
        upload = (Button) findViewById(R.id.editBtnUpload);
        edit = (Button) findViewById(R.id.btnedit);

        descript = (EditText) findViewById(R.id.editdescript);
        price = (EditText) findViewById(R.id.editprice);
        name = (EditText) findViewById(R.id.editProdName);
        pic = (ImageView) findViewById(R.id.editpic);

        descript.setText(item.getDesc());
        price.setText(item.getPrice());
        name.setText(item.getName());


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, types);
        protype.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ages);
        sage.setAdapter(adapter1);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 100);
            }
        });

    }

    public void updateItem(View view) {

        FirebaseStorage.getInstance().getReference()
                .child("images/users/" + mUser.getUid() + "/" + item.getName() + ".jpg").delete();

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            return;
        }

        mStorageRef = FirebaseStorage.getInstance().getReference();
        checkFilePermissions();
        showProgressDialog();

        if (!name.getText().equals("") && imageUri != null) {
            StorageReference storageReference = mStorageRef.child("images/users/" + mUser.getUid() + "/" + name.getText().toString() + ".jpg");
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                    hideProgressDialog();
                    updateItem();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                }
            });
        }
        if (imageUri == null)
            updateItem();
    }

    public void updateItem(){
        item.setName(name.getText().toString());
        item.setAge(ages[sage.getSelectedItemPosition()]);
        item.setCat(types[protype.getSelectedItemPosition()]);
        item.setDesc(descript.getText().toString());
        item.setPrice(price.getText().toString());
        if (imageUrl != null)
            item.setImg(imageUrl);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mUser != null;
        mUserTable.child(mUser.getUid()).child("prod_gist").child(item.getPid()).setValue(item.getName() + "," + item.getCat());

        mProductTable.child(item.getPid()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });

    }

    private void checkFilePermissions() {
        int permissionCheck = UpdateProductActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
        permissionCheck += UpdateProductActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
        if (permissionCheck != 0) {
            this.requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1001);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    this.imageUri = imageReturnedIntent.getData();
                    this.pic.setImageURI(this.imageUri);
                    this.upload.setEnabled(true);
                }
        }
    }

}
