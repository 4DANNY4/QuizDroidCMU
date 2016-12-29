package estg.ipp.pt.quizdroidcmu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HighscoreAnswersAdapter extends ArrayAdapter<Highscore> {

    private Context mContext;
    private ArrayList<Highscore> hList;

    public HighscoreAnswersAdapter(Context context, ArrayList<Highscore> hList) {
        super(context, R.layout.row_list_tophighscoresanswer, hList);
        this.mContext = context;
        this.hList = hList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_list_tophighscoresanswer, null);
        }

        TextView txt_difficulty = (TextView) v.findViewById(R.id.txt_tophighscoreanswer_difficulty);
        TextView txt_playerName = (TextView) v.findViewById(R.id.txt_tophighscoreanswer_playername);
        TextView txt_answerstreak = (TextView) v.findViewById(R.id.txt_tophighscoreanswer_answerstreak);

        txt_difficulty.setText(hList.get(position).getDifficulty().getName());
        txt_playerName.setText(hList.get(position).getPlayerName());
        txt_answerstreak.setText(String.valueOf(hList.get(position).getCorrectAnswers()));

        return v;
    }
}
