package estg.ipp.pt.quizdroidcmu;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class HighscoreAdapter extends ArrayAdapter<Highscore> {

    private Context mContext;
    private ArrayList<Highscore> hList;

    public HighscoreAdapter(Context context, ArrayList<Highscore> hList) {
        super(context, R.layout.row_list_tophighscores, hList);
        this.mContext = context;
        this.hList = hList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_list_tophighscores, null);
        }

        TextView txt_difficulty = (TextView) v.findViewById(R.id.txt_tophighscore_difficulty);
        TextView txt_playerName = (TextView) v.findViewById(R.id.txt_tophighscore_playername);
        TextView txt_score = (TextView) v.findViewById(R.id.txt_tophighscore_score);

        txt_difficulty.setText(hList.get(position).getDifficulty().getName());
        txt_playerName.setText(hList.get(position).getPlayerName());
        txt_score.setText(String.valueOf(hList.get(position).getScore()));

        return v;
    }
}
