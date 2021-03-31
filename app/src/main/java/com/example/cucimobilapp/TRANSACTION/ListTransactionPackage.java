package com.example.cucimobilapp.TRANSACTION;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cucimobilapp.ADAPTER.PackageAdapter;
import com.example.cucimobilapp.ADAPTER.TransactionAdapter;
import com.example.cucimobilapp.ADAPTER.TransactionPaketAdapter;
import com.example.cucimobilapp.CLASS.Paket;
import com.example.cucimobilapp.CLASS.PaketTransaction;
import com.example.cucimobilapp.CLASS.Transaction;
import com.example.cucimobilapp.LIST.ListPaketPencucianMobil;
import com.example.cucimobilapp.PENCUCIAN.PencucianActivity;
import com.example.cucimobilapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ListTransactionPackage extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView recyclerView_packageTransaction;
    private TextView textView_totalTransaction;
    private ArrayList<PaketTransaction> paketTransactions;
    private TransactionPackageAdapter transactionPackageAdapter;
    private FirebaseFirestore firebaseFirestore;
    private ImageView imageView_back;
    private ImageView imageView_filter;
    private int totalTransaksi;
    private String jenisKendaraan;
    private String namaCus;
    private String namaPaket;
    private BottomSheetDialog bottomSheetDialog;
    private Date date;
    private View sheetView;
    private Boolean filter;
    private String customer_id;
    private String package_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_transaction_package);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        bottomSheetDialog = new BottomSheetDialog(ListTransactionPackage.this,R.style.BottomSheetTheme);
        sheetView = LayoutInflater.from(ListTransactionPackage.this).inflate(R.layout.custom_bottom_sheet_filter_paket_layout, (ViewGroup) findViewById(R.id.bottomSheetLayout));
        filter = false;

        recyclerView_packageTransaction = (RecyclerView) findViewById(R.id.recycler_packageTransaction);
        textView_totalTransaction = (TextView) findViewById(R.id.textView_totalTransaction);
        imageView_back = (ImageView) findViewById(R.id.imageViewBack);
        imageView_filter = (ImageView) findViewById(R.id.imageView_filter);

        imageView_back.setOnClickListener(this);
        imageView_filter.setOnClickListener(this);

        paketTransactions = new ArrayList<>();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView_packageTransaction.setLayoutManager(new LinearLayoutManager(this));
        totalTransaksi = 0;

        firebaseFirestore.collection("transaction_package").orderBy("transaction_date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                paketTransactions.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        PaketTransaction data = doc.toObject(PaketTransaction.class);
                        paketTransactions.add(data);
                    }
                    transactionPackageAdapter = new TransactionPackageAdapter(ListTransactionPackage.this, paketTransactions);
                    recyclerView_packageTransaction.setAdapter(transactionPackageAdapter);

                    for(int i = 0; i < paketTransactions.size(); i++){
                        totalTransaksi += paketTransactions.get(i).getPackage_price();
                    }
                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                    textView_totalTransaction.setText("Total Transaksi :" + formatRupiah.format((double) totalTransaksi));
                }
            }
        });

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        recyclerView_packageTransaction.addItemDecoration(dividerItemDecoration);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageViewBack:
                finish();
                break;
            case R.id.imageView_filter:
                popUpBottomActionDialog();
                break;
        }
    }

    private void popUpBottomActionDialog(){
        bottomSheetDialog = new BottomSheetDialog(ListTransactionPackage.this,R.style.BottomSheetTheme);
        sheetView = LayoutInflater.from(ListTransactionPackage.this).inflate(R.layout.custom_bottom_sheet_filter_paket_layout, (ViewGroup) findViewById(R.id.bottomSheetLayout));

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        RadioGroup radioGroup_jenisKendaraan;
        RadioButton radioButton_mobil;
        RadioButton radioButton_motor;
        RadioButton radioButton_semua;

        // now define the properties of the
        // materialDateBuilder that is title text as SELECT A DATE
        materialDateBuilder.setTitleText("SELECT A DATE");

        // now create the instance of the material date
        // picker
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        // handle select date button which opens the
        // material design date picker
        sheetView.findViewById(R.id.buttonMaterial_pilihTanggal).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        sheetView.findViewById(R.id.buttonMaterial_pilihCustomer).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCustomer = new Intent(ListTransactionPackage.this, FilterListPemilihanCustomer.class);
                startActivityForResult(intentCustomer,1);
            }
        });

        sheetView.findViewById(R.id.buttonMaterial_paket).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPaket = new Intent(ListTransactionPackage.this, FilterListPemilihanPaket.class);
                startActivityForResult(intentPaket,2);

            }
        });


        TimeZone timeZoneUTC = TimeZone.getDefault();
        // It will be negative, so that's the -1
        final int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
        // Create a date format, then a date object with our offset
        final SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);

        radioGroup_jenisKendaraan = sheetView.findViewById(R.id.radioGroup_jenisKendaraan);

        radioButton_mobil = sheetView.findViewById(R.id.radioButton_mobil);
        radioButton_motor = sheetView.findViewById(R.id.radioButton_motor);
        radioButton_semua = sheetView.findViewById(R.id.radioButton_semua);

        if(filter == false){
            jenisKendaraan = "Semua";
            radioButton_semua.setChecked(true);
        }
        else if(filter == true){
            if(jenisKendaraan.equals("Motor")){
                radioButton_motor.setChecked(true);
            }
            else if(jenisKendaraan.equals("Mobil")){
                radioButton_mobil.setChecked(true);
            }
            else if(jenisKendaraan.equals("Semua")){
                radioButton_semua.setChecked(true);
            }
            if(namaCus != null){
                TextView textView_namaCustomer = sheetView.findViewById(R.id.textview_inputCustomer);
                textView_namaCustomer.setText(namaCus);
            }
            if(namaPaket != null){
                TextView textView_namaPaket = sheetView.findViewById(R.id.textview_inputPaket);
                textView_namaPaket.setText(namaPaket);
            }
            if(date != null){
                TextView textView_inputTanggalTransaksi;
                textView_inputTanggalTransaksi =  sheetView.findViewById(R.id.textView_inputTanggalTransaksi);
                textView_inputTanggalTransaksi.setText(simpleFormat.format(date));
            }
        }

        radioGroup_jenisKendaraan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_motor:
                        jenisKendaraan = "Motor";
                        break;
                    case R.id.radioButton_mobil:
                        jenisKendaraan = "Mobil";
                        break;
                    case R.id.radioButton_semua:
                        jenisKendaraan = "Semua";
                        break;
                }
            }
        });


        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selectedDate) {
                date = new Date(selectedDate + offsetFromUTC);
                TextView textView_inputTanggalTransaksi;
                textView_inputTanggalTransaksi =  sheetView.findViewById(R.id.textView_inputTanggalTransaksi);
                textView_inputTanggalTransaksi.setText(simpleFormat.format(date));
            }
        });

        sheetView.findViewById(R.id.cardView_simpanFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(jenisKendaraan.equals("Motor") || jenisKendaraan.equals("Mobil")){
                    filterData(date, jenisKendaraan, namaCus, namaPaket, customer_id, package_id);
                    Toast.makeText(getApplicationContext(), "A", Toast.LENGTH_SHORT).show();
                }
                else if(jenisKendaraan.equals("Semua")){
                    filterAlldata(date,namaCus,namaPaket, customer_id, package_id);
                    Toast.makeText(getApplicationContext(), "B", Toast.LENGTH_SHORT).show();
                }
                filter = true;
                bottomSheetDialog.dismiss();
            }
        });

        sheetView.findViewById(R.id.cardView_clearFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilter();
                filter = false;
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    private void clearFilter(){
        totalTransaksi = 0;
        date = null;
        namaCus = null;
        namaPaket = null;
        customer_id = null;
        package_id = null;

        firebaseFirestore.collection("transaction_package").orderBy("transaction_date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                paketTransactions.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        PaketTransaction data = doc.toObject(PaketTransaction.class);
                        paketTransactions.add(data);
                    }
                    transactionPackageAdapter = new TransactionPackageAdapter(ListTransactionPackage.this, paketTransactions);
                    recyclerView_packageTransaction.setAdapter(transactionPackageAdapter);

                    for(int i = 0; i < paketTransactions.size(); i++){
                        totalTransaksi += paketTransactions.get(i).getPackage_price();
                    }
                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                    textView_totalTransaction.setText("Total Transaksi :" + formatRupiah.format((double) totalTransaksi));
                }
            }
        });
    }

    private void filterData(Date date, String jenisKendaraan, String namaCus, String namaPaket, String customer_id, String package_id) {
        //new array list that will hold the filtered data
        ArrayList<PaketTransaction> data = new ArrayList<>();
        data.clear();

        //get only date
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        //looping through existing elements
        for (PaketTransaction t : paketTransactions) {
            //if the existing elements contains the search input
            if(jenisKendaraan.equals("Mobil") || jenisKendaraan.equals("Motor")){
                if(t.getPackage_vehicle_type().equals(jenisKendaraan)){
                    if(date != null && namaCus != null && namaPaket !=null){
                        if (simpleDateFormat.format(t.getTransaction_date()).equals(simpleDateFormat.format(date)) &&
                                t.getCustomer_id().equals(customer_id) &&
                                t.getPackage_id().equals(package_id)){
                            //adding the element to filtered list
                            data.add(t);
                        }
                    }
                    // nama cus
                    else if(namaCus != null && date == null && namaPaket == null){
                        if(t.getCustomer_id().equals(customer_id)){
                            data.add(t);
                        }
                    }
                    else if(namaCus != null && date != null && namaPaket == null){
                        if(t.getCustomer_id().equals(customer_id) && simpleDateFormat.format(t.getTransaction_date()).equals(simpleDateFormat.format(date))){
                            data.add(t);
                        }
                    }
                    else if(namaCus!= null && date == null && namaPaket != null){
                        if(t.getCustomer_id().equals(customer_id) && t.getPackage_id().equals(package_id) ){
                            data.add(t);
                        }
                    }

                    // date
                    else if(date != null && namaCus == null && namaPaket == null){
                        if(simpleDateFormat.format(t.getTransaction_date()).equals(simpleDateFormat.format(date))){
                            data.add(t);
                        }
                    }
                    else if(date != null && namaCus == null && namaPaket != null){
                        if(simpleDateFormat.format(t.getTransaction_date()).equals(simpleDateFormat.format(date)) && t.getPackage_id().equals(package_id)){
                            data.add(t);
                        }
                    }
                    // paket
                    else if(namaPaket != null && namaCus == null && date == null){
                        if(t.getPackage_id().equals(package_id) ){
                            data.add(t);
                        }
                    }

                    else if(date == null && namaCus == null && namaPaket == null){
                        if (t.getPackage_vehicle_type().equals(jenisKendaraan)) {
                            //adding the element to filtered list
                            data.add(t);
                        }
                    }
                }
            }
            totalTransaksi = 0;
            for(int i = 0; i < data.size(); i++)
            {
                totalTransaksi += data.get(i).getPackage_price();
            }

            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

            textView_totalTransaction.setText("Total Transaksi : " + formatRupiah.format(totalTransaksi));

            transactionPackageAdapter = new TransactionPackageAdapter(getApplicationContext(), data);
            recyclerView_packageTransaction.setAdapter(transactionPackageAdapter);
        }
    }
    private void filterAlldata(Date date, String namaCus, String namaPaket, String customer_id, String package_id) {
        //new array list that will hold the filtered data
        ArrayList<PaketTransaction> data = new ArrayList<>();
        data.clear();

        //get only date
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        //looping through existing elements
        for (PaketTransaction t : paketTransactions) {
            //if the existing elements contains the search input
            if(date != null && namaCus != null && namaPaket !=null){
                if (jenisKendaraan.equals("Semua") && simpleDateFormat.format(t.getTransaction_date()).equals(simpleDateFormat.format(date)) && t.getCustomer_id().equals(customer_id) && t.getPackage_id().equals(package_id)){
                    //adding the element to filtered list
                    data.add(t);
                }
            }
            // nama cus
            else if(namaCus != null && date == null && namaPaket == null){
                if(jenisKendaraan.equals("Semua") && t.getCustomer_id().equals(customer_id)){
                    data.add(t);
                }
            }
            else if(namaCus != null && date != null && namaPaket == null){
                if(jenisKendaraan.equals("Semua") && t.getCustomer_id().equals(customer_id) && simpleDateFormat.format(t.getTransaction_date()).equals(simpleDateFormat.format(date))){
                    data.add(t);
                }
            }
            else if(namaCus!= null && date == null && namaPaket != null){
                if(jenisKendaraan.equals("Semua")  && t.getCustomer_id().equals(customer_id) && t.getPackage_id().equals(package_id) ){
                    data.add(t);
                }
            }

            // date
            else if(date != null && namaCus == null && namaPaket == null){
                if(jenisKendaraan.equals("Semua") && simpleDateFormat.format(t.getTransaction_date()).equals(simpleDateFormat.format(date))){
                    data.add(t);
                }
            }
            else if(date != null && namaCus == null && namaPaket != null){
                if(jenisKendaraan.equals("Semua")  && simpleDateFormat.format(t.getTransaction_date()).equals(simpleDateFormat.format(date)) && t.getPackage_id().equals(package_id)){
                    data.add(t);
                }
            }
            // paket
            else if(namaPaket != null && namaCus == null && date == null){
                if(jenisKendaraan.equals("Semua") && t.getPackage_id().equals(package_id) ){
                    data.add(t);
                }
            }

            else if(date == null && namaCus == null && namaPaket == null){
                if (jenisKendaraan.equals("Semua")) {
                    //adding the element to filtered list
                    data.add(t);
                }
            }

            totalTransaksi = 0;
            for(int i = 0; i < data.size(); i++)
            {
                totalTransaksi += data.get(i).getPackage_price();
            }

            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

            textView_totalTransaction.setText("Total Transaksi : " + formatRupiah.format(totalTransaksi));

            transactionPackageAdapter = new TransactionPackageAdapter(getApplicationContext(), data);
            recyclerView_packageTransaction.setAdapter(transactionPackageAdapter);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == 1 && resultCode == RESULT_OK){
                namaCus = data.getStringExtra("nama_cus");
                customer_id = data.getStringExtra("customer_id");
                TextView textView_namaCustomer = sheetView.findViewById(R.id.textview_inputCustomer);
                textView_namaCustomer.setText(namaCus);

            }
            else if(requestCode == 2 && resultCode == RESULT_OK){
                 namaPaket = data.getStringExtra("nama_paket");
                 package_id = data.getStringExtra("package_id");
                 TextView textView_namaPaket = sheetView.findViewById(R.id.textview_inputPaket);
                 textView_namaPaket.setText(namaPaket);
        }
    }
}