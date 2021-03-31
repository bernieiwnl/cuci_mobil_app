package com.example.cucimobilapp.ADAPTER;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cucimobilapp.CLASS.Paket;
import com.example.cucimobilapp.R;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class TransactionPaketAdapter extends RecyclerView.Adapter<TransactionPaketAdapter.MyViewHolder> {

    private ArrayList<Paket> packages;
    private Context context;

    public TransactionPaketAdapter(Context c, ArrayList<Paket> packages){
        this.packages = packages;
        this.context = c;
    }

    @NonNull
    @Override
    public TransactionPaketAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionPaketAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_list_transaction_paket , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView_namaPaket.setText(packages.get(position).getPackage_name());
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
        holder.textView_hargaPaket.setText(formatRupiah.format((double) packages.get(position).getPackage_price()));
        if(packages.get(position).getPackage_photo().isEmpty()){
            if(packages.get(position).getPackage_vehicle_type().equals("Mobil")){
                holder.imageView_photoPaket.setImageResource(R.drawable.ic_no_image_package);
            }
            else if(packages.get(position).getPackage_vehicle_type().equals("Motor")){{
                holder.imageView_photoPaket.setImageResource(R.drawable.ic_no_image_package_motor);
            }}
            holder.imageView_photoPaket.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else{
            Picasso.get().load(packages.get(position).getPackage_photo().get(0)).into(holder.imageView_photoPaket);
        }
        holder.buttonMaterial_Hapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("hapus-paket");
                intent.putExtra("position", position);
                intent.putExtra("hargaPaket", packages.get(position).getPackage_price());
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{


        private ImageView imageView_photoPaket;
        private TextView textView_namaPaket;
        private TextView textView_hargaPaket;
        private MaterialButton buttonMaterial_Hapus;

        public MyViewHolder(View view){
            super(view);
            imageView_photoPaket = (ImageView) view.findViewById(R.id.imageView_photoPaket);
            textView_namaPaket = (TextView) view.findViewById(R.id.textView_namaPaket);
            textView_hargaPaket = (TextView) view.findViewById(R.id.textView_hargaPaket);
            buttonMaterial_Hapus = (MaterialButton) view.findViewById(R.id.buttonMaterial_Hapus);

        }
    }

}
