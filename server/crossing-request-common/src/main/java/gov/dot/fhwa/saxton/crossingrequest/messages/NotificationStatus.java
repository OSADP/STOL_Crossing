package gov.dot.fhwa.saxton.crossingrequest.messages;

/**
 * Status of the notification to the driver
 */
public class NotificationStatus {
    protected long timestamp;
    protected boolean active;
    protected long activeSince;

    public NotificationStatus() {
    }

    public NotificationStatus(long timestamp, boolean active, long activeSince) {
        this.timestamp = timestamp;
        this.active = active;
        this.activeSince = activeSince;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isActive() {
        return active;
    }

    public long getActiveSince() {
        return activeSince;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setActiveSince(long activeSince) {
        this.activeSince = activeSince;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NotificationStatus that = (NotificationStatus) o;

        if (timestamp != that.timestamp) return false;
        if (active != that.active) return false;
        return activeSince == that.activeSince;

    }

    @Override
    public int hashCode() {
        int result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (int) (activeSince ^ (activeSince >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "NotificationStatus{" +
                "timestamp=" + timestamp +
                ", active=" + active +
                ", activeSince=" + activeSince +
                '}';
    }
}
