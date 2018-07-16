package gov.dot.fhwa.saxton.crossingrequest.messages;

/**
 * Server response to CrossingRequest message from client
 */
public class CrossingRequestResponse {
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

    public CrossingRequestResponseStatus getStatus() {
        return status;
    }

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
