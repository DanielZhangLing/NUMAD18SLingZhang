package neu.edu.madcourse.lingzhang;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class TestDictionaryActivity extends AppCompatActivity {

    Dictionary dict;
    EditText editText;
    List<String> wordList;
    ToneGenerator toneGenerator;
    TextView textView;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_dictionary);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        wordList = new ArrayList<>();
        ll = findViewById(R.id.wordList);
        InputStream inputStream = getResources().openRawResource(R.raw.wordlist);
        try {
            dict = new Dictionary(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        editText = findViewById(R.id.editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (dict.isWord(str) && !wordList.contains(str)) {
                    wordList.add(str);
                    textView = new TextView(getApplicationContext());
                    textView.setText(str);
                    ll.addView(textView);
                    toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void clearWords(View view){
        wordList.clear();
        ll.removeAllViews();
        editText.setText("");
    }
}

