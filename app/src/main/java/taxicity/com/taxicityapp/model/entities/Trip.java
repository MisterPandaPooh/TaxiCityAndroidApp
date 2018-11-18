package taxicity.com.taxicityapp.model.entities;

import java.util.Date;

public class Trip {

    public  enum TripStatus {AVAILABLE,IN_PROGRESS,FINISHED}

    private String startingAddress;

    private String destinationAddress;

    private Date startingHour;

    private Date endingHour;

    private String customerName;

    private String customerPhone;

    private String customerEmail;

    private TripStatus status;


}
