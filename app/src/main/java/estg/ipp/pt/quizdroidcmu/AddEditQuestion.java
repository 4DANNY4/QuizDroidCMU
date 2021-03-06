package estg.ipp.pt.quizdroidcmu;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddEditQuestion extends AppCompatActivity implements View.OnClickListener{

    private TextView title;
    private EditText question;
    private Spinner difficulty;
    private EditText answer1;
    private EditText answer2;
    private EditText answer3;
    private EditText answer4;
    private Spinner correctAnswer;
    private EditText reward;

    private ArrayAdapter<Difficulty> dAdapter = null;
    private ArrayList<Difficulty> dList = new ArrayList<>();

    private Button btnAdd;
    private Button btnEdit;

    private String action;

    private int questionIDtoEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_question);

        title = (TextView) findViewById(R.id.tv_qmanTitle);
        question = (EditText) findViewById(R.id.txt_question);
        difficulty = (Spinner) findViewById(R.id.spin_difficulty);
        answer1 = (EditText) findViewById(R.id.txt_answer1);
        answer2 = (EditText) findViewById(R.id.txt_answer2);
        answer3 = (EditText) findViewById(R.id.txt_answer3);
        answer4 = (EditText) findViewById(R.id.txt_answer4);
        correctAnswer = (Spinner) findViewById(R.id.spin_correctAnswer);
        reward = (EditText) findViewById(R.id.txt_reward);

        btnAdd = (Button) findViewById(R.id.btnAddQuestion);
        btnAdd.setOnClickListener(this);
        btnEdit = (Button) findViewById(R.id.btnEditQuestion);
        btnEdit.setOnClickListener(this);

        dAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, dList);
        dAdapter.setDropDownViewResource(R.layout.spinner_drop_down_popup);
        difficulty.setAdapter(dAdapter);
        ArrayAdapter<CharSequence> answerNum = ArrayAdapter.createFromResource(this, R.array.numAnswers, R.layout.spinner_item);
        answerNum.setDropDownViewResource(R.layout.spinner_drop_down_popup);
        correctAnswer.setAdapter(answerNum);

        action = getIntent().getStringExtra("Action");
        if(action.equals("Add")) {
            title.setText("Add Question");
            btnAdd.setVisibility(View.VISIBLE);
        } else if(action.equals("Edit")) {
            title.setText("Edit Question");
            questionIDtoEdit = getIntent().getIntExtra("ID", 1);
            int diffid = getIntent().getIntExtra("DIFFID",1);
            difficulty.setSelection(diffid-1);
            question.setText(getIntent().getStringExtra("QUESTION"));
            answer1.setText(getIntent().getStringExtra("ANSWER1"));
            answer2.setText(getIntent().getStringExtra("ANSWER2"));
            answer3.setText(getIntent().getStringExtra("ANSWER3"));
            answer4.setText(getIntent().getStringExtra("ANSWER4"));
            int correctid = getIntent().getIntExtra("CORRECTANSWER",1);
            correctAnswer.setSelection(correctid-1);
            reward.setText(String.valueOf(getIntent().getIntExtra("REWARD", 100)));
            btnEdit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initDifficulties();
        dAdapter.notifyDataSetChanged();
    }

    private void initDifficulties() {
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        dList.clear();

        String sql = "SELECT * FROM tblDifficulties";
        Cursor c = db.rawQuery(sql, null);
        if(c != null && c.moveToFirst()) {
            do {
                Difficulty dif = new Difficulty(c.getInt(0), c.getString(1), c.getString(2));
                dList.add(dif);
            } while(c.moveToNext());
        }
        dbHelper.close();
        db.close();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnAddQuestion) {
            add();
            Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
            finish();
        } else if(v.getId() == R.id.btnEditQuestion) {
            edit();
            Toast doneToast = Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT);
            doneToast.show();
            finish();
        }
    }

    private void add() {
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int difID = -1;
        //Getting Difficulty ID
        String getDifIDsql = "SELECT * FROM tblDifficulties WHERE tblDifficulties.name='" + difficulty.getSelectedItem().toString() + "'";
        Cursor c = db.rawQuery(getDifIDsql, null);
        if(c != null && c.moveToFirst()) {
            difID = c.getInt(0);
        } else {
            System.out.println("########ERROR: difficulty id error");
        }
        String inSqlAnswers = "INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('"+answer1.getText().toString()+"','"+answer2.getText().toString()+"','"+
                answer3.getText().toString()+"','"+answer4.getText().toString()+"')";
        //Inserting Answers into DB
        db.execSQL(inSqlAnswers);
        //Getting Last ID inserted in Answers
        String getAnswerIDsql = "SELECT MAX(id) FROM tblAnswers";
        int ansId = -1;
        Cursor c2 = db.rawQuery(getAnswerIDsql, null);
        if(c2 != null && c2.moveToFirst()) {
            ansId = c2.getInt(0);
        } else {
            System.out.println("###########ERROR: ANSWER ID ERROR!");
        }
        String inSqlquestion = "INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('"+difID+"','"+question.getText().toString()+"','"+ansId+"','"+
                correctAnswer.getSelectedItem().toString()+"','"+reward.getText().toString()+"')";
        //Inserting Question into DB
        db.execSQL(inSqlquestion);

        dbHelper.close();
        db.close();
    }

    private void edit() {
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        int difID = -1;
        //Getting Difficulty ID
        String getDifIDsql = "SELECT * FROM tblDifficulties WHERE tblDifficulties.name='" + difficulty.getSelectedItem().toString() + "'";
        Cursor c = db.rawQuery(getDifIDsql, null);
        if(c != null && c.moveToFirst()) {
            do {
                difID = c.getInt(0);
            } while(c.moveToNext());
        }
        int ansID = -1;
        //Getting Answer ID in Question
        String sqlAnsId = "SELECT answerID FROM tblQuestions WHERE tblQuestions.id='" + questionIDtoEdit + "'";
        Cursor c3 = db.rawQuery(sqlAnsId, null);
        if(c3 != null && c3.moveToFirst()) {
            do {
                ansID = c3.getInt(0);
                String inSqlAnswersEdit = "UPDATE tblAnswers " +
                        "SET answers1='" + answer1.getText().toString() + "'," +
                        "answers2='" + answer2.getText().toString() + "'," +
                        "answers3='" + answer3.getText().toString() + "'," +
                        "answers4='" + answer4.getText().toString() + "' " +
                        "WHERE tblAnswers.id='" + ansID + "'";
                //Updating Answers into DB
                db.execSQL(inSqlAnswersEdit);
            } while(c3.moveToNext());
        }
        //Updating Question into DB
        String inSqlQuestionEdit = "UPDATE tblQuestions " +
                "SET difficultyID='" + difID + "'," +
                "text='" + question.getText().toString() + "'," +
                "answerID='" + ansID + "'," +
                "rightAnswer='" + correctAnswer.getSelectedItem().toString() + "'," +
                "reward='" + reward.getText().toString() + "' " +
                "WHERE tblQuestions.id='" + questionIDtoEdit + "'";
        db.execSQL(inSqlQuestionEdit);
    }
}
