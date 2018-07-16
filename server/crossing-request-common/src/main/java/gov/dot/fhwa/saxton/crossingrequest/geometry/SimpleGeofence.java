package gov.dot.fhwa.saxton.crossingrequest.geometry;

/**
 * Basic geofence class containing a single polygonal region
 */
public class SimpleGeofence extends Geofence {
    protected final Region region;

    public SimpleGeofence(Region region) {
        this.region = region;
    }

    @Override
    protected boolean testInsideCondition(Location loc) {
        return region.containsLocation(loc);
    }

    @Override
    public boolean testHeading(Location loc, double heading) {
        return region.testHeading(heading);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SimpleGeofence that = (SimpleGeofence) o;

        return !(region != null ? !region.equals(that.region) : that.region != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (region != null ? region.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimpleGeofence{" +
                "region=" + region +
                "} " + super.toString();
    }
}
