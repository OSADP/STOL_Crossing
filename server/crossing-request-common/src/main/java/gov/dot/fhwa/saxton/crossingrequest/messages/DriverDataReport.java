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

    public double getAvgLatency() {
        return avgLatency;
    }

    public void setAvgLatency(double avgLatency) {
        this.avgLatency = avgLatency;
    }

    public double getLocationAccuracy() {
        return locationAccuracy;
    }

    public void setLocationAccuracy(double locationAccuracy) {
        this.locationAccuracy = locationAccuracy;
    }

    public double getSpeedAccuracy() {
        return speedAccuracy;
    }

    public void setSpeedAccuracy(double speedAccuracy) {
        this.speedAccuracy = speedAccuracy;
    }

    public double getHeadingAccuracy() {
        return headingAccuracy;
    }

    public void setHeadingAccuracy(double headingAccuracy) {
        this.headingAccuracy = headingAccuracy;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public Location getPosition() {
        return position;
    }

    public double getSpeed() {
        return speed;
    }

    public double getHeading() {
        return heading;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPosition(Location position) {
        this.position = position;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

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
