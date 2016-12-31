package estg.ipp.pt.quizdroidcmu;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ScoreActivity extends AppCompatActivity implements View.OnClickListener {

    private Highscore score;
    private TextView txt_ScorePlayerName, txt_ScoreDifficulty, txt_ScoreCorrectAnswers, txt_Score;
    private Button btnMenu, btnHighscores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Intent intent = getIntent();
        score = new Highscore(0, (String) intent.getSerializableExtra("ScorePlayerName"),
                new Difficulty(0,(String) intent.getSerializableExtra("ScoreDifficulty"), " "),
                (int) intent.getSerializableExtra("ScoreCorrectAnswers"),
                (int) intent.getSerializableExtra("Score"));

        btnMenu = (Button) findViewById(R.id.btnEndMenu);
        btnMenu.setOnClickListener(this);
        btnHighscores = (Button) findViewById(R.id.btnEndHighscores);
        btnHighscores.setOnClickListener(this);


        txt_ScorePlayerName = (TextView) findViewById(R.id.txtScorePlayerName);
        txt_ScoreDifficulty = (TextView) findViewById(R.id.txtScoreDifficulty);
        txt_ScoreCorrectAnswers = (TextView) findViewById(R.id.txtScoreCorrectAnswers);
        txt_Score = (TextView) findViewById(R.id.txtScore);

        txt_ScorePlayerName.setText(score.getPlayerName());
        txt_ScoreDifficulty.setText(score.getDifficulty().getName());
        txt_ScoreCorrectAnswers.setText("" + score.getCorrectAnswers());
        txt_Score.setText("" + score.getScore());


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
}
