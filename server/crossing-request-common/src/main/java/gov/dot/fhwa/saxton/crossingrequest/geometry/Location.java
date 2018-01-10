package gov.dot.fhwa.saxton.crossingrequest.geometry;

/**
 * Basic latitude/longitude pair class. Immutable, must construct new instances to change value
 */
public class Location {
    private double latitude;
    private double longitude;

    public Location() {

    }

    public Location(double lat, double lon) {
        latitude = lat;
        longitude = lon;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Compute the heading angle between two Location objects.
     *
     * Uses a flat-earth approximation to simplify the math such that results are only valid for Locations whose
     * distances are not large and the points are not near the poles.
     * Reference angle for heading is assumed to be North = 0.0 degrees
     *
     * @param loc The location at the end of the heading
     * @return The heading angle between this location and the parameter location in degrees in the range [0, 360.0)
     */
    public double getHeadingTo(Location loc) {
        if (loc.getLatitude() == getLatitude()) {
            // ATAN is going to be undefined here, so we'll make some adjustments
            if (loc.getLongitude() >= getLongitude()) {
                return 0;
            } else {
                return 180;
            }
        }

        double rawAngle = 180.0 / Math.PI * Math.atan((loc.getLatitude() - getLatitude())/(loc.getLongitude() - getLongitude()));
        if (rawAngle < 0.0) {
            // Wrap around instead of allowing negative angles
            rawAngle += 360;
        }

        return rawAngle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (Double.compare(location.latitude, latitude) != 0) return false;
        return Double.compare(location.longitude, longitude) == 0;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Location{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
