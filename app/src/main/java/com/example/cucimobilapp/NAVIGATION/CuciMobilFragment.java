package com.example.cucimobilapp.NAVIGATION;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.cucimobilapp.MASTER.InsertCustomerActivity;
import com.example.cucimobilapp.MASTER.InsertPackageActivity;
import com.example.cucimobilapp.MASTER.MasterListCustomerMobil;
import com.example.cucimobilapp.MASTER.MasterListPaketMobil;
import com.example.cucimobilapp.MASTER.MasterListTransaksiMobil;
import com.example.cucimobilapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class CuciMobilFragment extends Fragment implements View.OnClickListener {

    private CardView cardView_DataCustomer;
    private CardView cardView_DataTransaksi;
    private CardView cardView_DataPaket;
    private CardView cardView_TambahCustomer;
    private CardView cardView_TambahDataPaket;
    private FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cuci_mobil, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        cardView_DataCustomer = (CardView) view.findViewById(R.id.cardView_DataCustomer);
        cardView_DataPaket = (CardView) view.findViewById(R.id.cardView_DataPaket);
        cardView_TambahCustomer = (CardView) view.findViewById(R.id.cardView_TambahCustomer);
        cardView_TambahDataPaket = (CardView) view.findViewById(R.id.cardView_TambahDataPaket);

        cardView_DataCustomer.setOnClickListener(this);
        cardView_DataPaket.setOnClickListener(this);
        cardView_TambahDataPaket.setOnClickListener(this);
        cardView_TambahCustomer.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getUid().equals("Q3fFpoWg5TS6UNpqagyAlIY1cEI3"))
        {
            cardView_TambahCustomer.setVisibility(View.VISIBLE);
            cardView_TambahDataPaket.setVisibility(View.VISIBLE);
        }
        else{
            cardView_TambahCustomer.setVisibility(View.GONE);
            cardView_TambahDataPaket.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardView_DataCustomer:
                startActivity(new Intent(getActivity(), MasterListCustomerMobil.class));
                break;
            case R.id.cardView_DataPaket:
                startActivity(new Intent(getActivity(), MasterListPaketMobil.class));
                break;
            case R.id.cardView_TambahCustomer:
                startActivity(new Intent(getActivity(), InsertCustomerActivity.class));
                break;
            case R.id.cardView_TambahDataPaket:
                startActivity(new Intent(getActivity(), InsertPackageActivity.class));
                break;

        }
    }
}