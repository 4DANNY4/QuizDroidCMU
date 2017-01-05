package estg.ipp.pt.quizdroidcmu;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Question> mQuiz = new ArrayList<>();
    private Game gameTable;
    private Question randQuestion, helpChange ;
    private Difficulty difficulty;
    private TextView txt_question;
    private Button btn_Answer1, btn_Answer2, btn_Answer3, btn_Answer4;
    private Button btn_Help1, btn_Help2, btn_Help3, btn_Help4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();



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

        if (intent.getBooleanExtra("isContinue", false)) {
            gameTable = new Game(intent.getIntExtra("GameTableId",0), new Highscore(0, "", new Difficulty(0,"",""), 0, 0, false), false, false);
            initDataContinue();
        } else{
            difficulty = new Difficulty(
                    intent.getIntExtra("DifficultyID", 0),
                    intent.getStringExtra("DifficultyName"),
                    intent.getStringExtra("DifficultyDescription")
            );
            SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
            gameTable = new Game(0, new Highscore(0, "", difficulty, 0, 0, false),
                    mSettings.getBoolean("unlimited", false), mSettings.getBoolean("helpsDisabled", false));

            final AlertDialog.Builder playerNameDialog = new AlertDialog.Builder(this);
            final EditText input = new EditText(this);
            final String txt = "Player Name:";
            playerNameDialog.setCancelable(false);
            playerNameDialog.setTitle(txt);
            playerNameDialog.setView(input);
            playerNameDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    gameTable.getHighScore().setPlayerName(input.getText().toString().trim());
                    Toast.makeText(getApplicationContext(), "Player: " + gameTable.getHighScore().getPlayerName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            playerNameDialog.show();

            if(gameTable.isUnlimited()){
                initDataUnlimited();
            }else{
                initDataLimit();
            }
        }

        nextQuestion();
        setGameTable();

        if(gameTable.isHelpsDisabled()){
            btn_Help1.setVisibility(View.GONE);
            btn_Help2.setVisibility(View.GONE);
            btn_Help3.setVisibility(View.GONE);
            btn_Help4.setVisibility(View.GONE);
        }
    }

    private void initDataUnlimited(){
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
    }

    private void initDataLimit(){
        initDataUnlimited();

        if(mQuiz.size() > 20){
            helpChange = mQuiz.get(mQuiz.size());
        }else{
            btn_Help4.setBackgroundResource(R.drawable.ic_50used); //TODO
            // change
            btn_Help4.setEnabled(false);
        }
        while (mQuiz.size() > 20) {
            mQuiz.remove(mQuiz.size() - 1);
        }
    }

    private void initDataContinue(){
        mQuiz.clear();

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sqlGame = "SELECT * FROM tblGames WHERE tblGames.id = '" + gameTable.getId()  + "'";
        Cursor cGame = db.rawQuery(sqlGame,null);
        if (cGame != null && cGame.moveToFirst()){
            String sqlDiff = "SELECT * FROM tblDifficulties WHERE tblDifficulties.id = '" + cGame.getInt(2)  + "'";
            Cursor cDiff = db.rawQuery(sqlDiff,null);
            if (cDiff != null && cDiff.moveToFirst()){
                difficulty = new Difficulty(cDiff.getInt(0), cDiff.getString(1), cDiff.getString(2));
            }
            if (cGame.getInt(6) == 0) { //unlimited
                gameTable.setHighScore(new Highscore(0, cGame.getString(1), difficulty, cGame.getInt(3), cGame.getInt(4), false));
            } else{
                gameTable.setHighScore(new Highscore(0, cGame.getString(1), difficulty, cGame.getInt(3), cGame.getInt(4), true));
            }
            if (cGame.getInt(7) == 1){ //helpsDisabled
                gameTable.setHelpsDisabled(true);
            } else{
                if (cGame.getInt(8) == 1) { //helpFiftyFifty
                    gameTable.setHelpFiftyFiftyUsed();
                }
                if (cGame.getInt(9) == 1) { //helpPhone
                    gameTable.setHelpPhoneUsed();
                }
                if (cGame.getInt(10) == 1) { //helpPublic
                    gameTable.setHelpPublicUsed();
                }
                if (cGame.getInt(11) == 1) { //helpChange
                    gameTable.setHelpChangeUsed();
                }
            }
        }

        String questions = cGame.getString(5);
        String[] qSplit = questions.split(";");
        for (String s : qSplit){
            gameTable.addQuestionsId(Integer.valueOf(s));
        }

        String sqlQuestions = "SELECT * FROM tblQuestions LEFT JOIN tblAnswers ON tblQuestions.answerID = tblAnswers.id WHERE tblQuestions.difficultyID = '" + gameTable.getHighScore().getDifficulty()  + "'";
        Cursor cQuestions = db.rawQuery(sqlQuestions,null);
        if (cQuestions != null && cQuestions.moveToFirst()){
            do {
                String[] answers = {cQuestions.getString(7),cQuestions.getString(8),cQuestions.getString(9),cQuestions.getString(10)};
                mQuiz.add(new Question(cQuestions.getInt(0),cQuestions.getString(3),difficulty, answers, cQuestions.getInt(4), cQuestions.getInt(5)));
            }while (cQuestions.moveToNext());
        }

        dbHelper.close();
        db.close();

        Collections.shuffle(mQuiz);


        for (int i = 0; i < gameTable.getQuestionsId().size(); i++){
            for (int j = 0; i < mQuiz.size(); i++){
                if(gameTable.getQuestionsId().get(i) == mQuiz.get(j).getId()){
                    mQuiz.remove(j);
                }
            }
        }

        if (!gameTable.isUnlimited()) {
            while (mQuiz.size() > 20) {
                mQuiz.remove(mQuiz.size() - 1);
            }
        }

        Toast.makeText(getApplicationContext(), "Welcome back " + gameTable.getHighScore().getPlayerName(),
                Toast.LENGTH_SHORT).show();

    }

    private void nextQuestion(){  //TODO para ver
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

            gameTable.addQuestionsId(mQuiz.get(rand).getId());
            mQuiz.remove(rand);
        }else{
            Intent newIntent = new Intent(this, ScoreActivity.class);
            newIntent.putExtra("ScorePlayerName", gameTable.getHighScore().getPlayerName());
            newIntent.putExtra("ScoreDifficultyID", gameTable.getHighScore().getDifficulty().getId());
            newIntent.putExtra("ScoreDifficultyName", gameTable.getHighScore().getDifficulty().getName());
            newIntent.putExtra("ScoreCorrectAnswers", gameTable.getHighScore().getCorrectAnswers());
            newIntent.putExtra("Score", gameTable.getHighScore().getScore());
            startActivity(newIntent);
            finish();
        }
    }

    private void setGameTable(){
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "INSERT INTO tblGames(playerName, difficultyID, correctAnswers, score," +
                " unlimited, helpsDisabled, helpFiftyFifty, helpPhone, helpPublic, helpChange)" +
                " VALUES('" + gameTable.getHighScore().getPlayerName() + "', '" + difficulty.getId() + "', '0', '0'" +
                ", '" + gameTable.isUnlimited() + "', '" + gameTable.isHelpsDisabled() + "'" +
                ", '" + gameTable.isHelpFiftyFifty() + "', '" + gameTable.isHelpPhone() + "', '" + gameTable.isHelpPublic() + "', '" + gameTable.isHelpChange() + "')";
        db.execSQL(sql);

        sql = "SELECT id FROM tblGames";
        Cursor c = db.rawQuery(sql,null);
        c.moveToLast();
        gameTable.setId(c.getInt(0));

        dbHelper.close();
        db.close();
    }

    private void setScores(int correctAnswer){
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        if(randQuestion.getCorrectAnswer() == correctAnswer){
            //Dialog correct answer
            gameTable.getHighScore().setScore(gameTable.getHighScore().getScore()+randQuestion.getReward());
            gameTable.getHighScore().setCorrectAnswers(gameTable.getHighScore().getCorrectAnswers()+1);

            String sql = "UPDATE tblGames SET score = '" + gameTable.getHighScore().getScore() +
                    "', correctAnswers = '" + gameTable.getHighScore().getCorrectAnswers() +
                    "' WHERE id = " + gameTable.getId() + ";";
            db.execSQL(sql);

        }else{
            if (gameTable.isUnlimited()){
                //TODO END GAME
            }
            //Dialog wrong answer
        }

        gameTable.addQuestionsId(randQuestion.getId());
        String questions = "";
        for(Integer i : gameTable.getQuestionsId()){
            if (questions.equals("")){
                questions = String.valueOf(i);
            }else {
                questions += ";" + i;
            }
        }

        String sql = "UPDATE tblGames " +
                "SET questions = ' " + questions + " '" +
                "WHERE id = " + gameTable.getId() + ";";
        db.execSQL(sql);

        dbHelper.close();
        db.close();

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
        btn_Help1.setBackgroundResource(R.drawable.ic_50used);

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "UPDATE tblGames " +
                "SET helpFiftyFifty = ' " + true + " '" +
                "WHERE id = " + gameTable.getId() + ";";
        db.execSQL(sql);

        dbHelper.close();
        db.close();
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

    public void contacts(String contact){
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
        final ArrayList<String> contacts = new ArrayList<>();
        contacts.add("Albert Einstein"); // 100%
        contacts.add("Doge"); // 80%
        contacts.add("Harambe"); //55%
        contacts.add("Donald Trump"); // 20%
        contacts.add("SID"); // 1%
        contacts.add("Potato"); //
        contacts.add("Waifu"); // 7 90%5%
        Collections.shuffle(contacts);

        FragmentManager fm = getFragmentManager();
        HelpPhoneDialogFragment helpPhoneDialog = new HelpPhoneDialogFragment();
        helpPhoneDialog.setCancelable(false);
        helpPhoneDialog.show(fm, "fragment_help_phone_dialog");

        btn_Help2.setEnabled(false);

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "UPDATE tblGames " +
                "SET helpPhone = ' " + true + " '" +
                "WHERE id = " + gameTable.getId() + ";";
        db.execSQL(sql);

        dbHelper.close();
        db.close();
    }

    private void helpPublic(){
        String[] answers = {
                "Answer A: " + randQuestion.getAnswers()[0].toString()
                , "Answer B: " + randQuestion.getAnswers()[1].toString()
                , "Answer C: " + randQuestion.getAnswers()[2].toString()
                , "Answer D: " + randQuestion.getAnswers()[3].toString()
        };

        int[] progress = new int[4];

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
                    //answers[randQuestion.getCorrectAnswer()-1] += " " + secondTop;
                    progress[randQuestion.getCorrectAnswer()-1] = secondTop;
                } else{
                    //answers[i] += " " + arrayRand.get(0);
                    progress[i] = arrayRand.get(0);
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
                    //answers[randQuestion.getCorrectAnswer()-1] += " " + top;
                    progress[randQuestion.getCorrectAnswer()-1] = top;
                } else{
                    //answers[i] += " " + arrayRand.get(0);
                    progress[i] = arrayRand.get(0);
                    arrayRand.remove(0);
                }
            }
        }

        FragmentManager fm = getFragmentManager();
        HelpPublicDialogFragment helpPublicDialog = new HelpPublicDialogFragment();
        helpPublicDialog.setCancelable(false);
        helpPublicDialog.setAnswers(answers, progress);
        helpPublicDialog.show(fm, "fragment_help_phone_dialog");

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "UPDATE tblGames " +
                "SET helpPublic = ' " + true + " '" +
                "WHERE id = " + gameTable.getId() + ";";
        db.execSQL(sql);

        dbHelper.close();
        db.close();
    }

    private void helpChange(){
        nextQuestion();

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "UPDATE tblGames " +
                "SET helpChange = ' " + true + " '" +
                "WHERE id = " + gameTable.getId() + ";";
        db.execSQL(sql);

        dbHelper.close();
        db.close();
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
            gameTable.setHelpFiftyFiftyUsed();
        } else if(view.getId() == R.id.btnHelp2){
            helpPhone();
            gameTable.setHelpPhoneUsed();
        } else if(view.getId() == R.id.btnHelp3){
            helpPublic();
            gameTable.setHelpPublicUsed();
        } else if(view.getId() == R.id.btnHelp4){
            helpChange();
            gameTable.setHelpChangeUsed();
            btn_Help4.setEnabled(false);
        }
    }
}
