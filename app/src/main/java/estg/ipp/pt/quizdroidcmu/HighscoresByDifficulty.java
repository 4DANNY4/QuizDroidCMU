package estg.ipp.pt.quizdroidcmu;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class HighscoresByDifficulty extends ListFragment implements View.OnClickListener {

    private Context mContext;
    private ArrayList<Highscore> hList = new ArrayList<>();
    private ArrayAdapter hAdapter = null;
    private ArrayList<Difficulty> difList = new ArrayList<>();

    private TextView txt_difname;
    private Button btn_HighscoreNext;
    private Button btn_HighscorePrevious;

    private Difficulty difficulty;

    public HighscoresByDifficulty() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        hAdapter = new HighscoreByDifficultyAdapter(mContext, hList);
        setListAdapter(hAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDifficulties();
        setdefault();
        getHighscores();
        hAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.fragment_highscores_by_difficulty, container, false);

        btn_HighscoreNext = (Button) mContentView.findViewById(R.id.btnHighscoresNext);
        btn_HighscoreNext.setOnClickListener(this);
        btn_HighscorePrevious = (Button) mContentView.findViewById(R.id.btnHighscoresPrevious);
        btn_HighscorePrevious.setOnClickListener(this);

        txt_difname = (TextView) mContentView.findViewById(R.id.txt_tophighscore_difname);

        return mContentView;
    }

    @Override
    public void onClick(View v) {
        int dPos = -1;
        // Getting difficulty position
        for(int i=0; i<difList.size(); i++) {
            if(difList.get(i).getId() == difficulty.getId()) {
                dPos = i;
                break;
            }
        }
        if (v.getId() == R.id.btnHighscoresNext){
            if(dPos == difList.size()-1){
                difficulty = difList.get(0);
                txt_difname.setText(difficulty.getName());
            } else {
                difficulty = difList.get(dPos+1);
                txt_difname.setText(difficulty.getName());
            }
            getHighscores();
            hAdapter.notifyDataSetChanged();
        }else if(v.getId() == R.id.btnHighscoresPrevious){
            if(dPos == 0){
                difficulty = difList.get(difList.size() - 1);
                txt_difname.setText(difficulty.getName());
            }else {
                difficulty = difList.get(dPos-1);
                txt_difname.setText(difficulty.getName());
            }
            getHighscores();
            hAdapter.notifyDataSetChanged();
        }
    }

    private void getDifficulties() {
        difList.clear();

        QdDbHelper dbHelper = new QdDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM tblDifficulties";

        Cursor c = db.rawQuery(sql,null);
        if (c != null && c.moveToFirst()){
            do {
                difList.add(new Difficulty(c.getInt(0),c.getString(1),c.getString(2)));
            }while (c.moveToNext());
        }

        dbHelper.close();
        db.close();
    }

    private void setdefault(){
        difficulty = difList.get(0);
        txt_difname.setText(difficulty.getName());
        getHighscores();
    }

    private void getHighscores() {
        QdDbHelper dbHelper = new QdDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        hList.clear();

        String sql = "SELECT * FROM tblHighscores WHERE tblHighscores.difficultyID='" + difficulty.getId() + "' ORDER BY tblHighscores.score DESC";
        Cursor c = db.rawQuery(sql, null);
        if(c != null && c.moveToFirst()) {
            do {
            hList.add(new Highscore(c.getInt(0), c.getString(1), difficulty, c.getInt(3), c.getInt(4)));
            } while(c.moveToNext());
        }

        dbHelper.close();
        db.close();
    }


}
