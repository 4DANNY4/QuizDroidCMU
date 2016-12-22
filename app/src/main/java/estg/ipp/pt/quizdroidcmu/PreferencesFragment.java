package estg.ipp.pt.quizdroidcmu;


import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference manageQuestions = findPreference("pref_key_manageQuestion");
        manageQuestions.setOnPreferenceClickListener(this);
        Preference manageDifficulties = findPreference("pref_key_manageDifficulties");
        manageDifficulties.setOnPreferenceClickListener(this);
        Preference manageCategories = findPreference("pref_key_manageCategories");
        manageCategories.setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Intent newIntent = new Intent(getActivity(), ListActivity.class);
        if(preference.getKey().equals("pref_key_manageQuestion")) {
            newIntent.putExtra("ItemToList", "questions");
        } else if (preference.getKey().equals("pref_key_manageDifficulties")) {
            newIntent.putExtra("ItemToList", "difficulties");
        } else if (preference.getKey().equals("pref_key_manageCategories")) {
            newIntent.putExtra("ItemToList", "categories");
        }
        startActivity(newIntent);
        return true;
    }
}
