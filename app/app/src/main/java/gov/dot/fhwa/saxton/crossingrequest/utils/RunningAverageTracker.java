package gov.dot.fhwa.saxton.crossingrequest.utils;

/**
 * Tracks a running average for a value over a configurable number of data points. Used for tracking
 * server comms latency in multiple places throughout the application.
 */

public class RunningAverageTracker {
    private double dataset[];
    private int idx = 0;
    private int numPoints = 0;
    private int curNumPoints = 0;

    public RunningAverageTracker(double initialValue, int numPoints) {
        dataset = new double[numPoints];
        for (int i = 0; i < numPoints; i++) {
            dataset[i] = initialValue;
        }

        this.numPoints = numPoints;
    }

    /**
     * Add a data point to the internal data set. If the data set size is already at maximum the oldest
     * datapoint in the set will be bumped out to make room.
     *
     * @param value The double value to add to the dataset.
     */
    public void addDatapoint(double value) {
        dataset[idx] = value;

        // Iterate and wrap around if necessary
        idx = (idx + 1) % numPoints;

        // Keep track of partially filled array
        if (curNumPoints < numPoints) {
            curNumPoints++;
        }
    }

    /**
     * Get the current size of the dataset
     * @return The number of points currently in the data set
     */
    public int getCurNumPoints() {
        return curNumPoints;
    }

    /**
     * Get the maximum size of the dataset
     * @return The maximum number of points in the dataset before the oldest points are removed
     */
    public int getMaxNumPoints() {
        return numPoints;
    }

    /**
     * Get the current average value of all datapoints still in the dataset. If the dataset is below
     * maximum capacity only the values that had been added already will be used to compute the average.
     *
     * @return A computed value that is the average of all data points in the set.
     */
    public double getAverage() {
        int endIdx = (curNumPoints < numPoints ? curNumPoints : numPoints);

        double sum = 0;
        for (int i = 0; i < endIdx; i++) {
            sum += dataset[i];
        }

        return sum / endIdx;
    }
}
