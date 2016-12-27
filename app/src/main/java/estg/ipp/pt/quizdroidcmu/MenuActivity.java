package estg.ipp.pt.quizdroidcmu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import layout.QuizFragment;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Difficulty> mDifficulty = new ArrayList<>();
    private ArrayList<Highscore> mScores = new ArrayList<>();
    private ArrayList<Highscore> mCorrectAnswers = new ArrayList<>();

    private Button btn_StartGame, btn_NextDifficulty, btn_PreviousDifficulty, btn_Settings;
    private TextView txt_highestScore, txt_answerStreak;
    private Difficulty difficulty;

    private SharedPreferences mSettings;
    private String preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btn_NextDifficulty = (Button) findViewById(R.id.btnNextDifficulty);
        btn_NextDifficulty.setOnClickListener(this);

        btn_PreviousDifficulty = (Button) findViewById(R.id.btnPreviousDifficulty);
        btn_PreviousDifficulty.setOnClickListener(this);

        btn_StartGame = (Button) findViewById(R.id.btnStartGame);
        btn_StartGame.setOnClickListener(this);

        btn_Settings = (Button) findViewById(R.id.btnSettings);
        btn_Settings.setOnClickListener(this);

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
        mDifficulty.clear();

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM tblDifficulties";

        Cursor c = db.rawQuery(sql,null);
        if (c != null && c.moveToFirst()){
            do {
                mDifficulty.add(new Difficulty(c.getInt(0),c.getString(1),c.getString(2)));
            }while (c.moveToNext());
        }

        for (Difficulty difi : mDifficulty){
            sql = "SELECT *, MAX(score) As score FROM tblHighscores WHERE tblHighscores.difficultyID = " + difi.getId();
            c = db.rawQuery(sql,null);
            if (c != null && c.moveToFirst()){
                do {
                    mScores.add(new Highscore(c.getInt(0),c.getString(1),difi,c.getInt(3),c.getInt(4)));
                }while (c.moveToNext());
            }
            sql = "SELECT *, MAX(correctAnswers) As score FROM tblHighscores WHERE tblHighscores.difficultyID = " + difi.getId();
            c = db.rawQuery(sql,null);
            if (c != null && c.moveToFirst()){
                do {
                    mCorrectAnswers.add(new Highscore(c.getInt(0),c.getString(1),difi,c.getInt(3),c.getInt(4)));
                }while (c.moveToNext());
            }
        }

        dbHelper.close();
        db.close();
    }

    private void setdefault(){
        difficulty = mDifficulty.get(0);
        btn_StartGame.setText(difficulty.getName());
        for (Highscore h : mScores){
            if(h.getDifficulty().getId() == difficulty.getId()){
                txt_highestScore.setText("" + h.getScore());
                break;
            }
        }
        for (Highscore h : mCorrectAnswers){
            if(h.getDifficulty().getId() == difficulty.getId()){
                txt_answerStreak.setText("" + h.getCorrectAnswers());
                break;
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnNextDifficulty){
            if(difficulty.getId() == mDifficulty.size()){
                difficulty = mDifficulty.get(0);
                btn_StartGame.setText(difficulty.getName());
            }else {
                difficulty = mDifficulty.get(difficulty.getId());
                btn_StartGame.setText(difficulty.getName());
            }
        }else if(view.getId() == R.id.btnPreviousDifficulty){
            if(difficulty.getId() == 1){
                difficulty = mDifficulty.get(mDifficulty.size() - 1);
                btn_StartGame.setText(difficulty.getName());
            }else {
                difficulty = mDifficulty.get(difficulty.getId() - 2);
                btn_StartGame.setText(difficulty.getName());
            }
        }else if(view.getId() == R.id.btnSettings){
            Intent newIntent = new Intent(this, PreferencesActivityHoneycomb.class);
            startActivity(newIntent);
        }else if(view.getId() == R.id.btnStartGame){
            Intent newIntent = new Intent(this, QuizActivity.class);
            newIntent.putExtra("DifficultyID", difficulty.getId());
            newIntent.putExtra("DifficultyName", difficulty.getName());
            newIntent.putExtra("DifficultyDescription", difficulty.getDescription());
            startActivity(newIntent);

        }

        for (Highscore h : mScores){
            if(h.getDifficulty().getId() == difficulty.getId()){
                txt_highestScore.setText("" + h.getScore());
                break;
            } else {
                txt_highestScore.setText("" + 0);
                break;
            }
        }
        for (Highscore h : mCorrectAnswers){
            if(h.getDifficulty().getId() == difficulty.getId()){
                txt_answerStreak.setText("" + h.getCorrectAnswers());
                break;
            } else {
                txt_answerStreak.setText("" + 0);
                break;
            }
        }
    }
}
