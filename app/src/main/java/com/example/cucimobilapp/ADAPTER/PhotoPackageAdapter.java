package com.example.cucimobilapp.ADAPTER;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cucimobilapp.R;

import java.util.List;

public class PhotoPackageAdapter extends RecyclerView.Adapter<PhotoPackageAdapter.MyViewHolder> {

    public List<Bitmap> fileImageList;
    public Context c;

    public PhotoPackageAdapter(List<Bitmap> fileImageList, Context c){
        this.fileImageList = fileImageList;
        this.c = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PhotoPackageAdapter.MyViewHolder(LayoutInflater.from(c).inflate(R.layout.custom_list_photo_package , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Bitmap imageBitmap = fileImageList.get(position);
        holder.imageView_photo.setImageBitmap(imageBitmap);
    }

    @Override
    public int getItemCount() {
        return fileImageList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView_photo;

        public MyViewHolder(View view){
            super(view);
            imageView_photo = (ImageView) view.findViewById(R.id.imageView_package);
        }
    }

}
