package estg.ipp.pt.quizdroidcmu;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PreferencesFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {

    private String password;
    private String correctPassword = "admin";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference manageQuestions = findPreference("pref_key_manageQuestion");
        manageQuestions.setOnPreferenceClickListener(this);
        Preference manageDifficulties = findPreference("pref_key_manageDifficulties");
        manageDifficulties.setOnPreferenceClickListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.primaryColor));

        return view;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        if(preference.getKey().equals("pref_key_manageQuestion")) {
            passDialog("pref_key_manageQuestion");
        } else if (preference.getKey().equals("pref_key_manageDifficulties")) {
            passDialog("pref_key_manageDifficulties");
        }
        return true;
    }

    private void passDialog(final String str) {
        final AlertDialog.Builder passwordDialog = new AlertDialog.Builder(getActivity());
        final EditText input = new EditText(getActivity());
        input.setSingleLine();
        input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
        final String txt = "Password:";
        passwordDialog.setCancelable(false);
        passwordDialog.setTitle(txt);
        passwordDialog.setView(input);
        passwordDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                password = input.getText().toString().trim();
                if(verifyPassword()){
                    Intent newIntent = new Intent(getActivity(), ListActivity.class);
                    if(str.equals("pref_key_manageQuestion"))
                        newIntent.putExtra("ItemToList", "questions");
                    if(str.equals("pref_key_manageDifficulties"))
                        newIntent.putExtra("ItemToList", "difficulties");
                    startActivity(newIntent);
                }
            }
        });
        passwordDialog.show();
    }

    private boolean verifyPassword() {
        if(password.equals(correctPassword)) {
            Toast.makeText(getActivity(), "Access granted!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getActivity(), "Incorrect password!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
