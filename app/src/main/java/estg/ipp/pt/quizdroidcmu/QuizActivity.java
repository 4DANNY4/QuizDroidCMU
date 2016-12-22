package estg.ipp.pt.quizdroidcmu;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private ArrayList<Question> mQuiz = new ArrayList<>();
    private Difficulty difficulty;
    private TextView txt_question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        txt_question = (TextView) findViewById(R.id.txtQuestion);
        //txt_question.setText();

    }

    private void initData(){
        mQuiz.clear();

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM tblQuestion LEFT JOIN tblAnswers ON tblQuestion.answers = tblAnswers.id WHERE tblQuestion.questionDifficulty = '" + difficulty.getId() + "'";
        Cursor c = db.rawQuery(sql,null);
        if (c != null && c.moveToFirst()){
            do {
                //mQuiz.add(new Question());
            }while (c.moveToNext());
        }
        dbHelper.close();
        db.close();
    }

    private void nextQuestion(){
        int min = 0;
        int max = 0 ;

    }
}
