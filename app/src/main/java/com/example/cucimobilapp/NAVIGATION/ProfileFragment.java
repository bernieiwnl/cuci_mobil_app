package com.example.cucimobilapp.NAVIGATION;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.cucimobilapp.LOGIN.LoginActivity;
import com.example.cucimobilapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Button button_logout;
    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().setStatusBarColor(Color.parseColor("#c7c7c7"));
        }

        button_logout = (Button) view.findViewById(R.id.button_Logout);

        button_logout.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_Logout:
                firebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                break;
        }
    }
}