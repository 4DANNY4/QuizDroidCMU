package estg.ipp.pt.quizdroidcmu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QdDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QuizDroid.db";
    private static final int DATABASE_VERSION = 19;

    public QdDbHelper(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Table Creations
        db.execSQL("PRAGMA foreign_keys = ON;");
        db.execSQL("CREATE TABLE tblDifficulties(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," + //Difficulties Table
                " name VARCHAR(20) NOT NULL," +
                " description VARCHAR(100) NOT NULL)");
        db.execSQL("CREATE TABLE tblAnswers(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," + //Answers Table
                " answers1 VARCHAR(100) NOT NULL," +
                " answers2 VARCHAR(100) NOT NULL," +
                " answers3 VARCHAR(100) NOT NULL,"+
                " answers4 VARCHAR(100) NOT NULL)");
        db.execSQL("CREATE TABLE tblQuestions(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," + //Questions Table
                " difficultyID INTEGER NOT NULL," +
                " answerID INTEGER NOT NULL," +
                " text VARCHAR(100) NOT NULL," +
                " rightAnswer INTEGER NOT NULL," +
                " reward INTEGER NOT NULL," +
                " FOREIGN KEY (difficultyID) REFERENCES tblDifficulties(id)," +
                " FOREIGN KEY (answerID) REFERENCES tblAnswers(id))");
        db.execSQL("CREATE TABLE tblHighscores(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," + //Highscores Table
                " playerName VARCHAR(50) NOT NULL," +
                " difficultyID INTEGER NOT NULL," +
                " correctAnswers INTEGER NOT NULL," +
                " score INTEGER NOT NULL," +
                " unlimited BOOLEAN NOT NULL," +
                " FOREIGN KEY (difficultyID) REFERENCES tblDifficulties(id))");
        db.execSQL("CREATE TABLE tblGames(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," + //Games Table (Games not finished)
                " playerName VARCHAR(50) NOT NULL," +
                " difficultyID INTEGER NOT NULL," +
                " correctAnswers INTEGER NOT NULL," +
                " score INTEGER NOT NULL," +
                " questions VARCHAR(100)," +
                " unlimited BOOLEAN NOT NULL," +
                " helpsDisabled BOOLEAN NOT NULL," +
                " helpFiftyFifty BOOLEAN NOT NULL," +
                " helpPhone BOOLEAN NOT NULL," +
                " helpPublic BOOLEAN NOT NULL," +
                " helpChange BOOLEAN NOT NULL," +
                " FOREIGN KEY (difficultyID) REFERENCES tblDifficulties(id))");

        //Inserts
        db.execSQL("INSERT INTO tblDifficulties(name, description) VALUES('Easy','For casual beginners.')");
        db.execSQL("INSERT INTO tblDifficulties(name, description) VALUES('Medium','For a more challenging experience.')");
        db.execSQL("INSERT INTO tblDifficulties(name, description) VALUES('Java','Language Fundamentals - General Questions.')");

        //Difficulty -> Easy

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('4','2','3','1')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('1','1+1?','1','2','100')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('1','2','4','5')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('1','2+2?','2','3','100')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('4','2','3','8')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('1','3-1?','3','2','150')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('4','2','3','7')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('1','5+2?','4','4','150')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('18','15','16','14')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('1','8+7?','5','2','200')");

        //Difficulty -> Normal

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('largest railway station','highest railway station','longest railway station','None of the above')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('2','Grand Central Terminal, Park Avenue, New York is the worlds?','6','1','100')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('Behavior of human beings','Insects','The origin and history of technical and scientific terms','he formation of rocks')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('2','Entomology is the science that studies?','7','2','100')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('Asia','Africa','Europe','Australia')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('2','Eritrea, which became the 182nd member of the UN in 1993, is in the continent of','8','2','100')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('Physics and Chemistry','Physiology or Medicine','Literature, Peace and Economics','All of the above')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('2','For which of the following disciplines is Nobel Prize awarded?','9','4','100')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('1839','1843','1833','1848')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('2','First Afghan War took place in','10','1','100')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('China and Britain','China and France','China and Egypt','China and Greek')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('2','First China War was fought between','11','1','100')");

        //Difficulty -> Java

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('int [] myList = {\"1\", \"2\", \"3\"};','int [] myList = (5, 8, 2);','int myList [] [] = {4,9,7,0};','int myList [] = {4, 3, 7};')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('3','Which will legally declare, construct, and initialize an array?','12','4','200')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('interface','string','Float','unsigned')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('3','Which is a valid keyword in java?','13','1','200')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('public double methoda();','public final double methoda();','static void methoda(double d1);','protected void methoda(double d1);')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('3','Which is the valid declarations within an interface definition?','14','1','200')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('boolean b1 = 0;','boolean b2 = \"false\";','boolean b3 = false;','boolean b4 = Boolean.false();')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('3','Which one is a valid declaration of a boolean?','15','3','200')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('-128 to 127','-(215) to (215) - 1','0 to 32767','0 to 65535')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('3','What is the numerical range of a char?','16','4','200')");



//        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
//                " VALUES('a1','a2','a3','a4')");
//        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
//                " VALUES('3','quest','+++','1','200')");




        //db.execSQL("INSERT INTO tblHighscores(playerName, difficultyID, correctAnswers, score, unlimited)" +
        //        " VALUES('GameEasy','1','30','2000','1')");
        //db.execSQL("INSERT INTO tblHighscores(playerName, difficultyID, correctAnswers, score, unlimited)" +
        //        " VALUES('GameMedium','2','15','1623','0')");
        //db.execSQL("INSERT INTO tblHighscores(playerName, difficultyID, correctAnswers, score, unlimited)" +
        //        " VALUES('GameHard','3','1','30','0')");
        //db.execSQL("INSERT INTO tblHighscores(playerName, difficultyID, correctAnswers, score, unlimited)" +
        //        " VALUES('GameHard2','3','6','400','1')");
        //db.execSQL("INSERT INTO tblHighscores(playerName, difficultyID, correctAnswers, score, unlimited)" +
        //        " VALUES('GameHard3','3','3','160','0')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE tblDifficulties");
        db.execSQL("DROP TABLE tblQuestions");
        db.execSQL("DROP TABLE tblAnswers");
        db.execSQL("DROP TABLE tblHighscores");
        db.execSQL("DROP TABLE tblGames");
        this.onCreate(db);
    }
}
