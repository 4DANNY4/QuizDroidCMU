package estg.ipp.pt.quizdroidcmu;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.widget.Toast;

/**
 * Created by Tiago Fernandes on 09/01/2017.
 */

public class QuizService extends Service {

    private boolean running = true;

    @Override
    public void onCreate() {
        super.onCreate();
        //startForeground(101, new Notification());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getBaseContext(), "Service Destroyed", Toast.LENGTH_LONG).show();
    }


}
