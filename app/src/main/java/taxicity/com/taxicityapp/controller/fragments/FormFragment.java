package taxicity.com.taxicityapp.controller.fragments;


import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import taxicity.com.taxicityapp.R;
import taxicity.com.taxicityapp.model.helper.Validate;

/**
 * A simple {@link Fragment} subclass.
 */
public class FormFragment extends Fragment {


    private final static String TAG = "jeanyves";
    private googleMapFragment mapFragment;
    private EditText nameEditText;
    private EditText mailEditText;
    private EditText phoneEditText;
    private TextView helperTextView;


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
        Button btn = view.findViewById(R.id.submit_btn_form);
        nameEditText = view.findViewById(R.id.name_input_form);
        mailEditText = view.findViewById(R.id.email_input_form);
        phoneEditText = view.findViewById(R.id.phone_input_form);
        helperTextView = view.findViewById(R.id.helper_form);

        btn.setOnClickListener(onClickInitFragmentListenner());
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

        if(!Validate.isValidEmail(mailEditText.getText().toString())){
            helperTextView.setVisibility(View.VISIBLE);
            helperTextView.setText("Please enter a valid email address.");
            return false;
        }



        return true;

    }

    private View.OnClickListener onClickInitFragmentListenner() {


        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isValid())
                    return;

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
