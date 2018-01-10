package gov.dot.fhwa.saxton.crossingrequest.messages;


import gov.dot.fhwa.saxton.crossingrequest.geometry.Location;

/**
 * Data report class for Driver application data
 */
public class DriverDataReport {
    protected long timestamp;
    protected Location position;
    protected double speed;
    protected double heading;
    protected double locationAccuracy;
    protected double speedAccuracy;
    protected double headingAccuracy;
    protected double avgLatency;

    public DriverDataReport() {
    }

    public DriverDataReport(long timestamp, Location position, double speed, double heading, double locationAccuracy,
                            double speedAccuracy, double headingAccuracy, double avgLatency) {
        this.timestamp = timestamp;
        this.position = position;
        this.speed = speed;
        this.heading = heading;
        this.locationAccuracy = locationAccuracy;
        this.speedAccuracy = speedAccuracy;
        this.headingAccuracy = headingAccuracy;
        this.avgLatency = avgLatency;
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
     * Get the location accuracy (in m, radius) of the driver device GPS
     */
    public double getLocationAccuracy() {
        return locationAccuracy;
    }

    /**
     * Set the location accuracy (in m, radius) of the driver device GPS
     */
    public void setLocationAccuracy(double locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    /**
     * Get the accuracy (in +/- m/s) of the driver device's speed measurement
     */
    public double getSpeedAccuracy() {
        return speedAccuracy;
    }

    /**
     * Set the accuracy (in +/- m/s) of the driver device's speed measurement
     */
    public void setSpeedAccuracy(double speedAccuracy) {
        this.speedAccuracy = speedAccuracy;
    }

    /**
     * Get the accuracy (in +/- deg) of the driver device's heading measurement
     */
    public double getHeadingAccuracy() {
        return headingAccuracy;
    }

    /**
     * Set the accuracy (in +/- deg) of the driver device's heading measurement
     */
    public void setHeadingAccuracy(double headingAccuracy) {
        this.headingAccuracy = headingAccuracy;
    }

    /**
     * Get the timestamp of this message generation formatted as a ms resolution Unix timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Get the GPS position as reported by the driver's device
     */
    public Location getPosition() {
        return position;
    }

    /**
     * Get the speed (in m/s) as reported by the driver's device
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Get the heading (in deg) as reported by the driver's device
     */
    public double getHeading() {
        return heading;
    }

    /**
     * Get the timestamp of this message generation formatted as a ms resolution Unix timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Set the GPS position as reported by the driver's device
     */
    public void setPosition(Location position) {
        this.position = position;
    }

    /**
     * Set the speed (in m/s) as reported by the driver's device
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Set the heading (in deg) as reported by the driver's device
     */
    public void setHeading(double heading) {
        this.heading = heading;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriverDataReport that = (DriverDataReport) o;

        if (timestamp != that.timestamp) return false;
        if (Double.compare(that.speed, speed) != 0) return false;
        if (Double.compare(that.heading, heading) != 0) return false;
        if (Double.compare(that.locationAccuracy, locationAccuracy) != 0) return false;
        if (Double.compare(that.speedAccuracy, speedAccuracy) != 0) return false;
        if (Double.compare(that.headingAccuracy, headingAccuracy) != 0) return false;
        if (Double.compare(that.avgLatency, avgLatency) != 0) return false;
        return !(position != null ? !position.equals(that.position) : that.position != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (position != null ? position.hashCode() : 0);
        temp = Double.doubleToLongBits(speed);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(heading);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(locationAccuracy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(speedAccuracy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(headingAccuracy);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(avgLatency);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "DriverDataReport{" +
                "timestamp=" + timestamp +
                ", position=" + position +
                ", speed=" + speed +
                ", heading=" + heading +
                ", locationAccuracy=" + locationAccuracy +
                ", speedAccuracy=" + speedAccuracy +
                ", headingAccuracy=" + headingAccuracy +
                ", avgLatency=" + avgLatency +
                '}';
    }
}
