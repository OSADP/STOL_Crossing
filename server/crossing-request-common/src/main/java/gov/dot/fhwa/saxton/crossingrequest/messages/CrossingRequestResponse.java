package gov.dot.fhwa.saxton.crossingrequest.messages;

/**
 * Server response to CrossingRequest message from client
 */
public class CrossingRequestResponse {
    /**
     * Simple yes/no response indicator, expandable for later options such as EXTENDED
     */
    public enum CrossingRequestResponseStatus {
        ACCEPTED,
        REJECTED
    }

    protected CrossingRequestResponseStatus status;

    public CrossingRequestResponse() {
    }

    public CrossingRequestResponse(CrossingRequestResponseStatus status) {
        this.status = status;
    }

    /**
     * Get the response status from the server.
     * @return ACCEPTED, if the request was accepted, REJECTED o.w.
     */
    public CrossingRequestResponseStatus getStatus() {
        return status;
    }

    /**
     * Set the response status from the server.
     */
    public void setStatus(CrossingRequestResponseStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CrossingRequestResponse that = (CrossingRequestResponse) o;

        return status == that.status;

    }

    @Override
    public int hashCode() {
        return status != null ? status.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CrossingRequestReponse{" +
                "status=" + status +
                '}';
    }
}
