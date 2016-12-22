package estg.ipp.pt.quizdroidcmu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener{

    private ArrayList<Difficulty> mDifficulty = new ArrayList<>();

    private Button btn_StartGame, btn_NextDifficulty, btn_PreviousDifficulty;
    private TextView txt_highestScore, txt_answerStreak;
    private Difficulty difficulty;

    private SharedPreferences mSettings;
    private String preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        initData();

        btn_NextDifficulty = (Button) findViewById(R.id.btnNextDifficulty);
        btn_NextDifficulty.setOnClickListener(this);

        btn_PreviousDifficulty = (Button) findViewById(R.id.btnPreviousDifficulty);
        btn_PreviousDifficulty.setOnClickListener(this);

        btn_StartGame = (Button) findViewById(R.id.btnStartGame);
        btn_StartGame.setOnClickListener(this);

        difficulty = mDifficulty.get(1);
        btn_StartGame.setText(difficulty.getName());

        txt_highestScore = (TextView) findViewById(R.id.txtHighestScore);
        txt_answerStreak = (TextView) findViewById(R.id.txtAnswerStreak);

        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        preference = mSettings.getString("pref_dificulty","ERRO");

        txt_highestScore.setText(preference);

    }

    private void initData(){
        mDifficulty.clear();

        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String sql = "SELECT * FROM tblDifficulty";

        Cursor c = db.rawQuery(sql,null);
        if (c != null && c.moveToFirst()){
            do {
                mDifficulty.add(new Difficulty(c.getInt(0),c.getString(1),c.getString(2)));
            }while (c.moveToNext());
        }
        dbHelper.close();
        db.close();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnNextDifficulty){
            if(difficulty.getId() == mDifficulty.size()){
                difficulty = mDifficulty.get(0);
                btn_StartGame.setText(difficulty.getName());
            }else {
                difficulty = mDifficulty.get(difficulty.getId());
                btn_StartGame.setText(difficulty.getName());
            }

        }else if(view.getId() == R.id.btnPreviousDifficulty){
            if(difficulty.getId() == 1){
                difficulty = mDifficulty.get(mDifficulty.size() - 1);
                btn_StartGame.setText(difficulty.getName());
            }else {
                difficulty = mDifficulty.get(difficulty.getId() - 2);
                btn_StartGame.setText(difficulty.getName());
            }

        }else if(view.getId() == R.id.btnSettings){

        }else if(view.getId() == R.id.btnStartGame){
            Intent newIntent = new Intent(this, QuizActivity.class);
            startActivity(newIntent);

        }
    }
}
