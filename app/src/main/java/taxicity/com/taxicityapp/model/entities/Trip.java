package taxicity.com.taxicityapp.model.entities;

import com.google.firebase.database.Exclude;

import java.text.SimpleDateFormat;
import java.util.Date;

import taxicity.com.taxicityapp.model.helper.Helpers;

public class Trip {




    public enum TripStatus {AVAILABLE, IN_PROGRESS, FINISHED}

    @Exclude
    private String key;

    private String startingHour; //Start Hours of the Trip.

    private String endingHour;  //End Hours of the Trip.

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private TripStatus status;

    private double sourceLongitude;

    private double sourceLatitude;

    private String sourceAddress;

    private double destinationLongitude;

    private double destinationLatitude;

    private String destinationAddress;

    private String destinationCity;

    private String driverEmail;



    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
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

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }


    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getStartingHour() {
        return startingHour;
    }

    public void setStartingHour(String startingHour) {
        this.startingHour = startingHour;
    }

    public String getEndingHour() {
        return endingHour;
    }

    public void setEndingHour(String endingHour) {
        this.endingHour = endingHour;
    }


    @Exclude
    public Date getStartingHourAsDate() {
        if (startingHour == null)
            return new Date();
        return new Date(Long.parseLong(startingHour));
    }

    @Exclude
    public Date getEndingHourAsDate() {
        return new Date(Long.parseLong(endingHour));
    }



    @Exclude
    public TripStatus getStatusAsEnum() {
        return status;
    }

    @Exclude
    public void setStatusAsEnum(TripStatus status) {
        this.status = status;
    }


    public String getStatus() {
        if (status == null) {
            return null;
        } else {
            return status.name();
        }
    }

    public void setStatus(String statusString) {
        if (statusString == null) {
            status = null;
        } else {
            this.status = TripStatus.valueOf(statusString);
        }
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

}

