package gov.dot.fhwa.saxton.crossingrequest.activities;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import gov.dot.fhwa.saxton.crossingrequest.R;

/**
 * Basic preference screen activity using PreferenceFragment
 */
public class PreferencesActivity extends Activity {


    @Override
    protected void onCreate (Bundle savedState) {
        super.onCreate(savedState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new BasicPreferenceFragment()).commit();
    }

    // Internal super-basic preference fragment class
    public static class BasicPreferenceFragment extends PreferenceFragment {

        public void onCreate(Bundle savedState) {
            super.onCreate(savedState);
            addPreferencesFromResource(R.xml.pref_general);
        }
    }
}
