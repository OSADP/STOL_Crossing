package gov.dot.fhwa.saxton.crossingrequest.logger;

import gov.dot.fhwa.saxton.crossingrequest.messages.DriverDataReport;
import gov.dot.fhwa.saxton.crossingrequest.messages.EventReport;
import gov.dot.fhwa.saxton.crossingrequest.messages.PedestrianDataReport;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Data and event logger class for CrossingRequest server
 */
public class CsvLogger {
    protected Logger log = LogManager.getLogger(getClass());

    protected boolean dataFilesOpen = false;

    protected OutputStreamWriter dataFileWriter;
    protected OutputStreamWriter eventFileWriter;

    protected static final String dataFileHeader = "timestamp (ms), user role,latitude (deg),longitude (deg),heading (deg), speed (m/s), location accuracy (m), avg latency (ms)\n";
    protected static final String eventFileHeader = "timestamp (ms),user role,event type\n";

    public CsvLogger() {
    }

    /**
     * Open the log files and begin writing received data reports and events to them. Expected to be called after
     * initialization of the CsvLogger object. Calls to any other method before this will result in exceptions.
     *
     * @param eventLogFilePath The String filepath to the file to write new event reports to
     * @param dataLogFilePath The String filepath to the file to write new data reports to
     */
    public void open(String eventLogFilePath, String dataLogFilePath) {
        if (dataFilesOpen) {
            close();
        }

        try {
            dataFileWriter = new OutputStreamWriter(new FileOutputStream(dataLogFilePath));
            eventFileWriter = new OutputStreamWriter(new FileOutputStream(eventLogFilePath));
            dataFilesOpen = true;

            eventFileWriter.write(eventFileHeader);
            dataFileWriter.write(dataFileHeader);
        } catch (IOException e) {
            log.error("Exception encountered opening log files!!");
        }
    }

    /**
     * Close the currently open files and force their buffers to write to disk.
     */
    public void close() {
        if (dataFilesOpen) {
            try {
                dataFileWriter.close();
                eventFileWriter.close();
                dataFilesOpen = false;
            } catch (IOException e) {
                log.error("Exception encountered closing log files!!");
            }
        } else {
            log.warn("Call to close logger files without log files being opened first.");
        }
    }

    /**
     * Format an event report to a CSV row and then write it to the currently open event log file
     * @param er The event report to record
     */
    public void recordEvent(EventReport er) {
        if (!dataFilesOpen) {
            log.warn("DATA LOGGING ATTEMPTED WHILE LOG FILES NOT OPENED. DATA WILL BE LOST!");
            return;
        }

        try {
            eventFileWriter.write(formatEventReport(er));
        } catch (IOException e) {
            log.error("Error writing log entry: " + formatEventReport(er));
        }
    }

    /**
     * Format a pedestrian data report to a CSV row and then write it to the currently open data log file
     * @param dataReport The data report to record
     */
    public void recordPedData(PedestrianDataReport dataReport) {
        if (!dataFilesOpen) {
            log.warn("DATA LOGGING ATTEMPTED WHILE LOG FILES NOT OPENED. DATA WILL BE LOST!");
            return;
        }

        try {
            dataFileWriter.write(formatPedestrianDataReport(dataReport));
        } catch (IOException e) {
            log.error("Error writing log entry: " + formatPedestrianDataReport(dataReport));
        }
    }

    /**
     * Format a driver data report to a CSV row and then write it to the currently open data log file
     * @param dataReport The data report to record
     */
    public void recordDriverData(DriverDataReport dataReport) {
        if (!dataFilesOpen) {
            log.warn("DATA LOGGING ATTEMPTED WHILE LOG FILES NOT OPENED. DATA WILL BE LOST!");
            return;
        }

        try {
            dataFileWriter.write(formatDriverDataReport(dataReport));
        } catch (IOException e) {
            log.error("Error writing log entry: " + formatDriverDataReport(dataReport));
        }
    }

    /**
     * Coerce an EventReport object into a string format containing a newline. This CSV formats the important values in
     * the event report object for writing to a file.
     * @param er The EventReport instance to serialize to CSV
     * @return The String representation of a CSV row with values from er, including a newline character at the end
     */
    protected String formatEventReport(EventReport er) {
        return "" + er.getTimestamp() +
                "," + er.getUserRole() +
                "," + er.getType() +
                "\n";
    }

    /**
     * Coerce an DriverDataReport object into a string format containing a newline. This CSV formats the important values in
     * the DriverDataReport object for writing to a file.
     * @param dataReport The DriverDataReport instance to serialize to CSV
     * @return The String representation of a CSV row with values from dataReport, including a newline character at the end
     */
    protected String formatDriverDataReport(DriverDataReport dataReport) {
        return "" + dataReport.getTimestamp() +
                ",MOTORIST" +
                "," + dataReport.getPosition().getLatitude() +
                "," + dataReport.getPosition().getLongitude() +
                "," + dataReport.getHeading() +
                "," + dataReport.getSpeed() +
                "," + dataReport.getLocationAccuracy() +
                "," + dataReport.getAvgLatency() +
                "\n";
    }

    /**
     * Coerce an PedestrianDataReport object into a string format containing a newline. This CSV formats the important values in
     * the PedestrianDataReport object for writing to a file.
     * @param dataReport The PedestrianDataReport instance to serialize to CSV
     * @return The String representation of a CSV row with values from dataReport, including a newline character at the end
     */
    protected String formatPedestrianDataReport(PedestrianDataReport dataReport) {
        return "" + dataReport.getTimestamp() +
                ",PEDESTRIAN" +
                "," + dataReport.getPosition().getLatitude() +
                "," + dataReport.getPosition().getLongitude() +
                "," +
                "," +
                "," + dataReport.getLocationAccuracy() +
                "," + dataReport.getAvgLatency() +
                "\n";
    }
}
