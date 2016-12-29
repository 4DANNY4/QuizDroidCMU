package estg.ipp.pt.quizdroidcmu;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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

        gameTable = new Highscore( 0, "", difficulty, 0, 0);

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

        final AlertDialog.Builder playerNameDialog = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        final String txt = "Player Name:";
        playerNameDialog.setCancelable(false);
        playerNameDialog.setTitle(txt);
        playerNameDialog.setView(input);
        playerNameDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                gameTable.setPlayerName(input.getText().toString().trim());
                Toast.makeText(getApplicationContext(), "Player: " + gameTable.getPlayerName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        playerNameDialog.show();

        initData();
        nextQuestion();
        setGameTable();

    }

    private void initData(){
        mQuiz.clear();

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM tblQuestions LEFT JOIN tblAnswers ON tblQuestions.answerID = tblAnswers.id WHERE tblQuestions.difficultyID = '" + difficulty.getId()  + "'";
        Cursor c = db.rawQuery(sql,null);
        if (c != null && c.moveToFirst()){
            do {
                String[] answers = {c.getString(7),c.getString(8),c.getString(9),c.getString(10)};
                mQuiz.add(new Question(c.getInt(0),c.getString(3),difficulty, answers, c.getInt(4), c.getInt(5)));
            }while (c.moveToNext());
        }
        dbHelper.close();
        db.close();

        Collections.shuffle(mQuiz);
        do {
            mQuiz.remove(mQuiz.size() - 1);
        }while (mQuiz.size() > 20);
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
            Intent newIntent = new Intent(this, ScoreActivity.class);
            newIntent.putExtra("ScorePlayerName", gameTable.getPlayerName());
            newIntent.putExtra("ScoreDifficulty", gameTable.getDifficulty().getName());
            newIntent.putExtra("ScoreCorrectAnswers", gameTable.getCorrectAnswers());
            newIntent.putExtra("Score", gameTable.getScore());
            startActivity(newIntent);
            finish();
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
            if (rand != (randQuestion.getCorrectAnswer())){
                switch (rand){
                    case 1:
                        if(btn_Answer1.getVisibility() == View.VISIBLE){
                            btn_Answer1.setVisibility(View.INVISIBLE);
                            help++;
                        }
                        break;
                    case 2:
                        if(btn_Answer2.getVisibility() == View.VISIBLE) {
                            btn_Answer2.setVisibility(View.INVISIBLE);
                            help++;
                        }
                        break;
                    case 3:
                        if(btn_Answer3.getVisibility() == View.VISIBLE) {
                            btn_Answer3.setVisibility(View.INVISIBLE);
                            help++;
                        }
                        break;
                    case 4:
                        if(btn_Answer4.getVisibility() == View.VISIBLE) {
                            btn_Answer4.setVisibility(View.INVISIBLE);
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

    private int randAnswer(){
        int min = 1;
        int max = 4;

        Random r = new Random();
        do{
            int rand = r.nextInt(max - min + 1) + min;
            if (rand != (randQuestion.getCorrectAnswer())){
                return rand - 1;
            }
        }while(true);
    }

    private void contacts(String contact){
        int min = 1;
        int max = 100;

        Random r = new Random();
        int rand = r.nextInt(max - min + 1) + min;

        switch (contact){
            case "Albert Einstein":
                Toast.makeText(getApplicationContext(),
                        "I think it is: " + randQuestion.getAnswers()[randQuestion.getCorrectAnswer() - 1].toString(),
                        Toast.LENGTH_LONG).show();
                break;
            case "Doge":
                if(rand <= 40 || rand >= 60){
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randQuestion.getCorrectAnswer() - 1].toString(),
                            Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randAnswer()].toString(),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case "Harambe":
                if(rand <= 35 || rand >= 80){
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randQuestion.getCorrectAnswer() - 1].toString(),
                            Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randAnswer()].toString(),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case "Donald Trump":
                if(rand <= 10 || rand >= 90){
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randQuestion.getCorrectAnswer() - 1].toString(),
                            Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randAnswer()].toString(),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case "SID":
                if(rand == 99){
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randQuestion.getCorrectAnswer() - 1].toString(),
                            Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randAnswer()].toString(),
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case "Potato":
                if(rand <= 50 || rand >= 60){
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randQuestion.getCorrectAnswer() - 1].toString(),
                            Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randAnswer()].toString(),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case "Waifu":
                if(rand <= 50 || rand >= 75){
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randQuestion.getCorrectAnswer() - 1].toString(),
                            Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(getApplicationContext(),
                            "I think it is: " + randQuestion.getAnswers()[randAnswer()].toString(),
                            Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    private void helpPhone(){
        final AlertDialog.Builder helpPhone = new AlertDialog.Builder(this, R.style.DialogStyleHelpPhone);
        final String txt = "Who would you like to contact:";
        final ArrayList<String> contacts = new ArrayList<>();
        contacts.add("Albert Einstein"); // 100%
        contacts.add("Doge"); // 80%
        contacts.add("Harambe"); //55%
        contacts.add("Donald Trump"); // 20%
        contacts.add("SID"); // 1%
        contacts.add("Potato"); // 90%
        contacts.add("Waifu"); // 75%
        Collections.shuffle(contacts);
/*
        helpPhone.setCancelable(false);

        helpPhone.setTitle(txt);
        helpPhone.setPositiveButton(contacts.get(0).toString(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                contacts(contacts.get(0).toString());
            }
        });
        helpPhone.setNeutralButton(contacts.get(1).toString(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                contacts(contacts.get(1).toString());
            }
        });
        helpPhone.setNegativeButton(contacts.get(2).toString(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                contacts(contacts.get(2).toString());
            }
        });

        helpPhone.show();
        */
        btn_Help2.setEnabled(false);
    }

    private void showEditDialog() {
        FragmentManager fm = getSupportFragmentManager();
        helpPhoneDialog editNameDialog = new helpPhoneDialog();
        editNameDialog.show(fm, "fragment_help_phone_dialog");
    }

    private void helpPublic(){
        final AlertDialog.Builder helpPublic = new AlertDialog.Builder(this);
        helpPublic.setCancelable(false);

        String answers[] = {
                "-> " + randQuestion.getAnswers()[0].toString() + " <-:"
                , "-> " + randQuestion.getAnswers()[1].toString() + " <-:"
                , "-> " + randQuestion.getAnswers()[2].toString() + " <-:"
                , "-> " + randQuestion.getAnswers()[3].toString() + " <-:"
        };

        int min = 1, max = 100, sum = 0;
        Random r = new Random();
        int rand = r.nextInt(max - min + 1) + min;

        ArrayList<Integer> arrayRand = new ArrayList<>();
        for (int i = 0; i < 4 - 1; i++)
        {
            arrayRand.add(r.nextInt((100 - sum) / 2) + 1);
            sum += arrayRand.get(i);
        }
        arrayRand.add(100 - sum);

        System.out.println(arrayRand.toString());
        System.out.println(rand);

        if(rand <= 5 || rand >= 95){
            int firstTop, secondTop;
            firstTop = Collections.max(arrayRand);
            arrayRand.remove(arrayRand.indexOf(firstTop));
            secondTop = Collections.max(arrayRand);
            arrayRand.remove(arrayRand.indexOf(secondTop));
            arrayRand.add(firstTop);
            Collections.shuffle(arrayRand);
            for(int i = 0; i < 4; i++){
                if((randQuestion.getCorrectAnswer()-1)==i){
                    answers[randQuestion.getCorrectAnswer()-1] += " " + secondTop + "%";
                } else{
                    answers[i] += " " + arrayRand.get(0) + "%";
                    arrayRand.remove(0);
                }
            }
        }else{
            int top;
            top = Collections.max(arrayRand);
            arrayRand.remove(arrayRand.indexOf(top));
            Collections.shuffle(arrayRand);
            for(int i = 0; i < 4; i++){
                if((randQuestion.getCorrectAnswer()-1)==i){
                    answers[randQuestion.getCorrectAnswer()-1] += " " + top + "%";
                } else{
                   answers[i] += " " + arrayRand.get(0) + "%";
                    arrayRand.remove(0);
                }
            }
        }
        helpPublic.setTitle("Public Help");
        helpPublic.setItems(answers, null);
        helpPublic.setNeutralButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        helpPublic.show();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnAnswer1){
            setScores(1);
        } else if(view.getId() == R.id.btnAnswer2){
            setScores(2);
        } else if(view.getId() == R.id.btnAnswer3){
            setScores(3);
        } else if(view.getId() == R.id.btnAnswer4){
            setScores(4);
        } else if(view.getId() == R.id.btnHelp1){
            helpFiftyFifty();
        } else if(view.getId() == R.id.btnHelp2){
            helpPhone();
        } else if(view.getId() == R.id.btnHelp3){
            helpPublic();
        } else if(view.getId() == R.id.btnHelp4){
            nextQuestion();
            btn_Help4.setEnabled(false);
        }
    }
}
