package gov.dot.fhwa.saxton.crossingrequest.geometry;

/**
 * Base abstract class from which other geofences will inherit, provides basic functionality re: entry heading checking
 */
public abstract class Geofence {
    protected String geofenceName;
    protected String geofenceUniqueId;
    protected String geofenceModificationDate;

    protected Location prevLock;
    protected Location curLoc;
    protected boolean enteredRegion;
    protected double entranceHeading;

    /**
     * Check if a location is inside the region(s) bounded by the Geofence
     * @param loc The location to test
     * @return True, if inside, false o.w.
     */
    public boolean checkInside(Location loc) {
        prevLock = curLoc;
        curLoc = loc;

        boolean inside = testInsideCondition(curLoc);
        if (inside && !enteredRegion) {
            enteredRegion = true;
            entranceHeading = (prevLock != null? prevLock.getHeadingTo(curLoc) : -1);
        } else if (!inside && enteredRegion) {
            enteredRegion = false;
        }

        return inside;
    }

    /**
     * Get the heading angle (determined by lat/lon coordinate changes) at which the geofence boundary was crossed
     */
    public double getEntranceHeading() {
        if (!enteredRegion) {
            return -1;
        }
        return entranceHeading;
    }

    /**
     * The actual logic used to determine "insideness". To be defined by subclasses
     */
    protected abstract boolean testInsideCondition(Location loc);

    /**
     * The actual logic used to determine if heading matters and if a given heading is acceptable
     * @param loc The location to test
     * @param heading The vehicle or pedestrian's heading at that location
     * @return True if the heading is valid or heading doesn't matter, false o.w.
     */
    public abstract boolean testHeading(Location loc, double heading);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Geofence geofence = (Geofence) o;

        if (enteredRegion != geofence.enteredRegion) return false;
        if (Double.compare(geofence.entranceHeading, entranceHeading) != 0) return false;
        if (geofenceName != null ? !geofenceName.equals(geofence.geofenceName) : geofence.geofenceName != null)
            return false;
        if (geofenceUniqueId != null ? !geofenceUniqueId.equals(geofence.geofenceUniqueId) : geofence.geofenceUniqueId != null)
            return false;
        if (geofenceModificationDate != null ? !geofenceModificationDate.equals(geofence.geofenceModificationDate) : geofence.geofenceModificationDate != null)
            return false;
        if (prevLock != null ? !prevLock.equals(geofence.prevLock) : geofence.prevLock != null) return false;
        return !(curLoc != null ? !curLoc.equals(geofence.curLoc) : geofence.curLoc != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = geofenceName != null ? geofenceName.hashCode() : 0;
        result = 31 * result + (geofenceUniqueId != null ? geofenceUniqueId.hashCode() : 0);
        result = 31 * result + (geofenceModificationDate != null ? geofenceModificationDate.hashCode() : 0);
        result = 31 * result + (prevLock != null ? prevLock.hashCode() : 0);
        result = 31 * result + (curLoc != null ? curLoc.hashCode() : 0);
        result = 31 * result + (enteredRegion ? 1 : 0);
        temp = Double.doubleToLongBits(entranceHeading);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Geofence{" +
                "geofenceName='" + geofenceName + '\'' +
                ", geofenceUniqueId='" + geofenceUniqueId + '\'' +
                ", geofenceModificationDate='" + geofenceModificationDate + '\'' +
                ", prevLock=" + prevLock +
                ", curLoc=" + curLoc +
                ", enteredRegion=" + enteredRegion +
                ", entranceHeading=" + entranceHeading +
                '}';
    }
}
