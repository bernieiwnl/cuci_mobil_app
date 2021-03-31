package com.example.cucimobilapp.MASTER;

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

import com.example.cucimobilapp.ADAPTER.CustomerAdapter;
import com.example.cucimobilapp.ADAPTER.MasterCustomerAdapter;
import com.example.cucimobilapp.CLASS.Customer;
import com.example.cucimobilapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MasterListCustomerMotor extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView_customerMotor;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Customer> Customers;
    private MasterCustomerAdapter customerAdapter;
    private EditText textInputEditText_searchMotor;
    private ImageView imageView_back;


    static MasterListCustomerMotor masterListCustomerMotor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master_list_customer_motor);

        recyclerView_customerMotor = (RecyclerView) findViewById(R.id.recycler_customerMotor);
        textInputEditText_searchMotor = (EditText) findViewById(R.id.editText_searchBar);
        imageView_back = (ImageView) findViewById(R.id.imageViewBack);

        imageView_back.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        // set layout recyclerview
        recyclerView_customerMotor.setLayoutManager(new LinearLayoutManager(this));

        //new array list
        Customers = new ArrayList<Customer>();

        //retreive data from database
        firebaseFirestore.collection("customer").whereEqualTo("customer_vehicle_type", "Motor").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Customers.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Customer data = doc.toObject(Customer.class);
                        Customers.add(data);
                    }
                    customerAdapter = new MasterCustomerAdapter(getApplicationContext(), Customers, firebaseAuth.getUid());
                    recyclerView_customerMotor.setAdapter(customerAdapter);
                }else{

                }
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView_customerMotor.addItemDecoration(dividerItemDecoration);

        textInputEditText_searchMotor.addTextChangedListener(new TextWatcher() {
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
            customerAdapter = new MasterCustomerAdapter(getApplicationContext(), data, firebaseAuth.getUid());
            recyclerView_customerMotor.setAdapter(customerAdapter);
        }
    }

    public static MasterListCustomerMotor getInstance(){
        return masterListCustomerMotor;
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