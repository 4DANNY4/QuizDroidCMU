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
import android.widget.TextView;

public class QuizWidgetProvider extends AppWidgetProvider {
    private Difficulty difficulty = new Difficulty(1, "", "");
    private Question question = new Question(0,"",difficulty,new String[]{"","","",""},0,0);

    public static String BTN_ANSWER1_WIDGET = "BtnAnswer1Widget";
    public static String BTN_ANSWER2_WIDGET = "BtnAnswer2Widget";
    public static String BTN_ANSWER3_WIDGET = "BtnAnswer3Widget";
    public static String BTN_ANSWER4_WIDGET = "BtnAnswer4Widget";

    private String isCorrect;
    private boolean wait = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("QuizWidgetProvider -> onReceive");

        ComponentName thisWidget = new ComponentName(context, QuizWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);

        if(intent.getAction().equals(QuizService.INTENT_ACTION_UPDATE)) {
            wait = false;
            question.setText(intent.getStringExtra("QuizQuestion"));
            String[] answers = {intent.getStringExtra("QuizAnswers1"),
                    intent.getStringExtra("QuizAnswers2"),
                    intent.getStringExtra("QuizAnswers3"),
                    intent.getStringExtra("QuizAnswers4")};
            question.setAnswers(answers);
            System.out.println("OnReceive -> HAS EXTRA!");

            System.out.println("OnReceive -> Question: " + question.getText());

            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisWidget));
            super.onReceive(context, intent);

        } else if(intent.getAction().equals(QuizService.INTENT_ACTION_WAIT)) {
            wait = true;

            isCorrect = intent.getStringExtra("IsCorrect");

            onUpdate(context, appWidgetManager, appWidgetManager.getAppWidgetIds(thisWidget));
            super.onReceive(context, intent);

        } else {
            Intent newIntent = new Intent("RESPONDED");
            newIntent.putExtra("Key", intent.getAction());
            context.sendBroadcast(newIntent);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        System.out.println("QuizWidgetProvider -> onUpdate");

        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            int appWidgetId = appWidgetIds[i];
            if(!wait) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            } else {
                updateAppWidgetWait(context, appWidgetManager, appWidgetId);
            }
        }

    }

    private void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        System.out.println("QuizWidgetProvider -> updateAppWidget");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.quiz_widget);

        remoteViews.setTextViewText(R.id.txtQuestionWidget, question.getText());
        remoteViews.setTextViewText(R.id.btnAnswer1Widget, question.getAnswers()[0]);
        remoteViews.setTextViewText(R.id.btnAnswer2Widget, question.getAnswers()[1]);
        remoteViews.setTextViewText(R.id.btnAnswer3Widget, question.getAnswers()[2]);
        remoteViews.setTextViewText(R.id.btnAnswer4Widget, question.getAnswers()[3]);

        Intent intent = new Intent(context, QuizWidgetProvider.class);
        intent.setAction(BTN_ANSWER1_WIDGET);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnAnswer1Widget, pendingIntent);

        intent = new Intent(context, QuizWidgetProvider.class);
        intent.setAction(BTN_ANSWER2_WIDGET);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnAnswer2Widget, pendingIntent);

        intent = new Intent(context, QuizWidgetProvider.class);
        intent.setAction(BTN_ANSWER3_WIDGET);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnAnswer3Widget, pendingIntent);

        intent = new Intent(context, QuizWidgetProvider.class);
        intent.setAction(BTN_ANSWER4_WIDGET);
        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btnAnswer4Widget, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private void updateAppWidgetWait(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        System.out.println("QuizWidgetProvider -> updateAppWidgetWait");

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.quiz_widget_wait);

        remoteViews.setTextViewText(R.id.txtWidgetCorrect, isCorrect);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }
}

