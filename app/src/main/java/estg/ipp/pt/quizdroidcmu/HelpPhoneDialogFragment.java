package estg.ipp.pt.quizdroidcmu;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.MediaDescriptionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import estg.ipp.pt.quizdroidcmu.QuizActivity;
import estg.ipp.pt.quizdroidcmu.R;

public class HelpPhoneDialogFragment extends DialogFragment{

    private TextView txt_HelpPhoneFrag;
    private Button btn_HelpPhone1, btn_HelpPhone2, btn_HelpPhone3, btn_HelpPhone4;
    final ArrayList<String> contacts = new ArrayList<>();
    QuizActivity quiz;

    public HelpPhoneDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        quiz = (QuizActivity) getActivity();

        final String txt = "Who would you like to contact:";

        contacts.add("Albert Einstein"); // 100%
        contacts.add("Doge"); // 80%
        contacts.add("Harambe"); //55%
        contacts.add("Donald Trump"); // 20%
        contacts.add("SID"); // 1%
        contacts.add("Potato"); // 90%
        contacts.add("Waifu"); // 75%
        Collections.shuffle(contacts);

        View view = inflater.inflate(R.layout.fragment_help_phone_dialog, container, false  );
        txt_HelpPhoneFrag = (TextView) view.findViewById(R.id.txtHelpPhoneFrag);
        txt_HelpPhoneFrag.setText(txt);
        btn_HelpPhone1 = (Button) view.findViewById(R.id.btnHelpPhone1);
        btn_HelpPhone1.setText(contacts.get(0).toString());
        btn_HelpPhone1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quiz.contacts(contacts.get(0).toString());
                dismiss();
            }
        });
        btn_HelpPhone2 = (Button) view.findViewById(R.id.btnHelpPhone2);
        btn_HelpPhone2.setText(contacts.get(1).toString());
        btn_HelpPhone2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quiz.contacts(contacts.get(1).toString());
                dismiss();
            }
        });
        btn_HelpPhone3 = (Button) view.findViewById(R.id.btnHelpPhone3);
        btn_HelpPhone3.setText(contacts.get(2).toString());
        btn_HelpPhone3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quiz.contacts(contacts.get(2).toString());
                dismiss();
            }
        });
        btn_HelpPhone4 = (Button) view.findViewById(R.id.btnHelpPhone4);
        btn_HelpPhone4.setText(contacts.get(3).toString());
        btn_HelpPhone4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quiz.contacts(contacts.get(3).toString());
                dismiss();
            }
        });
        //getDialog().setTitle("Phone Help");

        return view;
    }
}
