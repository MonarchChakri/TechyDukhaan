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

public class ProdAddActivity extends BaseActivity {
    Spinner protype, sage;
    Button upload, submit;
    EditText descript, price, name;
    ImageView pic;
    private Uri imageUri = null;
    String imageUrl = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prod_add);

        protype = (Spinner) findViewById(R.id.cat);
        sage = (Spinner) findViewById(R.id.sage);
        upload = (Button) findViewById(R.id.btnUpload);
        submit = (Button) findViewById(R.id.btnSubmit);
        descript = (EditText) findViewById(R.id.descript);
        price = (EditText) findViewById(R.id.price);
        name = (EditText) findViewById(R.id.etProdName);
        pic = (ImageView) findViewById(R.id.pic);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(ProdAddActivity.this, android.R.layout.simple_spinner_dropdown_item, types);
        protype.setAdapter(adapter);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(ProdAddActivity.this, android.R.layout.simple_spinner_dropdown_item, ages);
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

    public void addItem(View view) {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            return;
        }

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        checkFilePermissions();
        showProgressDialog();

        if (!name.getText().toString().equals("") && imageUri != null) {
            StorageReference storageReference = mStorageRef.child("images/users/" + mUser.getUid() + "/" + name.getText().toString() + ".jpg");
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageUrl = String.valueOf(taskSnapshot.getDownloadUrl());
                    hideProgressDialog();
                    addItem();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    hideProgressDialog();
                }
            });
        }

        if (imageUri == null) {
            StorageReference ref = null;
            switch (types[protype.getSelectedItemPosition()]) {
                case "Reference Books":
                    ref = mStorageRef.child("refbooksicon.jpg");
                    break;
                case "Stationery":
                    ref = mStorageRef.child("stationaryicon.png");
                    break;
                case "Equipments":
                    ref = mStorageRef.child("equipsicon.png");
                    break;
                case "Guide Books":
                    ref = mStorageRef.child("guidesicon.png");
                    break;
                case "Written Notes":
                    ref = mStorageRef.child("notesicon.jpg");
                    break;
                case "Others":
                    ref = mStorageRef.child("othersicon.png");
                    break;
            }

            assert ref != null;

            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    imageUrl = uri.toString();
                    addItem();
                }
            });
        }
    }

    public void addItem(){
        final Product item = new Product(imageUrl, ages[sage.getSelectedItemPosition()], types[protype.getSelectedItemPosition()],
                name.getText().toString(), descript.getText().toString(), mUser.getUid() + name.getText().toString(), mUser.getUid(), price.getText().toString());

        mProductTable.child(item.getPid()).setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                finish();
            }
        });
    }

    private void checkFilePermissions() {
        int permissionCheck = ProdAddActivity.this.checkSelfPermission("Manifest.permission.READ_EXTERNAL_STORAGE");
        permissionCheck += ProdAddActivity.this.checkSelfPermission("Manifest.permission.WRITE_EXTERNAL_STORAGE");
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
