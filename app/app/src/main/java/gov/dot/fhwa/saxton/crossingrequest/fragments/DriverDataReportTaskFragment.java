package gov.dot.fhwa.saxton.crossingrequest.fragments;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import gov.dot.fhwa.saxton.crossingrequest.messages.DriverDataReport;
import gov.dot.fhwa.saxton.crossingrequest.messages.NotificationStatus;
import gov.dot.fhwa.saxton.crossingrequest.utils.RunningAverageTracker;

import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.dataReportDelay;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relDriverDataReportUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.serverBaseUrl;

/**
 * Persistent fragment for handling background data-reporting tasks in the motorist activity
 * <p>
 * Requires that the using activity implement DriverDataReportListener to be able to feed this
 * fragment the proper data and handle the data flow back into the activity.
 */
public class DriverDataReportTaskFragment extends Fragment {

    private DriverDataReportListener attachedContext;
    private DriverDataReportTask task;
    private final Handler handler = new Handler();
    private Timer reportTimer = new Timer();
    private String TAG = "PedDataTask";
    private AtomicReference<Location> loc = new AtomicReference<>(null);
    private RunningAverageTracker latencyTracker = new RunningAverageTracker(0.0, 10);
    private boolean notificationActive = false;

    public DriverDataReportTaskFragment() {
        // Required empty public constructor
    }

    public static DriverDataReportTaskFragment newInstance() {
        DriverDataReportTaskFragment fragment = new DriverDataReportTaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DriverDataReportListener) {
            attachedContext = (DriverDataReportListener) context;
            TimerTask reportTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new DriverDataReportTask().execute();
                        }
                    });
                }
            };
            reportTimer.scheduleAtFixedRate(reportTask, 0, dataReportDelay);
        } else {
            throw new RuntimeException("Cannot attach PedDataReportTaskFragmnet to context!");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        attachedContext = null;
        reportTimer.cancel();
    }

    public void updateLocation(Location loc) {
        this.loc.set(loc);
    }

    /**
     * Interface which activities must implement to attach to this fragment
     */
    public interface DriverDataReportListener {
        void onNotificationActivate();
        void onNotificationDeactivate();
        void onNewLastServerComms(long timestamp);
    }

    /**
     * AsyncTask implementation for actually sending the driver data reports back to the server
     */
    private class DriverDataReportTask extends AsyncTask<Void, Void, Void> {
        private NotificationStatus serverResponse = null;
        private AtomicBoolean running = new AtomicBoolean(true);
        private long lastServerComms = 0;


        @Override
        protected Void doInBackground(Void... voids) {
            MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
            RestTemplate template = new RestTemplate();
            template.getMessageConverters().add(mapper);

            try {
                DriverDataReport dataReport;
                Location curLoc = loc.get();
                if (curLoc != null) {
                    dataReport = new DriverDataReport(
                            System.currentTimeMillis(),
                            new gov.dot.fhwa.saxton.crossingrequest.geometry.Location(
                                    curLoc.getLatitude(), curLoc.getLongitude()),
                            curLoc.getSpeed(),
                            curLoc.getBearing(),
                            (curLoc.hasAccuracy() ? curLoc.getAccuracy(): -1.0),
                            -1.0,
                            -1.0,
                            latencyTracker.getAverage());
                } else {
                    dataReport = new DriverDataReport(
                            System.currentTimeMillis(),
                            new gov.dot.fhwa.saxton.crossingrequest.geometry.Location(
                                    0.0, 0.0),
                            -1.0,
                            -1.0,
                            -1.0,
                            -1.0,
                            -1.0,
                            latencyTracker.getAverage());
                }

                Log.i(TAG, "doInBackground: Sent " + dataReport + " to server.");

                long commsStartTime = System.currentTimeMillis();
                serverResponse = template.postForObject(
                        serverBaseUrl + relDriverDataReportUrl,
                        dataReport,
                        NotificationStatus.class);

                long commsEndTime = System.currentTimeMillis();
                latencyTracker.addDatapoint(commsEndTime - commsStartTime);
                Log.i(TAG, "run: Geofence status check complete. Latency: " + (commsEndTime - commsStartTime));
                Log.i(TAG, "doInBackground: Crossing request status determined to be " + serverResponse);
                lastServerComms = commsEndTime;
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Error communicating with server", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            if (attachedContext != null) {
                if (!notificationActive && serverResponse != null && serverResponse.isActive()) {
                    notificationActive = true;
                    attachedContext.onNotificationActivate();
                } else if (notificationActive  && serverResponse != null && !serverResponse.isActive()) {
                    notificationActive = false;
                    attachedContext.onNotificationDeactivate();
                }

                attachedContext.onNewLastServerComms(lastServerComms);
            }
        }
    }
}

