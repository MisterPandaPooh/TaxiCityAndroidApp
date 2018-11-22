package taxicity.com.taxicityapp.controller.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import taxicity.com.taxicityapp.R;
import taxicity.com.taxicityapp.controller.fragments.RegisterFragment;
import taxicity.com.taxicityapp.controller.fragments.TaxiMapFragment;
import taxicity.com.taxicityapp.controller.fragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity implements WelcomeFragment.OnFragmentInteractionListener , RegisterFragment.OnFragmentInteractionListener, TaxiMapFragment.OnFragmentInteractionListener {

    /**
     * The Fragment who is displaying the request of trip form.
     */

    private FragmentManager fm = getSupportFragmentManager();

    private RegisterFragment fragment = null;

    private WelcomeFragment welcomeFragment = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); // Hiding Action Bar

        setContentView(R.layout.activity_main);

        configureWelcomeFragment();


       /* Trip tr = new Trip();
        tr.setCustomerName("Netanel");
        tr.setCustomerEmail("netanelcohensolal@me.com");
        tr.setCustomerPhone("0587250797");
        tr.setDestinationAddress("Strasbourg");
        tr.setStatus(Trip.TripStatus.IN_PROGRESS);
        FirebaseDatabase  db =  FirebaseDatabase.getInstance();
        DatabaseReference ref  = db.getReference("Trips");
        final String id = ref.push().getKey();

        ref.child(id).setValue(tr);

        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 Trip tr1 = dataSnapshot.getValue(Trip.class);
                Log.d("bb", tr1.getCustomerEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

*/

    }

    //TODO se debrouiller pour que le bouton ne se remet pas (Peut etre le reset de l'activity).

    /**
     * Configure and Init the "Trip Request" Fragment
     */
    private void configureWelcomeFragment() {

        //Singleton pattern of the fragment.
        if (welcomeFragment == null)
            welcomeFragment = new WelcomeFragment();

        //Adding Welcome Fragment
        fm.beginTransaction().add(R.id.main_frame_layout, welcomeFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
