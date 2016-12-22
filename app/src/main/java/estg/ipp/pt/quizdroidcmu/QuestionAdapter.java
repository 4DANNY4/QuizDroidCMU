package estg.ipp.pt.quizdroidcmu;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class QuestionAdapter extends ArrayAdapter<Question> {

    private Context mContext;
    private ArrayList<Question> mList;

    public QuestionAdapter(Context context, ArrayList<Question> mList) {
        super(context, R.layout.list_row, mList);
        this.mContext = context;
        this.mList = mList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.list_row, null);
        }

        TextView txt_name = (TextView) v.findViewById(R.id.txt_itemTxt);

        txt_name.setText(mList.get(position).getText());

        return v;
    }
}
