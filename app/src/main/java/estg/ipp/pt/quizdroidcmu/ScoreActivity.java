package estg.ipp.pt.quizdroidcmu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity implements View.OnClickListener {

    private Highscore score;
    private TextView txt_ScorePlayerName, txt_ScoreDifficulty, txt_ScoreCorrectAnswers, txt_Score, txt_unlimited;
    private Button btnMenu, btnHighscores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent intent = getIntent();
        score = new Highscore(0, intent.getStringExtra("ScorePlayerName"),
                new Difficulty(intent.getIntExtra("ScoreDifficultyID", 0), intent.getStringExtra("ScoreDifficultyName"), " "),
                intent.getIntExtra("ScoreCorrectAnswers", 0),
                intent.getIntExtra("Score", 0),
                intent.getBooleanExtra("Unlimited", false));

        btnMenu = (Button) findViewById(R.id.btnEndMenu);
        btnMenu.setOnClickListener(this);
        btnHighscores = (Button) findViewById(R.id.btnEndHighscores);
        btnHighscores.setOnClickListener(this);


        txt_ScorePlayerName = (TextView) findViewById(R.id.txtScorePlayerName);
        txt_ScoreDifficulty = (TextView) findViewById(R.id.txtScoreDifficulty);
        txt_ScoreCorrectAnswers = (TextView) findViewById(R.id.txtScoreCorrectAnswers);
        txt_Score = (TextView) findViewById(R.id.txtScore);
        txt_unlimited = (TextView) findViewById(R.id.txtScoreUnlimited);

        txt_ScorePlayerName.setText(score.getPlayerName());
        txt_ScoreDifficulty.setText(score.getDifficulty().getName());
        txt_ScoreCorrectAnswers.setText("" + score.getCorrectAnswers());
        txt_Score.setText("" + score.getScore());

        if(score.isUnlimited()) {
            txt_unlimited.setText("Unlimited");
        } else {
            txt_unlimited.setText("Normal");
        }

        insertHighscore();
        //TODO: ADICIONAR HIGHSCORE
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnEndMenu) {
            finish();
        } else if(v.getId() == R.id.btnEndHighscores) {
            Intent newIntent = new Intent(this, HighscoreContainer.class);
            startActivity(newIntent);
            finish();
        }
    }

    private void insertHighscore() {
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Get lowest value from DB
        //If score.getscore() > lowest value
        //Insert into DB

        String countScoresSql;
        int unlimited = -1;

        if(score.isUnlimited()) {
            unlimited = 1;
            countScoresSql = "SELECT COUNT(*) FROM tblHighscores WHERE tblHighscores.unlimited=" + unlimited;
        } else {
            unlimited = 0;
            countScoresSql = "SELECT COUNT(*) FROM tblHighscores WHERE tblHighscores.unlimited=" + unlimited;
        }

        Cursor c = db.rawQuery(countScoresSql, null);
        if(c != null) {
            c.moveToFirst();
        }

        String getLowestScoreSql;
        if(c.getInt(0) >= 10) {
            if(unlimited == 1) {
                getLowestScoreSql = "SELECT id, score FROM tblHighscores ORDER BY tblHighscores.correctAnswers ASC LIMIT 1";
            } else {
                getLowestScoreSql = "SELECT id, score FROM tblHighscores ORDER BY tblHighscores.score ASC LIMIT 1";
            }

            Cursor c2 = db.rawQuery(getLowestScoreSql, null);
            String delSql;
            if(c2 != null && c2.moveToFirst()) {
                if(c2.getInt(1) < score.getScore()) {
                    delSql = "DELETE FROM tblHighscores WHERE tblHighscores.id='" + c2.getInt(0) + "'";
                    db.execSQL(delSql, null);
                }
            }
        }

        String insertSql = "INSERT INTO tblHighscores(playerName, difficultyID, correctAnswers, score, unlimited)" +
                " VALUES('" + score.getPlayerName() + "'," +
                "'" + score.getDifficulty().getId() + "'," +
                "'" + score.getCorrectAnswers() + "'," +
                "'" + score.getScore() + "'," +
                "'" + unlimited + "')";
        db.execSQL(insertSql);

        //TODO: REMOVE PRINTS
        System.out.println(score.getPlayerName());
        System.out.println(score.getDifficulty().getId());
        System.out.println(score.getCorrectAnswers());
        System.out.println(score.getScore());

        dbHelper.close();
        db.close();
    }
}
