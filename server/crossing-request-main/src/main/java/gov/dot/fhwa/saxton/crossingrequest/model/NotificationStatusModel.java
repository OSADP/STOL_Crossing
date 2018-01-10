package gov.dot.fhwa.saxton.crossingrequest.model;

import gov.dot.fhwa.saxton.crossingrequest.config.Constants;
import gov.dot.fhwa.saxton.crossingrequest.logger.CsvLogger;
import gov.dot.fhwa.saxton.crossingrequest.messages.EventReport;
import gov.dot.fhwa.saxton.crossingrequest.messages.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Notification status manager. Manages the behind the scenes reset of the notification value.
 */
public class NotificationStatusModel {
    private AtomicBoolean notificationActive = new AtomicBoolean(false);
    private Thread statusResetTimerThread;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private CsvLogger csvLogger;

    @Value("${notification.duration_s}")
    private double notificationDuration_s;

    /**
     * Synchronously get the current notification status, waiting for a brief moment if the atomic value is being read or
     * modified
     * @return A regular boolean value for the notification status
     */
    public boolean getNotificationStatus() {
        return notificationActive.get();
    }

    /**
     * Spawn a thread which will sleep until notificationDuration_s seconds have elapsed and then reset the notification
     * status back to false.
     */
    private void sleepUntilNotificationExpiry() {
        long millisToSleep = (long) (notificationDuration_s * Constants.SECONDS_TO_MILLISECONDS);
        log.info("Sleeping " + millisToSleep + " before resetting notification status");
        try {
            Thread.sleep(millisToSleep);
            notificationActive.set(false);
            csvLogger.recordEvent(new EventReport(System.currentTimeMillis(), EventReport.EventType.PEDESTRIAN_WARNING_BROADCAST_END, UserRole.SERVER));
            log.info("Reset notification status");
        } catch (InterruptedException e) {
            // Should only be here if we get interrupted by another notification request, causing a timer reset
            e.printStackTrace();
        }
    }

    /**
     * Request the notification status to be set to true for a duration.
     *
     * The duration is configured by notification.duration_s, after which the value of the notification will reset
     * to false.
     *
     * If another request is received before the natural expiry of an already existing notification, the expiry will be
     * postponed by notification.duration_s again.
     */
    public void requestNotification() {
        // Set the initial status or reset the clock on the request
        if (!getNotificationStatus()) {
            notificationActive.set(true);
            csvLogger.recordEvent(new EventReport(System.currentTimeMillis(), EventReport.EventType.PEDESTRIAN_WARNING_BROADCAST_START, UserRole.SERVER));
        } else {
            statusResetTimerThread.interrupt();
        }

        // Make sure we always have a thread running to reset the status
        statusResetTimerThread = new Thread(this::sleepUntilNotificationExpiry);
        statusResetTimerThread.start();
    }

}
