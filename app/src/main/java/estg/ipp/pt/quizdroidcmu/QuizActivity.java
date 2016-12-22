package estg.ipp.pt.quizdroidcmu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Question> mQuiz = new ArrayList<>();
    private Question randQuestion;
    private Difficulty difficulty;
    private TextView txt_question;
    private Button btn_Answer1, btn_Answer2, btn_Answer3, btn_Answer4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();
        difficulty = new Difficulty(
                (int) intent.getSerializableExtra("DifficultyID"),
                (String) intent.getSerializableExtra("DifficultyName"),
                (String) intent.getSerializableExtra("DifficultyDescription")
        );

        txt_question = (TextView) findViewById(R.id.txtQuestion);

        btn_Answer1 = (Button) findViewById(R.id.btnAnswer1);
        btn_Answer1.setOnClickListener(this);
        btn_Answer2 = (Button) findViewById(R.id.btnAnswer2);
        btn_Answer2.setOnClickListener(this);
        btn_Answer3 = (Button) findViewById(R.id.btnAnswer3);
        btn_Answer3.setOnClickListener(this);
        btn_Answer4 = (Button) findViewById(R.id.btnAnswer4);
        btn_Answer4.setOnClickListener(this);

        initData();
        nextQuestion();

    }

    private void initData(){
        mQuiz.clear();

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM tblQuestions LEFT JOIN tblAnswers ON tblQuestions.answerID = tblAnswers.id WHERE tblQuestions.difficultyID = '" + (difficulty.getId() - 1)  + "'";
        Cursor c = db.rawQuery(sql,null);
        if (c != null && c.moveToFirst()){
            do {
                String[] answers = {c.getString(6),c.getString(7),c.getString(8),c.getString(9)};
                mQuiz.add(new Question(c.getInt(0),c.getString(3),difficulty, answers, c.getInt(4), c.getInt(5)));
            }while (c.moveToNext());
        }
        dbHelper.close();
        db.close();
    }

    private void nextQuestion(){
        if(mQuiz.size() != 0) {
            int min = 1;
            int max = mQuiz.size();

            Random r = new Random();
            int rand = r.nextInt((max - min + 1) + min) - 1;

            randQuestion = new Question(mQuiz.get(rand));
            txt_question.setText(randQuestion.getText());
            btn_Answer1.setText(randQuestion.getAnswers()[0]);
            btn_Answer2.setText(randQuestion.getAnswers()[1]);
            btn_Answer3.setText(randQuestion.getAnswers()[2]);
            btn_Answer4.setText(randQuestion.getAnswers()[3]);
        }else{
            
        }
    }

    @Override
    public void onClick(View v) {

    }
}
