package neu.edu.madcourse.lingzhang.wordGame;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;

import neu.edu.madcourse.lingzhang.Dictionary;
import neu.edu.madcourse.lingzhang.R;

public class WordGameMainActivity extends Activity {

    static Dictionary dict;
    MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_game_main);
        InputStream inputStream = getResources().openRawResource(R.raw.wordlist);
        try {
            dict = new Dictionary(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMediaPlayer = MediaPlayer.create(this, R.raw.menu_music);
        mMediaPlayer.setVolume(0.5f, 0.5f);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMediaPlayer.stop();
        mMediaPlayer.reset();
        mMediaPlayer.release();
    }

}
