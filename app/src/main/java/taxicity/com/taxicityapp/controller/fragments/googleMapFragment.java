package taxicity.com.taxicityapp.controller.fragments;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import taxicity.com.taxicityapp.model.entities.Trip;

/**
 * A simple {@link Fragment} subclass.
 */


//Source Adresse in Firebase
public class googleMapFragment extends Fragment implements OnMapReadyCallback {


    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private String TAG = "errorMap";
    private SupportPlaceAutocompleteFragment autoCompleteDestinationField;
    protected LatLng sourceLocation;
    protected LatLng destinationLocation;
    protected String destinationAddress;
    protected String sourceAddress;
    protected EditText sourceEditText;
    protected String tripKey;
    private FusedLocationProviderClient mFusedLocationClient;

    protected final BackEnd database = BackEndFactory.getInstance();

    public googleMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        tripKey = getArguments().getString("key");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sourceEditText = view.findViewById(R.id.input_src_address);
    }

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
                autoCompleteDestinationField.setOnPlaceSelectedListener(onPLaceSelectedAction());
            }
        }
    }

    //Autocomplete callback
    private PlaceSelectionListener onPLaceSelectedAction() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destinationLocation = place.getLatLng();
                destinationAddress = place.getAddress().toString();
                new AsyncUpdateTrip().doInBackground(tripKey);
                // new AsyncUpdateTrip().doInBackground(tripKey);
                new RouteAsyncTask().doInBackground(null);
            }

            @Override
            public void onError(Status status) {
                Toast.makeText(getContext(), "Unable to find the destination address.", Toast.LENGTH_LONG);

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

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        getCurrentLocation();


    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        //Get Current Location
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    sourceLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sourceLocation, 15));
                    new FindAddress().doInBackground(sourceLocation);
                }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Can't find your current location", Toast.LENGTH_LONG).show();
            }
        });

    }

    /*
        1) Get And Update Trip
        2) UpdateTrip
            FormatTrip
     */




    private class RouteAsyncTask extends AsyncTask {


        @SuppressLint("MissingPermission")
        @Override
        protected Object doInBackground(Object[] objects) {


            mMap.clear(); //Clear if we have other markers

            if (sourceLocation != null)
                mMap.addMarker(new MarkerOptions().position(sourceLocation).title(sourceAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));

            if (destinationAddress != null)
                mMap.addMarker(new MarkerOptions().position(destinationLocation).title(destinationAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            LatLng zaragoza = new LatLng(41.648823, -0.889085);

            //Define list to get all latlng for the route
            List<LatLng> path = new ArrayList();


            //Execute Directions API request
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAxDBxmD_m57PKQZkz_dIAbobGrK9VkhPc")
                    .build();

            DirectionsApiRequest req = DirectionsApi.getDirections(context, sourceLocation.latitude + "," + sourceLocation.longitude, destinationLocation.latitude + "," + destinationLocation.longitude);
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

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLocation, 12));

            return null;
        }

    }


    private class FindAddress extends AsyncTask<LatLng, Integer, Void> {


        private String convertLatLongToAddress(LatLng latLng) {
            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(getContext(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(sourceLocation.latitude, sourceLocation.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                sourceEditText.setText(address);
                return address;
            } catch (IOException e) {
                sourceEditText.setText(sourceLocation.toString()); // Show the lat ling
            }

            return null;
        }

        @Override
        protected Void doInBackground(LatLng... latLngs) {
            convertLatLongToAddress(latLngs[0]);
            return null;
        }
    }


    private class AsyncUpdateTrip extends AsyncTask<String, Long, Trip> {

        private void getAndUpdateTrip(String key) {

            try {
                database.getTrip(key, new ActionCallBack<Trip>() {
                    @Override
                    public void onSuccess(Trip obj) {
                        obj.setKey(tripKey);
                        updateTrip(obj);

                    }

                    @Override
                    public void onFailure(Exception exception) {

                        Log.i(TAG, "FAILURE TRIP GET");


                    }

                    @Override
                    public void onProgress(String status, double percent) {

                    }
                });


            } catch (Exception e) {

            }

        }

        private Trip formatTrip(Trip trip) {

            trip.setStartingHourAsDate(new Date());
            trip.setStatusAsEnum(Trip.TripStatus.AVAILABLE);

            trip.setDestinationAddress(destinationAddress);
            trip.setDestinationLongitude(destinationLocation.longitude);
            trip.setDestinationLatitude(destinationLocation.latitude);
            trip.setSourceLongitude(sourceLocation.longitude);
            trip.setSourceLatitude(sourceLocation.latitude);
            trip.setSourceAddress(sourceAddress);

            return trip;

        }

        private void updateTrip(Trip tr) {
            Trip trip = formatTrip(tr);
            Log.i(TAG, trip.getDestinationAddress());
            try {
                database.updateTrip(trip, new ActionCallBack() {
                    @Override
                    public void onSuccess(Object obj) {
                        Toast.makeText(getContext(), "Trip launched !", Toast.LENGTH_LONG).show();
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
                //Error
            }
        }

        @Override
        protected Trip doInBackground(String... strings) {
            getAndUpdateTrip(strings[0]);
            return null;

        }
    }


}

