package gov.dot.fhwa.saxton.crossingrequest.utils;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;

/**
 * Constant storage for the CrossingRequest server application
 */

public class Constants {
    // Map parameters
    public static final LatLng tfhrcLatLng = new LatLng(38.954920, -77.148812);
    public static final float defaultMapZoom = (float) 17.5;
    public static final float defaultMapTilt = 65.5f;
    public static final float mapTriangleDeltaV = 0.0001f;
    public static final float mapTriangleDeltaH = 0.00007f;
    public static final int vehicleTriangleColor = Color.BLUE;

    // REST API parameters
    public static final String serverBaseUrl = "http://sample-env.wi6rp8ykdn.us-east-1.elasticbeanstalk.com";
    public static final String relDriverGeofenceUrl = "/driver/geofence";
    public static final String relPedGeofenceUrl = "/ped/geofence";
    public static final String relCrossingRequestUrl = "/ped/requestCrossing";
    public static final String relStartLoggingUrl = "/logs/start";
    public static final String relStopLoggingUrl = "/logs/stop";
    public static final String relStatusUrl = "/ped/status";
    public static final String relDriverDataReportUrl = "/driver/data";
    public static final String relDriverEventReportUrl = "/driver/event";
    public static final String relPedDataReportUrl = "/ped/data";
    public static final String relPedEventReportUrl = "/ped/event";

    // Server parameters
    public static final long dataReportDelay = 200;

    // Notification parameters
    public static final String textToSpeechNotification = "Pedestrian ahead";
    public static final long crossingRequestDuration = 30000;

    // Misc parameters
    public static final long serverHealthTimeout = 500;
    public static final long serverHealthCheckDelay = 3000;
}
