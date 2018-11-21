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

    /**
     * The Fragment who is displaying the request of trip form.
     */
    private RegisterFragment fragment = null;

    /**
     * The button who is starting the App
     */
    private Button btnOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOrder = findViewById(R.id.btn_get_taxi);

        //Init Event Listener
        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                configureRegisterFragement();
            }
        });

    }

    //TODO se debrouiller pour que le bouton ne se remet pas (Peut etre le reset de l'activity).

    /**
     * Configure and Init the "Trip Request" Fragment
     */
    private void configureRegisterFragement() {

        //Singleton pattern of the fragment.
        if (fragment == null)
            fragment = new RegisterFragment();

        //Hide the Button when displaying the fragment.
        btnOrder.setVisibility(View.GONE);

        //Adding the Register fragment to the view.
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame_layout, fragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
