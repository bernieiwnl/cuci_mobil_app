package com.example.cucimobilapp.MASTER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.cucimobilapp.ADAPTER.EditPhotoPackageAdapter;
import com.example.cucimobilapp.ADAPTER.PhotoPackageAdapter;
import com.example.cucimobilapp.CLASS.Paket;
import com.example.cucimobilapp.LOADING.LoadingAnimation;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class EditPackageActivity extends AppCompatActivity implements View.OnClickListener, EditPhotoPackageAdapter.OnItemClickListener {

    private TextInputEditText textInputEditText_nama;
    private TextInputEditText textInputEditText_harga;
    private TextInputEditText textInputEditText_keterangan;
    private RadioGroup radioGroup_jenisKendaraan;
    private RadioButton radioButton_motor;
    private RadioButton radioButton_mobil;
    private MaterialButton materialButton_tambahFoto;
    private CardView cardView_simpanData;
    private RecyclerView recyclerView_photo;

    private ImageView imageView_back;

    private EditPhotoPackageAdapter editPhotoPackageAdapter;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private StorageTask storageTask;
    private FirebaseStorage firebaseStorage;
    private String jenis_kendaraan;
    private String idpackage;


    private static final int RESULT_LOAD_IMAGE1 = 1;
    private ArrayList<String> fileImageList;
    private ArrayList<Uri> fileUriList;

    static EditPackageActivity editPackageActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_package);

        //ubah warna status bar sesuai keinginan;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        textInputEditText_nama = (TextInputEditText) findViewById(R.id.txt_nama_paket);
        textInputEditText_harga = (TextInputEditText) findViewById(R.id.txt_harga_paket);
        textInputEditText_keterangan = (TextInputEditText) findViewById(R.id.txt_informasi_paket);
        materialButton_tambahFoto = (MaterialButton) findViewById(R.id.buttonMaterial_tambahFoto);
        cardView_simpanData = (CardView) findViewById(R.id.cardView_simpanData);
        radioGroup_jenisKendaraan = (RadioGroup) findViewById(R.id.radioGroup_jenisKendaraan);
        recyclerView_photo = (RecyclerView) findViewById(R.id.recycler_packagePhoto);
        radioButton_mobil = (RadioButton) findViewById(R.id.radioButton_mobil);
        radioButton_motor = (RadioButton) findViewById(R.id.radioButton_motor);
        imageView_back = (ImageView) findViewById(R.id.imageViewBack);

        idpackage = getIntent().getStringExtra("package_id");

        //firebase
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();

        //list image
        fileImageList = new ArrayList<>();
        fileUriList = new ArrayList<>();

        recyclerView_photo.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView_photo.setHasFixedSize(true);

        //get data foto paket
        firebaseFirestore.collection("package").document(idpackage).addSnapshotListener( new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Paket paket = documentSnapshot.toObject(Paket.class);
                textInputEditText_nama.setText(paket.getPackage_name());
                textInputEditText_harga.setText(paket.getPackage_price().toString());
                textInputEditText_keterangan.setText(paket.getPackage_information());
                fileImageList.clear();
                for (int i = 0; i < paket.getPackage_photo().size(); i++) {
                    fileImageList.add(paket.getPackage_photo().get(i));
                }

                jenis_kendaraan = paket.getPackage_vehicle_type();

                editPhotoPackageAdapter = new EditPhotoPackageAdapter(getApplicationContext(), fileImageList);
                recyclerView_photo.setAdapter(editPhotoPackageAdapter);
                editPhotoPackageAdapter.setOnItemClickListener(EditPackageActivity.this);

                if(jenis_kendaraan.equals("Mobil")){
                    radioButton_mobil.setChecked(true);
                }
                else if (jenis_kendaraan.equals("Motor")){
                    radioButton_motor.setChecked(true);
                }
            }
        });

        cardView_simpanData.setVisibility(View.GONE);

        editPhotoPackageAdapter = new EditPhotoPackageAdapter(EditPackageActivity.this, fileImageList);
        recyclerView_photo.setAdapter(editPhotoPackageAdapter);

        //setoncliklistener
        cardView_simpanData.setOnClickListener(this);
        materialButton_tambahFoto.setOnClickListener(this);
        imageView_back.setOnClickListener(this);

        //ambil data dari radio button
        radioGroup_jenisKendaraan.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton_motor:
                        jenis_kendaraan = "Motor";
                        break;
                    case R.id.radioButton_mobil:
                        jenis_kendaraan = "Mobil";
                        break;
                }
            }
        });

        textInputEditText_nama.addTextChangedListener(textWatcher);
        textInputEditText_harga.addTextChangedListener(textWatcher);
        textInputEditText_keterangan.addTextChangedListener(textWatcher);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

        if (requestCode == RESULT_LOAD_IMAGE1 && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int totalItemSelected = data.getClipData().getItemCount();
                for (int i = 0; i < totalItemSelected; i++) {
                    Uri fileUri = data.getClipData().getItemAt(i).getUri();
                    fileUriList.add(fileUri);
                }
            }
            else if(data.getData() != null){
                Uri fileUri = data.getData();
                fileUriList.add(fileUri);
            }
            uploadFoto(idpackage);
            fileUriList.clear();
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
            String harga = textInputEditText_harga.getText().toString().trim();
            String informasi = textInputEditText_keterangan.getText().toString().trim();

            if(nama !=null && harga != null && informasi !=null &&  jenis_kendaraan != null){
                cardView_simpanData.setVisibility(View.VISIBLE);
            }
            else {
                cardView_simpanData.setVisibility(View.GONE);
            }

        }
    };

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
        firebaseFirestore.collection("package").document(idpackage).update(paket).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent i = new Intent();
                if(jenis_kendaraan.equals("Mobil")){
                    i = new Intent(EditPackageActivity.this, MasterListPaketMobil.class);
                }
                else if(jenis_kendaraan.equals("Motor")){
                    i = new Intent(EditPackageActivity.this, MasterListPaketMotor.class);
                }
                startActivity(i);
                finish();
                Log.d("SERVICE", "SUKSES");
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
        //call time
        long current = Calendar.getInstance().getTimeInMillis();
        Integer data = 0;

        final LoadingAnimation loadingAnimation = new LoadingAnimation(EditPackageActivity.this);
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

                            }
                        });
                    }
                    else{
                        Toast.makeText(EditPackageActivity.this,
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

    private static EditPackageActivity getInstance(){
        return editPackageActivity;
    }

    @Override
    public void onImageclick(int position) {

    }

    @Override
    public void onImageDeleteClick(int position) {
        final String data = fileImageList.get(position);
        StorageReference storageReference = firebaseStorage.getReferenceFromUrl(data);
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseFirestore.collection("package").document(idpackage).update("package_photo", FieldValue.arrayRemove(data)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        editPhotoPackageAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}