package com.example.cucimobilapp.UTAMA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agrawalsuneet.dotsloader.loaders.LazyLoader;
import com.example.cucimobilapp.CLASS.Customer;
import com.example.cucimobilapp.HOME.HomeActivity;
import com.example.cucimobilapp.LIST.ListCustomerMobil;
import com.example.cucimobilapp.LIST.ListCustomerMotor;
import com.example.cucimobilapp.PENCUCIAN.PencucianActivity;
import com.example.cucimobilapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;

public class LoadScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private LazyLoader lazyLoader;
    private ImageView imageView_notFound;
    private RelativeLayout relativeLayout_layout1;
    private RelativeLayout relativeLayout_layout2;
    private Button button_getCustomer;
    private BottomSheetDialog bottomSheetDialog;

    private String customer_vehicle_number;

    private FirebaseFirestore firebaseFirestore;
    private ArrayList<Customer> customers;

    final int CAMERA_CAPTURE = 1;
    private Uri IMAGE_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen);

        //object data
        relativeLayout_layout1 = (RelativeLayout) findViewById(R.id.layout1);
        relativeLayout_layout2 = (RelativeLayout) findViewById(R.id.layout2);
        lazyLoader = (LazyLoader) findViewById(R.id.lazyLoader);
        imageView_notFound = (ImageView) findViewById(R.id.imageView_notFound);
        button_getCustomer = (Button) findViewById(R.id.button_getCustomer);

        button_getCustomer.setOnClickListener(this);

        //first run layout
        relativeLayout_layout2.setVisibility(View.GONE);
        relativeLayout_layout1.setVisibility(View.VISIBLE);
        button_getCustomer.setVisibility(View.GONE);

        //intent data
        customer_vehicle_number = getIntent().getStringExtra("data_plat_nomor").replaceAll("\n", "").replaceAll("\r", "").replace("\\s+", "").replace(" ", "").trim();
        Toast.makeText(getApplicationContext(), customer_vehicle_number, Toast.LENGTH_SHORT).show();

        //loading animation
        LazyLoader loader = new LazyLoader(this, 30, 20, ContextCompat.getColor(this, R.color.loader_selected),
                ContextCompat.getColor(this, R.color.loader_selected),
                ContextCompat.getColor(this, R.color.loader_selected));
        loader.setAnimDuration(500);
        loader.setFirstDelayDuration(100);
        loader.setSecondDelayDuration(200);
        loader.setInterpolator(new LinearInterpolator());
        lazyLoader.addView(loader);

        //get insance firebase
        firebaseFirestore = FirebaseFirestore.getInstance();

        //new array list data
        customers = new ArrayList<>();

        //get data on firebase
        firebaseFirestore.collection("customer").whereEqualTo("customer_vehicle_number", customer_vehicle_number).addSnapshotListener(LoadScreenActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                customers.clear();
                if(!queryDocumentSnapshots.isEmpty()){
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Customer data = doc.toObject(Customer.class);
                        customers.add(data);
                        Intent intent = new Intent(LoadScreenActivity.this, PencucianActivity.class);
                        intent.putExtra("vehicle_type", customers.get(0).getCustomer_vehicle_type());
                        intent.putExtra("customer_id", customers.get(0).getId());
                        intent.putExtra("customer_name", customers.get(0).getCustomer_name());
                        intent.putExtra("customer_vehicle_number", customers.get(0).getCustomer_vehicle_number());
                        intent.putExtra("customer_address",customers.get(0).getCustomer_address());
                        intent.putExtra("customer_phone_number",customers.get(0).getCustomer_phone_number());
                        startActivity(intent);
                    }
                }
                else {
                    relativeLayout_layout1.setVisibility(View.GONE);
                    relativeLayout_layout2.setVisibility(View.VISIBLE);
                    button_getCustomer.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_getCustomer:
                bottomSheetDialog = new BottomSheetDialog(LoadScreenActivity.this,R.style.BottomSheetTheme);
                View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_bottom_sheet_layout, (ViewGroup) findViewById(R.id.bottomSheetLayout));
                sheetView.findViewById(R.id.linier_search_customer_mobil).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), ListCustomerMobil.class);
                        startActivity(i);
                    }
                });

                sheetView.findViewById(R.id.linier_search_customer_motor).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(), ListCustomerMotor.class);
                        startActivity(i);
                    }
                });

                sheetView.findViewById(R.id.linier_scan_vehicle_number).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkPermissionOfCamera();
                        accessCamera();
                    }
                });
                bottomSheetDialog.setContentView(sheetView);
                bottomSheetDialog.show();
                break;
        }
    }

    private void checkPermissionOfCamera(){
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //grant the permission here
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CAPTURE);
        }
    }

    private void accessCamera(){
        CropImage.activity().start(LoadScreenActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                IMAGE_URI = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), IMAGE_URI);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
                //get instance firebase vision
                FirebaseVision firebaseVision = FirebaseVision.getInstance();
                //create Instance of firebaseVisionTextRecognation
                FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
                //task to process image to text
                firebaseVisionTextRecognizer.processImage(firebaseVisionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        // if data success
                        String platnomor = firebaseVisionText.getText();
                        platnomor.replaceAll("\n", "").replaceAll("\r", "").replaceAll("\\s+", "").trim();
                        Intent i = new Intent(getApplicationContext(), LoadScreenActivity.class);
                        i.putExtra("data_plat_nomor", platnomor.toUpperCase().replaceAll("\\s+",""));
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // if failure
                    }
                });
            }
        }

    }
}