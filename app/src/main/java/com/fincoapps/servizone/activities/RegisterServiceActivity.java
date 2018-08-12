package com.fincoapps.servizone.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;

import com.fincoapps.servizone.fragment.ServiceAddressFragment;
import com.fincoapps.servizone.fragment.ServiceDetailFragment;

import java.util.ArrayList;
import java.util.List;

import ivb.com.materialstepper.progressMobileStepper;

public class RegisterServiceActivity extends progressMobileStepper {

    List<Class> stepperFragemtList = new ArrayList<>();

    @Override
    public void onStepperCompleted() {
        showCompletedDialog();
    }

    @Override
    public List<Class> init() {
        stepperFragemtList.add(ServiceDetailFragment.class);
        stepperFragemtList.add(ServiceAddressFragment.class);

        return stepperFragemtList;
    }


    protected void showCompletedDialog(){
        android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(
                RegisterServiceActivity.this);

        // set title
        alertDialogBuilder.setTitle("Hooray");
        alertDialogBuilder
                .setMessage("We've completed the stepper")
                .setCancelable(true)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {

                    }
                });

        // create alert dialog
        android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
}
