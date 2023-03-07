package com.example.medi_mitra_v1.Client;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.medi_mitra_v1.R;

public class ProfileOthers extends Fragment {


    public ProfileOthers() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_profile_others, container, false);
        return v;
    }
}