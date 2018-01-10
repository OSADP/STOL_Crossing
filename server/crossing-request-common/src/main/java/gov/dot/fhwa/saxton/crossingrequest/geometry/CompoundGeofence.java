package gov.dot.fhwa.saxton.crossingrequest.geometry;

import java.util.Collections;
import java.util.List;

/**
 * Compound geofence containing multiple regions
 * <p>
 * Insideness is the logical OR of being inside any of of the consitutuent geofences
 */
public class CompoundGeofence extends Geofence {
    protected final List<ConvexPolygonRegion> regions;

    public CompoundGeofence(List<ConvexPolygonRegion> regions) {
        this.regions = Collections.unmodifiableList(regions);
    }

    @Override
    protected boolean testInsideCondition(Location loc) {
        boolean inside = false;
        for (ConvexPolygonRegion region : regions) {
            inside = inside || region.containsLocation(loc);
        }

        return inside;
    }

    @Override
    public boolean testHeading(Location loc, double heading) {
        boolean correctHeading = false;
        for (ConvexPolygonRegion region : regions) {
            if (region.containsLocation(loc) && region.testHeading(heading)) {
                return true;
            }
        }

        return correctHeading;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CompoundGeofence that = (CompoundGeofence) o;

        return !(regions != null ? !regions.equals(that.regions) : that.regions != null);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (regions != null ? regions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompoundGeofence{" +
                "regions=" + regions +
                "} " + super.toString();
    }
}
