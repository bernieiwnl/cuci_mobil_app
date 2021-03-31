package com.example.cucimobilapp.TRANSACTION;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.cucimobilapp.CLASS.Customer;
import com.example.cucimobilapp.R;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class FilterListPemilihanCustomer extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView_customerMobil;
    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Customer> Customers;
    private FilterCustomerAdapter customerAdapter;
    private EditText textInputEditText_search;
    private ImageView imageView_back;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_list_pemilihan_customer);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        recyclerView_customerMobil = (RecyclerView) findViewById(R.id.recycler_customer);
        textInputEditText_search = (EditText) findViewById(R.id.editText_searchBar);
        imageView_back = (ImageView) findViewById(R.id.imageViewBack);

        imageView_back.setOnClickListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();
        // set layout recyclerview
        recyclerView_customerMobil.setLayoutManager(new LinearLayoutManager(this));

        //new array list
        Customers = new ArrayList<Customer>();

        //retreive data from database
        firebaseFirestore.collection("customer").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Customers.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Customer data = doc.toObject(Customer.class);
                        Customers.add(data);
                    }
                    customerAdapter = new FilterCustomerAdapter(FilterListPemilihanCustomer.this, Customers);
                    recyclerView_customerMobil.setAdapter(customerAdapter);
                }else{

                }
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView_customerMobil.addItemDecoration(dividerItemDecoration);

        textInputEditText_search.addTextChangedListener(new TextWatcher() {
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
        ArrayList<Customer> data = new ArrayList<>();
        data.clear();
        //looping through existing elements
        for (Customer c : Customers) {
            //if the existing elements contains the search input
            if (c.getCustomer_name().toLowerCase().contains(text.toLowerCase()) || c.getCustomer_vehicle_number().toLowerCase().contains(text.toLowerCase())) {
                //adding the element to filtered list
                data.add(c);
            }

            customerAdapter = new FilterCustomerAdapter(FilterListPemilihanCustomer.this, data);
            recyclerView_customerMobil.setAdapter(customerAdapter);
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