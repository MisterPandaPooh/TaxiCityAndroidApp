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
 * FormFragment is the fragment with a form to request a new trip.
 */
public class FormFragment extends Fragment {


    //View Fields
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
        return inflater.inflate(R.layout.fragment_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Bind the views
        btnSubmit = view.findViewById(R.id.submit_btn_form);
        nameEditText = view.findViewById(R.id.name_input_form);
        mailEditText = view.findViewById(R.id.email_input_form);
        phoneEditText = view.findViewById(R.id.phone_input_form);
        helperTextView = view.findViewById(R.id.helper_form);

        //Button Click listener who init the google map fragment.
        btnSubmit.setOnClickListener(onClickInitFragmentListenner());
    }


    /**
     * Check if the all filed in the form are valid.
     *
     * @return true if valid else false;
     */
    private boolean isValid() {


        if (phoneEditText == null || nameEditText == null || mailEditText == null || helperTextView == null)
            return false;

        //Check Empty values
        if (TextUtils.isEmpty(nameEditText.getText()) ||
                TextUtils.isEmpty(mailEditText.getText()) ||
                TextUtils.isEmpty(phoneEditText.getText())) {
            helperTextView.setVisibility(View.VISIBLE);
            helperTextView.setText(getString(R.string.all_required_fields_error_msg));
            return false;

        }

        //Check if is a valid email
        if (!Helpers.isValidEmail(mailEditText.getText().toString())) {
            helperTextView.setVisibility(View.VISIBLE);
            helperTextView.setText(getString(R.string.valid_email_error_msg));
            return false;
        }

        //Check if the phone number is minimum 10 chars.
        if (phoneEditText.getText().toString().length() < 10) {
            helperTextView.setVisibility(View.VISIBLE);
            helperTextView.setText(getString(R.string.valid_phone_number_error_message));
            return false;
        }


        return true;

    }


    /**
     * Initialisation of googleMap Fragment
     * 1) Check if the form is valid
     * 2) Add the trip to the database
     */
    private View.OnClickListener onClickInitFragmentListenner() {


        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isValid()) //If not valid do nothing.
                    return;

                addTrip();//Add trip to database.


            }
        });


    }

    /**
     * Initialisation of the Google Map Fragment ith the fragment manager.
     *
     * @param args arguments
     */
    private void initMapFragment(String... args) {

        //Singleton Register fragment
        if (mapFragment == null)
            mapFragment = new googleMapFragment();


        //Get Fragment manager from the parent
        FragmentManager fm = getActivity().getSupportFragmentManager();

        if (fm == null || args[0] == null)
            return;

        //Pass the key of the trip
        Bundle bdn = new Bundle();
        bdn.putString("key", args[0]);
        mapFragment.setArguments(bdn);

        //Remove and Replace with the new
        fm.beginTransaction().remove(fm.findFragmentById(R.id.main_frame_layout)).commit();
        fm.beginTransaction().replace(R.id.main_frame_layout, mapFragment).commit();

    }


    /**
     * Create a Trip object from the UI fileds
     *
     * @return trip object.
     */
    private Trip formatTrip() {
        Trip tr = new Trip();
        tr.setCustomerName(nameEditText.getText().toString());
        tr.setCustomerPhone(phoneEditText.getText().toString());
        tr.setCustomerEmail(mailEditText.getText().toString());
        return tr;
    }

    private void addTrip() {
        try {

            final Trip trip = formatTrip(); //Get Trip Object from fields
            btnSubmit.setEnabled(false);//Disable the submit btn.

            BackEnd dbInstance = BackEndFactory.getInstance();

            //Add Trip to the database
            dbInstance.addTrip(trip, new ActionCallBack() {
                @Override
                public void onSuccess(Object obj) {
                    trip.setKey((String) obj);
                    initMapFragment(trip.getKey()); //If the trip is added ! Show next fragment.

                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    btnSubmit.setEnabled(true);

                }

                @Override
                public void onProgress(String status, double percent) {
                    Toast.makeText(getContext(), getString(R.string.in_progress_toast_msg), Toast.LENGTH_LONG).show();

                }
            });
        } catch (Exception e) {
            Throwable e1 = e.getCause();
            Toast.makeText(getContext(), e1.getMessage(), Toast.LENGTH_LONG).show();
            btnSubmit.setEnabled(true);
        }
    }


}
