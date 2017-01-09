package estg.ipp.pt.quizdroidcmu;

import android.app.ActivityManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isMyServiceRunning(QuizService.class)) {
            startService(new Intent(getApplicationContext(), QuizService.class));
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnTapStart) {
            Intent newIntent = new Intent(this, MenuActivity.class);
            startActivity(newIntent);
            finish();
        }
    }

    private boolean isMyServiceRunning(Class serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
