package taxicity.com.taxicityapp.model.backend;


import taxicity.com.taxicityapp.model.entities.Trip;

public interface BackEnd<T> {
    void addTrip(final Trip trip, final ActionCallBack<T> action);

    void removeTrip(final String key, final ActionCallBack<T> action);

    void getTrip(final String key, final ActionCallBack<Trip> action);

    void updateTrip(final Trip toUpdate, final ActionCallBack<T> action);


}
