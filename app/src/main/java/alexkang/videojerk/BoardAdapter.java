package alexkang.videojerk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class BoardAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> sounds;

    public BoardAdapter(Context context, ArrayList<String> sounds) {
        this.context = context;
        this.sounds = sounds;
    }

    @Override
    public int getCount() {
        return sounds.size();
    }

    @Override
    public String getItem(int position) {
        return sounds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String sound = getItem(position);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.board_button, parent, false);
        }

        TextView title = (TextView) convertView.findViewById(R.id.button_name);
        title.setText(sound);

        return convertView;
    }

}
