package com.robotca.ControlApp.Fragments;

import android.preference.PreferenceFragment;
import android.os.Bundle;
import com.robotca.ControlApp.R;



/**
 * Fragment containing the Map screen showing the real-world position of the Robot.
 *
 */
public class HostsFragment extends PreferenceFragment {

    // Log tag String
    private static final String TAG = "HostsFragment";


    /**
     * Default Constructor.
     */
    public HostsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null)
            return;

        // Add the preferences
        addPreferencesFromResource(R.xml.hosts);
    }

  



}

