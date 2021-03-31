package com.example.cucimobilapp.ADAPTER;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cucimobilapp.CLASS.Paket;
import com.example.cucimobilapp.CLASS.Transaction;
import com.example.cucimobilapp.MASTER.DetailTransaction;
import com.example.cucimobilapp.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import net.colindodd.gradientlayout.GradientRelativeLayout;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

    private ArrayList<Transaction> transactions;
    private Context context;

    public TransactionAdapter(Context c, ArrayList<Transaction> transactions){
        this.transactions = transactions;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_list_transaction , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView_namaCustomer.setText(transactions.get(position).getTransaction_customer_name());
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        SimpleDateFormat format = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        holder.textView_tanggalTransaksi.setText(format.format(transactions.get(position).getTransaction_date()));
        holder.textView_harga.setText("Total Transaksi " + formatRupiah.format((double) transactions.get(position).getTransaction_price()));
        holder.textView_jenisKendaraan.setText(transactions.get(position).getTransaction_customer_type());
        if(transactions.get(position).getTransaction_customer_type().equals("Mobil")){
            holder.imageView_jenisKendaraan.setImageResource(R.drawable.ic_baseline_directions_car_black_24);
            holder.imageView_jenisKendaraan.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            holder.gradientRelativeLayout.setStartColor(Color.parseColor("#376DF6"));
            holder.gradientRelativeLayout.setEndColor(Color.parseColor("#376DF6"));
        }
        else if (transactions.get(position).getTransaction_customer_type().equals("Motor")){
            holder.imageView_jenisKendaraan.setImageResource(R.drawable.ic_baseline_motorcycle_black_24);
            holder.imageView_jenisKendaraan.setImageTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            holder.gradientRelativeLayout.setStartColor(Color.parseColor("#FF416C"));
            holder.gradientRelativeLayout.setEndColor(Color.parseColor("#FF416C"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailTransaction.class);
                i.putExtra("transaction_id", transactions.get(position).getTransaction_id());
                i.putExtra("customer_id" , transactions.get(position).getTransaction_customer_id());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{


        private TextView textView_namaCustomer;
        private TextView textView_jenisKendaraan;
        private TextView textView_harga;
        private TextView textView_tanggalTransaksi;
        private ImageView imageView_jenisKendaraan;
        private GradientRelativeLayout gradientRelativeLayout;

        public MyViewHolder(View view){
            super(view);
            textView_namaCustomer = (TextView) view.findViewById(R.id.textView_namaCustomer);
            textView_jenisKendaraan = (TextView) view.findViewById(R.id.textView_jenisKendaraan);
            textView_harga = (TextView) view.findViewById(R.id.textView_harga);
            textView_tanggalTransaksi = (TextView) view.findViewById(R.id.textview_tanggalTransaksi);
            imageView_jenisKendaraan = (ImageView) view.findViewById(R.id.imageView_jenisKendaraan);
            gradientRelativeLayout = (GradientRelativeLayout) view.findViewById(R.id.gradient_relative);
        }
    }

}
