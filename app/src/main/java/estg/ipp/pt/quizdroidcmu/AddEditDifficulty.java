package estg.ipp.pt.quizdroidcmu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class AddEditDifficulty extends AppCompatActivity implements View.OnClickListener{

    private EditText question;
    private Spinner difficulty;
    private EditText answer1;
    private EditText answer2;
    private EditText answer3;
    private EditText answer4;
    private Spinner correctAnswer;
    private EditText reward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_question);

        question = (EditText) findViewById(R.id.txt_question);
        difficulty = (Spinner) findViewById(R.id.spin_difficulty);
        answer1 = (EditText) findViewById(R.id.txt_answer1);
        answer2 = (EditText) findViewById(R.id.txt_answer2);
        answer3 = (EditText) findViewById(R.id.txt_answer3);
        answer4 = (EditText) findViewById(R.id.txt_answer4);
        correctAnswer = (Spinner) findViewById(R.id.spin_correctAnswer);
        reward = (EditText) findViewById(R.id.txt_reward);

        ArrayAdapter<CharSequence> answerNum = ArrayAdapter.createFromResource(this, R.array.numAnswers, android.R.layout.simple_spinner_item);
        correctAnswer.setAdapter(answerNum);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnAdd) {

        } else if(v.getId() == R.id.btnEdit) {

        }
    }
}
