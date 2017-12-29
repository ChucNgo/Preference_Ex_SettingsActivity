package android.example.com.visualizerpreferences;

import android.content.SharedPreferences;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;

import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

/**
 * Created by Chức Ngô on 12/28/2017.
 */
public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener,Preference.OnPreferenceChangeListener{
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_visualizer);

        SharedPreferences preferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();

        int count = preferenceScreen.getPreferenceCount();

        for (int i = 0; i < count; i++){
            Preference p = preferenceScreen.getPreference(i);
            // if not CheckBoxPreference
            if (!(p instanceof CheckBoxPreference)){
                // by taking my SharedPreferences object and using getString with the key
                String value = preferences.getString(p.getKey(), "");
                // value lúc này đã đc chọn do người dùng
                setPreferenceSummary(p, value);
            }
        }

        // Sử dụng PreferenceChange để validate Edittext
        Preference preference = findPreference(getString(R.string.pref_size_key));
        preference.setOnPreferenceChangeListener(this);

    }

    private void setPreferenceSummary(Preference preference, final String value) {
        // Check xem preference có nằm trong ListPreference
        if (preference instanceof ListPreference) {
            // cast it into a new listPreference
            ListPreference listPreference = (ListPreference) preference;
            // find the index of the current preference which was selected
            int prefIndex = listPreference.findIndexOfValue(value);
            // prefIndex ở đây là vị trí lấy theo label, ra value
            // check the index is invalid
            if (prefIndex >= 0) {
                // if it is, use this function for my listPreference
                // this is getting the label that is associated with this value
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
            }

        } else {
            final EditTextPreference editTextPreference = (EditTextPreference) preference;
            editTextPreference.setSummary(value);

        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // finding preference by its key
        Preference preference = findPreference(key);

        if (preference != null){
            if (!(preference instanceof CheckBoxPreference)){
                // if it's not null, and not CheckBoxPreference
                // getting the value and set the summary
                String value = sharedPreferences.getString(preference.getKey(), "");
                setPreferenceSummary(preference, value);
            }
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {

        Toast error = Toast.makeText(getContext(), "Please select a number between 0.1 and 3", Toast.LENGTH_SHORT);

        String size_key = getString(R.string.pref_size_key);
        if (preference.getKey().equals(size_key)){

            String stringSize = ((String) newValue).trim();
            if (stringSize == null){
                stringSize = "1";
            }

            try {

                float size = Float.parseFloat(stringSize);
                if (size > 3 || size <= 0){
                    error.show();
                    return false;
                }

            }catch (Exception ex){
                error.show();
                return false;
            }

        }

        return true;
    }
}
