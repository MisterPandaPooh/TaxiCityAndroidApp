package taxicity.com.taxicityapp.controller.fragments;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;

import java.util.ArrayList;
import java.util.List;

import taxicity.com.taxicityapp.R;

/**
 * A simple {@link Fragment} subclass.
 */

//TODO FAIRE CURRENT LOCATION
//TODO COVERTER CURRENT LOCATION TO ADRESS GEOCODER AND PROJET
public class googleMapFragment extends Fragment implements OnMapReadyCallback {


    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private String TAG = "errorMap";
    private SupportPlaceAutocompleteFragment autoCompleteDestinationField;

    public googleMapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_google_map, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getActivity()!=null) {
            mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapGoogle);

            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }

            autoCompleteDestinationField = (SupportPlaceAutocompleteFragment) getChildFragmentManager().findFragmentById(R.id.autocompleter_google_destination);

            if (autoCompleteDestinationField != null) {
                autoCompleteDestinationField.setOnPlaceSelectedListener(onPLaceSelectedAction());
            }
        }
    }

    private PlaceSelectionListener onPLaceSelectedAction() {
        return new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng v = place.getLatLng();
                CharSequence cr = place.getAddress();
            }

            @Override
            public void onError(Status status) {

            }
        };
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        new RouteAsyncTask().doInBackground(null);


    }

    private class RouteAsyncTask extends AsyncTask{


        @Override
        protected Object doInBackground(Object[] objects) {
            LatLng barcelona = new LatLng(41.385064,2.173403);
            mMap.addMarker(new MarkerOptions().position(barcelona).title("Marker in Barcelona"));

            LatLng madrid = new LatLng(40.416775,-3.70379);
            mMap.addMarker(new MarkerOptions().position(madrid).title("Marker in Madrid"));

            LatLng zaragoza = new LatLng(41.648823,-0.889085);

            //Define list to get all latlng for the route
            List<LatLng> path = new ArrayList();


            //Execute Directions API request
            GeoApiContext context = new GeoApiContext.Builder()
                    .apiKey("AIzaSyAxDBxmD_m57PKQZkz_dIAbobGrK9VkhPc")
                    .build();
            DirectionsApiRequest req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379");
            try {
                DirectionsResult res = req.await();

                //Loop through legs and steps to get encoded polylines of each step
                if (res.routes != null && res.routes.length > 0) {
                    DirectionsRoute route = res.routes[0];

                    if (route.legs !=null) {
                        for(int i=0; i<route.legs.length; i++) {
                            DirectionsLeg leg = route.legs[i];
                            if (leg.steps != null) {
                                for (int j=0; j<leg.steps.length;j++){
                                    DirectionsStep step = leg.steps[j];
                                    if (step.steps != null && step.steps.length >0) {
                                        for (int k=0; k<step.steps.length;k++){
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
            } catch(Exception ex) {
                Log.e(TAG, ex.getLocalizedMessage());
            }

            //Draw the polyline
            if (path.size() > 0) {
                PolylineOptions opts = new PolylineOptions().addAll(path).color(Color.BLUE).width(5);
                mMap.addPolyline(opts);
            }

            mMap.getUiSettings().setZoomControlsEnabled(true);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(zaragoza, 6));

            return null;
        }
    }

}
