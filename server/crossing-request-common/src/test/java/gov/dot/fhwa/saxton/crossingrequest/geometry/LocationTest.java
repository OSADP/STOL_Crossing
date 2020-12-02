package gov.dot.fhwa.saxton.crossingrequest.geometry;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tes the get heading to method of Location for validity
 */
public class LocationTest {

    private boolean fpcmp(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }

    @Test
    public void testGetHeadingToPositiveLateralPoint() throws Exception {
        Location loc1 = new Location(0.0, 0.0);
        Location loc2 = new Location(1.0, 0.0);

        assertTrue(fpcmp(loc1.getHeadingTo(loc2), 90.0, 0.1));
    }

    @Test
    public void testGetHeadingToNegativeLateralPoint() throws Exception {
        Location loc1 = new Location(0.0, 0.0);
        Location loc2 = new Location(-1.0, 0.0);

        assertTrue(fpcmp(loc1.getHeadingTo(loc2), 270.0, 0.1));
    }

    @Test
    public void testGetHeadingToPositiveLongitudinalPoint() throws Exception {
        Location loc1 = new Location(0.0, 0.0);
        Location loc2 = new Location(0.0, 1.0);

        assertTrue(fpcmp(loc1.getHeadingTo(loc2), 0.0, 0.1));
    }

    @Test
    public void testGetHeadingToNegativeLongitudinalPoint() throws Exception {
        Location loc1 = new Location(0.0, 0.0);
        Location loc2 = new Location(0.0, -1.0);

        assertTrue(fpcmp(loc1.getHeadingTo(loc2), 180.0, 0.1));
    }
}