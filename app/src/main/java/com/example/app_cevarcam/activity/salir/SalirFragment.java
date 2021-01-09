package com.example.app_cevarcam.activity.salir;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.app_cevarcam.R;
import com.example.app_cevarcam.activity.LoginActivity;

public class SalirFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_salir, container, false);

        //ref: https://stackoverflow.com/questions/15478105/start-an-activity-from-a-fragment/15478302
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);

        return root;
    }
}