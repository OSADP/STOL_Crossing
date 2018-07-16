package gov.dot.fhwa.saxton.crossingrequest.messages;

import gov.dot.fhwa.saxton.crossingrequest.geometry.Location;

/**
 * Log entry for pedestrian data repo
 */
public class PedestrianDataReport {
    protected long timestamp;
    protected Location position;
    protected double locationAccuracy;
    protected double avgLatency;

    public PedestrianDataReport() {
    }

    public PedestrianDataReport(long timestamp, Location position, double locationAccuracy, double avgLatency) {
        this.timestamp = timestamp;
        this.position = position;
        this.locationAccuracy = locationAccuracy;
        this.avgLatency = avgLatency;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPosition(Location position) {
        this.position = position;
    }

    public double getLocationAccuracy() {
        return locationAccuracy;
    }

    public void setLocationAccuracy(double locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    public double getAvgLatency() {
        return avgLatency;
    }

    public void setAvgLatency(double avgLatency) {
        this.avgLatency = avgLatency;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Location getPosition() {
        return position;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PedestrianDataReport that = (PedestrianDataReport) o;

        if (timestamp != that.timestamp) return false;
        if (Double.compare(that.locationAccuracy, locationAccuracy) != 0) return false;
        if (Double.compare(that.avgLatency, avgLatency) != 0) return false;
        return !(position != null ? !position.equals(that.position) : that.position != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (position != null ? position.hashCode() : 0);
        temp = Double.doubleToLongBits(locationAccuracy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(avgLatency);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "PedestrianDataReport{" +
                "timestamp=" + timestamp +
                ", position=" + position +
                ", locationAccuracy=" + locationAccuracy +
                ", avgLatency=" + avgLatency +
                '}';
    }
}
