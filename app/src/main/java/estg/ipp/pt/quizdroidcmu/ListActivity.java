package estg.ipp.pt.quizdroidcmu;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends android.app.ListActivity {

    private ArrayList<Difficulty> dList = new ArrayList<>();
    private ArrayList<Question> qList = new ArrayList<>();
    private ArrayList<Difficulty> cList = new ArrayList<>();
    private DifficultyAdapter dAdapter = null;
    private QuestionAdapter qAdapter = null;

    String toList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        toList = getIntent().getStringExtra("ItemToList");
        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        if(toList.equals("questions")) {
            txt_title.setText("Questions");
            qAdapter = new QuestionAdapter(ListActivity.this, qList);
            setListAdapter(qAdapter);
        } else if (toList.equals("difficulties")) {
            txt_title.setText("Difficulties");
            dAdapter = new DifficultyAdapter(ListActivity.this, dList);
            setListAdapter(dAdapter);
        } else if (toList.equals("categories")) {
            txt_title.setText("Categories");
        }
    }

    public void onResume() {
        super.onResume();
        initData();
        if(toList.equals("questions")) {
            qAdapter.notifyDataSetChanged();
        } else if (toList.equals("difficulties")) {
            dAdapter.notifyDataSetChanged();
        } else if (toList.equals("categories")) {

        }
    }

    private void initData() {
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sql = null;
        if(toList.equals("questions")) {
            qList.clear();
            sql = "SELECT * FROM tblQuestions LEFT JOIN tblAnswers ON tblQuestions.answerID=tblAnswers.id";
        } else if (toList.equals("difficulties")) {
            dList.clear();
            sql = "SELECT * FROM tblDifficulties";
        } else if (toList.equals("categories")) {
            cList.clear();
            sql = "SELECT * FROM tblCategories";
        }

        Cursor c = db.rawQuery(sql, null);
        if(c != null && c.moveToFirst()) {
            do {
                if(toList.equals("questions")) {
                    String[] answers = {c.getString(7), c.getString(8), c.getString(9), c.getString(10)};
                    //Get Difficulty from DB
                    sql = "SELECT * FROM tblDifficulties WHERE tblDifficulties.id=" + c.getInt(1);
                    Cursor c2 = db.rawQuery(sql, null);
                    if(c2 != null && c2.moveToFirst()) {
                        do {
                            Difficulty dif = new Difficulty(c2.getInt(0), c2.getString(1), c2.getString(2));
                            qList.add(new Question(c.getInt(0), c.getString(3), dif, answers, c.getInt(4), c.getInt(5)));
                        } while(c.moveToNext());
                    }
                } else if (toList.equals("difficulties")) {
                    Difficulty dif = new Difficulty(c.getInt(0), c.getString(1), c.getString(2));
                    dList.add(dif);
                } else if (toList.equals("categories")) {
                    //TODO: Adicionar categorias para a lista
                }
            } while(c.moveToNext());
        }
        dbHelper.close();
        db.close();
    }
}
