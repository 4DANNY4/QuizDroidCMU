package estg.ipp.pt.quizdroidcmu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity {

    private Highscore score;
    private TextView txt_ScorePlayerName, txt_ScoreDifficulty, txt_ScoreCorrectAnswers, txt_Score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent intent = getIntent();
        score = new Highscore(0, (String) intent.getSerializableExtra("ScorePlayerName"),
                new Difficulty(0,(String) intent.getSerializableExtra("ScoreDifficulty"), " "),
                (int) intent.getSerializableExtra("ScoreCorrectAnswers"),
                (int) intent.getSerializableExtra("Score"));

        txt_ScorePlayerName = (TextView) findViewById(R.id.txtScorePlayerName);
        txt_ScoreDifficulty = (TextView) findViewById(R.id.txtScoreDifficulty);
        txt_ScoreCorrectAnswers = (TextView) findViewById(R.id.txtScoreCorrectAnswers);
        txt_Score = (TextView) findViewById(R.id.txtScore);

        txt_ScorePlayerName.setText(score.getPlayerName());
        txt_ScoreDifficulty.setText(score.getDifficulty().getName());
        txt_ScoreCorrectAnswers.setText("" + score.getCorrectAnswers());
        txt_Score.setText("" + score.getScore());

    }
}
