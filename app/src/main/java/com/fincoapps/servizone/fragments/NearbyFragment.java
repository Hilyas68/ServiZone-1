package com.fincoapps.servizone.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fincoapps.servizone.R;
import com.fincoapps.servizone.utils.AppSettings;

import org.androidannotations.annotations.App;

/**
 * A simple {@link Fragment} subclass.
 */
public class NearbyFragment extends Fragment {

    String TAG = "NearbyFragment";
    private AppSettings app;
    public NearbyFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public NearbyFragment(AppSettings app){
        this.app = app;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_nearby, container, false);
    }

}
