package estg.ipp.pt.quizdroidcmu;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListActivity extends android.app.ListActivity implements View.OnClickListener {

    private ArrayList<Difficulty> dList = new ArrayList<>();
    private ArrayList<Question> qList = new ArrayList<>();
    private ArrayList<Difficulty> cList = new ArrayList<>();
    private DifficultyAdapter dAdapter = null;
    private QuestionAdapter qAdapter = null;

    private ListView lv;

    String toList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        lv = getListView();
        registerForContextMenu(lv);

        toList = getIntent().getStringExtra("ItemToList");
        TextView txt_title = (TextView) findViewById(R.id.txt_title);
        if(toList.equals("questions")) {
            Button btnGotoAddQuestion = (Button) findViewById(R.id.btnGotoAddQuestion);
            btnGotoAddQuestion.setOnClickListener(this);
            btnGotoAddQuestion.setVisibility(View.VISIBLE);
            txt_title.setText("Questions");
            qAdapter = new QuestionAdapter(ListActivity.this, qList);
            setListAdapter(qAdapter);
        } else if (toList.equals("difficulties")) {
            Button btnGotoAddDifficulty = (Button) findViewById(R.id.btnGotoAddDifficulty);
            btnGotoAddDifficulty.setOnClickListener(this);
            btnGotoAddDifficulty.setVisibility(View.VISIBLE);
            txt_title.setText("Difficulties");
            dAdapter = new DifficultyAdapter(ListActivity.this, dList);
            setListAdapter(dAdapter);
        } else if (toList.equals("categories")) {
            Button btnGotoAddCategory = (Button) findViewById(R.id.btnGotoAddCategory);
            btnGotoAddCategory.setOnClickListener(this);
            btnGotoAddCategory.setVisibility(View.VISIBLE);
            txt_title.setText("Categories");
        }
        /**this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
                AlertDialog.Builder alert = new AlertDialog.Builder(ListActivity.this);
                alert.setTitle("Actions: ");
                alert.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent newIntent = new Intent(getApplicationContext(), AddEditQuestion.class);
                        newIntent.putExtra("Action", "Edit");
                        startActivity(newIntent);
                        dialog.dismiss();
                    }
                });
                alert.setNeutralButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(ListActivity.this);
                        alert.setTitle("Removing: ");
                        alert.setMessage("Are you sure?");
                        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                //TODO: REMOVE
                                dialog.dismiss();
                            }
                        });
                        //TODO: REMOVE
                        dialog.dismiss();
                    }
                });
                alert.show();
                return true;
            }
        });*/
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
                    sql = "SELECT * FROM tblDifficulties WHERE tblDifficulties.id='" + c.getInt(1) + "'";
                    Cursor c2 = db.rawQuery(sql, null);
                    if(c2 != null && c2.moveToFirst()) {

                        Difficulty dif = new Difficulty(c2.getInt(0), c2.getString(1), c2.getString(2));
                        qList.add(new Question(c.getInt(0), c.getString(3), dif, answers, c.getInt(4), c.getInt(5)));

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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnGotoAddQuestion) {
            Intent newIntent = new Intent(this, AddEditQuestion.class);
            newIntent.putExtra("Action", "Add");
            startActivity(newIntent);
        } else if(v.getId() == R.id.btnGotoAddDifficulty) {
            Intent newIntent = new Intent(this, AddEditDifficulty.class);
            startActivity(newIntent);
        } else if(v.getId() == R.id.btnGotoAddCategory) {
            Intent newIntent = new Intent(this, AddEditQuestion.class); //Category
            startActivity(newIntent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manage_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.edit_item:
                Intent newIntent = new Intent(getBaseContext(), AddEditQuestion.class);
                newIntent.putExtra("Action", "Edit");
                int pos = info.position;
                newIntent.putExtra("ID", qList.get(pos).getId());
                newIntent.putExtra("DIFFID", qList.get(pos).getDifficulty().getId());
                newIntent.putExtra("QUESTION", qList.get(pos).getText());
                newIntent.putExtra("ANSWER1", qList.get(pos).getAnswers()[0]);
                newIntent.putExtra("ANSWER2", qList.get(pos).getAnswers()[1]);
                newIntent.putExtra("ANSWER3", qList.get(pos).getAnswers()[2]);
                newIntent.putExtra("ANSWER4", qList.get(pos).getAnswers()[3]);
                newIntent.putExtra("CORRECTANSWER", qList.get(pos).getCorrectAnswer());
                newIntent.putExtra("REWARD", qList.get(pos).getReward());
                startActivity(newIntent);
                return true;
            case R.id.remove_item:
                //deleteNote(info.id);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }
}
