package neu.edu.madcourse.lingzhang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /** Called when the user taps the Send button */
    public void showAbout(View view) {
        Intent intent = new Intent(this, DisplayAboutActivity.class);
        startActivity(intent);
    }

    public void generateError(View view){
        throw new RuntimeException("This is an intentioned Error!");
    }
}
