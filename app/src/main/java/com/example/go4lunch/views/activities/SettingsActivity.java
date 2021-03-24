package com.example.go4lunch.views.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceFragmentCompat;

import com.example.go4lunch.BaseActivity;
import com.example.go4lunch.R;

public class SettingsActivity extends BaseActivity {

    private Boolean mNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }


    }

}

