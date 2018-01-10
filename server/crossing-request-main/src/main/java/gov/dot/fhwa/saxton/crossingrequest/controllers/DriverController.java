package gov.dot.fhwa.saxton.crossingrequest.controllers;

import gov.dot.fhwa.saxton.crossingrequest.logger.CsvLogger;
import gov.dot.fhwa.saxton.crossingrequest.messages.*;
import gov.dot.fhwa.saxton.crossingrequest.model.CrossingGeometryModel;
import gov.dot.fhwa.saxton.crossingrequest.model.NotificationStatusModel;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint handler for all driver related data
 */
@RestController
public class DriverController {
    protected Logger log = LogManager.getLogger(getClass());

    @Autowired
    protected CrossingGeometryModel crossingGeometryModel;

    @Autowired
    protected NotificationStatusModel notificationStatusModel;

    @Autowired
    protected CsvLogger csvLogger;

    /**
     * Get the JSON encoded representation of the motorist geofence configured for this application.
     * @return A Jackson encoded GeofenceDescription containing the data needed for a client
     */
    @RequestMapping(value="/driver/geofence")
    public GeofenceDescription getDriverGeofence() {
        log.info("Received request for pedestrian geofence, returning data.");
        return crossingGeometryModel.getDriverGeofenceDescription();
    }

    /**
     * Notify the vehicle of the current request status from the pedestrian. If this is active and the driver is inside
     * the motorist geofence, the driver's device should display the pedestrian alert.
     *
     * @return A NotificationStatus object containing the data needed by the driver.
     */
    @RequestMapping(value="/driver/notification-status")
    public NotificationStatus getRequestStatus() {
        log.info("Received request for pedestrian geofence status, returning data.");
        boolean active = notificationStatusModel.getNotificationStatus();
        NotificationStatus out = new NotificationStatus(System.currentTimeMillis(), active, System.currentTimeMillis());
        return out;
    }

    /**
     * Accept a data report from the driver and return the notification status. Obeys the same semantic behavior as
     * getRequestStatus() but reduces the extra network call to both report data and get status.
     *
     * @param report The driver's data report for the present timestep
     * @return A NotificationStatus object containing the state of the pedestrian warning
     */
    @RequestMapping(value="/driver/data")
    public NotificationStatus reportDriverData(@RequestBody DriverDataReport report) {
        log.info("Received driver data report, logging...");
        csvLogger.recordDriverData(report);

        boolean active = notificationStatusModel.getNotificationStatus();
        NotificationStatus out = new NotificationStatus(System.currentTimeMillis(), active, System.currentTimeMillis());
        if (active) {
            EventReport er = new EventReport(System.currentTimeMillis(), EventReport.EventType.PEDESTRIAN_WARNING_BROADCAST_DELIVERED, UserRole.SERVER);
            csvLogger.recordEvent(er);
        }
        return out;
    }

    /**
     * Accept an event report from the driver. This does not return the geofence status as these calls are unpredictable
     * unlike data reports.
     * @param report The event report fom the driver
     */
    @RequestMapping(value="/driver/event")
    private void reportDriverEvent(@RequestBody EventReport report) {
        log.info("Received driver event report, logging...");
        csvLogger.recordEvent(report);
    }
}
