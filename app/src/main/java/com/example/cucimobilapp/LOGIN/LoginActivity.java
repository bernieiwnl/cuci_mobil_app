package com.example.cucimobilapp.LOGIN;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cucimobilapp.HOME.HomeActivity;
import com.example.cucimobilapp.LOADING.LoadingAnimation;
import com.example.cucimobilapp.MainActivity;
import com.example.cucimobilapp.R;
import com.example.cucimobilapp.REGISTER.RegisterActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    //Object Dependencies
    private TextInputEditText textEmail, textPassword;

    private Button buttonLogin;
    FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Object Reference
        textEmail =(TextInputEditText) findViewById(R.id.txtEmail);
        textPassword = (TextInputEditText) findViewById(R.id.txtPassword);

        buttonLogin = (Button) findViewById(R.id.btnLogin);
        firebaseAuth = FirebaseAuth.getInstance();

        //membuat object dapat di click
        buttonLogin.setOnClickListener(this);


        //cek apakah email dan password terisi
        textEmail.addTextChangedListener(loginWatcher);
        textPassword.addTextChangedListener(loginWatcher);


        //check apakah akun sudah login atau belum
        checkLoginOrNot();




    }



    private void checkLoginOrNot(){
        //check if user logged in or not
        if(firebaseAuth.getInstance().getCurrentUser() != null)
        {
            startActivity( new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }

    }

    private TextWatcher loginWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = textEmail.getText().toString().trim();
            String passwordInput = textPassword.getText().toString().trim();
            buttonLogin.setEnabled(!emailInput.isEmpty() && !passwordInput.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogin:
                //ambil data dari email dan password
                String email = textEmail.getText().toString().trim();
                String password = textPassword.getText().toString().trim();

                //panggil metode login agar dapat login kedalam aplikasi
                loginFirebaseMethod(email,password);
                break;
        }
    }


    private void loginFirebaseMethod(String email, String password){

        //animasi ketika berhasil login
        final LoadingAnimation loadingAnimation = new LoadingAnimation(LoginActivity.this);

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loadingAnimation.startLoadingLoginDialog();
                    Handler handler = new Handler();

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingAnimation.dismissDialog();
                            Toast.makeText(LoginActivity.this, "Login Succsessfully", Toast.LENGTH_SHORT).show();
                            startActivity( new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        }
                    }, 5000);

                }
                else
                {
                    Toast.makeText(LoginActivity.this, "Error !" + task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}