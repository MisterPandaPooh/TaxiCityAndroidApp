package taxicity.com.taxicityapp.model.datasources;

import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.ExecutionException;

import taxicity.com.taxicityapp.model.backend.ActionCallBack;
import taxicity.com.taxicityapp.model.backend.BackEnd;
import taxicity.com.taxicityapp.model.backend.NotifyDataChange;
import taxicity.com.taxicityapp.model.entities.Trip;


/**
 * Implementation of the BackEnd with FireBase Datasource
 */
public class FireBase_Manager implements BackEnd<String> {
    //Database Reference
    private static FirebaseDatabase db = FirebaseDatabase.getInstance();
    private static DatabaseReference refTrips = db.getReference("Trips");
    private static ChildEventListener tripRefChildEventListener;
    private static final String TAG = "firebase_manager";


    public void addTrip(final Trip trip, final ActionCallBack<String> action) {
        String idTrip = trip.getKey();

        //Keep the same key when updating
        if (idTrip == null)
            idTrip = refTrips.push().getKey();
        else
            trip.setKey(null);


        final String finalIdTrip = idTrip;
        refTrips.child(idTrip).setValue(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(finalIdTrip);
                action.onProgress("Adding trip in progress...", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("Failed to add the trip...", 100);
            }
        });
    }


    public void removeTrip(final String key, final ActionCallBack<String> action) {

        refTrips.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Trip value = dataSnapshot.getValue(Trip.class);

                if (value == null) {
                    action.onFailure(new Exception("We can't find the request trip"));
                } else {
                    refTrips.child(key).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            action.onSuccess(key);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            action.onFailure(e);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                action.onFailure(databaseError.toException());
            }
        });

    }


    public void updateTrip(final Trip toUpdate, final ActionCallBack<String> action) {
        Log.e(TAG, "Update Started");
        Log.i(TAG, "updateTrip - key :" + toUpdate.getKey());
        Log.i(TAG, "updateTrip: - Destination :" + toUpdate.getDestinationAddress());

        final String finalIdTrip = toUpdate.getKey();
        refTrips.child(finalIdTrip).setValue(toUpdate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(finalIdTrip);
                action.onProgress("Adding trip in progress...", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("Adding trip in progress...", 100);
            }
        });


    }

    @Override
    public void getTrip(final String key, final ActionCallBack<Trip> action) {

        final Trip[] trip = {null};
        refTrips.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trip[0] = dataSnapshot.getValue(Trip.class);

                if (trip[0] == null) {

                    action.onFailure(new Exception("We can't find the Trip"));
                } else {
                    action.onSuccess(trip[0]);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
                action.onFailure(databaseError.toException());
            }
        });

    }

    public void notifyTripChanged(final String aKey, final NotifyDataChange<Trip> notifyDataChange) {

        Log.i(TAG, "notifyTripChanged: ");
        if (notifyDataChange != null) {

            if (tripRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("First unNotify  list"));
                return;
            }

            tripRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String key = dataSnapshot.getKey();
                    trip.setKey(key);

                    //Check if is the current key.
                    if (aKey.equals(key))
                        notifyDataChange.OnDataChanged(trip);

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String key = dataSnapshot.getKey();
                    trip.setKey(key);


                    //Check if is the current key.
                    if (aKey.equals(key))
                        notifyDataChange.OnDataChanged(trip);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };

            refTrips.addChildEventListener(tripRefChildEventListener);
        }

    }

    public void stopNotifyTripChanged() {
        if (tripRefChildEventListener != null) {
            refTrips.removeEventListener(tripRefChildEventListener);
            tripRefChildEventListener = null;
        }
    }


}
