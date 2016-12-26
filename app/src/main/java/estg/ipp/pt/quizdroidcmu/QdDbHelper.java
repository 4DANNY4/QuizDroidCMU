package estg.ipp.pt.quizdroidcmu;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QdDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "QuizDroid.db";
    private static final int DATABASE_VERSION = 12;

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
                " FOREIGN KEY (difficultyID) REFERENCES tblDifficulties(id))");
        db.execSQL("CREATE TABLE tblGames(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," + //Games Table (Games not finished)
                " playerName VARCHAR(50) NOT NULL," +
                " difficultyID INTEGER NOT NULL," +
                " correctAnswers INTEGER NOT NULL," +
                " score INTEGER NOT NULL," +
                " FOREIGN KEY (difficultyID) REFERENCES tblDifficulties(id))");

        //Inserts
        db.execSQL("INSERT INTO tblDifficulties(name, description) VALUES('Easy','For casual beginners.')");
        db.execSQL("INSERT INTO tblDifficulties(name, description) VALUES('Medium','For a more challenging experience.')");
        db.execSQL("INSERT INTO tblDifficulties(name, description) VALUES('Hard','For the ultimate challenge!')");

        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('4','2','3','1')");
        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('1','2','4','5')");
        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('4','2','3','8')");
        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('4','2','3','7')");
        db.execSQL("INSERT INTO tblAnswers(answers1, answers2, answers3, answers4)" +
                " VALUES('18','15','16','14')");

        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('1','1+1?','1','2','100')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('1','2+2?','2','3','100')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('1','3-1?','3','2','150')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('1','5+2?','4','4','150')");
        db.execSQL("INSERT INTO tblQuestions(difficultyID, text, answerID, rightAnswer, reward)" +
                " VALUES('1','8+7?','5','2','200')");

        db.execSQL("INSERT INTO tblHighscores(playerName, difficultyID, correctAnswers, score)" +
                " VALUES('GameEasy','1','30','2000')");
        db.execSQL("INSERT INTO tblHighscores(playerName, difficultyID, correctAnswers, score)" +
                " VALUES('GameMedium','2','15','1623')");
        db.execSQL("INSERT INTO tblHighscores(playerName, difficultyID, correctAnswers, score)" +
                " VALUES('GameHard','3','2','30')");
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
