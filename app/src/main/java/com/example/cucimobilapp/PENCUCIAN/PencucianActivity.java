package com.example.cucimobilapp.PENCUCIAN;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cucimobilapp.ADAPTER.TransactionPaketAdapter;
import com.example.cucimobilapp.ADAPTER.TransactionPhotoAdapter;
import com.example.cucimobilapp.CLASS.Customer;
import com.example.cucimobilapp.CLASS.Paket;
import com.example.cucimobilapp.LIST.ListPaketPencucianMobil;
import com.example.cucimobilapp.LIST.ListPaketPencucianMotor;
import com.example.cucimobilapp.LOADING.LoadingAnimation;
import com.example.cucimobilapp.MASTER.DetailTransaction;
import com.example.cucimobilapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PencucianActivity extends AppCompatActivity implements View.OnClickListener {

    //intent data
    private String customer_id;
    private String customer_vehicle_type;
    private String customer_name;
    private String customer_address;
    private String customer_phone_number;
    private String customer_vehicle_number;

    private TextView textView_namaCustomer;
    private TextView textView_nomorKendaraan;
    private TextView textView_nomorTelepon;
    private TextView textView_alamatCustomer;

    private MaterialButton materialButton_tambahPaket;
    private MaterialButton materialButton_tambahFoto;

    private CircleImageView imageView_photoCustomer;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private StorageTask storageTask;

    private ArrayList<Bitmap> fileImageList;
    private ArrayList<Uri> fileUriList;

    private ArrayList<Paket> packages;

    private RecyclerView recyclerView_photo;
    private RecyclerView recyclerView_paket;

    private TransactionPhotoAdapter transactionPhotoAdapter;
    private TransactionPaketAdapter transactionPaketAdapter;

    private CardView cardView_simpanData;

    private TextView textView_totalHargaPaket;

    private ImageView imageView_back;

    Integer totalHarga = 0;

    private static final int RESULT_LOAD_IMAGE1 = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pencucian);

        //ubah warna status bar sesuai keinginan;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        //get intent data
        customer_id =getIntent().getStringExtra("customer_id");
        customer_vehicle_type = getIntent().getStringExtra("vehicle_type");
        customer_name = getIntent().getStringExtra("customer_name");
        customer_address = getIntent().getStringExtra("customer_address");
        customer_phone_number = getIntent().getStringExtra("customer_phone_number");
        customer_vehicle_number = getIntent().getStringExtra("customer_vehicle_number");



        //get object data
        textView_namaCustomer = (TextView) findViewById(R.id.textView_namaCustomer);
        textView_nomorKendaraan = (TextView) findViewById(R.id.textView_nomorKendaraan);
        textView_alamatCustomer = (TextView) findViewById(R.id.textView_alamatCustomer);
        textView_nomorTelepon = (TextView) findViewById(R.id.textView_nomorTelepon);
        textView_totalHargaPaket = (TextView) findViewById(R.id.textView_hargaPaket);

        recyclerView_paket = (RecyclerView) findViewById(R.id.recycler_paketPencucian);
        recyclerView_photo = (RecyclerView) findViewById(R.id.recycler_fotoTransaksi);

        imageView_back = (ImageView) findViewById(R.id.imageViewBack);

        cardView_simpanData = (CardView) findViewById(R.id.cardView_simpanData);

        materialButton_tambahFoto = (MaterialButton) findViewById(R.id.buttonMaterial_tambahFoto);
        materialButton_tambahPaket = (MaterialButton) findViewById(R.id.buttonMaterial_tambahPaket);

        imageView_photoCustomer = (CircleImageView) findViewById(R.id.imageView_photoCustomer);

        //Firebase Database
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        recyclerView_paket.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_paket.setHasFixedSize(true);

        //Recylerview Layout
        recyclerView_photo.setLayoutManager(new GridLayoutManager(this , 2));
        recyclerView_photo.setHasFixedSize(true);

        //list image
        fileImageList = new ArrayList<>();
        fileUriList = new ArrayList<>();

        packages = new ArrayList<>();

        //onClickListener
        materialButton_tambahPaket.setOnClickListener(this);
        materialButton_tambahFoto.setOnClickListener(this);
        cardView_simpanData.setOnClickListener(this);
        imageView_back.setOnClickListener(this);

        cardView_simpanData.setVisibility(View.GONE);

        //get data customer from database firestore
        firebaseFirestore.collection("customer").document(customer_id).addSnapshotListener(PencucianActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Customer customers = documentSnapshot.toObject(Customer.class);
                textView_namaCustomer.setText(customers.getCustomer_name());
                textView_nomorKendaraan.setText(customers.getCustomer_vehicle_number());
                textView_alamatCustomer.setText(customers.getCustomer_address());
                textView_nomorTelepon.setText(customers.getCustomer_phone_number());
                if(customers.getCustomer_profile_picture().isEmpty()){
                    imageView_photoCustomer.setImageResource(R.drawable.ic_empty_profile_image);
                    imageView_photoCustomer.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }else {
                    Picasso.get().load(customers.getCustomer_profile_picture()).into(imageView_photoCustomer);
                }
            }
        });

        //recyler
        transactionPhotoAdapter = new TransactionPhotoAdapter(fileImageList, PencucianActivity.this);
        recyclerView_photo.setAdapter(transactionPhotoAdapter);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("hapus-paket"));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonMaterial_tambahFoto:
                openFoto();
                break;
            case R.id.buttonMaterial_tambahPaket:
                if(customer_vehicle_type.equals("Mobil")){
                    startActivityForResult( new Intent(PencucianActivity.this, ListPaketPencucianMobil.class), 1);
                }
                else if(customer_vehicle_type.equals("Motor")){
                    startActivityForResult( new Intent(PencucianActivity.this, ListPaketPencucianMotor.class), 1);
                }
                break;
            case R.id.cardView_simpanData:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Apakah kamu yakin untuk meyimpan data?").setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        simpanDataTransaksi();
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
            case R.id.imageViewBack:
                finish();
                break;

        }
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            Integer position = intent.getIntExtra("position", 0);
            Integer price = intent.getIntExtra("hargaPaket", 0);
            packages.remove(intent.getIntExtra("position", 0));

            totalHarga = totalHarga - price;
            Locale localeID = new Locale("in", "ID");
            NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);
            Log.d("isi list setelah hapus", packages.size() + "");
            textView_totalHargaPaket.setText(formatRupiah.format((double) totalHarga));
            transactionPaketAdapter.notifyDataSetChanged();
            if(packages.size() == 0){
                cardView_simpanData.setVisibility(View.GONE);
            }
        }
    };

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
                    transactionPhotoAdapter.notifyDataSetChanged();
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
                transactionPhotoAdapter.notifyDataSetChanged();
            }
            else if(requestCode == 1 && resultCode == RESULT_OK){

                String name = data.getStringExtra("name_package");
                Integer price = data.getIntExtra("price_package", 0);
                String type = data.getStringExtra("type_package");
                ArrayList<String> foto = new ArrayList<>();
                foto = data.getStringArrayListExtra("image_package");
                String information = data.getStringExtra("information_package");
                String id = data.getStringExtra("id");

                packages.add(new Paket(id,name, information, price , foto, type));
                transactionPaketAdapter = new TransactionPaketAdapter(PencucianActivity.this,packages);
                recyclerView_paket.setAdapter(transactionPaketAdapter);

                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

                totalHarga += price;

                textView_totalHargaPaket.setText(formatRupiah.format((double) totalHarga));
                Log.d("isi list setelah tambah", packages.size() + "");
                cardView_simpanData.setVisibility(View.VISIBLE);
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
            Intent i = new Intent(PencucianActivity.this, DetailTransaction.class);
            i.putExtra("transaction_id", id);
            i.putExtra("customer_id" , customer_id);
            startActivity(i);
            finish();
        }
        else{
            //call time
            long current = Calendar.getInstance().getTimeInMillis();
            Integer data = 0;

            for (int i = 0; i < fileUriList.size(); i++) {
                String fileName = getFileName(fileUriList.get(i));
                final StorageReference fileToUpload = storageReference.child("transaction_photo").child(current + "_" + id + "_" + i);
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
                            firebaseFirestore.collection("transaction").document(id).update("transaction_photo", FieldValue.arrayUnion(mUri)).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Intent i = new Intent(PencucianActivity.this, DetailTransaction.class);
                                    i.putExtra("transaction_id", id);
                                    i.putExtra("customer_id" , customer_id);
                                    startActivity(i);
                                    finish();
                                }
                            });
                        }
                        else{
                            Toast.makeText(PencucianActivity.this,
                                    "Gagal!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                data++;
            }
        }
    }

    private void simpanDataTransaksi(){
        String transaction_customer_id = customer_id;
        String transaction_customer_type = customer_vehicle_type;
        final String transaction_customer_name = customer_name;
        String transaction_customer_address = customer_address;
        String transaction_customer_phone_number = customer_phone_number;
        String transaction_customer_vehicle_number = customer_vehicle_number;
        final Timestamp transaction_date = new Timestamp(System.currentTimeMillis());
        Integer transaction_price = totalHarga;

        Map<String, Object> transaksi = new HashMap<>();
        transaksi.put("transaction_id", "");
        transaksi.put("transaction_customer_id", transaction_customer_id);
        transaksi.put("transaction_customer_type", transaction_customer_type);
        transaksi.put("transaction_customer_name", transaction_customer_name);
        transaksi.put("transaction_customer_address", transaction_customer_address);
        transaksi.put("transaction_customer_phone_number", transaction_customer_phone_number);
        transaksi.put("transaction_customer_vehicle_number", transaction_customer_vehicle_number);
        transaksi.put("transaction_date", transaction_date);
        ArrayList<String> transaction_photo = new ArrayList<>();
        transaksi.put("transaction_photo", transaction_photo);
        transaksi.put("transaction_price", transaction_price);

        firebaseFirestore.collection("transaction").add(transaksi).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(final DocumentReference documentReference) {
                for(int i = 0; i < packages.size(); i++){
                    Map<String, Object> PaketTransaction = new HashMap<>();
                    PaketTransaction.put("id", "");
                    PaketTransaction.put("package_id", packages.get(i).getId());
                    PaketTransaction.put("transaction_customer_name", transaction_customer_name);
                    PaketTransaction.put("customer_id", customer_id);
                    PaketTransaction.put("transaction_date", transaction_date);
                    PaketTransaction.put("transaction_id", documentReference.getId());
                    PaketTransaction.put("package_name", packages.get(i).getPackage_name());
                    PaketTransaction.put("package_price", packages.get(i).getPackage_price());
                    PaketTransaction.put("package_vehicle_type", packages.get(i).getPackage_vehicle_type());
                    PaketTransaction.put("package_photo", packages.get(i).getPackage_photo());
                    firebaseFirestore.collection("transaction_package").add(PaketTransaction).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            firebaseFirestore.collection("transaction_package").document(documentReference.getId()).update("id", documentReference.getId());
                        }
                    });
                }
                firebaseFirestore.collection("transaction").document(documentReference.getId()).update("transaction_id", documentReference.getId()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        uploadFoto(documentReference.getId());
                    }
                });
            }
        });
    }
}