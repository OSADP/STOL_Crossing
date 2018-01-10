package gov.dot.fhwa.saxton.crossingrequest.geometry;

import gov.dot.fhwa.saxton.crossingrequest.messages.GeofenceDescription;

/**
 * Converts a GeofenceDescription into a concrete geofence representation implementing the Geofence
 * contract
 */
public class GeofenceFactory {
    public Geofence buildGeofence(GeofenceDescription desc) {
        if (desc.getRegions().size() <= 1) {
            return new SimpleGeofence(desc.getRegions().get(0));
        } else {
            return new CompoundGeofence(desc.getRegions());
        }
    }
}
