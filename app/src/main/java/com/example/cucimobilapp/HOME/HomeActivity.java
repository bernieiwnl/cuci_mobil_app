package com.example.cucimobilapp.HOME;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.cucimobilapp.LIST.ListCustomerMobil;
import com.example.cucimobilapp.LIST.ListCustomerMotor;
import com.example.cucimobilapp.MainActivity;
import com.example.cucimobilapp.NAVIGATION.CuciMobilFragment;
import com.example.cucimobilapp.NAVIGATION.CuciMotorFragment;
import com.example.cucimobilapp.NAVIGATION.ProfileFragment;
import com.example.cucimobilapp.NAVIGATION.TransactionFragment;
import com.example.cucimobilapp.R;
import com.example.cucimobilapp.UTAMA.LoadScreenActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.shape.CornerFamily;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private BottomNavigationView bottomNavigationView;
    private BottomAppBar bottomAppBar;
    private FloatingActionButton floatingActionButton;
    private BottomSheetDialog bottomSheetDialog;

    private CuciMobilFragment cuciMobilFragment = new CuciMobilFragment();
    private CuciMotorFragment cuciMotorFragment = new CuciMotorFragment();
    private TransactionFragment transactionFragment = new TransactionFragment();
    private ProfileFragment profileFragment = new ProfileFragment();

    final int CAMERA_CAPTURE = 1;
    private Uri IMAGE_URI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation_button);
        bottomAppBar = (BottomAppBar) findViewById(R.id.bottomapp_bar);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_scan_button);


        bottomNavigationView.setBackground(null);
        bottomNavigationView.getMenu().getItem(2).setEnabled(false);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_mobil);

        floatingActionButton.setOnClickListener(this);


    }

    private void setShapeBottomAppBar(String color)
    {
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
        bottomAppBar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(color)));
        MaterialShapeDrawable bottomBarBackground = (MaterialShapeDrawable) bottomAppBar.getBackground();
        bottomBarBackground.setShapeAppearanceModel(
                bottomBarBackground.getShapeAppearanceModel()
                        .toBuilder()
                        .setTopRightCorner(CornerFamily.ROUNDED,70)
                        .setTopLeftCorner(CornerFamily.ROUNDED,70)
                        .build());
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.navigation_mobil:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout , cuciMobilFragment).commit();
                setShapeBottomAppBar("#ffffff");
                return true;
            case R.id.navigation_motor:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout , cuciMotorFragment).commit();
                setShapeBottomAppBar("#ffffff");
                return true;

            case R.id.navigation_salon:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, transactionFragment).commit();
                setShapeBottomAppBar("#ffffff");
                return true;
            case R.id.navigation_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout , profileFragment).commit();
                setShapeBottomAppBar("#ffffff");
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.floating_scan_button:
                //grant permission camera
                bottomSheetDialog = new BottomSheetDialog(HomeActivity.this,R.style.BottomSheetTheme);
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
        }
    }


    private void checkPermissionOfCamera(){
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            //grant the permission here
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_CAPTURE);
        }
    }

    private void accessCamera(){
        CropImage.activity().start(HomeActivity.this);
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
                        platnomor.replaceAll("\n", "").replaceAll("\r", "").replace("\\s+", "").replace(" ", "").trim();
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