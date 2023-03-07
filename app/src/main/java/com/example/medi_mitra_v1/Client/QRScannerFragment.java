package com.example.medi_mitra_v1.Client;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.medi_mitra_v1.R;

public class QRScannerFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CAMERA = 0;

    private Button qrCodeFoundButton;
    private String qrCode;

    public QRScannerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_q_r_scanner, container, false);
    }
}