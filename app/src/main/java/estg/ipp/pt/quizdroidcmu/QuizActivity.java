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
        String sql =
                "SELECT *" +
                        " FROM" +
                            " SELECT *" +
                                    " FROM tblQuestion INNER JOIN (" +
                                                        " SELECT *" +
                                                        " FROM tblDifficulty" +
                                                        " WHERE id='" + difficulty.getId() + "')" +
                                    " ON tblQuestion.questionDifficulty = tblDifficulty.id" +
                        " INNER JOIN tblAnswers" +
                        " ON tblQuestion.answers = tblAnswers.id";

        Cursor c = db.rawQuery(sql,null);
        if (c != null && c.moveToFirst()){
            do {
                mQuiz.add(new Question(c.getInt(0),c.getInt(1),c.getInt(2),c.getString(3),c.getInt(4),c.getInt(5)));
            }while (c.moveToNext());
        }
        dbHelper.close();
        db.close();
    }


}
