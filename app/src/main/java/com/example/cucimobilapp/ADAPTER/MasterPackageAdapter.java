package com.example.cucimobilapp.ADAPTER;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.autofill.AutofillId;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cucimobilapp.CLASS.Paket;
import com.example.cucimobilapp.MASTER.EditCustomerActivity;
import com.example.cucimobilapp.MASTER.EditPackageActivity;
import com.example.cucimobilapp.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class MasterPackageAdapter extends RecyclerView.Adapter<MasterPackageAdapter.MyViewHolder> {

    private ArrayList<Paket> packages;
    private Context context;
    private String uid;

    public MasterPackageAdapter(Context c, ArrayList<Paket> packages, String uid){
        this.packages = packages;
        this.context = c;
        this.uid = uid;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MasterPackageAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_master_list_package , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView_namaPaket.setText(packages.get(position).getPackage_name());
        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

        holder.textView_harga.setText(formatRupiah.format((double) packages.get(position).getPackage_price()));
        holder.textView_jenisKendaraan.setText(packages.get(position).getPackage_vehicle_type());
        if(packages.get(position).getPackage_photo().isEmpty()){
            if(packages.get(position).getPackage_vehicle_type().equals("Mobil")){
                holder.imageView_profile.setImageResource(R.drawable.ic_no_image_package);
            }
            else if(packages.get(position).getPackage_vehicle_type().equals("Motor")){{
                holder.imageView_profile.setImageResource(R.drawable.ic_no_image_package_motor);
            }}
            holder.imageView_profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }else{
            Picasso.get().load(packages.get(position).getPackage_photo().get(0)).into(holder.imageView_profile);
        }

        if(packages.get(position).getPackage_vehicle_type().equals("Mobil")){
            holder.imageView_jenisKendaraan.setImageResource(R.drawable.ic_baseline_directions_car_black_24);
            holder.imageView_jenisKendaraan.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
        }
        else if(packages.get(position).getPackage_vehicle_type().equals("Motor")){
            holder.imageView_jenisKendaraan.setImageResource(R.drawable.ic_baseline_motorcycle_black_24);
            holder.imageView_jenisKendaraan.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
        }

        if(uid.equals("Z6bJIRmGsZYBH1WgA9RNMgLDjH42") || uid.equals("nNNPxJUHk0TEV4kPudELIFTpGmo1")){
            holder.button_edit.setVisibility(View.GONE);
        }

        holder.button_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, EditPackageActivity.class);
                i.putExtra("package_id" , packages.get(position).getId());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return packages.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView_profile;
        private ImageView imageView_jenisKendaraan;
        private TextView textView_namaPaket;
        private TextView textView_jenisKendaraan;
        private TextView textView_harga;
        private Button button_edit;

        public MyViewHolder(View view){
            super(view);
            imageView_profile = (ImageView) view.findViewById(R.id.imageView_profile);
            imageView_jenisKendaraan = (ImageView) view.findViewById(R.id.imageView_jenisKendaraan);
            textView_namaPaket = (TextView) view.findViewById(R.id.textView_namaPaket);
            textView_jenisKendaraan = (TextView) view.findViewById(R.id.textView_jenisKendaraan);
            textView_harga = (TextView) view.findViewById(R.id.textView_harga);
            button_edit = (Button) view.findViewById(R.id.button_edit);
        }
    }
}
