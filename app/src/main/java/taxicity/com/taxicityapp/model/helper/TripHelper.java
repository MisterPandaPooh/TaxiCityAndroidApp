package taxicity.com.taxicityapp.model.helper;

import android.location.Location;

import taxicity.com.taxicityapp.model.entities.Trip;

/**
 * Class Trip Helper
 * <p>
 * Contain some method to improve Trip use.
 */
public class TripHelper {

    private final static double PRICE_BY_KM = 2.0;
    private final static double MIN_PRICE_TRIP = 11.90;

    /**
     * Calculate the trip distance between Source and Destination Address.
     *
     * @param trip The Trip.
     * @return The trip distance between Source and Destination Address
     */
    public static float calculTripDistance(Trip trip) {
        float[] result = new float[1];
        Location.distanceBetween(trip.getSourceLatitude(), trip.getSourceLongitude(), trip.getDestinationLatitude(), trip.getDestinationLongitude(), result);
        return result[0];
    }



    /**
     * Calculate total price of the Trip
     * Can return the minimum price of the trip if is a low price trip.
     *
     * @param tripDistanceInKm Distance of the Trip in mk.
     * @return Price of the trip.
     */
    public static double calculatePrice(float tripDistanceInKm) {
        double price = PRICE_BY_KM * tripDistanceInKm;
        return (price < MIN_PRICE_TRIP) ? MIN_PRICE_TRIP : price;
    }
}
