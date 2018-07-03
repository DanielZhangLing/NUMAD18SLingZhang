package neu.edu.madcourse.lingzhang.wordGame;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import neu.edu.madcourse.lingzhang.R;
import neu.edu.madcourse.lingzhang.wordGame.models.Record;

public class ScoreActivity extends Activity {

    private static final String TAG = ScoreActivity.class.getSimpleName();
    private static final String SERVER_KEY = "key=AAAAIJ7JUU8:APA91bGlKbMAkFaynsxtUC6DBCKSkBkMVyd_eUUUbUNxuRXAmNr9uHtGMWZJLityCx3hfNYkyHscdCoYg7bJW3EvedQ11NsNeGAxu8iuAmDX_LDjjTffgdqJVayk_M5S7XjWidIsv6cyyo-4_JXCBO-hWDR6DVrlsg";
    private DatabaseReference mDatabase;
    private String instanceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        final View leaderTableView = findViewById(R.id.table_highscore);
        final View scoreTableView = findViewById(R.id.table_myscore);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        instanceId = FirebaseInstanceId.getInstance().getToken();

        mDatabase.child("record").orderByChild("score").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int leaderBoardCounter = 0;
                int scoreBoardCounter = 0;
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    final Record record = child.getValue(Record.class);
                    if (leaderBoardCounter < 10) {
                        View row = getLayoutInflater().inflate(R.layout.fragment_high_row, (ViewGroup) leaderTableView, false);
                        TextView usernameText = row.findViewById(R.id.high_username);
                        TextView scoreText = row.findViewById(R.id.high_score);
                        TextView wordText = row.findViewById(R.id.hg_word);
                        TextView dateText = row.findViewById(R.id.hg_datetime);
                        View congratsButton = row.findViewById(R.id.hg_congrats);
                        String username = record.getUsername();
                        if (username == null || username.equals(""))
                            username = "Unknown";
                        usernameText.setText(username);
                        scoreText.setText(Integer.toString(record.getScore()));
                        wordText.setText(record.getWord());
                        dateText.setText(record.getCreateDate());
                        congratsButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sendMessageToDevice(record.getInstanceId());
                                    }
                                }).start();
                            }
                        });
                        ((ViewGroup) leaderTableView).addView(row, 2);
                        leaderBoardCounter++;
                    }
                    if (record.getInstanceId().equals(instanceId) && scoreBoardCounter < 10) {
                        View row = getLayoutInflater().inflate(R.layout.fragment_my_row, (ViewGroup) scoreTableView, false);
                        TextView dateText = row.findViewById(R.id.my_date);
                        TextView scoreText = row.findViewById(R.id.my_score);
                        TextView wordText = row.findViewById(R.id.my_word);
                        TextView wordScoreText = row.findViewById(R.id.my_word_score);
                        dateText.setText(record.getCreateDate());
                        scoreText.setText(Integer.toString(record.getScore()));
                        wordText.setText(record.getWord());
                        int length = record.getWord().length();
                        wordScoreText.setText(Integer.toString(length * length));
                        ((ViewGroup) scoreTableView).addView(row, 2);
                        scoreBoardCounter++;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void sendMessageToDevice(String targetToken) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("title", "Scrogger");
            jNotification.put("body", "You got new congrats from your friend!");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jPayload.put("to", targetToken);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();
            int responseCode = conn.getResponseCode();
            if (responseCode >= 400 && responseCode <= 499) {
                System.out.println(responseCode);
            }
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    String msg = getString(R.string.msg_feedback);
                    Log.e(TAG, "run: " + resp);
                    Toast.makeText(ScoreActivity.this, "Message sent successfully!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }
}
