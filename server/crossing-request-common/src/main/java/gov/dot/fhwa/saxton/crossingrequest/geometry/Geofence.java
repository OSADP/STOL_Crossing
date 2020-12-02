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

    public double getEntranceHeading() {
        if (!enteredRegion) {
            return -1;
        }
        return entranceHeading;
    }

    protected abstract boolean testInsideCondition(Location loc);
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
