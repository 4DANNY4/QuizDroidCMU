package estg.ipp.pt.quizdroidcmu;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Toast;

public class WidgetTask extends AsyncTask<Integer, Integer, Integer> {

    private Context mContext;
    private final boolean running = true;

    public WidgetTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        int i;
        for(i=0; i<10; i++) {
            try {
                publishProgress(i);
                Thread.sleep(5000);
            } catch(InterruptedException e) {
                System.out.println("Thread interrupted!");
            }
        }
        return i;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Toast.makeText(mContext, "Task Running", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }
}
