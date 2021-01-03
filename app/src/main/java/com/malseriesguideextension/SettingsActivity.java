package com.malseriesguideextension;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.malseriesguideextension.helpers.ThemeHelper;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyThemePreference(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference uiThemePreference = findPreference("ui_theme");
            if (uiThemePreference != null) {
                uiThemePreference.setEntries(R.array.ui_theme_entries);
                uiThemePreference.setEntryValues(R.array.ui_theme_values);
            }

            // Listen for changes in settings.
            SharedPreferences.OnSharedPreferenceChangeListener listener =
                    new SharedPreferences.OnSharedPreferenceChangeListener() {
                        @Override
                        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                            if (key.equals("ui_theme")) {
                                ThemeHelper.applyThemePreference(sharedPreferences);
                            }
                        }
                    };

            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
        }
    }
}
