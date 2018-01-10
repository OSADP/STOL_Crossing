package gov.dot.fhwa.saxton.crossingrequest.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import gov.dot.fhwa.saxton.crossingrequest.R;
import gov.dot.fhwa.saxton.crossingrequest.fragments.ServerStatusFragment;

/**
 * Initial activity displayed to the user when app is started, allows user to select between
 * pedestrian or motorist roles for the purpose of experimentation.
 */
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

    /**
     * Invoked when the user presses the Pedestrian button, loads the pedestrian activity
     */
    public void onSelectPedRole(View view) {
        Log.i(TAG, "onSelectPedRole: Selected ped role for application");
        Intent intent = new Intent(this, PedestrianViewActivity.class);
        serverStatusFragment.stopQuerying();
        startActivity(intent);
    }

    /**
     * Invoked when the user presses the Motorist button, loads the motorist activity
     */
    public void onSelectDriverRole(View view) {
        Intent intent = new Intent(this, DriverViewActivity.class);
        serverStatusFragment.stopQuerying();
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        // STUB
    }
}
