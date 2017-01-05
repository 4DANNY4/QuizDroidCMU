package estg.ipp.pt.quizdroidcmu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Tiago Fernandes on 05/01/2017.
 */

public class GameAdapter extends ArrayAdapter<Game>{

    private Context mContext;
    private ArrayList<Game> gList;

    public GameAdapter(Context context, ArrayList<Game> gList) {
        super(context, R.layout.row_list_continue_game, gList);
        this.mContext = context;
        this.gList = gList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_list_continue_game, null);
        }

        TextView txt_playerName = (TextView) v.findViewById(R.id.txt_continue_playerName);
        TextView txt_gameType = (TextView) v.findViewById(R.id.txt_continue_gametype);
        TextView txt_difficulty = (TextView) v.findViewById(R.id.txt_continue_difficulty);
        TextView txt_score = (TextView) v.findViewById(R.id.txt_continue_score);
        TextView txt_answers = (TextView) v.findViewById(R.id.txt_continue_answers);

        txt_playerName.setText(gList.get(position).getHighScore().getPlayerName());
        if(gList.get(position).getHighScore().isUnlimited()) {
            txt_gameType.setText("Unlimited");
            txt_answers.setVisibility(View.VISIBLE);
        } else {
            txt_gameType.setText("Normal");
            txt_score.setVisibility(View.VISIBLE);
        }
        txt_difficulty.setText(gList.get(position).getHighScore().getDifficulty().getName());
        txt_score.setText(String.valueOf(gList.get(position).getHighScore().getScore()));
        txt_answers.setText(String.valueOf(gList.get(position).getHighScore().getCorrectAnswers()));

        return v;
    }
}
