package taxicity.com.taxicityapp.model.backend;


import taxicity.com.taxicityapp.model.entities.Trip;

public interface BackEnd {
     void addTrip(Trip trip);
     void RemoveTrip(Trip trip);
     void updateTrip(Trip trip);

}
