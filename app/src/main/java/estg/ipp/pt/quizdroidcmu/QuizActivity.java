package estg.ipp.pt.quizdroidcmu;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.TransitionDrawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
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
    private TextView txt_question, txt_gameScore;
    private Button btn_Answer1, btn_Answer2, btn_Answer3, btn_Answer4;
    private Button btn_Help1, btn_Help2, btn_Help3, btn_Help4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Intent intent = getIntent();

        txt_question = (TextView) findViewById(R.id.txtQuestion);
        txt_gameScore = (TextView) findViewById(R.id.txt_gameScore);

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
            //lastQuestion();
        } else{
            difficulty = new Difficulty(
                    intent.getIntExtra("DifficultyID", 0),
                    intent.getStringExtra("DifficultyName"),
                    intent.getStringExtra("DifficultyDescription")
            );
            SharedPreferences mSettings = PreferenceManager.getDefaultSharedPreferences(this);
            gameTable = new Game(0, new Highscore(0, "", difficulty, 0, 0, false),
                    mSettings.getBoolean("pref_unlimited", false), mSettings.getBoolean("pref_helpsDisabled", false));
            setIdGameTable();
            final AlertDialog.Builder playerNameDialog = new AlertDialog.Builder(this);
            final EditText input = new EditText(this);
            input.setSingleLine();
            input.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
            InputFilter[] f = new InputFilter[1];
            f[0] = new InputFilter.LengthFilter(10);
            input.setFilters(f);
            final String txt = "Player Name:";
            playerNameDialog.setCancelable(false);
            playerNameDialog.setTitle(txt);
            playerNameDialog.setView(input);
            playerNameDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String tmpName = input.getText().toString().trim();
                    if(!tmpName.equals("")) {
                        gameTable.getHighScore().setPlayerName(tmpName);
                    } else {
                        gameTable.getHighScore().setPlayerName("Guest");
                    }
                    setGameTable();
                    Toast.makeText(getApplicationContext(), "Player: " + gameTable.getHighScore().getPlayerName(),
                            Toast.LENGTH_SHORT).show();
                }
            });
            playerNameDialog.show().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

            if(gameTable.isUnlimited()){
                initDataUnlimited();
            }else{
                initDataLimit();
            }

            txt_gameScore.setText(String.valueOf(0));
            nextQuestion();
        }

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
            //TODO btn_Help3.setBackgroundResource(R.drawable.-----used);
            gameTable.setHelpChangeUsed();
            btn_Help4.setEnabled(false);
            btn_Help4.setVisibility(View.GONE);

            QdDbHelper dbHelper = new QdDbHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String sql = "UPDATE tblGames " +
                    "SET helpChange = ' " + true + " '" +
                    "WHERE id = " + gameTable.getId() + ";";
            db.execSQL(sql);

            dbHelper.close();
            db.close();
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
            if (cGame.getString(6).equals("false")) { //unlimited
                gameTable.setHighScore(new Highscore(0, cGame.getString(1), difficulty, cGame.getInt(3), cGame.getInt(4), false));
                gameTable.setUnlimited(false);
            } else{
                gameTable.setHighScore(new Highscore(0, cGame.getString(1), difficulty, cGame.getInt(3), cGame.getInt(4), true));
                gameTable.setUnlimited(true);
            }
            if (cGame.getString(7).equals("true")) { //helpsDisabled
                gameTable.setHelpsDisabled(true);
                btn_Help1.setVisibility(View.GONE);
                btn_Help2.setVisibility(View.GONE);
                btn_Help3.setVisibility(View.GONE);
                btn_Help4.setVisibility(View.GONE);
            } else {
                gameTable.setHelpsDisabled(false);
                if (cGame.getString(8).equals("true")) { //helpFiftyFifty
                    gameTable.setHelpFiftyFiftyUsed();
                    btn_Help1.setEnabled(false);
                    btn_Help1.setBackgroundResource(R.drawable.ic_50used);
                }
                if (cGame.getString(9).equals("true")) { //helpPhone
                    gameTable.setHelpPhoneUsed();
                    btn_Help2.setEnabled(false);
                    //TODO btn_Help3.setBackgroundResource(R.drawable.-----used);
                }
                if (cGame.getString(10).equals("true")) { //helpPublic
                    gameTable.setHelpPublicUsed();
                    btn_Help3.setEnabled(false);
                    //TODO btn_Help3.setBackgroundResource(R.drawable.-----used);
                }
                if (cGame.getString(11).equals("true")) { //helpChange
                    gameTable.setHelpChangeUsed();
                    btn_Help4.setEnabled(false);
                    //TODO btn_Help4.setBackgroundResource(R.drawable.-----used);
                }
            }
        }


        if(cGame.getString(5) != null){
            String questions = cGame.getString(5);
            String[] qSplit = questions.split(";");
            for (String s : qSplit){
                gameTable.addQuestionsId(Integer.valueOf(s));
            }
        }else{
            gameTable.addQuestionsId(cGame.getInt(5));
        }

        String sqlQuestions = "SELECT * FROM tblQuestions LEFT JOIN tblAnswers ON tblQuestions.answerID = tblAnswers.id WHERE tblQuestions.difficultyID = '" + gameTable.getHighScore().getDifficulty().getId() + "'";
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
            for (int j = 0; j < mQuiz.size(); j++){
                if(gameTable.getQuestionsId().get(i) == mQuiz.get(j).getId()){
                    if (i == gameTable.getQuestionsId().size()-1){
                        mQuiz.add(0, mQuiz.get(j));
                        mQuiz.remove(j+1);
                    } else {
                        mQuiz.remove(j);
                    }
                }

            }
        }

        if (!gameTable.isUnlimited()) {
            while (mQuiz.size() > (20 - gameTable.getQuestionsId().size())) {
                mQuiz.remove(mQuiz.size() - 1);
            }
        }

        randQuestion = new Question(mQuiz.get(0));
        txt_question.setText(randQuestion.getText());
        btn_Answer1.setText(randQuestion.getAnswers()[0]);
        btn_Answer2.setText(randQuestion.getAnswers()[1]);
        btn_Answer3.setText(randQuestion.getAnswers()[2]);
        btn_Answer4.setText(randQuestion.getAnswers()[3]);

        if(gameTable.isUnlimited()) {
            txt_gameScore.setText(String.valueOf(gameTable.getHighScore().getCorrectAnswers()));
        }else{
            txt_gameScore.setText(String.valueOf(gameTable.getHighScore().getScore()));
        }

        mQuiz.remove(0);

        Toast.makeText(getApplicationContext(), "Welcome back " + gameTable.getHighScore().getPlayerName(),
                Toast.LENGTH_SHORT).show();

    }

    private void nextQuestion(){  //TODO para ver
        //Restoring button colors
        btn_Answer1.setBackgroundResource(R.drawable.box2shapewhite);
        btn_Answer2.setBackgroundResource(R.drawable.box2shapewhite);
        btn_Answer3.setBackgroundResource(R.drawable.box2shapewhite);
        btn_Answer4.setBackgroundResource(R.drawable.box2shapewhite);

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

            QdDbHelper dbHelper = new QdDbHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            gameTable.addQuestionsId(randQuestion.getId());
            String questions = "";
            for(Integer id : gameTable.getQuestionsId()){
                if (questions.equals("")) {
                    questions = String.valueOf(id);
                }else {
                    questions += ";" + id;
                }
            }

            String sql = "UPDATE tblGames" +
                    " SET questions = '" + questions + "'" +
                    " WHERE id = " + gameTable.getId() + ";";
            db.execSQL(sql);

            dbHelper.close();
            db.close();

            mQuiz.remove(rand);
        }else{
            Intent newIntent = new Intent(this, ScoreActivity.class);
            newIntent.putExtra("ScorePlayerName", gameTable.getHighScore().getPlayerName());
            newIntent.putExtra("ScoreDifficultyID", gameTable.getHighScore().getDifficulty().getId());
            newIntent.putExtra("ScoreDifficultyName", gameTable.getHighScore().getDifficulty().getName());
            newIntent.putExtra("ScoreCorrectAnswers", gameTable.getHighScore().getCorrectAnswers());
            newIntent.putExtra("Score", gameTable.getHighScore().getScore());
            newIntent.putExtra("Unlimited", gameTable.isUnlimited());
            newIntent.putExtra("SavedGameID", gameTable.getId());
            startActivity(newIntent);
            finish();
        }
    }

    private void setIdGameTable(){ //TODO ver isto.. do id
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "INSERT INTO tblGames(playerName, difficultyID, correctAnswers, score, unlimited, helpsDisabled, helpFiftyFifty, helpPhone, helpPublic, helpChange)" +
                " VALUES('NOVO', '1', '0', '0', 'false', 'false', 'false', 'false', 'false', 'false')";
        db.execSQL(sql);

        sql = "SELECT id FROM tblGames ORDER BY tblGames.id DESC LIMIT 1";
        Cursor c = db.rawQuery(sql,null);

        c.moveToFirst();
        gameTable.setId(c.getInt(0));

        dbHelper.close();
        db.close();
    }

    private void setGameTable(){
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "UPDATE tblGames SET" +
                " playerName = '" + gameTable.getHighScore().getPlayerName() + "'" +
                ", difficultyID = '" + difficulty.getId() + "'" +
                ", correctAnswers = '" + 0 + "'" +
                ", score = '" + 0 + "'" +
                ", unlimited = '" + gameTable.isUnlimited() + "'" +
                ", helpsDisabled = '" + gameTable.isHelpsDisabled() + "'" +
                ", helpFiftyFifty = '" + gameTable.isHelpFiftyFifty() + "'" +
                ", helpPhone = '" + gameTable.isHelpPhone() + "'" +
                ", helpPublic = '" + gameTable.isHelpPublic() + "'" +
                ", helpChange = '" + gameTable.isHelpChange() + "'" +
                " WHERE tblGames.id = '" + gameTable.getId() + "'";
        db.execSQL(sql);

        dbHelper.close();
        db.close();
    }

    private void setScores(final int answer){
        final QdDbHelper dbHelper = new QdDbHelper(this);
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        Animation anim = new AlphaAnimation(0.5f, 1);
        anim.setDuration(1500);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(0);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationStart(Animation anim) { }
            public void onAnimationRepeat(Animation anim) { }
            @Override
            public void onAnimationEnd(Animation anim) {
                if(randQuestion.getCorrectAnswer() == answer){
                    gameTable.getHighScore().setScore(gameTable.getHighScore().getScore()+randQuestion.getReward());
                    gameTable.getHighScore().setCorrectAnswers(gameTable.getHighScore().getCorrectAnswers()+1);

                    if(gameTable.isUnlimited()) {
                        txt_gameScore.setText(String.valueOf(gameTable.getHighScore().getCorrectAnswers()));
                    }else{
                        txt_gameScore.setText(String.valueOf(gameTable.getHighScore().getScore()));
                    }
                    String sql = "UPDATE tblGames SET score = '" + gameTable.getHighScore().getScore() +
                            "', correctAnswers = '" + gameTable.getHighScore().getCorrectAnswers() +
                            "' WHERE tblGames.id = '" + gameTable.getId() + "'";
                    db.execSQL(sql);
                } else {
                    if (gameTable.isUnlimited()) {
                        Intent newIntent = new Intent(getBaseContext(), ScoreActivity.class);
                        newIntent.putExtra("ScorePlayerName", gameTable.getHighScore().getPlayerName());
                        newIntent.putExtra("ScoreDifficultyID", gameTable.getHighScore().getDifficulty().getId());
                        newIntent.putExtra("ScoreDifficultyName", gameTable.getHighScore().getDifficulty().getName());
                        newIntent.putExtra("ScoreCorrectAnswers", gameTable.getHighScore().getCorrectAnswers());
                        newIntent.putExtra("Score", gameTable.getHighScore().getScore());
                        newIntent.putExtra("Unlimited", gameTable.isUnlimited());
                        newIntent.putExtra("SavedGameID", gameTable.getId());
                        startActivity(newIntent);
                        finish();
                        return;
                    }
                }
                dbHelper.close();
                db.close();

                btn_Answer1.setVisibility(View.VISIBLE);
                btn_Answer2.setVisibility(View.VISIBLE);
                btn_Answer3.setVisibility(View.VISIBLE);
                btn_Answer4.setVisibility(View.VISIBLE);
                btn_Answer1.setEnabled(true);
                btn_Answer2.setEnabled(true);
                btn_Answer3.setEnabled(true);
                btn_Answer4.setEnabled(true);
                nextQuestion();
            }
        });

        if(randQuestion.getCorrectAnswer() == answer){
            if(answer == 1) {
                btn_Answer1.setBackgroundResource(R.drawable.box2shapegreen);
                btn_Answer1.startAnimation(anim);
            } else if(answer == 2) {
                btn_Answer2.setBackgroundResource(R.drawable.box2shapegreen);
                btn_Answer2.startAnimation(anim);
            } else if(answer == 3 ) {
                btn_Answer3.setBackgroundResource(R.drawable.box2shapegreen);
                btn_Answer3.startAnimation(anim);
            } else {
                btn_Answer4.setBackgroundResource(R.drawable.box2shapegreen);
                btn_Answer4.startAnimation(anim);
            }
        }else{
            if(answer == 1) {
                btn_Answer1.setBackgroundResource(R.drawable.box2shapered);
                btn_Answer1.startAnimation(anim);
            } else if(answer == 2) {
                btn_Answer2.setBackgroundResource(R.drawable.box2shapered);
                btn_Answer2.startAnimation(anim);
            } else if(answer == 3 ) {
                btn_Answer3.setBackgroundResource(R.drawable.box2shapered);
                btn_Answer3.startAnimation(anim);
            } else {
                btn_Answer4.setBackgroundResource(R.drawable.box2shapered);
                btn_Answer4.startAnimation(anim);
            }
        }
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
                "SET helpFiftyFifty = '" + true + "'" +
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
        contacts.add("Potato"); // 90%
        contacts.add("Waifu"); // 75%
        Collections.shuffle(contacts);

        FragmentManager fm = getFragmentManager();
        HelpPhoneDialogFragment helpPhoneDialog = new HelpPhoneDialogFragment();
        helpPhoneDialog.setCancelable(false);
        helpPhoneDialog.show(fm, "fragment_help_phone_dialog");

        btn_Help2.setEnabled(false);
        btn_Help2.setBackgroundResource(R.drawable.ic_local_phone_black_48dp_used);

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = "UPDATE tblGames " +
                "SET helpPhone = '" + true + "'" +
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

        //System.out.println(arrayRand.toString());
        //System.out.println(rand);

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
        helpPublicDialog.setAnswers(answers, progress);
        helpPublicDialog.setCancelable(false);
        helpPublicDialog.show(fm, "fragment_help_phone_dialog");

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        btn_Help3.setEnabled(false);
        btn_Help3.setBackgroundResource(R.drawable.ic_people_black_48dp_used);

        String sql = "UPDATE tblGames " +
                "SET helpPublic = '" + true + "'" +
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
                "SET helpChange = '" + true + "'" +
                "WHERE id = " + gameTable.getId() + ";";
        db.execSQL(sql);

        dbHelper.close();
        db.close();


        btn_Help4.setEnabled(false);
        btn_Help4.setBackgroundResource(R.drawable.ic_autorenew_black_48dp_used);
    }

    @Override
    public void onClick(View view) {
        Animation anim = new AlphaAnimation(0.5f, 1);
        anim.setDuration(600);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(2);
        anim.setRepeatMode(Animation.REVERSE);
        if (view.getId() == R.id.btnAnswer1){
            btn_Answer1.setEnabled(false);
            btn_Answer2.setEnabled(false);
            btn_Answer3.setEnabled(false);
            btn_Answer4.setEnabled(false);
            btn_Answer1.setBackgroundResource(R.drawable.box2shapeorange);
            anim.setAnimationListener(new Animation.AnimationListener(){
                public void onAnimationStart(Animation anim) { }
                public void onAnimationRepeat(Animation anim) { }
                @Override
                public void onAnimationEnd(Animation anim) {
                    setScores(1);
                }
            });
            btn_Answer1.startAnimation(anim);
        } else if(view.getId() == R.id.btnAnswer2){
            btn_Answer1.setEnabled(false);
            btn_Answer2.setEnabled(false);
            btn_Answer3.setEnabled(false);
            btn_Answer4.setEnabled(false);
            btn_Answer2.setBackgroundResource(R.drawable.box2shapeorange);
            anim.setAnimationListener(new Animation.AnimationListener(){
                public void onAnimationStart(Animation anim) { }
                public void onAnimationRepeat(Animation anim) { }
                @Override
                public void onAnimationEnd(Animation anim) {
                    setScores(2);
                }
            });
            btn_Answer2.startAnimation(anim);
        } else if(view.getId() == R.id.btnAnswer3){
            btn_Answer1.setEnabled(false);
            btn_Answer2.setEnabled(false);
            btn_Answer3.setEnabled(false);
            btn_Answer4.setEnabled(false);
            btn_Answer3.setBackgroundResource(R.drawable.box2shapeorange);
            anim.setAnimationListener(new Animation.AnimationListener(){
                public void onAnimationStart(Animation anim) { }
                public void onAnimationRepeat(Animation anim) { }
                @Override
                public void onAnimationEnd(Animation anim) {
                    setScores(3);
                }
            });
            btn_Answer3.startAnimation(anim);
        } else if(view.getId() == R.id.btnAnswer4){
            btn_Answer1.setEnabled(false);
            btn_Answer2.setEnabled(false);
            btn_Answer3.setEnabled(false);
            btn_Answer4.setEnabled(false);
            btn_Answer4.setBackgroundResource(R.drawable.box2shapeorange);
            anim.setAnimationListener(new Animation.AnimationListener(){
                public void onAnimationStart(Animation anim) { }
                public void onAnimationRepeat(Animation anim) { }
                @Override
                public void onAnimationEnd(Animation anim) {
                    setScores(4);
                }
            });
            btn_Answer4.startAnimation(anim);
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
        }
    }

}
