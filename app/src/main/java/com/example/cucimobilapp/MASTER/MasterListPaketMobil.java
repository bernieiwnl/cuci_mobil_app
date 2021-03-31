package com.example.cucimobilapp.MASTER;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.cucimobilapp.ADAPTER.MasterPackageAdapter;
import com.example.cucimobilapp.CLASS.Paket;
import com.example.cucimobilapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class MasterListPaketMobil extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView_paketPencucianMobil;
    private EditText editText_searchPaket;
    private ArrayList<Paket> Pakets;
    private MasterPackageAdapter packageAdapter;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ImageView imageView_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_list_paket_mobil);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        recyclerView_paketPencucianMobil = (RecyclerView) findViewById(R.id.recycler_paketPencucianMobil);
        editText_searchPaket = (EditText) findViewById(R.id.editText_searchBar);
        imageView_back = (ImageView) findViewById(R.id.imageViewBack);
        imageView_back.setOnClickListener(this);

        Pakets = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        recyclerView_paketPencucianMobil.setLayoutManager(new LinearLayoutManager(this));

        //retreive data from database
        firebaseFirestore.collection("package").whereEqualTo("package_vehicle_type", "Mobil").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Pakets.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Paket data = doc.toObject(Paket.class);
                        Pakets.add(data);
                    }
                    Collections.sort(Pakets, new Comparator<Paket>() {
                        @Override
                        public int compare(Paket o1, Paket o2) {
                            return o1.getPackage_name().compareTo(o2.getPackage_name());
                        }
                    });
                    packageAdapter = new MasterPackageAdapter(MasterListPaketMobil.this, Pakets, firebaseAuth.getUid());
                    recyclerView_paketPencucianMobil.setAdapter(packageAdapter);
                }
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView_paketPencucianMobil.addItemDecoration(dividerItemDecoration);

        editText_searchPaket.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });
    }

    private void filter(String text) {
        //new array list that will hold the filtered data
        ArrayList<Paket> data = new ArrayList<>();
        data.clear();
        //looping through existing elements
        for (Paket c : Pakets) {
            //if the existing elements contains the search input
            if (c.getPackage_name().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                data.add(c);
            }

            packageAdapter = new MasterPackageAdapter(MasterListPaketMobil.this, data, firebaseAuth.getUid());
            recyclerView_paketPencucianMobil.setAdapter(packageAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewBack:
                finish();
                break;
        }
    }
}