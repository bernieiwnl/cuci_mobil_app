package com.example.cucimobilapp.MASTER;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cucimobilapp.ADAPTER.EditPhotoTransactionAdapter;
import com.example.cucimobilapp.ADAPTER.TransactionAdapter;
import com.example.cucimobilapp.ADAPTER.TransactionPaketAdapter;
import com.example.cucimobilapp.ADAPTER.TransactionPaketAdapterInvoice;
import com.example.cucimobilapp.ADAPTER.TransactionPhotoAdapter;
import com.example.cucimobilapp.CLASS.Customer;
import com.example.cucimobilapp.CLASS.Paket;
import com.example.cucimobilapp.CLASS.PaketTransaction;
import com.example.cucimobilapp.CLASS.Transaction;
import com.example.cucimobilapp.LIST.ListPaketPencucianMobil;
import com.example.cucimobilapp.LIST.ListPaketPencucianMotor;
import com.example.cucimobilapp.LOADING.LoadingAnimation;
import com.example.cucimobilapp.PENCUCIAN.PencucianActivity;
import com.example.cucimobilapp.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class DetailTransaction extends AppCompatActivity implements View.OnClickListener, EditPhotoTransactionAdapter.OnItemClickListener {

    private TextView textView_namaCustomer;
    private TextView textView_nomorKendaraan;
    private TextView textView_nomorTelepon;
    private TextView textView_alamatCustomer;

    private MaterialButton materialButton_tambahFoto;
    private MaterialButton materialButton_lihatInvoice;

    private ImageView imageView_back;

    private CircleImageView imageView_photoCustomer;

    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private StorageTask storageTask;
    private FirebaseStorage firebaseStorage;

    private ArrayList<String> fileImageList;
    private ArrayList<Uri> fileUriList;

    private ArrayList<PaketTransaction> packages;

    private RecyclerView recyclerView_photo;
    private RecyclerView recyclerView_paket;

    private EditPhotoTransactionAdapter editPhotoTransactionAdapter;
    private TransactionPaketAdapterInvoice transactionPaketAdapter;


    private TextView textView_totalHargaPaket;

    Integer totalHarga = 0;

    private static final int RESULT_LOAD_IMAGE1 = 1;

    private String transaction_id;
    private String customer_id;

    static DetailTransaction detailTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        //ubah warna status bar sesuai keinginan;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        transaction_id =getIntent().getStringExtra("transaction_id");
        customer_id = getIntent().getStringExtra("customer_id");

        //get object data
        textView_namaCustomer = (TextView) findViewById(R.id.textView_namaCustomer);
        textView_nomorKendaraan = (TextView) findViewById(R.id.textView_nomorKendaraan);
        textView_alamatCustomer = (TextView) findViewById(R.id.textView_alamatCustomer);
        textView_nomorTelepon = (TextView) findViewById(R.id.textView_nomorTelepon);
        textView_totalHargaPaket = (TextView) findViewById(R.id.textView_hargaPaket);

        recyclerView_paket = (RecyclerView) findViewById(R.id.recycler_paketPencucian);
        recyclerView_photo = (RecyclerView) findViewById(R.id.recycler_fotoTransaksi);

        materialButton_tambahFoto = (MaterialButton) findViewById(R.id.buttonMaterial_tambahFoto);

        imageView_photoCustomer = (CircleImageView) findViewById(R.id.imageView_photoCustomer);
        imageView_back = (ImageView) findViewById(R.id.imageViewBack);

        //Firebase Database
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseStorage = FirebaseStorage.getInstance();

        recyclerView_paket.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_paket.setHasFixedSize(true);
        //Recylerview Layout
        recyclerView_photo.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView_photo.setHasFixedSize(true);
        //list image
        fileImageList = new ArrayList<>();
        fileUriList = new ArrayList<>();
        packages = new ArrayList<>();
        //onClickListener
        materialButton_tambahFoto.setOnClickListener(this);
        imageView_back.setOnClickListener(this);

        //get data customer from transaction firestore
        firebaseFirestore.collection("transaction").document(transaction_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Transaction transactions = documentSnapshot.toObject(Transaction.class);
                textView_namaCustomer.setText(transactions.getTransaction_customer_name());
                textView_nomorKendaraan.setText(transactions.getTransaction_customer_vehicle_number());
                textView_alamatCustomer.setText(transactions.getTransaction_customer_address());
                textView_nomorTelepon.setText(transactions.getTransaction_customer_phone_number());

                Locale localeID = new Locale("in", "ID");
                NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);

                textView_totalHargaPaket.setText(formatRupiah.format((double) transactions.getTransaction_price()));

                fileImageList.clear();
                for (int i = 0; i < transactions.getTransaction_photo().size(); i++) {
                    fileImageList.add(transactions.getTransaction_photo().get(i));
                }

                //recyler
                editPhotoTransactionAdapter = new EditPhotoTransactionAdapter(DetailTransaction.this, fileImageList);
                recyclerView_photo.setAdapter(editPhotoTransactionAdapter);
                editPhotoTransactionAdapter.setOnItemClickListener(DetailTransaction.this);

            }
        });

        firebaseFirestore.collection("customer").document(customer_id).addSnapshotListener(DetailTransaction.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Customer customers = documentSnapshot.toObject(Customer.class);
                if(customers.getCustomer_profile_picture().isEmpty()){
                    imageView_photoCustomer.setImageResource(R.drawable.ic_empty_profile_image);
                    imageView_photoCustomer.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }else {
                    Picasso.get().load(customers.getCustomer_profile_picture()).into(imageView_photoCustomer);
                }
            }
        });


        firebaseFirestore.collection("transaction_package").whereEqualTo("transaction_id", transaction_id).addSnapshotListener(DetailTransaction.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                packages.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        PaketTransaction data = doc.toObject(PaketTransaction.class);
                        packages.add(data);
                    }
                    transactionPaketAdapter = new TransactionPaketAdapterInvoice(DetailTransaction.this, packages);
                    recyclerView_paket.setAdapter(transactionPaketAdapter);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
            uploadFoto(transaction_id);
            fileUriList.clear();
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
        //call time
        long current = Calendar.getInstance().getTimeInMillis();
        Integer data = 0;

        final LoadingAnimation loadingAnimation = new LoadingAnimation(DetailTransaction.this);
        loadingAnimation.startLoadingLoginDialog();

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
                                loadingAnimation.dismissDialog();
                                editPhotoTransactionAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    else{
                        Toast.makeText(DetailTransaction.this,
                                "Gagal!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            data++;
        }
    }

    protected void generateInvoice(){
        PdfDocument invoiceTransacionDocument = new PdfDocument();
        Paint paint = new Paint();
        Paint forLinePaint = new Paint();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(250,350,1).create();
        PdfDocument.Page myPage = invoiceTransacionDocument.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();

        paint.setTextSize(15.5f);
        paint.setColor(Color.rgb(0,50,250));

        canvas.drawText("Slamet Otomotif Laundry", 20, 20 , paint);
        paint.setTextSize(8.5f);
        canvas.drawText("Laundry dan Detailing Motor dan Mobil", 20,40, paint);
        forLinePaint.setStyle(Paint.Style.STROKE);
        forLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5}, 0));
        forLinePaint.setStrokeWidth(2);
        canvas.drawLine(20,55,230, 55, forLinePaint );

        canvas.drawText("Nama Customer: " + textView_namaCustomer.getText().toString(), 20 , 65 , paint);
        canvas.drawText("Nomor Kendaraan : " + textView_nomorKendaraan.getText().toString(), 20 , 75, paint);
        canvas.drawLine(20,55,230, 85, forLinePaint );
        canvas.drawText("Paket Pencucian :", 20,95, paint);

        Locale localeID = new Locale("in", "ID");
        NumberFormat formatRupiah = NumberFormat.getCurrencyInstance(localeID);


        int y = 105;

        for(int i = 0; i < packages.size(); i++){
            canvas.drawText(packages.get(i).getPackage_name(),20,y ,paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(formatRupiah.format((double) packages.get(i).getPackage_price()), 230, y, paint);
            y = y + 10;
        }
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawLine( 20, y + 10 , 230 , y + 10, paint);

        canvas.drawText("TOTAL : ", 20 , y + 10, paint);

        paint.setTextAlign(Paint.Align.RIGHT);

        canvas.drawText(formatRupiah.format((double)totalHarga), 230, y + 10 , paint);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("Date : " + dateFormat.format(new Date().getTime()), 20 , y + 10, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("TERIMA KASIH !", canvas.getWidth() / 2 , y + 30, paint);

        invoiceTransacionDocument.finishPage(myPage);
        File file = new File(this.getExternalFilesDir("/"), "INVOICE-" + textView_namaCustomer.getText().toString() + " " + dateFormat.format(new Date().getTime()) + ".pdf");
        try {
            invoiceTransacionDocument.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        invoiceTransacionDocument.close();
    }

    public static DetailTransaction getInstance(){
        return  detailTransaction;
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
                firebaseFirestore.collection("transaction").document(transaction_id).update("transaction_photo", FieldValue.arrayRemove(data)).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        editPhotoTransactionAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}