package taxicity.com.taxicityapp.model.entities;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Trip {


    public enum TripStatus {AVAILABLE, IN_PROGRESS, FINISHED}

    @Exclude
    private String key;

    private SimpleDateFormat startingHour;

    private SimpleDateFormat endingHour;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private TripStatus status;

    private double sourceLongitude;

    private double sourceLatitude;

    private double destinationLongitude;

    private double destinationLatitude;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public double getSourceLongitude() {
        return sourceLongitude;
    }

    public void setSourceLongitude(double sourceLongitude) {
        this.sourceLongitude = sourceLongitude;
    }

    public double getSourceLatitude() {
        return sourceLatitude;
    }

    public void setSourceLatitude(double sourceLatitude) {
        this.sourceLatitude = sourceLatitude;
    }

    public double getDestinationLongitude() {
        return destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude) {
        this.destinationLongitude = destinationLongitude;
    }

    public double getDestinationLatitude() {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude) {
        this.destinationLatitude = destinationLatitude;
    }


    public SimpleDateFormat getStartingHour() {
        return startingHour;
    }

    public void setStartingHour(SimpleDateFormat startingHour) {
        this.startingHour = startingHour;
    }

    public SimpleDateFormat getEndingHour() {
        return endingHour;
    }

    public void setEndingHour(SimpleDateFormat endingHour) {
        this.endingHour = endingHour;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        this.status = status;
    }


}
