package neu.edu.madcourse.lingzhang;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DisplayAboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_about);
        this.setTitle(getResources().getText(R.string.button_about));
        String message = "" +
                "Name: Ling Zhang\n" +
                "Email: zhang.ling1@husky.neu.edu\n" +
                "Year: 2nd\n" +
                "MEID: 99000736312328\n";
        TextView textView = findViewById(R.id.textView);
        /* Set Message to Text View */
        textView.setText(message);
    }
}
