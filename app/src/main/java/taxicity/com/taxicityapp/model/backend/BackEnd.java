package taxicity.com.taxicityapp.model.backend;


import taxicity.com.taxicityapp.model.entities.Trip;

public interface BackEnd<T> {
    /**
     * Add trip to the Database
     *
     * @param trip   the trip to Add.
     * @param action the Action CallBack
     */
    void addTrip(final Trip trip, final ActionCallBack<T> action);

    /**
     * Remove Trip to the dateBase.
     *
     * @param key    key of the trip to remove
     * @param action the Action CallBack
     */
    void removeTrip(final String key, final ActionCallBack<T> action);


    /**
     * Get Trip from DataBase
     *
     * @param key    The key of the trip to get
     * @param action The Action CallBack
     */
    void getTrip(final String key, final ActionCallBack<Trip> action);

    /**
     * Update trip to the Database
     *
     * @param toUpdate the trip to update.
     * @param action   the Action CallBack
     */
    void updateTrip(final Trip toUpdate, final ActionCallBack<T> action);

    /**
     * Notify when the value of the trip changed.
     * @param key The key of the trip to listen.
     * @param notifyDataChange The callBack.
     */
    void notifyTripChanged(final String key, final NotifyDataChange<Trip> notifyDataChange);

    /**
     * Unregister the listener of the trip.
     */
    void stopNotifyTripChanged();

}
