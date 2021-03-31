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

import com.example.cucimobilapp.CLASS.Customer;
import com.example.cucimobilapp.PENCUCIAN.PencucianActivity;
import com.example.cucimobilapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder> {

    private ArrayList<Customer> Customers;
    private Context context;

    public CustomerAdapter(Context c, ArrayList<Customer> Customers){
        this.Customers = Customers;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CustomerAdapter.MyViewHolder(LayoutInflater.from(context).inflate(R.layout.custom_list_customer , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.textView_namaCustomer.setText(Customers.get(position).getCustomer_name());
        holder.textView_nomorKendaraan.setText(Customers.get(position).getCustomer_vehicle_number());
        holder.textView_jenisKendaraan.setText(Customers.get(position).getCustomer_vehicle_type());
        holder.textView_alamatRumah.setText(Customers.get(position).getCustomer_address());
        holder.textView_alamatEmail.setText(Customers.get(position).getCustomer_email());

        if(Customers.get(position).getCustomer_vehicle_type().equals("Mobil")){
            holder.imageView_jenisKendaraan.setImageResource(R.drawable.ic_baseline_directions_car_black_24);
            holder.imageView_jenisKendaraan.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
        }
        else{
            holder.imageView_jenisKendaraan.setImageResource(R.drawable.ic_baseline_motorcycle_black_24);
            holder.imageView_jenisKendaraan.setImageTintList(ColorStateList.valueOf(Color.parseColor("#000000")));

        }
        if(Customers.get(position).getCustomer_profile_picture().equals("")){
            holder.imageView_profile.setBackgroundResource(R.drawable.ic_empty_profile_image);
            holder.imageView_profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        else{
            Picasso.get().load(Customers.get(position).getCustomer_profile_picture()).into(holder.imageView_profile);
            holder.imageView_profile.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }


        holder.button_pilih.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Customers.get(position).getCustomer_vehicle_type().equals("Mobil")){
                    Intent intent = new Intent(context,  PencucianActivity.class);
                    intent.putExtra("vehicle_type", "Mobil");
                    intent.putExtra("customer_id", Customers.get(position).getId());
                    intent.putExtra("customer_name", Customers.get(position).getCustomer_name());
                    intent.putExtra("customer_vehicle_number", Customers.get(position).getCustomer_vehicle_number());
                    intent.putExtra("customer_address",Customers.get(position).getCustomer_address());
                    intent.putExtra("customer_phone_number",Customers.get(position).getCustomer_phone_number());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }
                else if(Customers.get(position).getCustomer_vehicle_type().equals("Motor")){
                    Intent intent = new Intent(context,  PencucianActivity.class);
                    intent.putExtra("vehicle_type", "Motor");
                    intent.putExtra("customer_id", Customers.get(position).getId());
                    intent.putExtra("customer_name", Customers.get(position).getCustomer_name());
                    intent.putExtra("customer_vehicle_number", Customers.get(position).getCustomer_vehicle_number());
                    intent.putExtra("customer_address",Customers.get(position).getCustomer_address());
                    intent.putExtra("customer_phone_number",Customers.get(position).getCustomer_phone_number());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.getApplicationContext().startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return Customers.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{


        private ImageView imageView_profile;
        private ImageView imageView_jenisKendaraan;
        private TextView textView_namaCustomer;
        private TextView textView_nomorKendaraan;
        private TextView textView_jenisKendaraan;
        private TextView textView_alamatRumah;
        private TextView textView_alamatEmail;
        private Button button_pilih;

        public MyViewHolder(View view){
            super(view);
            imageView_profile = (ImageView) view.findViewById(R.id.imageView_profile);
            imageView_jenisKendaraan = (ImageView) view.findViewById(R.id.imageView_jenisKendaraan);
            textView_namaCustomer = (TextView) view.findViewById(R.id.textView_namaCustomer);
            textView_jenisKendaraan = (TextView) view.findViewById(R.id.textView_jenisKendaraan);
            textView_nomorKendaraan = (TextView) view.findViewById(R.id.textView_nomorKendaraan);
            textView_alamatEmail = (TextView) view.findViewById(R.id.textView_email);
            textView_alamatRumah = (TextView) view.findViewById(R.id.textView_alamat);
            button_pilih = (Button) view.findViewById(R.id.button_pilih);
        }
    }


}
