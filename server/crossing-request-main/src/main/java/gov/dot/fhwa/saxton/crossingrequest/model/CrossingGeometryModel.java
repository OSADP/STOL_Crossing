package gov.dot.fhwa.saxton.crossingrequest.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.dot.fhwa.saxton.crossingrequest.geometry.Geofence;
import gov.dot.fhwa.saxton.crossingrequest.geometry.GeofenceFactory;
import gov.dot.fhwa.saxton.crossingrequest.messages.GeofenceDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

/**
 * Object responsible for maintaining information related to the geometric properties of the mid-block crossing
 */
public class CrossingGeometryModel {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected GeofenceFactory geofenceFactory;

    protected GeofenceDescription driverGeofenceDescription;
    protected GeofenceDescription pedestrianGeofenceDescription;

    protected Geofence driverGeofence;
    protected Geofence pedestrianGeofence;

    /**
     * Get the live geofence object for the driver geofence
     * @return A freshly constructed instance of Geofence build from the driverGeofenceDescription
     */
    public Geofence getDriverGeofence() {
        driverGeofence = geofenceFactory.buildGeofence(driverGeofenceDescription);
        return driverGeofence;
    }

    /**
     * Get the live geofence object for the pedestrian geofence
     * @return A freshly constructed instance of Geofence build from the pedestrianGeofenceDescription
     */
    public Geofence getPedestrianGeofence() {
        pedestrianGeofence = geofenceFactory.buildGeofence(pedestrianGeofenceDescription);
        return pedestrianGeofence;
    }


    /**
     * Get the GeofenceDescription for the pedestrian geofence. Use this for transferring the geofence data over the
     * network rather than the Geofence object itself.
     * @return The GeofenceDescription used to create the pedestrian geofence
     */
    public GeofenceDescription getPedestrianGeofenceDescription() {
        return pedestrianGeofenceDescription;
    }

    /**
     * Get the GeofenceDescription for the driver geofence. Use this for transferring the geofence data over the
     * network rather than the Geofence object itself.
     * @return The GeofenceDescription used to create the driver geofence
     */
    public GeofenceDescription getDriverGeofenceDescription() {
        return driverGeofenceDescription;
    }

    /**
     * Uses Jackson ObjectMapper to load a JSON encoded GeofenceDescription from disk. Saves the data as the pedestrian
     * geofence description.
     *
     * @param pedestrianGeofenceFileName The String encoded path to the geofence file to load
     */
    public void loadPedestrianGeofenceFromFile(String pedestrianGeofenceFileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GeofenceDescription description = mapper.readValue(new File(pedestrianGeofenceFileName), GeofenceDescription.class);
            pedestrianGeofenceDescription = description;
        } catch (IOException e) {
            log.error("Unable to read pedestrian geofence descriptor file" + pedestrianGeofenceFileName, e);
        }
    }

    /**
     * Uses Jackson ObjectMapper to load a JSON encoded GeofenceDescription from disk. Saves the data as the driver
     * geofence description.
     *
     * @param driverGeofenceFileName The String encoded path to the geofence file to load
     */
    public void loadDriverGeofenceFromFile(String driverGeofenceFileName) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            GeofenceDescription description = mapper.readValue(new File(driverGeofenceFileName), GeofenceDescription.class);
            driverGeofenceDescription = description;
        } catch (IOException e) {
            log.error("Unable to read driver geofence descriptor file" + driverGeofenceFileName, e);
        }
    }
}
