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

    /**
     * Set the timestamp at which this pedestrian data report was generated
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Set the position measured by the GPS in the pedestrian device
     */
    public void setPosition(Location position) {
        this.position = position;
    }

    /**
     * Get the location accuracy (in m, radius) reported by the pedestrian device
     */
    public double getLocationAccuracy() {
        return locationAccuracy;
    }

    /**
     * Set the location accuracy (in m, radius) reported by the pedestrian device
     */
    public void setLocationAccuracy(double locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    /**
     * Get the average latency (in ms) over the last 10 timesteps between the server and the driver device
     */
    public double getAvgLatency() {
        return avgLatency;
    }

    /**
     * Set the average latency (in ms) over the last 10 timesteps between the server and the driver device
     */
    public void setAvgLatency(double avgLatency) {
        this.avgLatency = avgLatency;
    }

    /**
     * Get the timestamp at which this pedestrian data report was generated
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Get the position data reported by the GPS in the pedestrian device
     */
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
