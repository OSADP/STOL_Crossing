package gov.dot.fhwa.saxton.crossingrequest.model;

import gov.dot.fhwa.saxton.crossingrequest.model.messages.GeofenceDescription;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Unit test
 */
public class CrossingGeometryModelTest {

    @Test
    public void testLoadPedestrianGeofenceFromFile() throws Exception {
        CrossingGeometryModel model = new CrossingGeometryModel();
        model.loadPedestrianGeofenceFromFile("data/pedestrian_geofence.json");

        GeofenceDescription fence = model.getPedestrianGeofenceDescription();

        assertTrue(fence != null);
        System.out.println(fence.toString());
    }

    @Test
    public void testLoadDriverGeofenceFromFile() throws Exception {
        CrossingGeometryModel model = new CrossingGeometryModel();
        model.loadDriverGeofenceFromFile("data/driver_geofence.json");

        GeofenceDescription fence = model.getDriverGeofenceDescription();

        assertTrue(fence != null);
        System.out.println(fence.toString());
    }
}