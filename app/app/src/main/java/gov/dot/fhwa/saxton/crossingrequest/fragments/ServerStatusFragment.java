package gov.dot.fhwa.saxton.crossingrequest.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import gov.dot.fhwa.saxton.crossingrequest.R;
import gov.dot.fhwa.saxton.crossingrequest.utils.Constants;

import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relStatusUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.serverBaseUrl;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServerStatusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServerStatusFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Used to display server status on the RoleSelect screen. The status indicator will turn green if
 * connectivity is good, and turn into an X when connectivity is poor.
 */
public class ServerStatusFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ServerStatusFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    protected ImageView statusIcon;
    protected TextView statusText;
    protected final Handler handler = new Handler();
    protected Timer serverStatusTimer = new Timer();
    protected long lastServerCommTime = 0;
    protected AtomicBoolean running = new AtomicBoolean(true);
    protected Runnable asyncRestTask;

    private OnFragmentInteractionListener mListener;

    public ServerStatusFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServerStatusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServerStatusFragment newInstance(String param1, String param2) {
        ServerStatusFragment fragment = new ServerStatusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Constants.serverBaseUrl = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext()).getString("server_url", "http://52.205.207.216");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        asyncRestTask = new Runnable(){
            @Override
            public void run() {
                if (running.get()) {
                    AsyncTask<Void, Void, Void> asyncRestCall = new AsyncTask<Void, Void, Void>() {

                        boolean serverStatus = false;

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            if (running.get()) {
                                Log.i(TAG, "onPostExecute: Setting server status indicator");
                                setServerStatus(serverStatus);
                            }
                        }

                        @Override
                        protected Void doInBackground(Void... voids) {
                            if (running.get()) {
                                MappingJackson2HttpMessageConverter mapper = new MappingJackson2HttpMessageConverter();
                                RestTemplate template = new RestTemplate();
                                template.getMessageConverters().add(mapper);

                                long commsStartTime = System.currentTimeMillis();
                                try {
                                    Boolean b = template.getForObject(serverBaseUrl + relStatusUrl, Boolean.class);

                                    long commsEndTime = System.currentTimeMillis();
                                    Log.i(TAG, "run: Server status check complete. Latency: " + (commsEndTime - commsStartTime));
                                    lastServerCommTime = commsEndTime;
                                    serverStatus = true;
                                } catch (Exception e) {
                                    Log.e(TAG, "doInBackground: Error communicating with server", e);
                                    serverStatus = false;
                                }
                            }

                            return null;
                        }
                    }.execute();
                }
            }
        };


        final TimerTask serverStatusTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(asyncRestTask);
            }
        };
        serverStatusTimer.schedule(serverStatusTask, 0, 1000);
    }

    /**
     * Kill the querying background tasks and prevent more from being spawned. Also prevents their
     * callbacks from being invoked. Used to prevent crashing at the RoleSelectScreen.
     */
    public void stopQuerying() {
        running.set(false);
        handler.removeCallbacks(asyncRestTask);
        serverStatusTimer.cancel();
    }

    /**
     * Update the UI indicator to reflect the current server status.
     * @param serverStatusOk True if the server is connected, False o.w.
     */
    public void setServerStatus(boolean serverStatusOk) {
        if (serverStatusOk) {
            statusIcon.setImageResource(android.R.drawable.presence_online);
            statusText.setText("Server connected!");
        } else {
            statusIcon.setImageResource(android.R.drawable.presence_offline);
            statusText.setText("Server disconnected...");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_server_status, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstance) {
        super.onActivityCreated(savedInstance);
        statusIcon = (ImageView) getView().findViewById(R.id.statusIcon);
        statusText = (TextView) getView().findViewById(R.id.statusText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Set these to null or we leak the activity
        statusIcon = null;
        statusText = null;
        serverStatusTimer.cancel();
        handler.removeCallbacks(asyncRestTask);
        running.set(false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        handler.removeCallbacks(asyncRestTask);
        serverStatusTimer.cancel();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
