package gov.dot.fhwa.saxton.crossingrequest.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;

import gov.dot.fhwa.saxton.crossingrequest.R;
import gov.dot.fhwa.saxton.crossingrequest.fragments.ServerStatusFragment;
import gov.dot.fhwa.saxton.crossingrequest.messages.CrossingRequestResponse;

import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relCrossingRequestUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.relStatusUrl;
import static gov.dot.fhwa.saxton.crossingrequest.utils.Constants.serverBaseUrl;

public class RoleSelectActivity extends AppCompatActivity implements ServerStatusFragment.OnFragmentInteractionListener {

    protected Button pedestrianSelectButton;
    protected Button driverSelectButton;
    protected ServerStatusFragment serverStatusFragment;
    protected String TAG = "RoleSelectActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate: Created role select activity!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_select);
        pedestrianSelectButton = (Button) findViewById(R.id.pedSelectButton);
        driverSelectButton = (Button) findViewById(R.id.driverSelectButton);
        serverStatusFragment = (ServerStatusFragment) getFragmentManager().findFragmentById(R.id.serverStatusFragment);
    }

    public void onSelectPedRole(View view) {
        Log.i(TAG, "onSelectPedRole: Selected ped role for application");
        Intent intent = new Intent(this, PedestrianViewActivity.class);
        serverStatusFragment.stopQuerying();
        startActivity(intent);
    }

    public void onSelectDriverRole(View view) {
        Intent intent = new Intent(this, DriverViewActivity.class);
        serverStatusFragment.stopQuerying();
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
