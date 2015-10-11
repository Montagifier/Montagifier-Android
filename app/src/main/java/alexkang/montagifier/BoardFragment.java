package alexkang.montagifier;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

public class BoardFragment extends Fragment {

    public BoardFragment() {}

    @Override
    @SuppressWarnings("unchecked")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_board, container, false);

        final String category = getArguments().getString("category");
        ArrayList<String> sounds = (ArrayList<String>) getArguments().getSerializable("sounds");

        GridView board = (GridView) rootView.findViewById(R.id.board_list);
        final BoardAdapter adapter = new BoardAdapter(getActivity(), sounds);

        board.setAdapter(adapter);
        board.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Controller.getInstance().requestSound(category, adapter.getItem(position));
            }
        });

        return rootView;
    }
}
