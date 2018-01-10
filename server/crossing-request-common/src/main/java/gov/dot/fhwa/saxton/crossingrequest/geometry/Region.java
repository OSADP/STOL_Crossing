package gov.dot.fhwa.saxton.crossingrequest.geometry;

/**
 * Interface to be implemented by various classes encoding the logic for geometric regions
 */
public interface Region {
    boolean containsLocation(Location loc);
    boolean testHeading(double heading);
}
