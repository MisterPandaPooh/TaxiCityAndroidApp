package taxicity.com.taxicityapp.controller.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import taxicity.com.taxicityapp.R;
import taxicity.com.taxicityapp.controller.fragments.WelcomeFragment;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_ASK_PERMISSIONS = 522;
    private FragmentManager fm = getSupportFragmentManager();
    private WelcomeFragment welcomeFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide(); // Hiding Action Bar

        setContentView(R.layout.activity_main);

        requestAllPermissions(); //Request all permissions needed for the app.

        configureWelcomeFragment(); //Configure first fragment of the App.


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


    /**
     * Request all permissions needed for the app.
     * - ACCESS_FINE_LOCATION
     * - SEND_SMS
     */
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
        requestAllPermissions(); //Check Again the permissions after the results (Loop).
    }

}
