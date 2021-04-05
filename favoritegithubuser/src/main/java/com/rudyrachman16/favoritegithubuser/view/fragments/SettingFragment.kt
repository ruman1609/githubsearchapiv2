package com.rudyrachman16.favoritegithubuser.view.fragments

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.rudyrachman16.favoritegithubuser.R
import com.rudyrachman16.favoritegithubuser.background.broadcast.AlarmReceiver

class SettingFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var switch: SwitchPreference
    private lateinit var SWITCH: String
    private lateinit var alarmReceiver: AlarmReceiver

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.setting)
        alarmReceiver = AlarmReceiver()
        SWITCH = getString(R.string.key_setting)
        switch = findPreference<SwitchPreference>(SWITCH) as SwitchPreference
        switch.isChecked = alarmReceiver.isAlarmSet(requireContext())
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        println(key)
        if (key == SWITCH) {
            if (switch.isChecked) alarmReceiver.setAlarm(requireContext())
            else alarmReceiver.cancelAlarm(requireContext())
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}