package gov.dot.fhwa.saxton.crossingrequest.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicBoolean;

import gov.dot.fhwa.saxton.crossingrequest.R;
import gov.dot.fhwa.saxton.crossingrequest.fragments.DriverDataReportTaskFragment;
import gov.dot.fhwa.saxton.crossingrequest.fragments.DriverNotificationFragment;
import gov.dot.fhwa.saxton.crossingrequest.geometry.Geofence;
import gov.dot.fhwa.saxton.crossingrequest.geometry.GeofenceFactory;
import gov.dot.fhwa.saxton.crossingrequest.messages.EventReport;
import gov.dot.fhwa.saxton.crossingrequest.messages.GeofenceDescription;
import gov.dot.fhwa.saxton.crossingrequest.messages.UserRole;
import gov.dot.fhwa.saxton.crossingrequest.utils.RunningAverageTracker;

import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.defaultMapZoom;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relDriverEventReportUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relDriverGeofenceUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.serverBaseUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.textToSpeechNotification;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.tfhrcLatLng;

/**
 * Primary activity for the Driver mode of the application.
 *
 * Handles management of GPS data, data reporting, driver notification, event detection, server event
 * reporting, map display, latency measurement.
 */
public class DriverViewActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, DriverNotificationFragment.OnFragmentInteractionListener, TextToSpeech.OnInitListener, DriverDataReportTaskFragment.DriverDataReportListener {
    private GoogleMap mMap;
    protected Location curLoc;
    protected String TAG = "DriverViewActivity";
    private int permissionsRequestCode = 6300;
    private Geofence driverGeofence;
    private DriverNotificationFragment notificationFragment;
    private TextToSpeech tts;
    private boolean ttsInited = false;
    private MediaPlayer mediaPlayer;
    private boolean inside = false;
    protected long lastServerCommTime = 0;
    protected AtomicBoolean crossingRequestStatus = new AtomicBoolean(false);
    protected AtomicBoolean alertStatus = new AtomicBoolean(false);
    protected Marker vehMarker;
    protected RunningAverageTracker latencyTracker = new RunningAverageTracker(0, 10);
    protected DriverDataReportTaskFragment dataReportTaskFragment;
    private static final String TAG_DRIVER_DATA_REPORT_TASK_FRAGMENT = "driver_data_report_task_fragment";

    /**
     * Init method for this activity, loads the necessary data and instantiates objects
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_view);

        tts = new TextToSpeech(this, this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        dataReportTaskFragment = (DriverDataReportTaskFragment) fm.findFragmentByTag(TAG_DRIVER_DATA_REPORT_TASK_FRAGMENT);

        if (dataReportTaskFragment == null) {
            dataReportTaskFragment = new DriverDataReportTaskFragment();
            fm.beginTransaction().add(dataReportTaskFragment, TAG_DRIVER_DATA_REPORT_TASK_FRAGMENT).commit();
        }

        reportEvent(EventReport.EventType.MOTORIST_REGISTRATION);

        getGeofenceFromServer();

        notificationFragment = ((DriverNotificationFragment) getFragmentManager().findFragmentById(R.id.driverNotificationFragment));
        getFragmentManager().beginTransaction().hide(notificationFragment).commitAllowingStateLoss();

        mediaPlayer = MediaPlayer.create(this, R.raw.toneb_left);
    }

    /**
     * Cause the notification to display. If called with the notification already displayed another
     * notification will display below it. Must be dismissed with the dismissNotification method.
     *
     * Also causes an auditory alert in the form of 3 beeps, a text to speech "Pedestrian Ahead" message,
     * followed by 3 more beeps.
     */
    private void triggerNotification() {
        // Show alert pane
        getFragmentManager().beginTransaction().show(notificationFragment).commitAllowingStateLoss();

        // Play sound
        mediaPlayer.start();

        // Play text to speech
        if (ttsInited) {
            int status = tts.speak(textToSpeechNotification, TextToSpeech.QUEUE_ADD, null, "alert");
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String s) {

                }

                @Override
                public void onDone(String s) {
                    mediaPlayer.seekTo(0);
                    mediaPlayer.start();
                }

                @Override
                public void onError(String s) {

                }
            });
            Log.i(TAG, "recomputeNotification: TTS queued with status " + status);
        } else {
            Log.w(TAG, "recomputeNotification: Text to speech requested but engine not yet initialized!");
        }
        Log.i(TAG, "recomputeNotification: Pedestrian crossing alert delivered to driver!");
        alertStatus.set(true);
    }

    /**
     * Checks our current status with regards to the geofence position and notification status to see
     * if we need to trigger the alert to the driver.
     */
    private void recomputeNotification() {
        if (crossingRequestStatus.get() && inside && !alertStatus.get()) {
            Log.i(TAG, "recomputeNotification: Triggering notification!");
            triggerNotification();
        } else if (!inside || !crossingRequestStatus.get()) {
            Log.i(TAG, "recomputeNotification: Resetting notification.");
            getFragmentManager().beginTransaction().hide(notificationFragment).commitAllowingStateLoss();
            alertStatus.set(false);
        }
    }

    /**
     * Trigger an async task to fetch the driver's geofence data from the server component
     */
    private void getGeofenceFromServer() {
        AsyncTask<Void, Void, Void> asyncRestCall = new AsyncTask<Void, Void, Void>() {
            GeofenceDescription desc = null;

            @Override
            protected Void doInBackground(Void... voids) {
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(mapper);

                long commsStartTime = System.currentTimeMillis();
                desc = template.getForObject(serverBaseUrl + relDriverGeofenceUrl, GeofenceDescription.class);
                long commsEndTime = System.currentTimeMillis();
                latencyTracker.addDatapoint(commsEndTime - commsStartTime);

                driverGeofence = new GeofenceFactory().buildGeofence(desc);
                Log.i(TAG, "doInBackground: Successfully received driver geofence from server.");
                Log.i(TAG, "doInBackground: " + driverGeofence.toString());
                return null;
            }
            @Override
            public void onPostExecute(Void v) {
                // NO-OP
            }
        }.execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Check for fine location permissions failed.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.INTERNET},
                    permissionsRequestCode);
        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Log.i(TAG, "onCreate: Registered request for location updates.");
        }
    }

    /**
     * Callback for after we request permissions
     *
     * @param requestCode The ID for the request that this is a response to
     * @param permissions The list of permissions we're receiving responses for
     * @param grantResults The list of results
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == permissionsRequestCode) {
            boolean res = true;
            for (int result : grantResults) {
                res = res && (result == PackageManager.PERMISSION_GRANTED);
            }

            if (res) {
                Log.i(TAG, "onRequestPermissionsResult: Permissions granted for INTERNET and FINE_LOCATION");
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                //noinspection MissingPermission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
                Log.i(TAG, "onCreate: Registered request for location updates.");
            } else {
                Log.e(TAG, "onRequestPermissionsResult: Permission denied for INTERNET and FINE_LOCATION");
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(TAG, "onMapReady: Map is ready ");
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // mMap.addMarker(new MarkerOptions().position(tfhrcLatLng).title("Turner Fairbanks Highway Research Center"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tfhrcLatLng, defaultMapZoom));
    }

    /**
     * Interface method to be called whenever we receive new location data.
     * @param location The new updated location value
     */
    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged: Received new location data.");
        curLoc = location;
        dataReportTaskFragment.updateLocation(location);
        if (mMap != null) {
            // If map is initialized, set our location and heading
            centerCameraOnPoint(curLoc);
            drawVehicleIcon(curLoc);
            Log.i(TAG, "onLocationChanged: Updated map rendering.");
        }

        if (driverGeofence != null) {
            if (!inside) {
                inside = driverGeofence.checkInside(new gov.dot.fhwa.saxton.crossingrequest.geometry.Location(
                        location.getLatitude(), location.getLongitude()));

                if (curLoc.hasBearing()) {
                    boolean oriented = driverGeofence.testHeading(new gov.dot.fhwa.saxton.crossingrequest.geometry.Location(
                            location.getLatitude(), location.getLongitude()), curLoc.getBearing());

                    inside = inside && oriented;
                }

                if (inside) {
                    Log.i(TAG, "onLocationChanged: Reporting GEOFENCE_ENTERED event to server.");
                    reportEvent(EventReport.EventType.GEOFENCE_ENTERED);
                    driverGeofence.getEntranceHeading();
                }
            } else {
                inside = driverGeofence.checkInside(new gov.dot.fhwa.saxton.crossingrequest.geometry.Location(
                        location.getLatitude(), location.getLongitude()));

                if (curLoc.hasBearing()) {
                    boolean oriented = driverGeofence.testHeading(new gov.dot.fhwa.saxton.crossingrequest.geometry.Location(
                            location.getLatitude(), location.getLongitude()), curLoc.getBearing());

                    inside = inside && oriented;
                }

                if (!inside) {
                    Log.i(TAG, "onLocationChanged: Reporting GEOFENCE_EXITED event to server.");
                    reportEvent(EventReport.EventType.GEOFENCE_EXITED);
                }
            }
        }

        recomputeNotification();
    }

    /**
     * Move the camera to a location and rotate to the correct bearing. Also will adjust the zoom
     * level if it has been modified.
     *
     * @param location The location to center on.
     */
    private void centerCameraOnPoint(Location location) {
        if (mMap != null) {
            // If map is initialized, set our location and heading
            CameraPosition target = CameraPosition.builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                    .bearing(location.getBearing())
                    .zoom(defaultMapZoom)
                    //.tilt(0)
                    .build();

            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
        }
    }

    /**
     * Draw the triangle representing the vehicle onto the GoogleMap
     * @param loc The location of the vehicle
     */
    private void drawVehicleIcon(Location loc) {
        if (vehMarker != null) {
            vehMarker.remove();
        }

        if (mMap != null) {
            vehMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(loc.getLatitude(), loc.getLongitude()))
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.veh_icon)));
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        // STUB
    }

    @Override
    public void onProviderEnabled(String s) {
        // STUB
    }

    @Override
    public void onProviderDisabled(String s) {
        // STUB
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // STUB
    }

    /**
     * Callback invoked once the text-to-speech engine is ready for usage
     */
    @Override
    public void onInit(int i) {
        ttsInited = true;
        Log.i(TAG, "onInit: Text to speech engine initialized!");
    }

    /**
     * Asynchronously report to the server that the application has detected an event as defined by
     * the EventReport.EventType value
     * @param type The type of event detected
     */
    protected void reportEvent(final EventReport.EventType type) {
        final EventReport.EventType typeTmp = type;
        AsyncTask<Void, Void, Void> asyncRestCall = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(mapper);


                EventReport report = new EventReport(System.currentTimeMillis(),
                        typeTmp,
                        UserRole.MOTORIST);

                long commsStartTime = System.currentTimeMillis();
                template.postForObject(serverBaseUrl + relDriverEventReportUrl, report, Void.class);
                long commsEndTime = System.currentTimeMillis();
                latencyTracker.addDatapoint(commsEndTime - commsStartTime);

                Log.i(TAG, "doInBackground: Successfully reported driver event " + typeTmp.toString() + " to server.");
                return null;
            }
        }.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tts.shutdown();
    }

    @Override
    public void onNotificationActivate() {
        crossingRequestStatus.set(true);
        recomputeNotification();
    }

    @Override
    public void onNotificationDeactivate() {
        crossingRequestStatus.set(false);
        recomputeNotification();
    }

    @Override
    public void onNewLastServerComms(long timestamp) {
        lastServerCommTime = timestamp;
    }
}
