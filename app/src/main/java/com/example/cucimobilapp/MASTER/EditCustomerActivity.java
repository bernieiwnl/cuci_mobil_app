package com.example.cucimobilapp.MASTER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.cucimobilapp.ADAPTER.TransactionPaketAdapterInvoice;
import com.example.cucimobilapp.CLASS.Customer;
import com.example.cucimobilapp.CLASS.PaketTransaction;
import com.example.cucimobilapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EditCustomerActivity extends AppCompatActivity implements View.OnClickListener {

    private MaterialButton materialButton_tambahFoto;
    private TextInputEditText textInputEditText_nama, textInputEditText_email, textInputEditText_nomorKendaraan, textInputEditText_nomorTelepon, textInputEditText_alamat;
    private RadioGroup radioGroup_jenisCustomer;

    private ImageView imageView_profile;
    private ImageView imageView_back;
    private CardView cardView_simpanData;

    //data string
    private String jenisCustomer;
    private String customer_name;
    private String customer_email;
    private String customer_vehicle_number;
    private String customer_vehicle_type;
    private String customer_address;
    private String customer_phone;
    private String customer_id;

    //firestore dan storage
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private StorageTask storageTask;

    private RadioButton radioButton_motor;
    private RadioButton radioButton_mobil;

    //galery request
    private static final int GALLERY_REQUEST_CODE = 2;

    static EditCustomerActivity editCustomerActivity;

    //foto
    private Uri uri_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);

        //ubah warna status bar sesuai keinginan;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#B00020"));
        }

        //ambil data object yang ada didalam layout;
        textInputEditText_nama = (TextInputEditText) findViewById(R.id.txt_nama_customer);
        textInputEditText_alamat = (TextInputEditText) findViewById(R.id.txt_alamat_lengkap);
        textInputEditText_email = (TextInputEditText) findViewById(R.id.txt_email_customer);
        textInputEditText_nomorKendaraan = (TextInputEditText) findViewById(R.id.txt_plat_nomor);
        textInputEditText_nomorTelepon = (TextInputEditText) findViewById(R.id.txt_nomor_telepon);
        materialButton_tambahFoto = (MaterialButton) findViewById(R.id.buttonMaterial_tambahFoto);
        radioGroup_jenisCustomer = (RadioGroup) findViewById(R.id.radioGroup_jenisKendaraan);
        imageView_profile = (ImageView) findViewById(R.id.imageView_profile);
        imageView_back = (ImageView) findViewById(R.id.imageViewBack);
        cardView_simpanData = (CardView) findViewById(R.id.cardView_simpanData);
        radioButton_mobil = (RadioButton) findViewById(R.id.radioButton_mobil);
        radioButton_motor = (RadioButton) findViewById(R.id.radioButton_motor);

        customer_id = getIntent().getStringExtra("customer_id");

        //firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //setOnclick listener untuk button yang dibutuhkan;
        materialButton_tambahFoto.setOnClickListener(this);
        imageView_back.setOnClickListener(this);

        cardView_simpanData.setOnClickListener(this);

        //hubungkan ke database;
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("customer_profile_picture");


        //ambil data dari radio button
        radioGroup_jenisCustomer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radioButton_motor:
                        jenisCustomer = "Motor";
                        break;
                    case R.id.radioButton_mobil:
                        jenisCustomer = "Mobil";
                        break;
                }
            }
        });

        firebaseFirestore.collection("customer").document(customer_id).addSnapshotListener(EditCustomerActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Customer customers = documentSnapshot.toObject(Customer.class);
                textInputEditText_nama.setText(customers.getCustomer_name());
                textInputEditText_alamat.setText(customers.getCustomer_address());
                textInputEditText_nomorKendaraan.setText(customers.getCustomer_vehicle_number());
                textInputEditText_email.setText(customers.getCustomer_email());
                textInputEditText_nomorTelepon.setText(customers.getCustomer_phone_number());
                if(customers.getCustomer_profile_picture().isEmpty()){
                    imageView_profile.setImageResource(R.drawable.ic_empty_profile_image);
                }
                else{
                    Picasso.get().load(customers.getCustomer_profile_picture()).into(imageView_profile);
                }
                jenisCustomer = customers.getCustomer_vehicle_type();
                if(jenisCustomer.equals("Mobil")){
                    radioButton_mobil.setChecked(true);
                }
                else if (jenisCustomer.equals("Motor")){
                    radioButton_motor.setChecked(true);
                }

            }
        });

        textInputEditText_nama.addTextChangedListener(textWatcher);
        textInputEditText_nomorTelepon.addTextChangedListener(textWatcher);
        textInputEditText_nomorKendaraan.addTextChangedListener(textWatcher);
        textInputEditText_email.addTextChangedListener(textWatcher);
        textInputEditText_alamat.addTextChangedListener(textWatcher);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonMaterial_tambahFoto:
                openImage();
                break;
            case R.id.cardView_simpanData:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Apakah kamu yakin untuk meyimpan data?").setCancelable(false).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        simpanDataCustomer();
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
            String alamat = textInputEditText_alamat.getText().toString().trim();
            String email = textInputEditText_email.getText().toString().trim();
            String nomorKendaraan = textInputEditText_nomorKendaraan.toString().trim();
            String nomorTelepon = textInputEditText_nomorTelepon.toString().trim();
            String jenis = jenisCustomer;

            if(nama != null && alamat != null && email != null && nomorKendaraan != null  && nomorTelepon != null && jenis != null ){
                cardView_simpanData.setVisibility(View.VISIBLE);
            }
            else{
                cardView_simpanData.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            setPic();
            uri_image = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri_image));
                imageView_profile.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView_profile.setImageBitmap(bitmap);
            uploadFotoFirebase(customer_id);
        }
    }

    //mmethod simpan data customer ke firestore
    private void simpanDataCustomer(){
        //tampung input data yang berasal dari textbox ke dalam string
        customer_name = textInputEditText_nama.getText().toString().trim();
        customer_email = textInputEditText_email.getText().toString().trim();
        customer_vehicle_number = textInputEditText_nomorKendaraan.getText().toString().trim();
        customer_vehicle_type = jenisCustomer;
        customer_address = textInputEditText_alamat.getText().toString().trim();
        customer_phone = textInputEditText_nomorTelepon.getText().toString().trim();

        //buat array untuk menampung data sementara
        Map<String, Object> customer = new HashMap<>();
        customer.put("customer_name", customer_name);
        customer.put("customer_address", customer_address);
        customer.put("customer_email", customer_email);
        customer.put("customer_vehicle_number", customer_vehicle_number);
        customer.put("customer_vehicle_type", customer_vehicle_type);
        customer.put("customer_phone_number", customer_phone);

        firebaseFirestore.collection("customer").document(customer_id).update(customer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("SERVICE", "SUKSES");
                Toast.makeText(getApplicationContext(), "SUKSES SIMPAN DATA", Toast.LENGTH_SHORT).show();
                Intent i = new Intent();
                if(jenisCustomer.equals("Mobil")){
                    i = new Intent(EditCustomerActivity.this, MasterListCustomerMobil.class);
                }
                else if(jenisCustomer.equals("Motor")){
                    i = new Intent(EditCustomerActivity.this, MasterListCustomerMotor.class);
                }
                startActivity(i);
                finish();
            }
        });
    }

    private void uploadFotoFirebase(final String id){
        if(uri_image != null){
            final StorageReference fileReference = storageReference.child(customer_id + "." + getFileExtension(uri_image));
            storageTask = fileReference.putFile(uri_image);

            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw  task.getException();
                    }
                    return  fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();
                        Log.e("customer_vehicle" , "" + mUri);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("customer_profile_picture", mUri);
                        firebaseFirestore.collection("customer").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "SUKSES SIMPAN FOTO", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
    }

    //ambil foto ke galeri
    private void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent , GALLERY_REQUEST_CODE );
    }

    //pasang foto di imageview profile
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView_profile.getWidth();
        int targetH = imageView_profile.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile("fCurrentPath", bmOptions);
        imageView_profile.setImageBitmap(bitmap);
    }

    // ambil extension dari gambar yang sudah diupload
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private static EditCustomerActivity getInstance(){
        return editCustomerActivity;
    }


}