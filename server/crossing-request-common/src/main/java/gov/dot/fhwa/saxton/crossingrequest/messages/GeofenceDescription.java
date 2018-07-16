package gov.dot.fhwa.saxton.crossingrequest.messages;


import gov.dot.fhwa.saxton.crossingrequest.geometry.ConvexPolygonRegion;
import gov.dot.fhwa.saxton.crossingrequest.geometry.GeofenceRole;

import java.util.List;


/**
 * Informational description of a Geofence. Unable to perform Geofence computations but easier to transfer over the network
 * or deserializer from disk. Can be converted into a normal Geofence instance using a GeofenceFactory
 */
public class GeofenceDescription {
    // Non-functional fields, but useful for analysis and recordskeeping
    private String geofenceName;
    private String geofenceUniqueID;
    private String geofenceModificationDate;

    // Functional fields
    private GeofenceRole role;
    private List<ConvexPolygonRegion> regions; // Field will determine the type of geofence created

    public GeofenceDescription() {
    }

    public GeofenceDescription(String geofenceName, String geofenceUniqueID,
                               String geofenceModificationDate, GeofenceRole role, List<ConvexPolygonRegion> regions) {
        this.geofenceName = geofenceName;
        this.geofenceUniqueID = geofenceUniqueID;
        this.geofenceModificationDate = geofenceModificationDate;
        this.role = role;
        this.regions = regions;
    }

    public String getGeofenceName() {
        return geofenceName;
    }

    public void setGeofenceName(String geofenceName) {
        this.geofenceName = geofenceName;
    }

    public String getGeofenceUniqueID() {
        return geofenceUniqueID;
    }

    public void setGeofenceUniqueID(String geofenceUniqueID) {
        this.geofenceUniqueID = geofenceUniqueID;
    }

    public String getGeofenceModificationDate() {
        return geofenceModificationDate;
    }

    public void setGeofenceModificationDate(String geofenceModificationDate) {
        this.geofenceModificationDate = geofenceModificationDate;
    }

    public GeofenceRole getRole() {
        return role;
    }

    public void setRole(GeofenceRole role) {
        this.role = role;
    }

    public List<ConvexPolygonRegion> getRegions() {
        return regions;
    }

    public void setRegions(List<ConvexPolygonRegion> regions) {
        this.regions = regions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeofenceDescription that = (GeofenceDescription) o;

        if (geofenceName != null ? !geofenceName.equals(that.geofenceName) : that.geofenceName != null) return false;
        if (geofenceUniqueID != null ? !geofenceUniqueID.equals(that.geofenceUniqueID) : that.geofenceUniqueID != null)
            return false;
        if (geofenceModificationDate != null ? !geofenceModificationDate.equals(that.geofenceModificationDate) : that.geofenceModificationDate != null)
            return false;
        if (role != that.role) return false;
        return !(regions != null ? !regions.equals(that.regions) : that.regions != null);

    }

    @Override
    public int hashCode() {
        int result = geofenceName != null ? geofenceName.hashCode() : 0;
        result = 31 * result + (geofenceUniqueID != null ? geofenceUniqueID.hashCode() : 0);
        result = 31 * result + (geofenceModificationDate != null ? geofenceModificationDate.hashCode() : 0);
        result = 31 * result + (role != null ? role.hashCode() : 0);
        result = 31 * result + (regions != null ? regions.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GeofenceDescription{" +
                "geofenceName='" + geofenceName + '\'' +
                ", geofenceUniqueID='" + geofenceUniqueID + '\'' +
                ", geofenceModificationDate='" + geofenceModificationDate + '\'' +
                ", role=" + role +
                ", regions=" + regions +
                '}';
    }
}
