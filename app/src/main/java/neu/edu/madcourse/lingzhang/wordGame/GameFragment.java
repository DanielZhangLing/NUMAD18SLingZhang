package neu.edu.madcourse.lingzhang.wordGame;

import android.app.Fragment;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.autofill.Dataset;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nhaarman.supertooltips.ToolTip;
import com.nhaarman.supertooltips.ToolTipRelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

import neu.edu.madcourse.lingzhang.R;
import neu.edu.madcourse.lingzhang.wordGame.models.Record;
import neu.edu.madcourse.lingzhang.wordGame.models.User;

import static android.content.ContentValues.TAG;
import static neu.edu.madcourse.lingzhang.wordGame.WordGameMainActivity.dict;

public class GameFragment extends Fragment {

    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3, R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8, R.id.large9,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3, R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8, R.id.small9,};
    private Tile mEntireBoard = new Tile(this);
    private Tile mLargeTiles[] = new Tile[9];
    private Tile mSmallTiles[][] = new Tile[9][9];
    private String[] mWordList = new String[9];
    private int mLastLarge;
    private int mLastSmall;
    private int mSoundClick, mSoundFalse, mSoundWin, mSoundLose, mSoundFlip;
    private SoundPool mSoundPool;
    private float mVolume = 1f;
    private int score;
    private int phase;
    private int phase1_score;
    private int phase2_score;
    private StatusFragment mStatusFragment;
    private ToolTipRelativeLayout ttl;
    private String maxWord;
    private DatabaseReference mDatabase;
    private String instanceId;
    private String username;
    private static final String TAG = GameActivity.class.getSimpleName();
    private static final String SERVER_KEY = "key=AAAAIJ7JUU8:APA91bGlKbMAkFaynsxtUC6DBCKSkBkMVyd_eUUUbUNxuRXAmNr9uHtGMWZJLityCx3hfNYkyHscdCoYg7bJW3EvedQ11NsNeGAxu8iuAmDX_LDjjTffgdqJVayk_M5S7XjWidIsv6cyyo-4_JXCBO-hWDR6DVrlsg";
    private ToolTip toolTip;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        initGame();
        mStatusFragment = (StatusFragment) getFragmentManager().findFragmentById(R.id.fragment_game_status);
        ttl = mStatusFragment.getView().findViewById(R.id.tooltip_topframe);
        mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        mSoundClick = mSoundPool.load(getActivity(), R.raw.erkanozan_miss, 1);
        mSoundFalse = mSoundPool.load(getActivity(), R.raw.department64_draw, 1);
        mSoundWin = mSoundPool.load(getActivity(), R.raw.oldedgar_winner, 1);
        mSoundLose = mSoundPool.load(getActivity(), R.raw.notr_loser, 1);
        mSoundFlip = mSoundPool.load(getActivity(), R.raw.sergenious_movex, 1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.large_board, container, false);
        initViews(rootView);
        updateAllTiles();
        return rootView;
    }

    public char randomLetter(List<Character> charList) {
        return charList.remove(new Random().nextInt(charList.size()));
    }

    public void initGame() {
        Log.d("Scroggle", "init game");
        score = 0;
        phase = 1;
        phase1_score = 0;
        phase2_score = 0;
        maxWord = "";
        instanceId = FirebaseInstanceId.getInstance().getToken();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mEntireBoard = new Tile(this); // Create all the tiles
        mEntireBoard.setStack();
        mWordList = dict.generateWordList();
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large] = new Tile(this);
            mLargeTiles[large].setStack();
            String word = mWordList[large];
            System.out.println(word);
            List<Character> charList = new ArrayList<>();
            for (char c : word.toCharArray()) {
                charList.add(c);
            }
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small] = new Tile(this);
                mSmallTiles[large][small].setLetter(randomLetter(charList));
                System.out.println(mSmallTiles[large][small].getLetter());
            }
            mLargeTiles[large].setSubTiles(mSmallTiles[large]);
        }
        mEntireBoard.setSubTiles(mLargeTiles);
        // If the player moves first, set which spots are available
        mLastSmall = -1;
        mLastLarge = -1;
    }

    private void initViews(View rootView) {
        mEntireBoard.setView(rootView);
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            mLargeTiles[large].setView(outer);
            for (int small = 0; small < 9; small++) {
                ImageButton inner = outer.findViewById
                        (mSmallIds[small]);
                final int fLarge = large;
                final int fSmall = small;
                final Tile smallTile = mSmallTiles[large][small];
                smallTile.setView(inner);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (smallTile.isAvailable()) {
                            if (smallTile.isSelected() && fSmall == mLargeTiles[fLarge].getStack().peek()) {
                                mSoundPool.play(mSoundClick, mVolume, mVolume, 1, 0, 1f);
                                releaseTile(mLargeTiles[fLarge], smallTile);
                            } else {
                                if (fLarge != mLastLarge && mLastLarge != -1) {
                                    checkWord();

                                }
                                if (!smallTile.isSelected()) {
                                    makeMove(fLarge, fSmall);
                                }
                            }
                        }
                    }
                });
            }
        }
        toolTip = new ToolTip()
                .withText("In Phase 1. Try to find the longest word in each box!")
                .withColor(R.color.white_color)
                .withShadow();
        ttl.removeAllViews();
        ttl.showToolTipForView(toolTip, mStatusFragment.getView().findViewById(R.id.text_phase));
    }

    public void checkWord() {
        if (phase == 1) {
            Tile largeTile = mLargeTiles[mLastLarge];
            Tile[] mLastLargeSub = largeTile.getSubTiles();
            String word = largeTile.getmWord();
            if (dict.isWord(word, 2)) {
                mSoundPool.play(mSoundFlip, mVolume, mVolume, 1, 0, 1f);
                largeTile.setAvailable(false);
                System.out.println(largeTile);
                calculateScore(word, true);
                for (int i = 0; i < 9; i++) {
                    mLastLargeSub[i].setAvailable(false);
                    if (!mLastLargeSub[i].isSelected()) {
                        mLastLargeSub[i].setLetter('|');
                        mLastLargeSub[i].updateDrawableState();
                    }
                }
            } else {
                mSoundPool.play(mSoundFalse, mVolume, mVolume, 1, 0, 1f);
                calculateScore(word, false);
                for (int i = 0; i < 9; i++) {
                    if (mLastLargeSub[i].isSelected()) {
                        releaseTile(mLargeTiles[mLastLarge], mLastLargeSub[i]);
                    }
                    resetAvailable(mLargeTiles[mLastLarge].getSubTiles());
                }
            }
        } else {
            String word = mEntireBoard.getmWord();
            if (dict.isWord(word, 2)) {
                calculateScore(word, true);
                gameFinished(true);
            } else {
                calculateScore(word, false);
                mSoundPool.play(mSoundFalse, mVolume, mVolume, 1, 0, 1f);
            }
        }

    }

    public void gameFinished(boolean isFinish) {
        if (isFinish)
            mSoundPool.play(mSoundWin, mVolume, mVolume, 1, 0, 1f);
        else
            mSoundPool.play(mSoundLose, mVolume, mVolume, 1, 0, 1f);
        addRecord();
        ((GameActivity) getActivity()).onFinish(score, isFinish);
    }

    public void addRecord() {
        DatabaseReference newRef = mDatabase.child("record");
        Record newRecord = new Record(instanceId, username, score, phase1_score, phase2_score, maxWord);
        newRef.push().setValue(newRecord);
        subscribeToRecord();
        mDatabase.child("record").orderByChild("createDate").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int max = Integer.MIN_VALUE;
                Stack<Integer> scoreStack = new Stack<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Record record = child.getValue(Record.class);
                    int score = record.getScore();
                    if (score > max)
                        max = score;
                    scoreStack.push(score);
                }
                if (!scoreStack.isEmpty() && scoreStack.pop() == max)
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sendMessageToRecord();
                        }
                    }).start();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException());
            }
        });

    }

    ;

    private void releaseTile(Tile largeTile, Tile small) {
        small.setSelected(false);
        small.revAnimate();
        small.updateDrawableState();
        small.setAvailable(true);
        largeTile.getStack().pop();
        String curWord = largeTile.getmWord();
        largeTile.setmWord(curWord.substring(0, curWord.length() - 1));
    }

    private void makeMove(int large, int small) {
        mSoundPool.play(mSoundClick, mVolume, mVolume, 1, 0, 1f);
        mLastLarge = large;
        mLastSmall = small;
        Tile smallTile = mSmallTiles[large][small];
        Tile largeTile = mLargeTiles[large];
        smallTile.setSelected(true);
        smallTile.animate();
        smallTile.updateDrawableState();
        System.out.println(smallTile.getLetter());
        largeTile.setmWord(largeTile.getmWord() + smallTile.getLetter());
        largeTile.getStack().push(small);
        updateAvailable(largeTile.getSubTiles(), small);
        if (readyForPrePhaseTwo())
            prePhaseTwo(false);
    }

    public void phaseTwoMove(int large) {
        mSoundPool.play(mSoundClick, mVolume, mVolume, 1, 0, 1f);
        Tile largeTile = mLargeTiles[large];
        largeTile.setSelected(true);
        largeTile.animate();
        largeTile.updateDrawableState();
        mEntireBoard.setmWord(mEntireBoard.getmWord() + largeTile.getLetter());
        mEntireBoard.getStack().push(large);
        updateAvailable(mEntireBoard.getSubTiles(), large);
    }

    public void resetAvailable(Tile[] smallTiles) {
        for (int i = 0; i < 9; i++) {
            smallTiles[i].setAvailable(true);
        }
    }

    public void updateAvailable(Tile[] smallTiles, int small) {
        resetAvailable(smallTiles);
        switch (small) {
            case 0:
                for (int i : new int[]{2, 5, 6, 7, 8}) {
                    smallTiles[i].setAvailable(false);
                }
                break;
            case 1:
                for (int i : new int[]{6, 7, 8}) {
                    smallTiles[i].setAvailable(false);
                }
                break;
            case 2:
                for (int i : new int[]{0, 3, 6, 7, 8}) {
                    smallTiles[i].setAvailable(false);
                }
                break;
            case 3:
                for (int i : new int[]{2, 5, 8}) {
                    smallTiles[i].setAvailable(false);
                }
                break;
            case 5:
                for (int i : new int[]{0, 3, 6}) {
                    smallTiles[i].setAvailable(false);
                }
                break;
            case 6:
                for (int i : new int[]{0, 1, 2, 5, 8}) {
                    smallTiles[i].setAvailable(false);
                }
                break;
            case 7:
                for (int i : new int[]{0, 1, 2}) {
                    smallTiles[i].setAvailable(false);
                }
                break;
            case 8:
                for (int i : new int[]{0, 1, 2, 3, 6}) {
                    smallTiles[i].setAvailable(false);
                }
                break;
            default:
                break;
        }
        for (int i = 0; i < 9; i++) {
            if (smallTiles[i].isSelected())
                smallTiles[i].setAvailable(true);
        }
    }

    public void calculateScore(String word, boolean find) {
        int length = word.length();
        if (!find) {
            score -= 5;
            if (score < 0)
                score = 0;
        } else {
            if (phase == 1)
                score += length * length;
            else
                score += length * length * 5;
            if (length >= maxWord.length())
                maxWord = word;
        }
        if (phase == 1)
            phase1_score = score;
        else
            phase2_score = score - phase1_score;

        mStatusFragment.setScore(score);
    }

    public void checkFinish() {
        if (phase == 1) {
            int totalNotChooseTile = 0;
            for (Tile largeTile : mLargeTiles) {
                if (largeTile.isAvailable()) {
                    totalNotChooseTile++;
                }
            }
            if (totalNotChooseTile > 7) {
                gameFinished(false);
            } else {
                prePhaseTwo(false);
            }
        } else
            gameFinished(false);

    }

    public void prePhaseTwo(boolean isLoad) {
        phase = 2;
        mStatusFragment.setPhase(2);
        View rootView = getView();
        for (int large = 0; large < 9; large++) {
            if (mLargeTiles[large].isAvailable() && !isLoad) {
                mLargeTiles[large].setLetter('|');
                mLargeTiles[large].setAvailable(false);
                mLargeTiles[large].updateDrawableState();
                for (Tile small : mLargeTiles[large].getSubTiles()) {
                    small.setAvailable(false);
                    small.setLetter('{');
                    small.updateDrawableState();
                }
            } else {
                if (!isLoad)
                    mLargeTiles[large].setAvailable(true);
                View outer = rootView.findViewById(mLargeIds[large]);
                for (int small = 0; small < 9; small++) {
                    final int fLarge = large;
                    final Tile smallTile = mLargeTiles[large].getSubTiles()[small];
                    if (smallTile.isSelected()) {
                        smallTile.setAvailable(true);
                        smallTile.setSelected(false);
                        smallTile.updateDrawableState();
                    }
                    ImageButton inner = outer.findViewById
                            (mSmallIds[small]);
                    inner.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (smallTile.isAvailable()) {
                                mLargeTiles[fLarge].setLetter(smallTile.getLetter());
                                mLargeTiles[fLarge].updateDrawableState();
                                mLargeTiles[fLarge].setAvailable(false);
                                for (Tile t : mLargeTiles[fLarge].getSubTiles()) {
                                    t.setAvailable(false);
                                    t.setLetter('{');
                                    t.updateDrawableState();
                                }
                                if (readyForPhaseTwo())
                                    initPhaseTwo(false);
                            }
                        }
                    });
                }

            }
        }
        toolTip = new ToolTip()
                .withText("In Phase 2. Start selecting a letter from each available box!")
                .withColor(R.color.white_color)
                .withShadow();
        ttl.removeAllViews();
        ttl.showToolTipForView(toolTip, mStatusFragment.getView().findViewById(R.id.text_phase));
    }

    public boolean readyForPrePhaseTwo() {
        for (int large = 0; large < 9; large++) {
            if (mLargeTiles[large].isAvailable())
                return false;
        }
        return true;
    }

    public boolean readyForPhaseTwo() {
        for (int large = 0; large < 9; large++) {
            if (mLargeTiles[large].getLetter() == '{')
                return false;
        }
        return true;
    }

    public void initPhaseTwo(boolean isLoad) {
        View rootView = getView();
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
            final View fOuter = outer;
            final Tile fLargeTile = mLargeTiles[large];
            if (fLargeTile.getLetter() != '|' && !isLoad)
                fLargeTile.setAvailable(true);
            final int fLarge = large;
            outer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (fLargeTile.isAvailable()) {
                        if (fLargeTile.isSelected() && fLarge == mEntireBoard.getStack().peek())
                            releaseTile(mEntireBoard, fLargeTile);
                        else {
                            if (!fLargeTile.isSelected()) {
                                phaseTwoMove(fLarge);
                            }
                        }
                    }
                }
            });
            for (int small = 0; small < 9; small++) {
                ImageButton inner = outer.findViewById
                        (mSmallIds[small]);
                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fOuter.callOnClick();
                    }
                });
            }
        }
        toolTip = new ToolTip()
                .withText("Last step! Find the longest word now!")
                .withColor(R.color.white_color)
                .withShadow();
        ttl.removeAllViews();
        ttl.showToolTipForView(toolTip, mStatusFragment.getView().findViewById(R.id.text_phase));
    }

    public void restartGame() {
        initGame();
        mStatusFragment.resetTimer();
        mStatusFragment.setPhase(1);
        mStatusFragment.setScore(0);
        ;
        initViews(getView());
        updateAllTiles();
    }


    public String getTileString(Tile tile) {
        StringBuilder build = new StringBuilder();
        build.append(tile.isSelected() == true ? 1 : 0);
        build.append('~');
        build.append(tile.isAvailable() == true ? 1 : 0);
        build.append('~');
        build.append(tile.getLetter());
        build.append('~');
        if (tile.getStack() != null && !tile.getStack().isEmpty()) {
            StringBuilder stackStr = new StringBuilder();
            Stack<Integer> tmp = (Stack<Integer>) tile.getStack().clone();
            while (!tmp.isEmpty()) {
                stackStr.insert(0, tmp.pop());
            }
            build.append(stackStr.toString());
        }
        build.append('~');
        if (tile.getmWord() != "")
            build.append(tile.getmWord());
        build.append('~');
        return build.toString();
    }

    public void getTileFromString(Tile tile, String str) {
        System.out.println(str);
        String[] fields = str.split("~", -1);
        int index = 0;
        tile.setSelected("1".equals(fields[index++]) ? true : false);
        tile.setAvailable("1".equals(fields[index++]) ? true : false);
        tile.setLetter(fields[index++].charAt(0));
        if (fields[index++] != " ") {
            tile.setStack();
            for (char c : fields[index - 1].toCharArray()) {
                tile.getStack().push((int) c - 48);
            }
        }
        if (fields[index++] != " ")
            tile.setmWord(fields[index - 1]);
    }

    public String getState() {
        StringBuilder builder = new StringBuilder();
        builder.append(mLastLarge);
        builder.append(';');
        builder.append(mLastSmall);
        builder.append(';');
        builder.append(mStatusFragment.getTimer());
        builder.append(';');
        builder.append(score);
        builder.append(';');
        builder.append(phase);
        builder.append(';');
        builder.append(phase1_score);
        builder.append(';');
        builder.append(phase2_score);
        builder.append(';');
        builder.append(maxWord);
        builder.append(';');
        for (int large = 0; large < 9; large++) {
            builder.append(getTileString(mLargeTiles[large]));
            builder.append(';');
            for (int small = 0; small < 9; small++) {
                builder.append(getTileString(mSmallTiles[large][small]));
                builder.append(';');
            }
        }
        builder.append(getTileString(mEntireBoard));
        builder.append(';');
        return builder.toString();
    }

    public void putState(String gameData) {
        String[] fields = gameData.split(";");
        int index = 0;
        mLastLarge = Integer.parseInt(fields[index++]);
        mLastSmall = Integer.parseInt(fields[index++]);
        mStatusFragment.setTimer(Integer.parseInt(fields[index++]) + 1);
        score = Integer.parseInt(fields[index++]);
        mStatusFragment.setScore(score);
        phase = Integer.parseInt(fields[index++]);
        mStatusFragment.setPhase(phase);
        phase1_score = Integer.parseInt(fields[index++]);
        phase2_score = Integer.parseInt(fields[index++]);
        maxWord = fields[index++];
        for (int large = 0; large < 9; large++) {
            getTileFromString(mLargeTiles[large], fields[index++]);
            for (int small = 0; small < 9; small++) {
                getTileFromString(mSmallTiles[large][small], fields[index++]);
            }
        }
        getTileFromString(mEntireBoard, fields[index++]);
        updateAllTiles();
        if (phase == 2) {
            if (readyForPhaseTwo())
                initPhaseTwo(true);
            else
                prePhaseTwo(true);
        }
    }

    private void updateAllTiles() {
        mEntireBoard.updateDrawableState();
        for (int large = 0; large < 9; large++) {
            mLargeTiles[large].updateDrawableState();
            for (int small = 0; small < 9; small++) {
                mSmallTiles[large][small].updateDrawableState();
            }
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void subscribeToRecord() {
        // [START subscribe_topics]
        FirebaseMessaging.getInstance().subscribeToTopic("record");
        // [END subscribe_topics]

        // Log and toast
        String msg = getString(R.string.msg_subscribed);
        Log.d(TAG, msg);
    }

    public void sendMessageToRecord() {
        if (username == null || username.equals(""))
            username = "Unknown";
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("message", "Scroggle");
            jNotification.put("body", username + " has created the new high score!");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");

            // Populate the Payload object.
            // Note that "to" is a topic, not a token representing an app instance
            jPayload.put("to", "/topics/record");
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);

            // Open the HTTP connection and send the payload
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: " + getString(R.string.send_topic_msg));
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.send_topic_msg), Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }
}
