package page.chungjungsoo.easyqrcheck.activities

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import page.chungjungsoo.easyqrcheck.R

class PreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
        activity?.setTheme(R.style.preference_theme)
    }
}