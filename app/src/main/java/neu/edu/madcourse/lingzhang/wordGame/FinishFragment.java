package neu.edu.madcourse.lingzhang.wordGame;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import neu.edu.madcourse.lingzhang.R;

public class FinishFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_finish, container, false);
        View main = rootView.findViewById(R.id.button_main);
        View restart = rootView.findViewById(R.id.button_restart);
        main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });
        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((GameActivity) getActivity()).restartGame();
            }
        });
        return rootView;
    }

    public void setHeader(boolean isFinish) {
        View rootView = getView();
        TextView headerView = rootView.findViewById(R.id.text_finish);
        if (isFinish)
            headerView.setText("Finish !!!");
        else
            headerView.setText("TimeOut !!!");
    }

    public void setScore(int score) {
        View rootView = getView();
        ((TextView) rootView.findViewById(R.id.finish_score)).setText("SCORE:" + Integer.toString(score));
    }


}