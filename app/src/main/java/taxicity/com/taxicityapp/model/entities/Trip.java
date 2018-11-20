package taxicity.com.taxicityapp.model.entities;

import java.util.Date;

public class Trip {


    public enum TripStatus {AVAILABLE,IN_PROGRESS,FINISHED}

    private long tripId;

    private String startingAddress;

    private String destinationAddress;

    private Date startingHour;

    private Date endingHour;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private TripStatus status;



    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public String getStartingAddress() {
        return startingAddress;
    }

    public void setStartingAddress(String startingAddress) {
        this.startingAddress = startingAddress;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public Date getStartingHour() {
        return startingHour;
    }

    public void setStartingHour(Date startingHour) {
        this.startingHour = startingHour;
    }

    public Date getEndingHour() {
        return endingHour;
    }

    public void setEndingHour(Date endingHour) {
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
