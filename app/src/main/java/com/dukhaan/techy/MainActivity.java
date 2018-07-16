package com.dukhaan.techy;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends SharedPref
        implements NavigationView.OnNavigationItemSelectedListener {

    int a = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        List<Category> lst = new ArrayList<Category>();

        lst.add(new Category(R.drawable.refbooksicon, types[0]));
        lst.add(new Category(R.drawable.stationaryicon, types[1]));
        lst.add(new Category(R.drawable.equipsicon, types[2]));
        lst.add(new Category(R.drawable.guidesicon, types[3]));
        lst.add(new Category(R.drawable.notesicon, types[4]));
        lst.add(new Category(R.drawable.othersicon, types[5]));

        ListView catlv = findViewById(R.id.lvCat);
        CategoryAdapter adap = new CategoryAdapter(MainActivity.this, R.layout.cat_item, lst);
        catlv.setAdapter(adap);

        catlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String src = ((TextView) view.findViewById(R.id.tvCatName)).getText().toString();
                Intent in = new Intent(MainActivity.this, ResultsActivity.class);
                in.putExtra("src", src);
                startActivity(in);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        a = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        a = 0;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        a = 0;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (a++ == 0) {
                Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            Intent editProfile = new Intent(MainActivity.this, EditProfileActivity.class);
            editProfile.putExtra("sender", "main");
            startActivity(editProfile);
            return true;
        }
        else if (id == R.id.action_logout) {
            signOut();
            startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        mAuth.signOut();

        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this, "Signed Out.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_inRequests) {
            Intent requestsIntent = new Intent(MainActivity.this, RequestsActivity.class);
            requestsIntent.putExtra("type", "in");
            startActivity(requestsIntent);
        } else if (id == R.id.nav_outRequests) {
            Intent requestsIntent = new Intent(MainActivity.this, RequestsActivity.class);
            requestsIntent.putExtra("type", "out");
            startActivity(requestsIntent);
        } else if (id == R.id.nav_accRequests) {
            Intent requestsIntent = new Intent(MainActivity.this, RequestsActivity.class);
            requestsIntent.putExtra("type", "accepted");
            startActivity(requestsIntent);
        }  else if (id == R.id.nav_my_products) {
            startActivity(new Intent(MainActivity.this, MyProductsActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void RequestItem(View v) {
        EditText search = (EditText) findViewById(R.id.search);
        String src = search.getText().toString();
        search.setText("");

        if (!TextUtils.isEmpty(src)) {
            Toast.makeText(this, "Searching..", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, ResultsActivity.class);
            i.putExtra("src", src);
            startActivity(i);
        } else {
            Toast.makeText(this, "Enter what to search.", Toast.LENGTH_SHORT).show();
        }
    }

}
