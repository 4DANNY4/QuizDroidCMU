package estg.ipp.pt.quizdroidcmu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import layout.QuizFragment;

public class GameContainer extends AppCompatActivity {

    private QuizFragment quiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_container);

        if(findViewById(R.id.activity_game_container) != null) {
            if(savedInstanceState != null)
                return;

            quiz = new QuizFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.activity_game_container, quiz).commit();
        }
    }
}
