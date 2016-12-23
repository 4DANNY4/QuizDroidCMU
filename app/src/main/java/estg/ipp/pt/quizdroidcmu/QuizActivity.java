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
    private Highscore gameTable;
    private Question randQuestion;
    private Difficulty difficulty;
    private TextView txt_question;
    private Button btn_Answer1, btn_Answer2, btn_Answer3, btn_Answer4;
    private Button btn_Help1, btn_Help2, btn_Help3, btn_Help4;

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

        gameTable = new Highscore( 0, "NULL", difficulty, 0, 0);

        txt_question = (TextView) findViewById(R.id.txtQuestion);

        btn_Answer1 = (Button) findViewById(R.id.btnAnswer1);
        btn_Answer1.setOnClickListener(this);
        btn_Answer2 = (Button) findViewById(R.id.btnAnswer2);
        btn_Answer2.setOnClickListener(this);
        btn_Answer3 = (Button) findViewById(R.id.btnAnswer3);
        btn_Answer3.setOnClickListener(this);
        btn_Answer4 = (Button) findViewById(R.id.btnAnswer4);
        btn_Answer4.setOnClickListener(this);
        btn_Help1 = (Button) findViewById(R.id.btnHelp1);
        btn_Help1.setOnClickListener(this);
        btn_Help2 = (Button) findViewById(R.id.btnHelp2);
        btn_Help2.setOnClickListener(this);
        btn_Help3 = (Button) findViewById(R.id.btnHelp3);
        btn_Help3.setOnClickListener(this);
        btn_Help4 = (Button) findViewById(R.id.btnHelp4);
        btn_Help4.setOnClickListener(this);

        initData();
        nextQuestion();
        setGameTable();

    }

    private void initData(){
        mQuiz.clear();

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM tblQuestions LEFT JOIN tblAnswers ON tblQuestions.answerID = tblAnswers.id WHERE tblQuestions.difficultyID = '" + (difficulty.getId() - 1)  + "'";
        Cursor c = db.rawQuery(sql,null);
        if (c != null && c.moveToFirst()){
            do {
                String[] answers = {c.getString(7),c.getString(8),c.getString(9),c.getString(10)};
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
            int rand = (r.nextInt(max - min + 1) + min) - 1;

            randQuestion = new Question(mQuiz.get(rand));
            txt_question.setText(randQuestion.getText());
            btn_Answer1.setText(randQuestion.getAnswers()[0]);
            btn_Answer2.setText(randQuestion.getAnswers()[1]);
            btn_Answer3.setText(randQuestion.getAnswers()[2]);
            btn_Answer4.setText(randQuestion.getAnswers()[3]);

            mQuiz.remove(rand);
        }else{
            
        }
    }

    private void setGameTable(){
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "INSERT INTO tblGames(playerName, difficultyID, correctAnswers, score)" +
                " VALUES('" + gameTable.getPlayerName() + "','" + difficulty.getId() + "','0','0')";
        db.execSQL(sql);
        sql = "SELECT * FROM tblGames";
        Cursor c = db.rawQuery(sql,null);
        c.moveToLast();
        if (c != null ){
            gameTable.setId(c.getInt(0));
        }
    }

    private void setScores(int correctAnswer){
        if(randQuestion.getCorrectAnswer() == correctAnswer){
            //Dialog correct answer
            gameTable.setScore(gameTable.getScore()+randQuestion.getReward());
            gameTable.setCorrectAnswers(gameTable.getCorrectAnswers()+1);
            QdDbHelper dbHelper = new QdDbHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String sql = "UPDATE tblGames SET score = '" + gameTable.getScore() +
                    "', correctAnswers = '" + gameTable.getCorrectAnswers() +
                    "' WHERE id = " + gameTable.getId() + ";";
            db.execSQL(sql);
        }else{
            //Dialog wrong answer
        }
        btn_Answer1.setVisibility(View.VISIBLE);
        btn_Answer2.setVisibility(View.VISIBLE);
        btn_Answer3.setVisibility(View.VISIBLE);
        btn_Answer4.setVisibility(View.VISIBLE);
        nextQuestion();
    }
    private void helpFiftyFifty(){
        int min = 1;
        int max = 4;
        int help= 0;

        Random r = new Random();
        do{
            int rand = r.nextInt(max - min + 1) + min;
            if (rand != (randQuestion.getCorrectAnswer() + 1)){
                switch (rand){
                    case 1:
                        if(btn_Answer1.getVisibility() == View.VISIBLE){
                            btn_Answer1.setVisibility(View.GONE);
                            help++;
                        }
                        break;
                    case 2:
                        if(btn_Answer2.getVisibility() == View.VISIBLE) {
                            btn_Answer2.setVisibility(View.GONE);
                            help++;
                        }
                        break;
                    case 3:
                        if(btn_Answer3.getVisibility() == View.VISIBLE) {
                            btn_Answer3.setVisibility(View.GONE);
                            help++;
                        }
                        break;
                    case 4:
                        if(btn_Answer4.getVisibility() == View.VISIBLE) {
                            btn_Answer4.setVisibility(View.GONE);
                            help++;
                        }
                        break;
                    default:
                        break;
                }
            }
        }while (help < 2);
        btn_Help1.setEnabled(false);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAnswer1){
            setScores(0);
        } else if(view.getId() == R.id.btnAnswer2){
            setScores(1);
        } else if(view.getId() == R.id.btnAnswer3){
            setScores(2);
        } else if(view.getId() == R.id.btnAnswer4){
            setScores(3);
        } else if(view.getId() == R.id.btnHelp1){
            helpFiftyFifty();
        } else if(view.getId() == R.id.btnHelp2){

        } else if(view.getId() == R.id.btnHelp3){

        } else if(view.getId() == R.id.btnHelp4){

        }

    }
}
