package taxicity.com.taxicityapp.controller.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import taxicity.com.taxicityapp.R;

public class RegisterFragment extends Fragment {


    private googleMapFragment mapFragment;

    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        Button btn = v.findViewById(R.id.submit_btn_order);

        btn.setOnClickListener(onClickInitFragmentListenner());
        return v;
    }


    private View.OnClickListener onClickInitFragmentListenner() {

        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Singleton Register fragment
                if (mapFragment == null)
                    mapFragment = new googleMapFragment();

                //Get Fragment manager from the parent
                FragmentManager fm = getActivity().getSupportFragmentManager();

                //Remove and Replace with the new
                fm.beginTransaction().remove(fm.findFragmentById(R.id.main_frame_layout)).commit();
                fm.beginTransaction().replace(R.id.main_frame_layout, mapFragment).commit();


            }
        });

    }



}


