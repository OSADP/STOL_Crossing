package gov.dot.fhwa.saxton.crossingrequest.messages;


/**
 * Class representing the report from a client device that an event has occurred
 */
public class EventReport {
    private long timestamp;

    private String type;

    private String userRole;

    public EventReport() {
    }

    public EventReport(long timestamp, EventType type, UserRole userRole) {
        this.timestamp = timestamp;
        this.type = type.toString();
        this.userRole = userRole.toString();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type.toString();
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(UserRole userRole) {
        this.userRole = userRole.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EventReport that = (EventReport) o;

        if (timestamp != that.timestamp) return false;
        if (type != that.type) return false;
        return userRole == that.userRole;

    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (userRole != null ? userRole.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "EventReport{" +
                "timestamp=" + timestamp +
                ", type=" + type +
                ", userRole=" + userRole +
                '}';
    }

    public enum EventType {
        PEDESTRIAN_REGISTRATION,
        MOTORIST_REGISTRATION,
        GEOFENCE_ENTERED,
        GEOFENCE_EXITED,
        CROSSING_REQUEST_RECEIVED,
        CROSSING_REQUEST_APPROVED,
        CROSSING_REQUEST_DENIED,
        PEDESTRIAN_WARNING_BROADCAST_START,
        PEDESTRIAN_WARNING_BROADCAST_END,
        PEDESTRIAN_WARNING_BROADCAST_DELIVERED
    }
}
