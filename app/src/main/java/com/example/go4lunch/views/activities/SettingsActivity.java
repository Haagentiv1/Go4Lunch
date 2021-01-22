package com.example.go4lunch.views.activities;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.example.go4lunch.BaseActivity;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;

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

    private void deleteUserFromFirebase(){
    if (this.getCurrentUser() != null){
        UserHelper.deleteUser(this.getCurrentUser().getUid()).addOnFailureListener(this.onFailureListener());
    }
    }

    private void updateUserNotification(){
        if (this.getCurrentUser() != null){
            UserHelper.updateNotification(this.getCurrentUser().getUid(),mNotification);
        }
    }
}

