package estg.ipp.pt.quizdroidcmu;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class AddEditDifficulty extends AppCompatActivity implements View.OnClickListener{

    private TextView title;
    private EditText name;
    private EditText desc;

    private Button btnAdd;
    private Button btnEdit;

    private String action;

    private int difficultyIDtoEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_difficulty);

        title = (TextView) findViewById(R.id.tv_dmanTitle);
        name = (EditText) findViewById(R.id.txt_dname);
        desc = (EditText) findViewById(R.id.txt_ddesc);

        btnAdd = (Button) findViewById(R.id.btnAddDifficulty);
        btnAdd.setOnClickListener(this);
        btnEdit = (Button) findViewById(R.id.btnEditDifficulty);
        btnEdit.setOnClickListener(this);

        action = getIntent().getStringExtra("Action");
        if(action.equals("Add")) {
            title.setText("Add Difficulty");
            btnAdd.setVisibility(View.VISIBLE);
        } else if(action.equals("Edit")) {
            title.setText("Edit Difficulty");
            difficultyIDtoEdit = getIntent().getIntExtra("ID", 1);
            name.setText(getIntent().getStringExtra("NAME"));
            desc.setText(getIntent().getStringExtra("DESCRIPTION"));
            btnEdit.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnAddDifficulty) {
            add();
            Toast.makeText(this, "Added!", Toast.LENGTH_SHORT).show();
            finish();
        } else if(v.getId() == R.id.btnEditDifficulty) {
            edit();
            Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void add() {
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sqlAddDifficulty = "INSERT INTO tblDifficulties(name, description) " +
                "VALUES('"+ name.getText().toString() +"', '"+ desc.getText().toString() +"')";

        db.execSQL(sqlAddDifficulty);

        dbHelper.close();
        db.close();
    }

    private void edit() {
        QdDbHelper dbHelper = new QdDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sqlUpdateDifficulty = "UPDATE tblDifficulties " +
                "SET name='" + name.getText().toString() + "'," +
                "description='" + desc.getText().toString() + "' " +
                "WHERE tblDifficulties.id='" + difficultyIDtoEdit + "'";
        db.execSQL(sqlUpdateDifficulty);

        dbHelper.close();
        db.close();
    }

}
