package gov.dot.fhwa.saxton.crossingrequest.fragments;

import android.app.Activity;
import android.content.Context;
import android.location.Location;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;


import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;


import gov.dot.fhwa.saxton.crossingrequest.messages.NotificationStatus;
import gov.dot.fhwa.saxton.crossingrequest.messages.PedestrianDataReport;
import gov.dot.fhwa.saxton.crossingrequest.utils.RunningAverageTracker;

import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.dataReportDelay;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relDriverDataReportUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relPedDataReportUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.serverBaseUrl;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PedDataReportTaskFragment.PedDataReportListener} interface
 * to handle interaction events.
 * Use the {@link PedDataReportTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PedDataReportTaskFragment extends Fragment {

    private PedDataReportListener attachedContext;
    private PedDataReportTask task;
    private final Handler handler = new Handler();
    private Timer reportTimer = new Timer();
    private String TAG = "PedDataTask";
    private AtomicReference<Location> loc = new AtomicReference<>(null);
    private RunningAverageTracker latencyTracker = new RunningAverageTracker(0.0, 10);
    private boolean notificationActive = false;

    public PedDataReportTaskFragment() {
        // Required empty public constructor
    }

    public static PedDataReportTaskFragment newInstance() {
        PedDataReportTaskFragment fragment = new PedDataReportTaskFragment();
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
        if (context instanceof PedDataReportListener) {
            attachedContext = (PedDataReportListener) context;
            TimerTask reportTask = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            new PedDataReportTask().execute();
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

    public interface PedDataReportListener {
        void onNotificationActivate();
        void onNotificationDeactivate();
        void onNewLastServerComms(long timestamp);
    }

    private class PedDataReportTask extends AsyncTask<Void, Void, Void> {
        private NotificationStatus serverResponse = null;
        private AtomicBoolean running = new AtomicBoolean(true);
        private long lastServerComms = 0;


        @Override
        protected Void doInBackground(Void... voids) {
            if (running.get()) {
                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                RestTemplate template = new RestTemplate();
                template.getMessageConverters().add(mapper);

                try {
                    PedestrianDataReport dataReport;

                    Location location = loc.get();
                    if (location != null) {
                        dataReport = new PedestrianDataReport(
                                System.currentTimeMillis(),
                                new gov.dot.fhwa.saxton.crossingrequest.geometry.Location(
                                        location.getLatitude(), location.getLongitude()),
                                (location.hasAccuracy() ? location.getAccuracy() : -1.0),
                                latencyTracker.getAverage());
                    } else {
                        dataReport = new PedestrianDataReport(
                                System.currentTimeMillis(),
                                new gov.dot.fhwa.saxton.crossingrequest.geometry.Location(
                                        0.0, 0.0),
                                -1.0,
                                latencyTracker.getAverage());
                    }

                    Log.i(TAG, "doInBackground: Sent " + dataReport + " to server.");

                    long commsStartTime = System.currentTimeMillis();
                    try {
                        serverResponse = template.postForObject(
                                serverBaseUrl + relPedDataReportUrl,
                                dataReport,
                                NotificationStatus.class);

                        long commsEndTime = System.currentTimeMillis();
                        latencyTracker.addDatapoint(commsEndTime - commsStartTime);
                        Log.i(TAG, "run: Pedestrian data report complete. Latency: " + (commsEndTime - commsStartTime));
                        lastServerComms = commsEndTime;
                    } catch (Exception e) {
                        Log.w(TAG, "Error communicating with server");
                    }

                } catch (Exception e) {
                    Log.e(TAG, "doInBackground: Error communicating with server", e);
                }
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
