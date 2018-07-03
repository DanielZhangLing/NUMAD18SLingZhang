package neu.edu.madcourse.lingzhang.wordGame;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.nhaarman.supertooltips.ToolTipRelativeLayout;

import neu.edu.madcourse.lingzhang.R;

public class GameActivity extends Activity {

    public static final String KEY_RESTORE = "key_restore";
    public static final String USERNAME_RESTORE = "username";
    public static final String PREF_RESTORE = "pref_restore";
    private GameFragment mGameFragment;
    private StatusFragment mStatusFragment;
    private FinishFragment mFinishFragment;
    private MediaPlayer mMediaPlayer;
    private Handler mHandler = new Handler();
    private ToolTipRelativeLayout ttl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Restore game here
        mGameFragment = (GameFragment) getFragmentManager().findFragmentById(R.id.fragment_game);
        mStatusFragment = (StatusFragment) getFragmentManager().findFragmentById(R.id.fragment_game_status);
        mFinishFragment = (FinishFragment) getFragmentManager().findFragmentById(R.id.fragment_game_finish);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        String username = getIntent().getStringExtra(USERNAME_RESTORE);
        mGameFragment.setUsername(username);
        ttl = mStatusFragment.getView().findViewById(R.id.tooltip_topframe);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE).getString(PREF_RESTORE, null);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
        Log.d("Scroggle", "restore = " + restore);
        View resume = findViewById(R.id.button_resume);
        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickContinue();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStatusFragment.pauseTimer();
        mHandler.removeCallbacks(null);
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("Scroggle", "state = " + gameData);
    }

    public void restartGame() {
        mGameFragment.restartGame();
        findViewById(R.id.fragment_game).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_game_controls).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_game_status).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_game_finish).setVisibility(View.GONE);
    }

    protected void onResume() {
        super.onResume();
        mStatusFragment.resumeTimer();
        mMediaPlayer = MediaPlayer.create(this, R.raw.game_music);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    public void onFinish(int score, boolean isFinish) {
        ((TextView) findViewById(R.id.finish_score)).setText("SCORE: " + Integer.toString(score));
        mFinishFragment.setHeader(isFinish);
        findViewById(R.id.fragment_game_finish).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_game).setVisibility(View.GONE);
        findViewById(R.id.fragment_game_controls).setVisibility(View.GONE);
        findViewById(R.id.fragment_game_status).setVisibility(View.GONE);
    }

    @Override
    public void finish() {
        super.finish();
        mStatusFragment.cancelTimer();
        ttl.removeAllViews();
    }

    public void clickPause(){
        findViewById(R.id.fragment_game).setVisibility(View.GONE);
        findViewById(R.id.fragment_game_controls).setVisibility(View.GONE);
        findViewById(R.id.button_resume).setVisibility(View.VISIBLE);
        ttl.removeAllViews();
        onPause();
    }

    public void clickContinue(){
        findViewById(R.id.fragment_game).setVisibility(View.VISIBLE);
        findViewById(R.id.fragment_game_controls).setVisibility(View.VISIBLE);
        findViewById(R.id.button_resume).setVisibility(View.GONE);
        onResume();
    }

}
