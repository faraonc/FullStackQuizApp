package edu.udacity.faraonc.fullstackquizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OSActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os);

        OSQuizMaker  osQuestionGenerator = new OSQuizMaker(this);

        QuestionNode node =  osQuestionGenerator.queryQuestion(QuestionTypeEnum.RADIO); //TODO
    }
}
