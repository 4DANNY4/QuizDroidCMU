package estg.ipp.pt.quizdroidcmu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Difficulty> difList = new ArrayList<>();

    private LinearLayout btn_StartGame;
    private Button btn_NextDifficulty, btn_PreviousDifficulty, btn_Settings, btn_Highscores, btn_Continue;
    private TextView txt_highestScore, txt_answerStreak, txt_diff;
    private Difficulty difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn_NextDifficulty = (Button) findViewById(R.id.btnNextDifficulty);
        btn_NextDifficulty.setOnClickListener(this);

        btn_PreviousDifficulty = (Button) findViewById(R.id.btnPreviousDifficulty);
        btn_PreviousDifficulty.setOnClickListener(this);

        btn_StartGame = (LinearLayout) findViewById(R.id.btnStartGame);
        btn_StartGame.setOnClickListener(this);

        btn_Continue = (Button) findViewById(R.id.btnContinueGame);
        btn_Continue.setOnClickListener(this);

        txt_diff = (TextView) findViewById(R.id.txt_diff);

        btn_Settings = (Button) findViewById(R.id.btnSettings);
        btn_Settings.setOnClickListener(this);

        btn_Highscores = (Button) findViewById(R.id.btnHighscores);
        btn_Highscores.setOnClickListener(this);

        txt_highestScore = (TextView) findViewById(R.id.txtHighestScore);
        txt_answerStreak = (TextView) findViewById(R.id.txtAnswerStreak);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
        setdefault();
    }

    private void initData(){
        difList.clear();

        QdDbHelper dbHelper = new QdDbHelper(this);
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
        txt_diff.setText(difficulty.getName());
        getHighscores();
    }

    @Override
    public void onClick(View view) {
        int dPos = -1;
        // Getting difficulty position
        for(int i=0; i<difList.size(); i++) {
            if(difList.get(i).getId() == difficulty.getId()) {
                dPos = i;
                break;
            }
        }
        if (view.getId() == R.id.btnNextDifficulty){
            if(dPos == difList.size()-1){
                difficulty = difList.get(0);
                txt_diff.setText(difficulty.getName());
            } else {
                difficulty = difList.get(dPos+1);
                txt_diff.setText(difficulty.getName());
            }
            getHighscores();
        }else if(view.getId() == R.id.btnPreviousDifficulty){
            if(dPos == 0){
                difficulty = difList.get(difList.size() - 1);
                txt_diff.setText(difficulty.getName());
            }else {
                difficulty = difList.get(dPos-1);
                txt_diff.setText(difficulty.getName());
            }
            getHighscores();
        } else if(view.getId() == R.id.btnSettings) {
            Intent newIntent = new Intent(this, PreferencesActivityHoneycomb.class);
            startActivity(newIntent);
        } else if(view.getId() == R.id.btnStartGame) {
            Intent newIntent = new Intent(this, QuizActivity.class);
            newIntent.putExtra("DifficultyID", difficulty.getId());
            newIntent.putExtra("DifficultyName", difficulty.getName());
            newIntent.putExtra("DifficultyDescription", difficulty.getDescription());
            newIntent.putExtra("isContinue", false);
            startActivity(newIntent);
        } else if(view.getId() == R.id.btnContinueGame) {
            Intent newIntent = new Intent(this, ListContinueActivity.class);
            startActivity(newIntent);
        } else if(view.getId() == R.id.btnHighscores) {
            Intent newIntent = new Intent(this, HighscoreContainer.class);
            startActivity(newIntent);
        }
    }

    private void getHighscores() {
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Get highscore for difficulty
        String sqlGetHighscore = "SELECT MAX(score) AS score FROM tblHighscores WHERE tblHighscores.difficultyID='" + difficulty.getId() + "'";
        Cursor c = db.rawQuery(sqlGetHighscore, null);
        if(c != null && c.moveToFirst()) {
            txt_highestScore.setText("" + c.getInt(0));
        }

        //Get most correct answers for difficulty
        String sqlGetCorrectAnswer = "SELECT MAX(correctAnswers) AS correctAnswers FROM tblHighscores WHERE tblHighscores.difficultyID='" + difficulty.getId() + "'";
        Cursor c2 = db.rawQuery(sqlGetCorrectAnswer, null);
        if(c2 != null && c2.moveToFirst()) {
            txt_answerStreak.setText("" + c2.getInt(0));
        }

        dbHelper.close();
        db.close();
    }
}
