package taxicity.com.taxicityapp.controller.fragments;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ServerValue;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import taxicity.com.taxicityapp.R;
import taxicity.com.taxicityapp.model.backend.ActionCallBack;
import taxicity.com.taxicityapp.model.backend.BackEnd;
import taxicity.com.taxicityapp.model.backend.BackEndFactory;
import taxicity.com.taxicityapp.model.backend.NotifyDataChange;
import taxicity.com.taxicityapp.model.entities.Trip;
import taxicity.com.taxicityapp.model.helper.TripHelper;

/**
 * A simple {@link Fragment} subclass.
 */

public class googleMapFragment extends Fragment implements OnMapReadyCallback {


    private static String TAG = "errorMap";
    private FusedLocationProviderClient mFusedLocationClient; //Location client
    protected LatLng sourceLocation;
    protected String sourceAddress;
    protected LatLng destinationLocation;
    protected String destinationAddress;
    protected String tripKey;//Current Trip key

    //View fields
    protected Button tripInfoLabel;
    protected EditText sourceEditText; //Source Adresse EditText
    private SupportPlaceAutocompleteFragment autoCompleteDestinationField; //AutoComplete fragment
    private SupportMapFragment mapFragment; //Map fragment
    private GoogleMap mMap; //Google map


    protected final BackEnd database = BackEndFactory.getInstance();

    public googleMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        tripKey = getArguments().getString("key"); //Get the key from the previous fragment.
        return inflater.inflate(R.layout.fragment_google_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Bind view
        sourceEditText = view.findViewById(R.id.input_src_address);
        tripInfoLabel = view.findViewById(R.id.btn_info_trip);

    }

    /**
     * Initialisation of :
     * - The map fragment.
     * - The mFusedLocationClient.
     * - The Autocomplete fragment.
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            //Map init
            mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapGoogle);

            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            //Get current location init
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

            //AutoComplete init
            autoCompleteDestinationField = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.autocompleter_google_destination);

            if (autoCompleteDestinationField != null) {
                //Define the listener when the place is selected (
                autoCompleteDestinationField.setOnPlaceSelectedListener(onPLaceSelectedAction());
            }
        }

    }

    /**
     * AutoComplete Callback
     * 1) Get the location of the place selected.
     * 2) Update the Trip location to the database. (Async)
     * 3) Calculation the route and show it to the map. (Async)
     *
     * @return
     */
    private PlaceSelectionListener onPLaceSelectedAction() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destinationLocation = place.getLatLng();
                destinationAddress = place.getAddress().toString();

                new AsyncUpdateTrip().doInBackground(tripKey); //Update the Trip location to the database. (Async)

                new RouteAsyncTask().doInBackground(null); //Calculation the route and show it to the map. (Async)
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getContext(), getString(R.string.unable_to_find_location_error_msg), Toast.LENGTH_LONG).show();

            }
        };
    }

    /*
        1) MAP READY
        2) GET CURRENT LOCATION
           2') Define Source Adresse
        3) GET DESTINATION LOCATION
        5) UPDATE TRIP
        4) TRACE ROUTE
     */


    /**
     * CallBack when the fragment is loaded.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true); // Show the current location to the map.

        getCurrentLocation(); //Get the current location of the client.


    }


    /**
     * Get the current location of the client.
     * - Move the map to the current location.
     * - Convert current location to an address.
     */
    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        //Get Current Location
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    sourceLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    //Move the map to the current location
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 15));

                    //Convert current location to an address.
                    new FindAddress().doInBackground(sourceLocation);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), getString(R.string.unable_to_find_location_error_msg), Toast.LENGTH_LONG).show();
            }
        });

    }




    /*
        1) Get And Update Trip
        2) UpdateTrip
            FormatTrip
     */


    /**
     * AsyncTask class who is calculate the route between the source and the destination location, and trace it.
     */
    private class RouteAsyncTask extends AsyncTask {


        @SuppressLint("MissingPermission")
        @Override
        protected Object doInBackground(Object[] objects) {


            mMap.clear(); //Clear if we have other markers

            //Add marker of sourceLocation
            if (sourceLocation != null)
                mMap.addMarker(new MarkerOptions().position(sourceLocation).title(sourceAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

            //Add marker of destinationAddress
            if (destinationAddress != null)
                mMap.addMarker(new MarkerOptions().position(destinationLocation).title(destinationAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


            //Define list to get all latlng for the route
            List<LatLng> path = new ArrayList();


            //Execute Directions API request
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAxDBxmD_m57PKQZkz_dIAbobGrK9VkhPc")
                    .build();

            DirectionsApiRequest req = DirectionsApi.getDirections(context, sourceLocation.latitude + "," + sourceLocation.longitude, destinationLocation.latitude + "," + destinationLocation.longitude);

            //Decoding Direction information and trace the route in the map.
            try {
                DirectionsResult res = req.await();

                //Loop through legs and steps to get encoded polylines of each step
                if (res.routes != null && res.routes.length > 0) {
                    DirectionsRoute route = res.routes[0];

                    if (route.legs != null) {
                        for (int i = 0; i < route.legs.length; i++) {
                            DirectionsLeg leg = route.legs[i];
                            if (leg.steps != null) {
                                for (int j = 0; j < leg.steps.length; j++) {
                                    DirectionsStep step = leg.steps[j];
                                    if (step.steps != null && step.steps.length > 0) {
                                        for (int k = 0; k < step.steps.length; k++) {
                                            DirectionsStep step1 = step.steps[k];
                                            EncodedPolyline points1 = step1.polyline;
                                            if (points1 != null) {
                                                //Decode polyline and add points to list of route coordinates
                                                List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                for (com.google.maps.model.LatLng coord1 : coords1) {
                                                    path.add(new LatLng(coord1.lat, coord1.lng));
                                                }
                                            }
                                        }
                                    } else {
                                        EncodedPolyline points = step.polyline;
                                        if (points != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords = points.decodePath();
                                            for (com.google.maps.model.LatLng coord : coords) {
                                                path.add(new LatLng(coord.lat, coord.lng));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }

            //Draw the polyline
            if (path.size() > 0) {
                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(15);
                mMap.addPolyline(opts);
            }

            mMap.getUiSettings().setZoomControlsEnabled(true);

            //Move the map to the destination location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 12));

            return null;
        }

    }


    /**
     * AsyncTask who convert the source location  to location address.
     */
    private class FindAddress extends AsyncTask<LatLng, Integer, Void> {


        private String convertLatLongToAddress(LatLng latLng) {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(sourceLocation.latitude, sourceLocation.longitude, 1); // Here 1 represent max location result to returned.

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                sourceEditText.setText(address);
                return address;
            } catch (IOException e) {
                sourceEditText.setText(sourceLocation.toString()); //if error show the lat long
            }

            return null;
        }

        @Override
        protected Void doInBackground(LatLng... latLngs) {
            convertLatLongToAddress(latLngs[0]);
            return null;
        }
    }


    /**
     * AsyncTask : Update the trip to the database.
     */
    private class AsyncUpdateTrip extends AsyncTask<String, Long, Trip> {

        /**
         * Get the trip from the database with the key passed by the previous fragment.
         *
         * @param key The key of the trip.
         */
        private void getAndUpdateTrip(String key) {

            try {
                database.getTrip(key, new ActionCallBack<Trip>() {
                    @Override
                    public void onSuccess(Trip obj) {
                        obj.setKey(tripKey);
                        updateTrip(obj); // Update the trip with the new source and destination locations.

                    }

                    @Override
                    public void onFailure(Exception exception) {

                        Log.i(TAG, getString(R.string.failure_getting_trip_error_msg));


                    }

                    @Override
                    public void onProgress(String status, double percent) {

                    }
                });


            } catch (Exception e) {

            }

        }

        /**
         * Update the trip object with the new values.
         */
        private Trip formatTrip(Trip trip) {

            String sourceAddress = convertLatLongToAddresse(sourceLocation); //why we do this operation 2 time ??
            trip.setStartingHour(String.valueOf(System.currentTimeMillis())); //Date compatible with firebase
            trip.setStatusAsEnum(Trip.TripStatus.AVAILABLE);

            trip.setDestinationAddress(destinationAddress);
            trip.setDestinationLongitude(destinationLocation.longitude);
            trip.setDestinationLatitude(destinationLocation.latitude);
            trip.setSourceLongitude(sourceLocation.longitude);
            trip.setSourceLatitude(sourceLocation.latitude);
            trip.setSourceAddress(sourceAddress);

            return trip;

        }


        /**
         * Converting the source location  to location address.
         */
        private String convertLatLongToAddresse(LatLng latLng) {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getContext(), Locale.getDefault());
            String address = null;
            try {
                addresses = geocoder.getFromLocation(sourceLocation.latitude, sourceLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0);
                sourceEditText.setText(address);
                return address;
            } catch (IOException e) {
                sourceEditText.setText(sourceLocation.toString()); // Show the lat ling
            }

            return address;
        }

        /**
         * 1) Update the trip to database.
         * 2) Notify user when status changed.
         *
         * @param tr The trip to update
         */
        private void updateTrip(Trip tr) {
            final Trip trip = formatTrip(tr); //Format the trip
            tr.setKey(tripKey);

            try {
                database.updateTrip(trip, new ActionCallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        showInfoTrip(getString(R.string.waiting_for_driver_msg), 3000, 0); //Show UI message

                        //Notify the user when the trip values changed.
                        database.notifyTripChanged(trip.getKey(), new NotifyDataChange<Trip>() {
                            @Override
                            public void OnDataChanged(Trip obj) {

                                //Notify user when the trip was taken by driver (trip in progress).
                                if (obj.getDriverEmail() != null && obj.getStatusAsEnum() == Trip.TripStatus.IN_PROGRESS) {

                                    showInfoTrip(getString(R.string.trip_in_progress_msg), 2500, 2);
                                }

                                //Notify user when the trip is ended and show it the cost of the trip.
                                if (obj.getStatusAsEnum() == Trip.TripStatus.FINISHED) {

                                    int distance = (int) TripHelper.calculTripDistance(obj) / 1000;

                                    showInfoTrip("Trip finished - Total cost : " + TripHelper.calculatePrice(distance) + " $", 5000, 1);


                                    //After 20sec Reset the activity (Come back to the Beginning)
                                    final Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (getActivity() != null) {
                                                getActivity().recreate(); //Bug with emulator
                                            }

                                        }
                                    }, 20000);


                                }

                            }

                            @Override
                            public void onFailure(Exception exception) {

                            }
                        });
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        //Error

                    }

                    @Override
                    public void onProgress(String status, double percent) {

                    }
                });
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                for (StackTraceElement elemt : e.getStackTrace()) {
                    Log.e(TAG, elemt.toString());
                }
            }
        }


        /**
         * Order of Functions :
         *  1) getAndUpdateTrip
         *  2) updateTrip
         *      2.1) formatTrip
         *      2.2) notifyTripChanged
         *
         */
        @Override
        protected Trip doInBackground(String... strings) {
            getAndUpdateTrip(strings[0]);
            return null;

        }


    }

    @Override
    public void onStop() {
        database.stopNotifyTripChanged(); //Unregister the listener.
        super.onStop();
    }

    /**
     * Showing message to the user with UI design. (like toast).
     * @param msg The message to show.
     * @param delay The delay of visibility in seconds.
     * @param levelMsg The Level of message (Change the background tint)
     */
    private void showInfoTrip(String msg, int delay, int levelMsg) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //Better with enum ?
            switch (levelMsg) {
                case 0:
                    tripInfoLabel.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorLightenGrey)));
                    break;
                case 1:
                    tripInfoLabel.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorTaxiYellow)));
                    break;
                case 2:
                    tripInfoLabel.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorSuccess)));
                    break;
                case 3:
                    tripInfoLabel.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorDanger)));
                    break;
                default:
                    break;

            }

        }

        tripInfoLabel.setText(msg);
        tripInfoLabel.setVisibility(View.VISIBLE);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tripInfoLabel.setVisibility(View.GONE);
            }
        }, delay);


    }
}

