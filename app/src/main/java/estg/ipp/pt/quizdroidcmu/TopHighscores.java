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
import android.widget.TextView;

import java.util.ArrayList;

public class TopHighscores extends ListFragment {

    private Context mContext;
    private ArrayList<Highscore> hList = new ArrayList<>();
    private ArrayAdapter hAdapter = null;

    public TopHighscores() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        hAdapter = new HighscoreAdapter(mContext, hList);
        setListAdapter(hAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        getHighscores();
        hAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mContentView = inflater.inflate(R.layout.fragment_top_highscores, container, false);

        return mContentView;
    }

    private void getHighscores() {
        QdDbHelper dbHelper = new QdDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        hList.clear();

        String sqlAllDif = "SELECT * FROM tblDifficulties";
        Cursor c = db.rawQuery(sqlAllDif, null);
        Difficulty dif = null;
        if(c != null && c.moveToFirst()) {
            do {
                dif = new Difficulty(c.getInt(0), c.getString(1), c.getString(2));
                String sql = "SELECT *, MAX(score) AS score FROM tblHighscores WHERE tblHighscores.difficultyID='" + dif.getId() + "'";
                Cursor c2 = db.rawQuery(sql, null);
                if(c2 != null && c2.moveToFirst()) {
                    hList.add(new Highscore(c2.getInt(0), c2.getString(1), dif, c2.getInt(3), c2.getInt(4)));
                }
            } while(c.moveToNext());
        }

        dbHelper.close();
        db.close();
    }
}
