package taxicity.com.taxicityapp.controller.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import taxicity.com.taxicityapp.R;

/**
 * WelcomeFragment is the first fragment of the app.
 */
public class WelcomeFragment extends Fragment {

    private Button btnOrder; //Button "Get Started !"
    private FormFragment formFragment = null;


    public WelcomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_welcome, container, false);

        btnOrder = v.findViewById(R.id.btn_get_taxi);

        btnOrder.setOnClickListener(onClickInitFragmentListener());

        return v;


    }


    /**
     * Initialisation of the second fragment (the form fragment).
     *
     * @return
     */
    private View.OnClickListener onClickInitFragmentListener() {

        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Singleton Register fragment
                if (formFragment == null)
                    formFragment = new FormFragment();

                //Get Fragment manager from the parent
                FragmentManager fm = getActivity().getSupportFragmentManager();

                //Remove and Replace with the new
                fm.beginTransaction().remove(fm.findFragmentById(R.id.main_frame_layout)).commit();
                fm.beginTransaction().replace(R.id.main_frame_layout, formFragment).commit();


            }
        });

    }
}
