package neu.edu.madcourse.lingzhang.wordGame.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Record {
    private String instanceId;
    private String username;
    private int score;
    private int phase1;
    private int phase2;
    private String word;
    private String createDate;

    public Record() {

    }

    public Record(String instanceId, String username, int score, int phase1, int phase2, String word) {
        this.instanceId = instanceId;
        this.username = username;
        this.score = score;
        this.phase1 = phase1;
        this.phase2 = phase2;
        this.createDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()).toString();
        this.word = word;
    }

    public int getPhase2() {
        return phase2;
    }

    public void setPhase2(int phase2) {
        this.phase2 = phase2;
    }

    public int getPhase1() {
        return phase1;
    }

    public void setPhase1(int phase1) {
        this.phase1 = phase1;
    }

    public int getScore() {
        return score;
    }

    public void getScore(int score) {
        this.score = score;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

//    @Exclude
//    public Map<String, Object> toMap() {
//        HashMap<String, Object> result = new HashMap<>();
//        result.put("username", username);
//        result.put("score", score);
//        result.put("phase1", phase1);
//        result.put("phase2", phase2);
//        result.put("createDate", createDate);
//        return result;
//    }
}
