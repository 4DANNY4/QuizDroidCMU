package estg.ipp.pt.quizdroidcmu;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class QuizService extends Service {

    public static final String INTENT_ACTION_UPDATE = "android.appwidget.action.APPWIDGET_UPDATE";
    public static final String INTENT_ACTION_WAIT = "android.appwidget.action.APPWIDGET_WAIT";

    private Difficulty difficulty = new Difficulty(1, "", "");
    private ArrayList<Question> mQuiz = new ArrayList<>();

    private SharedPreferences mSettings;

    String timer;

    @Override
    public void onCreate() {
        super.onCreate();
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("RESPONDED");
        filter.addAction("UPDATE_PREF");

        registerReceiver(receiver, filter);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        initData();
        Intent newIntent = new Intent(INTENT_ACTION_UPDATE);
        newIntent.putExtra("QuizQuestion",mQuiz.get(0).getText());
        newIntent.putExtra("QuizAnswers1",mQuiz.get(0).getAnswers()[0]);
        newIntent.putExtra("QuizAnswers2",mQuiz.get(0).getAnswers()[1]);
        newIntent.putExtra("QuizAnswers3",mQuiz.get(0).getAnswers()[2]);
        newIntent.putExtra("QuizAnswers4",mQuiz.get(0).getAnswers()[3]);
        sendBroadcast(newIntent);
        System.out.println("QuizService -> Question: " + mQuiz.get(0).getText());
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        //Toast.makeText(getBaseContext(), "Service Stopped", Toast.LENGTH_SHORT).show();
    }

    private void initData(){
        System.out.println("QuizService -> initData"); //TODO: REMOVE TEST LINE

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM tblQuestions LEFT JOIN tblAnswers ON tblQuestions.answerID = tblAnswers.id";
        Cursor c = db.rawQuery(sql,null);
        if (c != null && c.moveToFirst()){
            do {
                String[] answers = {c.getString(7),c.getString(8),c.getString(9),c.getString(10)};
                mQuiz.add(new Question(c.getInt(0),c.getString(3),difficulty, answers, c.getInt(4), c.getInt(5)));
            }while (c.moveToNext());
        }

        Collections.shuffle(mQuiz);

        dbHelper.close();
        db.close();
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("QuizService -> Receiving " + intent.getAction());

            String action = intent.getAction();
            if(action.equals("RESPONDED")){
                if(intent.getStringExtra("Key").equals("BtnAnswer1Widget")) {
                    waitForTimer(1);
                } else if(intent.getStringExtra("Key").equals("BtnAnswer2Widget")) {
                    waitForTimer(2);
                } else if(intent.getStringExtra("Key").equals("BtnAnswer3Widget")) {
                    waitForTimer(3);
                } else if(intent.getStringExtra("Key").equals("BtnAnswer4Widget")) {
                    waitForTimer(4);
                }
                widgetWait();
                sendQuestion();
            } else if(action.equals("UPDATE_PREF")) {
                timer = intent.getStringExtra("TIMER");
                System.out.println("UPDATE PREF -> " + timer); //TODO: REMOVE COMMENT
                mSettings.edit().putString("pref_widget_timer", timer).apply();
            }
        }
    };

    private void sendQuestion() {
        mQuiz.remove(0);
        if(mQuiz.size() == 0) {
            initData();
        }
        Intent newIntent = new Intent(INTENT_ACTION_UPDATE);
        newIntent.putExtra("QuizQuestion",mQuiz.get(0).getText());
        newIntent.putExtra("QuizAnswers1",mQuiz.get(0).getAnswers()[0]);
        newIntent.putExtra("QuizAnswers2",mQuiz.get(0).getAnswers()[1]);
        newIntent.putExtra("QuizAnswers3",mQuiz.get(0).getAnswers()[2]);
        newIntent.putExtra("QuizAnswers4",mQuiz.get(0).getAnswers()[3]);
        sendBroadcast(newIntent);
        System.out.println("QuizService -> Question: " + mQuiz.get(0).getText());
    }

    private void waitForTimer(int answer) {
        Intent newIntent = new Intent(INTENT_ACTION_WAIT);
        if (mQuiz.get(0).getCorrectAnswer() == answer) {
            newIntent.putExtra("IsCorrect", "Your answer is Correct!");
        } else {
            newIntent.putExtra("IsCorrect", "Your answer is Incorrect!");
        }
        sendBroadcast(newIntent);
    }

    private void widgetWait() {
        int timer = Integer.parseInt(mSettings.getString("pref_widget_timer", "10000"));
        System.out.println("Wait Timer: " + timer);
        try {
            Thread.sleep(timer);
            System.out.println("New question available!");
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.drawable.logo)
                            .setContentTitle("New question available")
                            .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.logo))
                            .setContentText("Check widget to respond.");
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        } catch (InterruptedException e) { }
    }

}
