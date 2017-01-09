package estg.ipp.pt.quizdroidcmu;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Implementation of App Widget functionality.
 */
public class QuizWidget extends AppWidgetProvider {
    private Difficulty difficulty = new Difficulty(1, "", "");
    private Question question = new Question(0,"",difficulty,new String[]{},0,0);
    private int score = 0;
    public static String BTN_ANSWER1_WIDGET = "BtnAnswer1Widget";
    public static String BTN_ANSWER2_WIDGET = "BtnAnswer2Widget";
    public static String BTN_ANSWER3_WIDGET = "BtnAnswer3Widget";
    public static String BTN_ANSWER4_WIDGET = "BtnAnswer4Widget";

    private void getQuestion(Context context, int id){
        System.out.println("QuizWidget -> getQuestion");
        QdDbHelper dbHelper = new QdDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM tblQuestions LEFT JOIN tblAnswers ON tblQuestions.answerID = tblAnswers.id WHERE tblQuestions.id = '" + id  + "'";
        Cursor c = db.rawQuery(sql,null);
        if (c != null && c.moveToFirst()){
            String[] answers = {c.getString(7),c.getString(8),c.getString(9),c.getString(10)};
            question = new Question(c.getInt(0),c.getString(3),difficulty, answers, c.getInt(4), c.getInt(5));
        }
        dbHelper.close();
        db.close();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("QuizWidget -> onReceive");
        //getQuestion(context, intent.getIntExtra("QuestionID", 0));
        question.setText(intent.getStringExtra("QuizQuestion"));
        String[] answers = {intent.getStringExtra("QuizAnswers1"),
                intent.getStringExtra("QuizAnswers2"),
                intent.getStringExtra("QuizAnswers3"),
                intent.getStringExtra("QuizAnswers4")};
        question.setAnswers(answers);

        score = intent.getIntExtra("QuestionScore", 0);
        if (intent.getAction().equals(BTN_ANSWER1_WIDGET)) {
            if(question.getCorrectAnswer() == 1){
                score += question.getReward();
            }
        } else if (intent.getAction().equals(BTN_ANSWER2_WIDGET)) {
            if(question.getCorrectAnswer() == 2){
                score += question.getReward();
            }
        } else if (intent.getAction().equals(BTN_ANSWER3_WIDGET)) {
            if(question.getCorrectAnswer() == 3){
                score += question.getReward();
            }
        } else if (intent.getAction().equals(BTN_ANSWER4_WIDGET)) {
            if(question.getCorrectAnswer() == 4){
                score += question.getReward();
            }
        } else {
            super.onReceive(context, intent);
        }

        ComponentName thisWidget = new ComponentName(context, QuizWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisWidget));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        System.out.println("QuizWidget -> onUpdate");

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        System.out.println("QuizWidget -> updateAppWidget");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.quiz_widget);

        //initData(context, question.getId());

        remoteViews.setTextViewText(R.id.txtScoreWidget, String.valueOf(score));
        remoteViews.setTextViewText(R.id.txtQuestionWidget, question.getText());
        remoteViews.setTextViewText(R.id.btnAnswer1Widget, question.getAnswers()[0]);
        remoteViews.setTextViewText(R.id.btnAnswer2Widget, question.getAnswers()[1]);
        remoteViews.setTextViewText(R.id.btnAnswer3Widget, question.getAnswers()[2]);
        remoteViews.setTextViewText(R.id.btnAnswer4Widget, question.getAnswers()[3]);

        Intent intent = new Intent(context, QuizWidget.class);
        intent.setAction(BTN_ANSWER1_WIDGET);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnAnswer1Widget, pendingIntent);

        intent = new Intent(context, QuizWidget.class);
        intent.setAction(BTN_ANSWER2_WIDGET);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnAnswer2Widget, pendingIntent);

        intent = new Intent(context, QuizWidget.class);
        intent.setAction(BTN_ANSWER3_WIDGET);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnAnswer3Widget, pendingIntent);

        intent = new Intent(context, QuizWidget.class);
        intent.setAction(BTN_ANSWER4_WIDGET);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnAnswer4Widget, pendingIntent);

        intent.putExtra("QuestionID", question.getId());
        intent.putExtra("QuestionScore", score);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
}

