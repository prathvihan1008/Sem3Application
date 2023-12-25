package com.example.sem3application.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.sem3application.R;

public class JogFragment extends Fragment {
    private static final String TAG = "JogFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_jog, container, false);

        // Add log statements to check if the fragment is loaded
        Log.d(TAG, "onCreateView Jog fragment is loaded is loaded");

        return view;
    }
}
