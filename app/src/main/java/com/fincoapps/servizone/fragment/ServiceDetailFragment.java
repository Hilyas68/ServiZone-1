package com.fincoapps.servizone.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fincoapps.servizone.R;

import ivb.com.materialstepper.stepperFragment;

public class ServiceDetailFragment extends stepperFragment {

    public ServiceDetailFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.servicedetail_fragment, container, false);
    }

    @Override
    public boolean onNextButtonHandler() {
        return true;
    }
}
