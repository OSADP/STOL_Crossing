package gov.dot.fhwa.saxton.crossingrequest.messages;

/**
 * Crossing request message for use by the client device and receipt by the server
 */
public class CrossingRequest {
    protected int crossingId;
    protected int pedestrianId;

    public CrossingRequest() {
    }

    public CrossingRequest(int crossingId, int pedestrianId) {
        this.crossingId = crossingId;
        this.pedestrianId = pedestrianId;
    }

    /**
     * Get the ID associated with the mid-block crossing the request is for
     */
    public int getCrossingId() {
        return crossingId;
    }

    /**
     * Get the ID of the pedestrian device making the request
     */
    public int getPedestrianId() {
        return pedestrianId;
    }


    /**
     * Set the ID associated with the mid-block crossing the request is for
     */
    public void setCrossingId(int crossingId) {
        this.crossingId = crossingId;
    }

    /**
     * Set the ID of the pedestrian device making the request
     */
    public void setPedestrianId(int pedestrianId) {
        this.pedestrianId = pedestrianId;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrossingRequest that = (CrossingRequest) o;

        if (crossingId != that.crossingId) return false;
        return pedestrianId == that.pedestrianId;

    }

    @Override
    public int hashCode() {
        int result = crossingId;
        result = 31 * result + pedestrianId;
        return result;
    }

    @Override
    public String toString() {
        return "CrossingRequest{" +
                "crossingId=" + crossingId +
                ", pedestrianId=" + pedestrianId +
                '}';
    }
}
