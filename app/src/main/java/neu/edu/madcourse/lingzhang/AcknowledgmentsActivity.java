package neu.edu.madcourse.lingzhang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class AcknowledgmentsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgments);
        this.setTitle(getResources().getText(R.string.button_acknowledgments));
    }
}
