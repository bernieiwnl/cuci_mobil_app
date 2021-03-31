package com.example.cucimobilapp.TRANSACTION;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cucimobilapp.ADAPTER.TransactionAdapter;
import com.example.cucimobilapp.CLASS.PaketTransaction;
import com.example.cucimobilapp.CLASS.Transaction;
import com.example.cucimobilapp.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;


import net.colindodd.gradientlayout.GradientRelativeLayout;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionPackageAdapter extends RecyclerView.Adapter<TransactionPackageAdapter.MyViewHolder> {

    private ArrayList<PaketTransaction> paketTransactions;
    private Context context;

    public TransactionPackageAdapter(Context c, ArrayList<PaketTransaction> paketTransactions){
        this.paketTransactions = paketTransactions;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionPackageAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_list_transaction_per_paket , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
         SimpleDateFormat format = new SimpleDateFormat("EEEE, dd MMMM yyyy");
         holder.textview_tanggalTransaksi.setText(format.format(paketTransactions.get(position).getTransaction_date()));
         holder.textView_namaCustomer.setText(paketTransactions.get(position).getTransaction_customer_name());
         holder.textView_paketTransaksi.setText(paketTransactions.get(position).getPackage_name());
         Locale localeID = new Locale("in", "ID");
         NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
         holder.textview_hargaPaket.setText(formatRupiah.format((double) paketTransactions.get(position).getPackage_price()));
         holder.textView_jenisKendaraan.setText(paketTransactions.get(position).getPackage_vehicle_type());

        if(paketTransactions.get(position).getPackage_vehicle_type().equals("Mobil")){
            holder.imageView_jenisKendaraan.setImageResource(R.drawable.ic_baseline_directions_car_black_24);
            holder.imageView_jenisKendaraan.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
        }
        else if (paketTransactions.get(position).getPackage_vehicle_type().equals("Motor")){
            holder.imageView_jenisKendaraan.setImageResource(R.drawable.ic_baseline_motorcycle_black_24);
            holder.imageView_jenisKendaraan.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
        }
    }

    @Override
    public int getItemCount() {
        return paketTransactions.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_namaCustomer;
        private TextView textView_jenisKendaraan;
        private TextView textView_paketTransaksi;
        private TextView textview_tanggalTransaksi;
        private TextView textview_hargaPaket;
        private ImageView imageView_jenisKendaraan;
        private GradientRelativeLayout gradientRelativeLayout;

        public MyViewHolder(View view){
            super(view);
            textView_namaCustomer = (TextView) view.findViewById(R.id.textView_namaCustomer);
            textView_jenisKendaraan = (TextView) view.findViewById(R.id.textView_jenisKendaraan);
            textView_paketTransaksi = (TextView) view.findViewById(R.id.textview_paketTransaksi);
            textview_tanggalTransaksi = (TextView) view.findViewById(R.id.textview_tanggalTransaksi);
            textview_hargaPaket = (TextView) view.findViewById(R.id.textview_hargaP);
            imageView_jenisKendaraan = (ImageView) view.findViewById(R.id.imageView_jenisKendaraan);
            gradientRelativeLayout = (GradientRelativeLayout) view.findViewById(R.id.gradient_relative);
        }

    }
}
