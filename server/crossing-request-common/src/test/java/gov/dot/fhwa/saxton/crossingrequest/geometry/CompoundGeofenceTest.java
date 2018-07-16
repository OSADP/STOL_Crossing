package gov.dot.fhwa.saxton.crossingrequest.geometry;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for compound concrete implementation of Geofence, in lieu of tests for Geofence itself, all implementations will
 * be tested individually.
 */
public class CompoundGeofenceTest {

    ConvexPolygonRegion region1;
    ConvexPolygonRegion region2;

    List<Location> region1Vertices;
    List<Location> region2Vertices;
    List<ConvexPolygonRegion> regions;

    @Before
    public void setUp() {
        region1Vertices = new ArrayList<>();
        region1Vertices.add(new Location(0.0, 0.0));
        region1Vertices.add(new Location(0.0, 1.0));
        region1Vertices.add(new Location(1.0, 1.0));
        region1Vertices.add(new Location(1.0, 0.0));
        region1 = new ConvexPolygonRegion(region1Vertices);

        region2Vertices = new ArrayList<>();
        region2Vertices.add(new Location(1.0, 1.0));
        region2Vertices.add(new Location(1.0, 2.0));
        region2Vertices.add(new Location(2.0, 2.0));
        region2Vertices.add(new Location(2.0, 1.0));
        region2 = new ConvexPolygonRegion(region2Vertices);

        regions = new ArrayList<>();
        regions.add(region1);
        regions.add(region2);
    }

    @Test
    public void testCheckInsideFirstRegion() throws Exception {
        CompoundGeofence compoundGeofence = new CompoundGeofence(regions);
        assertTrue(compoundGeofence.checkInside(new Location(0.5, 0.5)));
    }

    @Test
    public void testCheckInsideSecondRegion() throws Exception {
        CompoundGeofence compoundGeofence = new CompoundGeofence(regions);
        assertTrue(compoundGeofence.checkInside(new Location(1.5, 1.5)));
    }

    @Test
    public void testCheckInsideBothRegions() throws Exception {
        CompoundGeofence compoundGeofence = new CompoundGeofence(regions);
        assertTrue(compoundGeofence.checkInside(new Location(1.0, 1.0)));
    }

    @Test
    public void testCheckInsideNeitherRegion() throws Exception {
        CompoundGeofence compoundGeofence = new CompoundGeofence(regions);
        assertFalse(compoundGeofence.checkInside(new Location(3.0, 3.0)));
    }

    @Test
    public void testGetEntranceHeading() throws Exception {

    }
}