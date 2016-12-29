package estg.ipp.pt.quizdroidcmu;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;

public class HelpPublicDialogFragment extends DialogFragment {

    TextView txt_Answer1Dialog, txt_Answer2Dialog, txt_Answer3Dialog, txt_Answer4Dialog;
    ProgressBar answer1Dialog_Progress, answer2Dialog_Progress, answer3Dialog_Progress, answer4Dialog_Progress;
    TextView txt_1DialogProgress, txt_2DialogProgress, txt_3DialogProgress, txt_4DialogProgress;
    Button btn_EndDialog;

    private String[] answers;
    int[] progress;

    public HelpPublicDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_help_public_dialog, container, false  );
        txt_Answer1Dialog = (TextView) view.findViewById(R.id.txtAnswer1Dialog);
        txt_Answer2Dialog = (TextView) view.findViewById(R.id.txtAnswer2Dialog);
        txt_Answer3Dialog = (TextView) view.findViewById(R.id.txtAnswer3Dialog);
        txt_Answer4Dialog = (TextView) view.findViewById(R.id.txtAnswer4Dialog);

        txt_1DialogProgress = (TextView) view.findViewById(R.id.txt1DialogProgress);
        txt_2DialogProgress = (TextView) view.findViewById(R.id.txt2DialogProgress);
        txt_3DialogProgress = (TextView) view.findViewById(R.id.txt3DialogProgress);
        txt_4DialogProgress = (TextView) view.findViewById(R.id.txt4DialogProgress);

        answer1Dialog_Progress = (ProgressBar) view.findViewById(R.id.answer1DialogProgress);
        answer2Dialog_Progress = (ProgressBar) view.findViewById(R.id.answer2DialogProgress);
        answer3Dialog_Progress = (ProgressBar) view.findViewById(R.id.answer3DialogProgress);
        answer4Dialog_Progress = (ProgressBar) view.findViewById(R.id.answer4DialogProgress);

        btn_EndDialog = (Button) view.findViewById(R.id.btnEndDialog);
        btn_EndDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setTitle("Public Help");

        for (int i = 0; i < answers.length; i++){
            //String[] splitAnswer = answers[i].split("\\s+");
            switch (i){
                case 0:
                    txt_Answer1Dialog.setText(answers[i].toString());
                    answer1Dialog_Progress.incrementProgressBy(progress[i]);
                    txt_1DialogProgress.setText("" + progress[i] + " %");
                    break;
                case 1:
                    txt_Answer2Dialog.setText(answers[i].toString());
                    answer2Dialog_Progress.incrementProgressBy(progress[i]);
                    txt_2DialogProgress.setText("" + progress[i] + " %");
                    break;
                case 2:
                    txt_Answer3Dialog.setText(answers[i].toString());
                    answer3Dialog_Progress.incrementProgressBy(progress[i]);
                    txt_3DialogProgress.setText("" + progress[i] + " %");
                    break;
                case 3:
                    txt_Answer4Dialog.setText(answers[i].toString());
                    answer4Dialog_Progress.incrementProgressBy(progress[i]);
                    txt_4DialogProgress.setText("" + progress[i] + " %");
                    break;
                default:
                    break;
            }
        }

        return view;
    }

    public void setAnswers(String[] answers, int[] progress){
        this.answers = answers;
        this.progress = progress;
    }
}
