package estg.ipp.pt.quizdroidcmu;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.SystemClock;

import java.util.ArrayList;

public class QuizTask extends AsyncTask<Integer,Integer,Integer>{

    private Difficulty difficulty = new Difficulty(1, "", "");
    private ArrayList<Question> mQuiz = new ArrayList<>();
    Context mContext;
    private int time;

    public QuizTask(Context mContext, int time) {
        this.mContext = mContext;
        this.time = time;
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        System.out.println("QuizTask -> doInBackground");

        SystemClock.sleep(params[0]);
        mQuiz.remove(0);

        return null;
    }

    @Override
    protected void onPreExecute() {
        System.out.println("QuizTask -> onPreExecute");
        //ArrayList<Question> mQuiz = new ArrayList<>();

        QdDbHelper dbHelper = new QdDbHelper(mContext);
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

        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        System.out.println("QuizTask -> onProgressUpdate");
        Intent intent = new Intent("QUIZ");
        intent.putExtra("QuizQuestion",mQuiz.get(0).getText());
        intent.putExtra("QuizAnswers1",mQuiz.get(0).getAnswers()[0]);
        intent.putExtra("QuizAnswers2",mQuiz.get(0).getAnswers()[1]);
        intent.putExtra("QuizAnswers3",mQuiz.get(0).getAnswers()[2]);
        intent.putExtra("QuizAnswers4",mQuiz.get(0).getAnswers()[3]);
        mContext.sendBroadcast(intent);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        System.out.println("QuizTask -> onPostExecute");
        super.onPostExecute(integer);
    }

    @Override
    protected void onCancelled(Integer integer) {
        System.out.println("QuizTask -> onCancelled");
        super.onCancelled(integer);
    }
}
