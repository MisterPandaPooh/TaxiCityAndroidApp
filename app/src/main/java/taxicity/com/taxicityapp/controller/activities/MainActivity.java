package taxicity.com.taxicityapp.controller.activities;

import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import taxicity.com.taxicityapp.R;
import taxicity.com.taxicityapp.controller.fragments.RegisterFragment;

public class MainActivity extends AppCompatActivity implements RegisterFragment.OnFragmentInteractionListener {

    private RegisterFragment fragment = null;
    private Button btnOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOrder = findViewById(R.id.btn_get_taxi);

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureRegisterFragement();
            }
        });

    }

    //TODO se debrouiller pour que le bouton ne se remet pas

    private void configureRegisterFragement() {

        if (fragment == null) { //Singleton of the fragement
            fragment = new RegisterFragment();
        }
        btnOrder.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_layout, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
