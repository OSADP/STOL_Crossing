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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller containing all the HTTP REST endpoints for use by Pedestrian users
 */
@RestController
public class PedestrianController {
    protected Logger log = LogManager.getLogger(getClass());

    @Autowired
    protected CrossingGeometryModel crossingGeometryModel;

    @Autowired
    protected NotificationStatusModel notificationStatusModel;

    @Autowired
    protected CsvLogger csvLogger;

    /**
     * Get the GeofenceDescription pertaining to the configured pedestrian geofence
     * @return A Jackson encoded JSON representation of the geofence description
     */
    @RequestMapping(value="/ped/geofence")
    public GeofenceDescription getPedestrianGeofence() {
        log.info("Received request for pedestrian geofence, returning data.");
        return crossingGeometryModel.getPedestrianGeofenceDescription();
    }

    /**
     * Receive a crossing request from the pedestrian.
     *
     * If a request is not currently active the notification status model will accept the request and we will indciate
     * that to the user.
     *
     * If a request is already active, the notification status model will reject the request and the currently ongoing
     * request will proceed as normal.
     *
     * @return The accept or reject status of the request
     */
    @RequestMapping(value="/ped/requestCrossing")
    public CrossingRequestResponse requestCrossing() {
        log.info("Received crossing request from pedestrian.");
        if (!notificationStatusModel.getNotificationStatus()) {
            log.info("Accepted crossing request, changing notification status to active.");
            notificationStatusModel.requestNotification();

            EventReport er = new EventReport(System.currentTimeMillis(), EventReport.EventType.CROSSING_REQUEST_APPROVED, UserRole.SERVER);
            csvLogger.recordEvent(er);

            return new CrossingRequestResponse(CrossingRequestResponse.CrossingRequestResponseStatus.ACCEPTED);
        }

        log.info("Crossing request rejected, notification status remains unchanged.");
        EventReport er = new EventReport(System.currentTimeMillis(), EventReport.EventType.CROSSING_REQUEST_DENIED, UserRole.SERVER);
        csvLogger.recordEvent(er);
        return new CrossingRequestResponse(CrossingRequestResponse.CrossingRequestResponseStatus.REJECTED);
    }

    /**
     * Get the status of the pedestrian crossing request.
     * @return True if the pedestrian crossing request is currently active, False o.w.
     */
    @RequestMapping(value="/ped/status")
    public boolean getRequestStatus() {
        log.info("Received request for pedestrian geofence status, returning data.");
        return notificationStatusModel.getNotificationStatus();
    }

    /**
     * Accept a pedestrian data report from the pedestrian's device
     * @param report The JSON encoded data report
     */
    @RequestMapping(value="/ped/data", method=RequestMethod.POST)
    public NotificationStatus reportPedestrianData(@RequestBody PedestrianDataReport report) {
        log.info("Received pedestrian data report, logging...");
        csvLogger.recordPedData(report);

        return new NotificationStatus(System.currentTimeMillis(), notificationStatusModel.getNotificationStatus(), 0);
    }

    /**
     * Accept a pedestrian event report from the pedestrian's device
     * @param report The JSON encoded event report
     */
    @RequestMapping(value="/ped/event", method=RequestMethod.POST)
    public void reportPedestrianEvent(@RequestBody EventReport report) {
        log.info("Received pedestrian event report, logging...");
        csvLogger.recordEvent(report);
    }
}
