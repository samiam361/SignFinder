package com.example.signfinder;

import android.content.SharedPreferences;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import static com.example.signfinder.LoginActivity.editor;

public class SettingsActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();

    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        public Preference notif, radius, stayLogged, username;

        //Initializes all preferences on the preference screen
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            PreferenceScreen screen = getPreferenceScreen();

            username = findPreference("username_setting");

            screen.removePreference(username);

            //Change listener for the notifications switch
            notif = findPreference("notifications_switch");
            notif.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    editor.putBoolean("notifications_switch", (boolean) newValue);
                    editor.commit();
                    return true;
                }
            });

            //Change listener for the radius list
            radius = findPreference("search_radius");
            radius.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    editor.putString("search_radius", (String) newValue);
                    editor.commit();
                    return true;
                }
            });

            //Change listener for the stay logged in tick box
            stayLogged = findPreference("stay_logged");
            stayLogged.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    editor.putBoolean("stay_logged", (boolean) newValue);
                    editor.commit();
                    return true;
                }
            });
        }
    }
}
