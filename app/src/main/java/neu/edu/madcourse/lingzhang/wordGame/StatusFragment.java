package neu.edu.madcourse.lingzhang.wordGame;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import neu.edu.madcourse.lingzhang.R;

public class StatusFragment extends Fragment {

    private MyTimer mTimer;
//    private TextView timer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.fragment_status, container, false);
//        TextView timer = rootView.findViewById(R.id.text_timer);
        mTimer = new MyTimer();
        mTimer.execute();
        return rootView;

    }

    public void resetTimer() {
        ((TextView) getView().findViewById(R.id.text_timer)).setTextColor(getResources().getColor(R.color.dark_border_color));
        mTimer.cancel(true);
        mTimer = new MyTimer();
        mTimer.execute();
    }

    public void cancelTimer() {
        mTimer.cancel(true);
    }

    public void setScore(int score) {
        View statusView = getView();
        TextView scoreView = statusView.findViewById(R.id.text_score);
        scoreView.setText("SCORE: " + Integer.toString(score));
    }

    public void setTimer(int tick) {
        mTimer.setTick(tick);
    }

    public int getTimer() {
        return mTimer.getTick();
    }

    public void updateTextTimer(int second) {
        TextView timerView = getView().findViewById(R.id.text_timer);
        timerView.setText("TIMER: " + Integer.toString(90 - second));
        if (second >= 60 && second <= 90)
            timerView.setTextColor(getResources().getColor(R.color.red_color));
        if (second == 90) {
            resetTimer();
            GameFragment game = (GameFragment) getFragmentManager().findFragmentById(R.id.fragment_game);
            game.checkFinish();
        }
    }

    public void pauseTimer() {
        mTimer.setPause(true);
    }

    public void resumeTimer() {
        mTimer.setPause(false);
    }

    public void setPhase(int phase) {
        ((TextView) getView().findViewById(R.id.text_phase)).setText("Phase " + Integer.toString(phase));
    }

    private class MyTimer extends AsyncTask<Void, Integer, Void> {

        private boolean isPause = false;

        private int tick = 0;

        public int getTick() {
            return tick;
        }

        public void setTick(int tick) {
            this.tick = tick;
        }

        public void setPause(boolean isPause) {
            this.isPause = isPause;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            updateTextTimer(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            for (; tick <= 90; tick++) {
                if (isCancelled())
                    break;
                if (isPause) {
                    tick--;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                publishProgress(tick);
            }
            return null;
        }
    }
}