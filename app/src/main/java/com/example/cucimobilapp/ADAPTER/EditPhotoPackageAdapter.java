package com.example.cucimobilapp.ADAPTER;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cucimobilapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class EditPhotoPackageAdapter extends RecyclerView.Adapter<EditPhotoPackageAdapter.MyViewHolder> {

    private Context c;
    private ArrayList<String> fotoPackage;
    private OnItemClickListener clickListener;

    public EditPhotoPackageAdapter(Context c, ArrayList<String> fotoPackage){
        this.c = c;
        this.fotoPackage = fotoPackage;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EditPhotoPackageAdapter.MyViewHolder(LayoutInflater.from(c).inflate(R.layout.custom_list_photo_package , parent , false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(fotoPackage.get(position)).into(holder.imageView_photo);
    }

    @Override
    public int getItemCount() {
        return fotoPackage.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        private ImageView imageView_photo;

        public MyViewHolder(View view){
            super(view);
            imageView_photo = (ImageView) view.findViewById(R.id.imageView_package);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(clickListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            clickListener.onImageDeleteClick(position);
                            return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    clickListener.onImageclick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem Delete = menu.add(Menu.NONE, 1 , 1 , "Delete");
            Delete.setOnMenuItemClickListener(this);
        }
    }

    public interface OnItemClickListener{
        void onImageclick(int position);
        void onImageDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        clickListener = listener;
    }

}
