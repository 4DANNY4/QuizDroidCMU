package estg.ipp.pt.quizdroidcmu;

import android.app.*;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class ListContinueActivity extends android.app.ListActivity implements View.OnClickListener {

    private ArrayList<Game> gList = new ArrayList<>();
    private GameAdapter gAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_continue);

        gAdapter = new GameAdapter(ListContinueActivity.this, gList);
        setListAdapter(gAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSaves();
        gAdapter.notifyDataSetChanged();
    }

    private void loadSaves() {
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sqlGame = "SELECT * FROM tblGames";
        Cursor cGame = db.rawQuery(sqlGame,null);
        Difficulty difficulty = null;
        if (cGame != null && cGame.moveToFirst()){
            do {
                String sqlDiff = "SELECT * FROM tblDifficulties WHERE tblDifficulties.id = '" + cGame.getInt(2) + "'";
                Cursor cDiff = db.rawQuery(sqlDiff, null);
                Game savedGame = new Game();
                savedGame.setId(cGame.getInt(0));
                if (cDiff != null && cDiff.moveToFirst()) {
                    difficulty = new Difficulty(cDiff.getInt(0), cDiff.getString(1), cDiff.getString(2));
                }
                if (cGame.getString(6).equals("false")) { //unlimited
                    savedGame.setHighScore(new Highscore(0, cGame.getString(1), difficulty, cGame.getInt(3), cGame.getInt(4), false));
                } else {
                    savedGame.setHighScore(new Highscore(0, cGame.getString(1), difficulty, cGame.getInt(3), cGame.getInt(4), true));
                }
                if (cGame.getString(7).equals("true")) { //helpsDisabled
                    savedGame.setHelpsDisabled(true);
                } else {
                    if (cGame.getString(8).equals("true")) { //helpFiftyFifty
                        savedGame.setHelpFiftyFiftyUsed();
                    }
                    if (cGame.getString(9).equals("true")) { //helpPhone
                        savedGame.setHelpPhoneUsed();
                    }
                    if (cGame.getString(10).equals("true")) { //helpPublic
                        savedGame.setHelpPublicUsed();
                    }
                    if (cGame.getString(11).equals("true")) { //helpChange
                        savedGame.setHelpChangeUsed();
                    }
                }
                gList.add(savedGame);
            } while(cGame.moveToNext());
        }

        dbHelper.close();
        db.close();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent newIntent = new Intent(this, QuizActivity.class);
        newIntent.putExtra("isContinue", true);
        newIntent.putExtra("GameTableId", gList.get(position).getId());
        startActivity(newIntent);
    }
}
