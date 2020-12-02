package gov.dot.fhwa.saxton.crossingrequest.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;

import gov.dot.fhwa.saxton.crossingrequest.R;
import gov.dot.fhwa.saxton.crossingrequest.fragments.PedDataReportTaskFragment;
import gov.dot.fhwa.saxton.crossingrequest.geometry.ConvexPolygonRegion;
import gov.dot.fhwa.saxton.crossingrequest.geometry.Geofence;
import gov.dot.fhwa.saxton.crossingrequest.geometry.GeofenceFactory;
import gov.dot.fhwa.saxton.crossingrequest.messages.CrossingRequestResponse;
import gov.dot.fhwa.saxton.crossingrequest.messages.EventReport;
import gov.dot.fhwa.saxton.crossingrequest.messages.GeofenceDescription;
import gov.dot.fhwa.saxton.crossingrequest.messages.UserRole;
import gov.dot.fhwa.saxton.crossingrequest.utils.Constants;
import gov.dot.fhwa.saxton.crossingrequest.utils.RunningAverageTracker;

import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.defaultMapZoom;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relCrossingRequestUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relPedEventReportUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relPedGeofenceUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relStartLoggingUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relStopLoggingUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.serverBaseUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.serverHealthCheckDelay;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.serverHealthTimeout;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.tfhrcLatLng;

/**
 * The primary class for the Pedestrian mode of the activity. Displays the map, geofence and pedestrian
 * location while providing the pedestrian with server and GPS status information as well as enabling
 * pedestrian crossing requests and manipulation of serverside data logging.
 */
public class PedestrianViewActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, PedDataReportTaskFragment.PedDataReportListener, SensorEventListener {
    private String TAG = "PedestrianViewActivity";
    private GoogleMap mMap;
    protected Location location;
    protected Geofence geofence;
    protected boolean inside = false;
    protected Button requestButton;
    private int permissionsRequestCode = 6301;
    private RunningAverageTracker latencyTracker;
    private long lastServerComms = 0;
    private long lastGpsUpdate = 0;
    protected Marker marker;
    protected final Handler handler = new Handler();
    protected ImageView gpsStatusImageView;
    protected ImageView serverStatusImageView;
    protected ImageView requestStatusImageView;
    protected Runnable asyncServerStatusCheckTask;
    protected PedDataReportTaskFragment dataReportTaskFragment;
    protected String[] trialPattern = { "A" };
    protected int trialIndex = 0;
    protected static final String TAG_PED_DATA_REPORT_TASK_FRAGMENT = "ped_data_report_task_fragment";
    protected boolean flashing = false;
    protected ViewFlipper viewFlipper;
    protected LatLng defaultLocation = Constants.tfhrcLatLng;
    protected boolean waitingForNextStep = false;
    protected SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy-HH-mm-SS");
    protected boolean signalActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constants.serverBaseUrl = PreferenceManager.getDefaultSharedPreferences(this).getString("server_url", "http://52.205.207.216");

        setContentView(R.layout.activity_pedestrian_view);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        dataReportTaskFragment = (PedDataReportTaskFragment) fm.findFragmentByTag(TAG_PED_DATA_REPORT_TASK_FRAGMENT);

        if (dataReportTaskFragment == null) {
            dataReportTaskFragment = new PedDataReportTaskFragment();
            fm.beginTransaction().add(dataReportTaskFragment, TAG_PED_DATA_REPORT_TASK_FRAGMENT).commit();
        }

        reportEvent(EventReport.EventType.PEDESTRIAN_REGISTRATION);

        // Trigger async HTTP request to get geofence from server
        requestButton = (Button) findViewById(R.id.requestButton);
        serverStatusImageView = (ImageView) findViewById(R.id.serverStatusImageView);
        serverStatusImageView.setImageResource(android.R.drawable.presence_offline);

        gpsStatusImageView = (ImageView) findViewById(R.id.gpsStatusImageView);
        gpsStatusImageView.setImageResource(android.R.drawable.presence_offline);

        requestStatusImageView = (ImageView) findViewById(R.id.requestStatusImageView);
        requestStatusImageView.setImageResource(android.R.drawable.presence_offline);

        latencyTracker = new RunningAverageTracker(0.0, 10);
    }

    protected LatLng processLatLngPreference(String latLngPreference) {
        String[] coords = latLngPreference.split(",");
        if (coords.length < 2) {
            return tfhrcLatLng;
        } else {
            try {
                return new LatLng(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
            } catch (NumberFormatException nfe) {
                return tfhrcLatLng;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Constants.darkMode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("night_mode", false);
        defaultLocation = processLatLngPreference(
                PreferenceManager.getDefaultSharedPreferences(this)
                .getString("default_map_location", "38.954920,-77.148812"));
        trialPattern = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("trial_pattern", "A")
                .toUpperCase()
                .replace(" ", "")
                .split(",");


        if (mMap != null) {
            if (Constants.darkMode) {
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.night_mode));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }
            } else {
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = mMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.day_mode));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }
            }
        }

        getGeofenceFromServer();
        scheduleServerStatusChecks();

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

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Constants.darkMode) {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.night_mode));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        } else {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                this, R.raw.day_mode));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }
        }

        // Add a marker in Sydney and move the camera
        marker = mMap.addMarker(new MarkerOptions().position(tfhrcLatLng).title("Turner Fairbanks Highway Research Center"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultMapZoom));
    }

    /**
     * Trigger an async task to fetch the driver's geofence data from the server component
     */
    private void getGeofenceFromServer() {
        AsyncTask<Void, Void, Void> asyncRestCall = new AsyncTask<Void, Void, Void>() {
            protected GeofenceDescription desc;

            @Override
            protected Void doInBackground(Void... voids) {
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(mapper);

                try {
                    desc = template.getForObject(serverBaseUrl + relPedGeofenceUrl, GeofenceDescription.class);
                    Log.i(TAG, "doInBackground: Successfully received driver geofence from server.");
                    Log.i(TAG, "doInBackground: " + desc.toString());
                    geofence = new GeofenceFactory().buildGeofence(desc);
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Unable to get geofence. Please restart application.", Toast.LENGTH_LONG).show();
                        }
                    });
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (desc != null) {
                    renderGeofence(desc);
                }

                serverStatusImageView.setImageResource(android.R.drawable.presence_online);

            }
        }.execute();
    }

    /**
     * Callback for after we request permissions
     *
     * @param requestCode The ID for the request that this is a response to
     * @param permissions The list of permissions we're receiving responses for
     * @param grantResults The list of results
     */
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
     * Display the pedestrian geofence as transmitted by the server. Renders as a google maps polygon
     * with thin black lines.
     *
     * @param geofence The GeofenceDescription sent by the server
     */
    private void renderGeofence(GeofenceDescription geofence) {
        List<PolygonOptions> polys = new ArrayList<>();
        for (ConvexPolygonRegion region : geofence.getRegions()) {
            List<LatLng> latLngs = new ArrayList<>();
            for (gov.dot.fhwa.saxton.crossingrequest.geometry.Location vertex : region.getVertices()) {
                latLngs.add(new LatLng(vertex.getLatitude(), vertex.getLongitude()));
            }
            polys.add(new PolygonOptions().addAll(latLngs));
        }

        for (PolygonOptions poly : polys) {
            mMap.addPolygon(poly);
        }
    }

    /**
     * Schedule background tasks to evaluate the last known communications time with the server and
     * the last known location update from the Location Service. If either are greater than 500ms in
     * the past, change the appropriate status indicator to the negative indicator (X). If not, change
     * them to the positive green circle indicator.
     */
    private void scheduleServerStatusChecks() {
        asyncServerStatusCheckTask = new Runnable() {
            @Override
            public void run() {
                long t = System.currentTimeMillis();
                if (t - lastServerComms > serverHealthTimeout) {
                    serverStatusImageView.setImageResource(android.R.drawable.presence_offline);
                } else {
                    serverStatusImageView.setImageResource(android.R.drawable.presence_online);
                }

                if (t - lastGpsUpdate > serverHealthTimeout) {
                    gpsStatusImageView.setImageResource(android.R.drawable.presence_offline);
                } else {
                    gpsStatusImageView.setImageResource(android.R.drawable.presence_online);
                }

                handler.postDelayed(this, serverHealthCheckDelay);
            }
        };

        handler.postDelayed(asyncServerStatusCheckTask, serverHealthCheckDelay);
    }

    /**
     * Handle when the user presses the request button. Triggers an async HTTP request to the server.
     * @param view
     */
    public void handleRequestButtonPress(View view) {
        Log.i(TAG, "handleRequestButtonPress: Crossing request button pressed.");

        if (!signalActive) {
            if (inside) {
                String trial = trialPattern[trialIndex];
                trialIndex = ++trialIndex % trialPattern.length;

                if (!trial.equals("A")) {
                    flashing = true;
                    viewFlipper.setDisplayedChild(1);
                    AlertDialog.Builder b = new AlertDialog.Builder(this);
                    b.setMessage("Please cross with caution!")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User clicked OK button, dismiss modal
                                }
                            })
                            .show();
                    asyncRequestCrossing(EventReport.EventType.CROSSING_REQUESTED_TRIAL_B);
                } else {
                    asyncRequestCrossing(EventReport.EventType.CROSSING_REQUESTED_TRIAL_A);
                }
                captureNextStepEvent();
            } else {

                Toast.makeText(getApplicationContext(), "Please approach the crosswalk.", Toast.LENGTH_LONG).show();
                Log.i(TAG, "handleRequestButtonPress: Crossing request not sent.");
            }
        } else {
            Log.i(TAG, "handleRequestButtonPress: Crossing request not sent because signal already active.");
        }
    }

    public void captureNextStepEvent() {
        waitingForNextStep = true;
    }

    @Override
    public void onBackPressed() {
        if (!flashing) {
            super.onBackPressed();
        } else {
            viewFlipper.setDisplayedChild(0);
            flashing = false;
        }
    }

    /**
     * Instructs the server to begin a log file at the current date and time.
     */
    private void startLogging() {
        final EditText filenameInput = new EditText(this);
        filenameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        filenameInput.setText(dateFormat.format(new Date()));
        filenameInput.setSelectAllOnFocus(true);

        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    AsyncTask<Void, Void, Void> asyncRestCall = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPostExecute(Void v) {
                            Toast.makeText(getApplicationContext(), "Server logging started.", Toast.LENGTH_LONG).show();
                            Log.i(TAG, "doInBackground: Loggging on server started..");
                            lastServerComms = System.currentTimeMillis();
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                            RestTemplate template = new RestTemplate();
                            template.getMessageConverters().add(mapper);

                            try {
                                String res = template.postForObject(serverBaseUrl + relStartLoggingUrl, filenameInput.getText().toString(), String.class);
                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Error communicating with server.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            return null;
                        }
                    }.execute();
                }
            }
        };

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setView(filenameInput);
        b.setMessage("Start logging on server?")
                .setPositiveButton("Yes", clickListener)
                .setNegativeButton("No", clickListener).show();
    }

    /**
     * Instructs the server to stop logging to the current log file and flush it to disk.
     */
    private void stopLogging() {
        DialogInterface.OnClickListener clickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == DialogInterface.BUTTON_POSITIVE) {
                    AsyncTask<Void, Void, Void> asyncRestCall = new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected void onPostExecute(Void v) {
                            Toast.makeText(getApplicationContext(), "Server logging stopped.", Toast.LENGTH_LONG).show();
                            Log.i(TAG, "doInBackground: Loggging on server stopped..");
                            lastServerComms = System.currentTimeMillis();
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                            RestTemplate template = new RestTemplate();
                            template.getMessageConverters().add(mapper);
                            try {
                                String res = template.getForObject(serverBaseUrl + relStopLoggingUrl, String.class);
                            } catch (Exception e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(getApplicationContext(), "Error communicating with server.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            return null;
                        }
                    }.execute();
                }
            }
        };

        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Stop logging on server?")
                .setPositiveButton("Yes", clickListener)
                .setNegativeButton("No", clickListener).show();
    }

    /**
     * Trigger an async task to request a crossing from the server
     */
    private void asyncRequestCrossing(final EventReport.EventType event) {
        AsyncTask<Void, Void, Void> asyncRestCall = new AsyncTask<Void, Void, Void>() {

            protected CrossingRequestResponse res;

            @Override
            protected void onPostExecute(Void v) {
                if (res.getStatus() == CrossingRequestResponse.CrossingRequestResponseStatus.ACCEPTED) {
                    Log.i(TAG, "doInBackground: Crossing request accepted..");
                    if (!flashing) {
                        Toast.makeText(getApplicationContext(), "Crossing signal broadcasting.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (!flashing) {
                        Toast.makeText(getApplicationContext(), "Crossing signal rejected by server!", Toast.LENGTH_LONG).show();
                    }
                    Log.i(TAG, "doInBackground: Crossing request rejected.");
                }
                lastServerComms = System.currentTimeMillis();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(mapper);

                try {
                    res = template.getForObject(serverBaseUrl + relCrossingRequestUrl, CrossingRequestResponse.class);
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error communicating with server.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                reportEvent(event);

                return null;
            }
        }.execute();
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(asyncServerStatusCheckTask);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.removeUpdates(this);
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensorManager.unregisterListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pedestrian_view_menu, menu);
        return true;
    }

    /**
     * Display the acknowledgements menu, required to use icons from flaticon.com, the source for the
     * vehicle icon used on the Driver mode of this application.
     */
    public void displayAcknowledgements() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Vehicle icon  designed by Freepik from www.flaticon.com used under the Flaticon Free License\n\n" +
                "Role select icons courtesy of Google (https://github.com/material-components) used under the Apache 2.0 License");
        b.show();
    }

    /**
     * Launch the settings activity to adjust user preferences
     */
    private void showSettingsMenu() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.stop_logging_item:
                stopLogging();
                return true;
            case R.id.start_logging_item:
                startLogging();
                return true;
            case R.id.acknowledgements_item:
                displayAcknowledgements();
                return true;
            case R.id.settings_button:
                showSettingsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

        if (dataReportTaskFragment != null) {
            dataReportTaskFragment.updateLocation(location);
        }

        if (geofence != null) {
            if (!inside) {
                inside = geofence.checkInside(new gov.dot.fhwa.saxton.crossingrequest.geometry.Location(
                        location.getLatitude(), location.getLongitude()));
                
                // Check if we entered
                if (inside) {
                    reportEvent(EventReport.EventType.GEOFENCE_ENTERED);
                    lastServerComms = System.currentTimeMillis();
                    requestButton.setEnabled(true);
                }
            } else {
                inside = geofence.checkInside(new gov.dot.fhwa.saxton.crossingrequest.geometry.Location(
                        location.getLatitude(), location.getLongitude()));

                // Check if we exited
                if (!inside) {
                    reportEvent(EventReport.EventType.GEOFENCE_EXITED);
                    lastServerComms = System.currentTimeMillis();
                    requestButton.setEnabled(false);
                }
            }
        }

        // Update status indicator
        gpsStatusImageView.setImageResource(android.R.drawable.presence_online);

        if (marker != null) {
            marker.remove();
        }
        marker = mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())));

        lastGpsUpdate = System.currentTimeMillis();
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
                        UserRole.PEDESTRIAN);

                long commsStartTime = System.currentTimeMillis();
                try {
                    template.postForObject(serverBaseUrl + relPedEventReportUrl, report, Void.class);
                    long commsEndTime = System.currentTimeMillis();
                    lastServerComms = commsEndTime;
                    latencyTracker.addDatapoint(commsEndTime - commsStartTime);

                    Log.i(TAG, "doInBackground: Successfully reported pedestrian event " + typeTmp.toString() + " to server.");
                } catch (Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error communicating with server.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Log.w(TAG, "Error communicating with server");
                }

                return null;
            }
        }.execute();
    }

    @Override
    public void onNotificationActivate() {
        signalActive = true;
        requestStatusImageView.setImageResource(android.R.drawable.presence_online);

        if (!flashing) {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setMessage("Crossing signal is now active. Please cross with caution!")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button, dismiss modal
                        }
                    })
                    .show();
        }

        requestButton.setText("Crossing Signal In Progress");
    }

    @Override
    public void onNotificationDeactivate() {
        signalActive = false;
        requestStatusImageView.setImageResource(android.R.drawable.presence_offline);
        requestButton.setText("Signal Crossing");
    }

    @Override
    public void onNewLastServerComms(long timestamp) {
        lastServerComms = timestamp;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.d(TAG, "Recorded step event!");
        if (waitingForNextStep) {
            reportEvent(EventReport.EventType.PEDESTRIAN_BEGIN_CROSSING);
            waitingForNextStep = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // NO-OP
    }
}
