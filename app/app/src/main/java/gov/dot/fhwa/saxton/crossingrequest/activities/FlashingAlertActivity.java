package gov.dot.fhwa.saxton.crossingrequest.activities;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import gov.dot.fhwa.saxton.crossingrequest.FlashingView;

public class FlashingAlertActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new FlashingView(getApplicationContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setMessage("Please cross with caution!")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button, dismiss modal
                    }
                })
                .show();
    }
}
