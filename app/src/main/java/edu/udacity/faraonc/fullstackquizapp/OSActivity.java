package edu.udacity.faraonc.fullstackquizapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
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
import android.widget.Toast;

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
        setupSubmitButton();
        mapRadioButtons();
        mapCheckBoxes();
        this.answerEditText = (EditText) findViewById(R.id.answer_edit_text);
        display();
    }

    private void setupPrevButton() {

        Button prevButton = (Button) findViewById(R.id.prev_button);

        // Set a click listener on that View
        prevButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (currentQuestionNode > 0) {
                    saveQuestionNodeState();
                    currentQuestionNode--;
                    display();
                }
            }
        });
    }

    private void setupSubmitButton() {

        Button submitButton = (Button) findViewById(R.id.submit_button);

        // Set a click listener on that View
        submitButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                saveQuestionNodeState();
                int correctAnswer = 0;
                for (int i = 0; i < questionNodesVector.size(); i++) {
                    if (questionNodesVector.get(i).compareAnswer()) {
                        correctAnswer++;
                    }
                }
                String message = getText(R.string.score_heading) + "  " + String.valueOf(correctAnswer) + " / " + String.valueOf(questionNodesVector.size());
                Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL | Gravity.RIGHT, 25, 10);
                toast.show();
            }
        });
    }

    private void setupNextButton() {

        Button nextButton = (Button) findViewById(R.id.next_button);

        // Set a click listener on that View
        nextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (currentQuestionNode < questionNodesVector.size() - 1) {
                    saveQuestionNodeState();
                    currentQuestionNode++;
                    display();
                }
            }
        });
    }

    private void saveQuestionNodeState() {

        switch (this.questionNodesVector.get(this.currentQuestionNode).getType()) {
            case RADIO:
                saveRadioButtons();
                break;

            case CHECK_BOX:
                saveCheckBoxes();
                break;

            case TEXT:
                saveTextBox();
        }
    }

    private void saveRadioButtons() {

        boolean states[] = new boolean[this.radioButtonsVector.size()];
        String answers[] = {this.answerEditText.getText().toString()};
        for (int i = 0; i < this.radioButtonsVector.size(); i++) {
            states[i] = this.radioButtonsVector.get(i).isChecked();

        }
        this.questionNodesVector.get(this.currentQuestionNode).setState(states);
        RadioButton rb = ((RadioButton) findViewById(this.radioPanel.getCheckedRadioButtonId()));
        if (rb != null) {
            this.questionNodesVector.get(this.currentQuestionNode).setStateAnswer(rb.getText().toString());
        }
    }

    private void saveCheckBoxes() {

        boolean states[] = new boolean[this.checkBoxesVector.size()];
        String answers[] = new String[this.checkBoxesVector.size()];
        int answerIndex = 0;
        for (int i = 0; i < this.checkBoxesVector.size(); i++) {
            if (this.checkBoxesVector.get(i).isChecked()) {
                states[i] = true;
                answers[answerIndex++] = this.checkBoxesVector.get(i).getText().toString();
            }
        }
        this.questionNodesVector.get(this.currentQuestionNode).setState(states);
        this.questionNodesVector.get(this.currentQuestionNode).setStateAnswer(answers);
    }

    private void saveTextBox() {
        this.questionNodesVector.get(this.currentQuestionNode).setStateAnswer(this.answerEditText.getText().toString());
    }

    private void mapRadioButtons() {

        this.radioPanel = (RadioGroup) findViewById(R.id.choices_radio_group);
//        this.radioButtonsVector.add((RadioButton) findViewById(R.id.radio_choice_A));
//        this.radioButtonsVector.add((RadioButton) findViewById(R.id.radio_choice_B));
//        this.radioButtonsVector.add((RadioButton) findViewById(R.id.radio_choice_C));
//        this.radioButtonsVector.add((RadioButton) findViewById(R.id.radio_choice_D));

    }

    private void mapCheckBoxes() {

        this.checkBoxPanel = (LinearLayout) findViewById(R.id.checkboxes_group);
//        this.checkBoxesVector.add((CheckBox) findViewById(R.id.checkbox_A));
//        this.checkBoxesVector.add((CheckBox) findViewById(R.id.checkbox_B));
//        this.checkBoxesVector.add((CheckBox) findViewById(R.id.checkbox_C));
//        this.checkBoxesVector.add((CheckBox) findViewById(R.id.checkbox_D));

    }

    private void display() {

        updateQuestionNumber();
        displayCurrentQuestionNode();
        setupCurrentChoices();
        restoreStates();
        displayChoices();

    }

    private void updateQuestionNumber() {
        ((TextView) findViewById(R.id.question_number_textview)).setText(getText(R.string.os_textview_string) + ": " +
                getText(R.string.question_heading) + String.valueOf(this.currentQuestionNode + 1));
    }

    private void displayCurrentQuestionNode() {
        ((TextView) findViewById(R.id.question_textview)).setText(this.questionNodesVector.get(this.currentQuestionNode).getQuestion());
    }

    private void restoreStates() {

        switch (this.questionNodesVector.get(this.currentQuestionNode).getType()) {
            case RADIO:
                restoreRadioButtons();
                break;

            case CHECK_BOX:
                restoreCheckBoxes();
                break;

            case TEXT:
                restoreTextBox();
        }
    }

    private void restoreRadioButtons() {
        boolean states[] = this.questionNodesVector
                .get(this.currentQuestionNode)
                .getStates(this.questionNodesVector.get(this.currentQuestionNode).getType());

        this.radioPanel.clearCheck();

        for (int i = 0; i < this.radioButtonsVector.size(); i++) {
            this.radioButtonsVector.get(i).setChecked(states[i]);
        }
    }

    private void restoreCheckBoxes() {
        boolean states[] = this.questionNodesVector
                .get(this.currentQuestionNode)
                .getStates(this.questionNodesVector.get(this.currentQuestionNode).getType());

        for (int i = 0; i < this.checkBoxesVector.size(); i++) {
            this.checkBoxesVector.get(i).setChecked(states[i]);
        }
    }

    private void restoreTextBox() {
        if (!this.questionNodesVector.get(this.currentQuestionNode).getState().isEmpty()) {
            this.answerEditText.setText(this.questionNodesVector.get(this.currentQuestionNode).getState().firstElement());
        } else {
            this.answerEditText.setText("");
        }

    }

    private void displayChoices(){
        switch (this.questionNodesVector.get(this.currentQuestionNode).getType()) {
            case RADIO:
                this.radioPanel.setVisibility(View.VISIBLE);
                break;

            case CHECK_BOX:
                this.checkBoxPanel.setVisibility(View.VISIBLE);
                break;

            case TEXT:
                displayTextBox();
        }



    }

    private void setupCurrentChoices() {

        switch (this.questionNodesVector.get(this.currentQuestionNode).getType()) {
            case RADIO:
                setupRadioChoices();
                break;

            case CHECK_BOX:
                setupCheckBoxes();
        }
    }

    private void setupRadioChoices() {

        hideCheckBoxPanel();
        hideTextBox();
        hideRadioPanel();

        this.radioPanel.removeAllViews();
        this.radioButtonsVector.clear();

        String choices[] = this.questionNodesVector.get(this.currentQuestionNode).getChoices();

        for (int i = 0; i < choices.length; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setTextAppearance(this, R.style.RadioButtonStyle);
            rb.setText(choices[i]);
            this.radioPanel.addView(rb);
            this.radioButtonsVector.add(rb);
        }

    }

    private void setupCheckBoxes() {

        hideCheckBoxPanel();
        hideTextBox();
        hideRadioPanel();

        this.checkBoxPanel.removeAllViews();
        this.checkBoxesVector.clear();

        String choices[] = this.questionNodesVector.get(this.currentQuestionNode).getChoices();

        for (int i = 0; i < choices.length; i++) {
            CheckBox cb = new CheckBox(this);
            cb.setTextAppearance(this, R.style.CheckBoxStyle);
            cb.setText(choices[i]);
            this.checkBoxesVector.add(cb);
            this.checkBoxPanel.addView(cb);
        }
    }

    private void displayTextBox() {
        hideCheckBoxPanel();
        hideRadioPanel();
        this.answerEditText.setVisibility(View.VISIBLE);

    }

    private void hideRadioPanel() {
//        for (int i = 0; i < this.radioButtonsVector.size(); i++) {
//            this.radioButtonsVector.get(i).setVisibility(View.GONE);
//        }
        this.radioPanel.setVisibility(View.GONE);
    }

    private void hideCheckBoxPanel() {
//        for (int i = 0; i < this.checkBoxesVector.size(); i++) {
//            this.checkBoxesVector.get(i).setVisibility(View.GONE);
//        }
        this.checkBoxPanel.setVisibility(View.GONE);
    }

    private void hideTextBox() {
        this.answerEditText.setVisibility(View.GONE);
    }

}
