package com.fincoapps.servizone.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fincoapps.servizone.R;
import com.google.android.gms.maps.SupportMapFragment;

import ivb.com.materialstepper.stepperFragment;

public class FnalFragment extends stepperFragment {

    @Override
    public boolean onNextButtonHandler() {
        return false;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.final_fragment, container, false);

        return view;
    }

}
