package taxicity.com.taxicityapp.controller.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import taxicity.com.taxicityapp.R;
import taxicity.com.taxicityapp.controller.fragments.FormFragment;
import taxicity.com.taxicityapp.controller.fragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ASK_PERMISSIONS = 522;
    private FragmentManager fm = getSupportFragmentManager();
    private FormFragment fragment = null;
    private WelcomeFragment welcomeFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); // Hiding Action Bar

        setContentView(R.layout.activity_main);
        requestAllPermissions();

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
        });*/



    }


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


    private void requestAllPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.SEND_SMS
                    }, REQUEST_CODE_ASK_PERMISSIONS);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        requestAllPermissions();
    }

}
