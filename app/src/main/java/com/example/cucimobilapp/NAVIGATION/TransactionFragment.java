package com.example.cucimobilapp.NAVIGATION;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cucimobilapp.ADAPTER.CustomerAdapter;
import com.example.cucimobilapp.ADAPTER.PackageAdapter;
import com.example.cucimobilapp.ADAPTER.TransactionAdapter;
import com.example.cucimobilapp.CLASS.Customer;
import com.example.cucimobilapp.CLASS.Paket;
import com.example.cucimobilapp.CLASS.PaketTransaction;
import com.example.cucimobilapp.CLASS.Transaction;
import com.example.cucimobilapp.HOME.HomeActivity;
import com.example.cucimobilapp.LIST.ListCustomerMobil;
import com.example.cucimobilapp.LIST.ListCustomerMotor;
import com.example.cucimobilapp.LIST.ListPaketPencucianMobil;
import com.example.cucimobilapp.R;
import com.example.cucimobilapp.TRANSACTION.ListTransactionPackage;
import com.example.cucimobilapp.TRANSACTION.TransactionPackageAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TransactionFragment extends Fragment implements View.OnClickListener {


    private RecyclerView recycler_transaksi;
    private ArrayList<Transaction> transactions;
    private TransactionAdapter transactionAdapter;
    private FirebaseFirestore firebaseFirestore;
    private ImageView imageView_filter;
    private TextView textView_filter;
    private MaterialButton materialButtonTransaksiPerpaket;
    private String jenisKendaraan;
    private Date date;
    private TextView textView_totalTransaction;
    private int totalTransaction;
    private Boolean filter;
    private BottomSheetDialog bottomSheetDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        jenisKendaraan = "Semua";
        totalTransaction = 0;
        filter = false;

        recycler_transaksi = (RecyclerView) view.findViewById(R.id.recycler_transaksi);
        imageView_filter = (ImageView) view.findViewById(R.id.imageView_filter);
        textView_filter = (TextView) view.findViewById(R.id.textView_filter);
        textView_totalTransaction = (TextView) view.findViewById(R.id.textView_totalTransaction);
        materialButtonTransaksiPerpaket = (MaterialButton) view.findViewById(R.id.buttonMaterial_transaksiperPaket);
        imageView_filter.setOnClickListener(this);
        textView_filter.setOnClickListener(this);
        materialButtonTransaksiPerpaket.setOnClickListener(this);

        transactions = new ArrayList<>();

        firebaseFirestore = FirebaseFirestore.getInstance();

        recycler_transaksi.setLayoutManager(new LinearLayoutManager(getActivity()));

        firebaseFirestore.collection("transaction").orderBy("transaction_date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                transactions.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Transaction data = doc.toObject(Transaction.class);
                        transactions.add(data);
                    }
                    transactionAdapter = new TransactionAdapter(getActivity(), transactions);
                    recycler_transaksi.setAdapter(transactionAdapter);

                    //get total transaction
                    for(int i = 0; i < transactions.size(); i++){
                        totalTransaction += transactions.get(i).getTransaction_price();
                    }
                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                    textView_totalTransaction.setText("Total Transaksi : " + formatRupiah.format(totalTransaction));
                }
            }
        });



        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL);
        recycler_transaksi.addItemDecoration(dividerItemDecoration);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.imageView_filter:
                popUpBottomActionDialog();
                break;
            case R.id.textView_filter:
                popUpBottomActionDialog();
                break;
            case R.id.buttonMaterial_transaksiperPaket:
                startActivity(new Intent(getActivity(), ListTransactionPackage.class));
                break;
        }
    }

    private void popUpBottomActionDialog(){
        bottomSheetDialog = new BottomSheetDialog(getActivity(),R.style.BottomSheetTheme);

        final View sheetView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_bottom_sheet_filter_layout, (ViewGroup) getActivity().findViewById(R.id.bottomSheetLayout));

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

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
                        materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                    }
                });

        RadioGroup radioGroup_jenisKendaraan;
        RadioButton radioButton_mobil;
        RadioButton radioButton_motor;
        RadioButton radioButton_semua;
        radioGroup_jenisKendaraan = sheetView.findViewById(R.id.radioGroup_jenisKendaraan);
        radioButton_mobil = sheetView.findViewById(R.id.radioButton_mobil);
        radioButton_motor = sheetView.findViewById(R.id.radioButton_motor);
        radioButton_semua = sheetView.findViewById(R.id.radioButton_semua);

        TimeZone timeZoneUTC = TimeZone.getDefault();
        // It will be negative, so that's the -1
        final int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
        // Create a date format, then a date object with our offset
        final SimpleDateFormat simpleFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);


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
                Toast.makeText(getActivity(), jenisKendaraan + "", Toast.LENGTH_SHORT).show();
                filterData(date, jenisKendaraan);
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
        totalTransaction = 0;
        date = null;
        firebaseFirestore.collection("transaction").orderBy("transaction_date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                transactions.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Transaction data = doc.toObject(Transaction.class);
                        transactions.add(data);
                    }
                    transactionAdapter = new TransactionAdapter(getActivity(), transactions);
                    recycler_transaksi.setAdapter(transactionAdapter);

                    //get total transaction
                    for(int i = 0; i < transactions.size(); i++){
                        totalTransaction += transactions.get(i).getTransaction_price();
                    }
                    Locale localeID = new Locale("in", "ID");
                    NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
                    textView_totalTransaction.setText("Total Transaksi : " + formatRupiah.format(totalTransaction));
                }
            }
        });
    }


    private void filterData(Date date, String jenisKendaraan) {
        //new array list that will hold the filtered data
        ArrayList<Transaction> data = new ArrayList<>();
        data.clear();

        //get only date
        String pattern = "MM-dd-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        //looping through existing elements
        for (Transaction t : transactions) {
            //if the existing elements contains the search input
            if(date != null){
                if (t.getTransaction_customer_type().equals(jenisKendaraan) && simpleDateFormat.format(t.getTransaction_date()).equals(simpleDateFormat.format(date))) {
                    //adding the element to filtered list
                    data.add(t);
                }
                else if(jenisKendaraan.equals("Semua") && simpleDateFormat.format(t.getTransaction_date()).equals(simpleDateFormat.format(date))){
                    data.add(t);
                }
            }
            else{
                if (t.getTransaction_customer_type().equals(jenisKendaraan) ) {
                    //adding the element to filtered list
                    data.add(t);
                }
                else if(jenisKendaraan.equals("Semua")){
                    data.add(t);
                }
            }

            totalTransaction = 0;
            for(int i = 0; i < data.size(); i++)
            {
                totalTransaction += data.get(i).getTransaction_price();
            }

            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

            textView_totalTransaction.setText("Total Transaksi : " + formatRupiah.format(totalTransaction));

            transactionAdapter = new TransactionAdapter(getActivity(), data);
            recycler_transaksi.setAdapter(transactionAdapter);
        }
    }
}