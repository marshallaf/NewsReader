package xyz.marshallaf.newsreader;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Activity and fragment to set and view user settings.
 *
 * Created by Andrew Marshall on 1/18/2017.
 */

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // load preferences from xml resource
            addPreferencesFromResource(R.xml.preferences);

            // get references to preference values
            Preference orderBy = findPreference("order_by");
            Preference searchTerm = findPreference("search_term");

            // bind the summary values to preference values
            bindSummaryToValue(orderBy);
            bindSummaryToValue(searchTerm);
        }

        // initializes the summary values and binds the change listener
        private void bindSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);

            // get the string value of passed preference
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String prefVal = preferences.getString(preference.getKey(), "");

            // set summary text
            onPreferenceChange(preference, prefVal);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object prefVal) {
            if (preference instanceof ListPreference) {
                // if preference is a list preference, use the preference entry as the summary
                ListPreference listPreference = (ListPreference) preference;
                int i = listPreference.findIndexOfValue(prefVal.toString());
                if (i >= 0) {
                    preference.setSummary(listPreference.getEntries()[i]);
                }
            } else {
                // otherwise use the value
                preference.setSummary(prefVal.toString());
            }

            return true;
        }
    }
}
