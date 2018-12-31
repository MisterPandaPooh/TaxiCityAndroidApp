package taxicity.com.taxicityapp.controller.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import taxicity.com.taxicityapp.R;
import taxicity.com.taxicityapp.model.backend.ActionCallBack;
import taxicity.com.taxicityapp.model.backend.BackEnd;
import taxicity.com.taxicityapp.model.backend.BackEndFactory;
import taxicity.com.taxicityapp.model.entities.Trip;
import taxicity.com.taxicityapp.model.helper.Helpers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormFragment extends Fragment {


    private final static String TAG = "register_form";
    private googleMapFragment mapFragment;
    private EditText nameEditText;
    private EditText mailEditText;
    private EditText phoneEditText;
    private TextView helperTextView;
    private Button btnSubmit;


    public FormFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_form, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnSubmit = view.findViewById(R.id.submit_btn_form);
        nameEditText = view.findViewById(R.id.name_input_form);
        mailEditText = view.findViewById(R.id.email_input_form);
        phoneEditText = view.findViewById(R.id.phone_input_form);
        helperTextView = view.findViewById(R.id.helper_form);

        btnSubmit.setOnClickListener(onClickInitFragmentListenner());
    }


    private boolean isValid() {


        if (phoneEditText == null || nameEditText == null || mailEditText == null || helperTextView == null)
            return false;

        if (TextUtils.isEmpty(nameEditText.getText()) ||
                TextUtils.isEmpty(mailEditText.getText()) ||
                TextUtils.isEmpty(phoneEditText.getText())) {
            helperTextView.setVisibility(View.VISIBLE);
            helperTextView.setText("Please fill in all the required fields.");
            return false;

        }

        if (!Helpers.isValidEmail(mailEditText.getText().toString())) {
            helperTextView.setVisibility(View.VISIBLE);
            helperTextView.setText("Please enter a valid email address.");
            return false;
        }


        return true;

    }


    //Ajout de la fonction SMS


    private View.OnClickListener onClickInitFragmentListenner() {


        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (!isValid())
                    return;
*/
                addTrip();


            }
        });


    }

    private void initMapFragment(String...args) {

        //Singleton Register fragment
        if (mapFragment == null)
            mapFragment = new googleMapFragment();



        //Get Fragment manager from the parent
        FragmentManager fm = getActivity().getSupportFragmentManager();

        if(fm == null || args[0]==null)
            return;

        //Pass the key of the trip
        Bundle bdn = new Bundle();
        bdn.putString("key",args[0]);
        mapFragment.setArguments(bdn);

        //Remove and Replace with the new
        fm.beginTransaction().remove(fm.findFragmentById(R.id.main_frame_layout)).commit();
        fm.beginTransaction().replace(R.id.main_frame_layout, mapFragment).commit();

    }


    private Trip formatTrip() {
        Trip tr = new Trip();
        tr.setCustomerName(nameEditText.getText().toString());
        tr.setCustomerPhone(phoneEditText.getText().toString());
        tr.setCustomerEmail(mailEditText.getText().toString());
        return tr;
    }

    private void addTrip() {
        try {

            final Trip trip = formatTrip();
            btnSubmit.setEnabled(false);

            BackEnd dbInstance = BackEndFactory.getInstance();

            dbInstance.addTrip(trip, new ActionCallBack() {
                @Override
                public void onSuccess(Object obj) {
                    trip.setKey((String) obj);
                    initMapFragment(trip.getKey());

                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG);
                    btnSubmit.setEnabled(true);

                }

                @Override
                public void onProgress(String status, double percent) {
                    Toast.makeText(getContext(), "In progress ...", Toast.LENGTH_LONG).show();

                }
            });
        } catch (Exception e) {
            Throwable e1 = e.getCause();
            Toast.makeText(getContext(), "Error !", Toast.LENGTH_LONG).show();
            btnSubmit.setEnabled(true);
        }
    }


}
