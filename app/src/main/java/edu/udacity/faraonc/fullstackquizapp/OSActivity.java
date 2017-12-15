package edu.udacity.faraonc.fullstackquizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.util.Log;

import java.util.Collections;
import java.util.Vector;


public class OSActivity extends AppCompatActivity {

    private static final int RADIO_QUESTIONS_COUNT = 6;
    private static final int CHECKBOX_QUESTIONS_COUNT = 2;
    private static final int TEXT_QUESTIONS_COUNT = 2;

    private Vector<QuestionNode> questionNodesVector = new Vector<>();
    private Vector<RadioButton> radioButtonsVector = new Vector<>();
    private Vector<CheckBox> checkBoxesVector = new Vector<>();

    private RadioGroup radioPanel;
    private LinearLayout checkBoxPanel;
    private EditText answerEditText;

    private int currentQuestionNode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_os);

        OSQuizMaker osQuestionGenerator = new OSQuizMaker(this);

        for (int i = 0; i < RADIO_QUESTIONS_COUNT; i++) {
            this.questionNodesVector.add(osQuestionGenerator.queryQuestion(QuestionTypeEnum.RADIO));
        }

        for (int i = 0; i < CHECKBOX_QUESTIONS_COUNT; i++) {
            this.questionNodesVector.add(osQuestionGenerator.queryQuestion(QuestionTypeEnum.CHECK_BOX));
        }

        for (int i = 0; i < TEXT_QUESTIONS_COUNT; i++) {
            this.questionNodesVector.add(osQuestionGenerator.queryQuestion(QuestionTypeEnum.TEXT));
        }

        Collections.shuffle(this.questionNodesVector);

        setupPrevButton();
        setupNextButton();
        mapRadioButtons();
        mapCheckBoxes();
        this.answerEditText = (EditText)findViewById(R.id.answer_edit_text);
        display();
    }

    //TODO save states
    private void setupPrevButton(){

        Button prevButton = (Button) findViewById(R.id.prev_button);

        // Set a click listener on that View
        prevButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                if(currentQuestionNode > 0){
                    currentQuestionNode--;
                    display();
                }
            }
        });

    }

    //TODO save states
    private void setupNextButton(){

        Button nextButton = (Button) findViewById(R.id.next_button);

        // Set a click listener on that View
        nextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if(currentQuestionNode < questionNodesVector.size() - 1){
                    currentQuestionNode++;
                    display();
                }
            }
        });

    }

    private void mapRadioButtons() {

        this.radioPanel = (RadioGroup) findViewById(R.id.choices_radio_group);
        this.radioButtonsVector.add((RadioButton) findViewById(R.id.radio_choice_A));
        this.radioButtonsVector.add((RadioButton) findViewById(R.id.radio_choice_B));
        this.radioButtonsVector.add((RadioButton) findViewById(R.id.radio_choice_C));
        this.radioButtonsVector.add((RadioButton) findViewById(R.id.radio_choice_D));
        this.radioButtonsVector.add((RadioButton) findViewById(R.id.radio_choice_E));
    }

    private void mapCheckBoxes() {

        this.checkBoxPanel = (LinearLayout) findViewById(R.id.checkboxes_group);
        this.checkBoxesVector.add((CheckBox) findViewById(R.id.checkbox_A));
        this.checkBoxesVector.add((CheckBox) findViewById(R.id.checkbox_B));
        this.checkBoxesVector.add((CheckBox) findViewById(R.id.checkbox_C));
        this.checkBoxesVector.add((CheckBox) findViewById(R.id.checkbox_D));
        this.checkBoxesVector.add((CheckBox) findViewById(R.id.checkbox_E));
    }

    private void display() {
        updateQuestionNumber();
        displayCurrentQuestionNode();
        displayCurrentChoices();
    }

    private void updateQuestionNumber() {
        ((TextView) findViewById(R.id.question_number_textview)).setText(getText(R.string.os_textview_string) + ": " +
                getText(R.string.question_heading) + String.valueOf(this.currentQuestionNode + 1));
    }

    private void displayCurrentQuestionNode() {
        ((TextView) findViewById(R.id.question_textview)).setText(this.questionNodesVector.get(this.currentQuestionNode).getQuestion());
    }

    private void displayCurrentChoices() {

        QuestionNode node = this.questionNodesVector.get(this.currentQuestionNode);

        switch (node.getType()) {
            case RADIO:
                displayRadioChoices(node);
                break;

            case CHECK_BOX:
                displayCheckBoxes(node);
                break;

            case TEXT:
                displayTextBox(node);
                break;
        }
    }

    private void displayRadioChoices(QuestionNode node) {

        hideCheckBoxPanel();
        hideTextBox();
        hideRadioPanel();

        String choices[] = node.getChoices();

        for (int i = 0; i < choices.length; i++) {
            this.radioButtonsVector.get(i).setText(choices[i]);
            this.radioButtonsVector.get(i).setVisibility(View.VISIBLE);
        }
        this.radioPanel.setVisibility(View.VISIBLE);
    }

    private void displayCheckBoxes(QuestionNode node) {

        hideRadioPanel();
        hideTextBox();
        hideCheckBoxPanel();

        String choices[] = node.getChoices();

        for (int i = 0; i < choices.length; i++) {
            this.checkBoxesVector.get(i).setText(choices[i]);
            this.checkBoxesVector.get(i).setVisibility(View.VISIBLE);
        }
        this.checkBoxPanel.setVisibility(View.VISIBLE);
    }

    private void displayTextBox(QuestionNode node){
        hideCheckBoxPanel();
        hideRadioPanel();
        this.answerEditText.setVisibility(View.VISIBLE);

    }

    private void hideRadioPanel() {
        for (int i = 0; i < this.radioButtonsVector.size(); i++) {
            this.radioButtonsVector.get(i).setVisibility(View.GONE);
        }
        this.radioPanel.setVisibility(View.GONE);
    }

    private void hideCheckBoxPanel() {
        for (int i = 0; i < this.checkBoxesVector.size(); i++) {
            this.checkBoxesVector.get(i).setVisibility(View.GONE);
        }
        this.checkBoxPanel.setVisibility(View.GONE);
    }

    private void hideTextBox(){
        this.answerEditText.setVisibility(View.GONE);
    }
}
