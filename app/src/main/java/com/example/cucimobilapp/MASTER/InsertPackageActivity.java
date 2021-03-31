package com.example.cucimobilapp.MASTER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.cucimobilapp.ADAPTER.PhotoPackageAdapter;
import com.example.cucimobilapp.LIST.ListPaketPencucianMobil;
import com.example.cucimobilapp.LIST.ListPaketPencucianMotor;
import com.example.cucimobilapp.LOADING.LoadingAnimation;
import com.example.cucimobilapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class InsertPackageActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText textInputEditText_nama;
    private TextInputEditText textInputEditText_harga;
    private TextInputEditText textInputEditText_keterangan;
    private RadioGroup radioGroup_jenisKendaraan;
    private MaterialButton materialButton_tambahFoto;
    private CardView cardView_simpanData;
    private RecyclerView recyclerView_photo;

    private PhotoPackageAdapter photoPackageAdapter;

    private ImageView imageView_back;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private StorageTask storageTask;
    private String jenis_kendaraan;
    private static final int RESULT_LOAD_IMAGE1 = 1;
    private ArrayList<Bitmap> fileImageList;
    private ArrayList<Uri> fileUriList;

    static InsertPackageActivity insertPackageActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_package);

        textInputEditText_nama = (TextInputEditText) findViewById(R.id.txt_nama_paket);
        textInputEditText_harga = (TextInputEditText) findViewById(R.id.txt_harga_paket);
        textInputEditText_keterangan = (TextInputEditText) findViewById(R.id.txt_informasi_paket);
        materialButton_tambahFoto = (MaterialButton) findViewById(R.id.buttonMaterial_tambahFoto);
        cardView_simpanData = (CardView) findViewById(R.id.cardView_simpanData);
        radioGroup_jenisKendaraan = (RadioGroup) findViewById(R.id.radioGroup_jenisKendaraan);
        recyclerView_photo = (RecyclerView) findViewById(R.id.recycler_packagePhoto);
        imageView_back = (ImageView) findViewById(R.id.imageViewBack);

        //firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //list image
        fileImageList = new ArrayList<>();
        fileUriList = new ArrayList<>();

        recyclerView_photo.setLayoutManager(new GridLayoutManager(this , 3));
        recyclerView_photo.setHasFixedSize(true);
        //recyclerView
        photoPackageAdapter = new PhotoPackageAdapter(fileImageList, InsertPackageActivity.this);
        recyclerView_photo.setAdapter(photoPackageAdapter);

        //setoncliklistener
        cardView_simpanData.setOnClickListener(this);
        materialButton_tambahFoto.setOnClickListener(this);
        imageView_back.setOnClickListener(this);


        //ambil data dari radio button
        radioGroup_jenisKendaraan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_motor:
                        jenis_kendaraan = "Motor";
                        break;
                    case R.id.radioButton_mobil:
                        jenis_kendaraan = "Mobil";
                        break;
                }
            }
        });

        //ubah warna status bar sesuai keinginan;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        textInputEditText_nama.addTextChangedListener(textWatcher);
        textInputEditText_harga.addTextChangedListener(textWatcher);
        textInputEditText_keterangan.addTextChangedListener(textWatcher);


    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String nama = textInputEditText_nama.getText().toString().trim();
            String harga = textInputEditText_harga.getText().toString().trim();
            String informasi = textInputEditText_keterangan.getText().toString().trim();

            if(!nama.isEmpty() && !harga.isEmpty() && !informasi.isEmpty() && !jenis_kendaraan.isEmpty()){
                cardView_simpanData.setVisibility(View.VISIBLE);
            }
            else {
                cardView_simpanData.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardView_simpanData:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Apakah kamu yakin untuk meyimpan data?").setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        simpanDataPackage();
                    }
                }).setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            case R.id.buttonMaterial_tambahFoto:
                openFoto();
                break;
            case R.id.imageViewBack:
                finish();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK){
            if(data.getClipData() != null){
                int totalItemSelected = data.getClipData().getItemCount();
                for(int i = 0; i < totalItemSelected; i++){
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(fileUri));
                        fileImageList.add(bitmap);
                        fileUriList.add(fileUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    photoPackageAdapter.notifyDataSetChanged();
                }
            }
            else if(data.getData() != null){
                Uri fileUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(fileUri));
                    fileImageList.add(bitmap);
                    fileUriList.add(fileUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                photoPackageAdapter.notifyDataSetChanged();
            }
        }
    }

    protected  void openFoto(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE1);
    }

    private void simpanDataPackage(){
        String package_name = textInputEditText_nama.getText().toString().trim();
        String package_harga = textInputEditText_harga.getText().toString().trim();
        String package_information = textInputEditText_keterangan.getText().toString().trim();

        Map<String, Object> paket = new HashMap<>();
        ArrayList<String> package_photo = new ArrayList<>();
        paket.put("package_name", package_name);
        paket.put("package_price", Integer.valueOf(package_harga));
        paket.put("package_information", package_information);
        paket.put("package_vehicle_type", jenis_kendaraan);
        paket.put("package_photo", package_photo);
        paket.put("id","");

        firebaseFirestore.collection("package").add(paket).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                firebaseFirestore.collection("package").document(documentReference.getId()).update("id", documentReference.getId());
                uploadFoto(documentReference.getId());

            }
        });
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    public void uploadFoto(final String id) {

        if(fileUriList.isEmpty()){
            Intent i = new Intent();
            if(jenis_kendaraan.equals("Mobil")){
                i = new Intent(InsertPackageActivity.this, MasterListPaketMobil.class);
            }
            else if(jenis_kendaraan.equals("Motor")){
                i = new Intent(InsertPackageActivity.this, MasterListPaketMotor.class);
            }
            startActivity(i);
            finish();
        }
        else{
            //call time
            long current = Calendar.getInstance().getTimeInMillis();
            Integer data = 0;

            final LoadingAnimation loadingAnimation = new LoadingAnimation(InsertPackageActivity.this);
            loadingAnimation.startLoadingLoginDialog();

            for (int i = 0; i < fileUriList.size(); i++) {
                String fileName = getFileName(fileUriList.get(i));
                final StorageReference fileToUpload = storageReference.child("package_photo").child(current + "_" + id + "_" + i);
                storageTask = fileToUpload.putFile(fileUriList.get(i));
                storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }
                        return fileToUpload.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            String mUri = downloadUri.toString();
                            firebaseFirestore.collection("package").document(id).update("package_photo", FieldValue.arrayUnion(mUri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent i = new Intent();
                                    if(jenis_kendaraan.equals("Mobil")){
                                        i = new Intent(InsertPackageActivity.this, MasterListPaketMobil.class);
                                    }
                                    else if(jenis_kendaraan.equals("Motor")){
                                        i = new Intent(InsertPackageActivity.this, MasterListPaketMotor.class);
                                    }
                                    startActivity(i);
                                    finish();
                                }
                            });
                        }
                        else{
                            Toast.makeText(InsertPackageActivity.this,
                                    "Gagal!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                data++;
            }

            if(data == fileUriList.size()){
                loadingAnimation.dismissDialog();
            }
        }
    }

    private static InsertPackageActivity getInstance(){
        return insertPackageActivity;
    }



}