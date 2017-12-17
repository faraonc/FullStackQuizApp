package edu.udacity.faraonc.fullstackquizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * This the the MainActivty for the main category page.
 *
 * @author ConardJames
 * @version 121617-01
 */
public class MainActivity extends AppCompatActivity {

    //key for intent
    final static String SESSION = "SESSION_TYPE";

    @Override
    /**
     * Set activity and layout.
     *
     * @param savedInstanceState the state of the activity.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView os = (TextView) findViewById(R.id.os_textview);
        os.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent osIntent = new Intent(MainActivity.this, QuizActivity.class);
                osIntent.putExtra(SESSION, QuizTypeEnum.OS);
                startActivity(osIntent);
            }
        });

        os = (TextView) findViewById(R.id.networking_textview);
        os.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent osIntent = new Intent(MainActivity.this, QuizActivity.class);
                osIntent.putExtra(SESSION, QuizTypeEnum.NETWORKING);
                startActivity(osIntent);
            }
        });

        os = (TextView) findViewById(R.id.webdev_textview);
        os.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent osIntent = new Intent(MainActivity.this, QuizActivity.class);
                osIntent.putExtra(SESSION, QuizTypeEnum.WEB_DEVELOPMENT);
                startActivity(osIntent);
            }
        });
    }
}
