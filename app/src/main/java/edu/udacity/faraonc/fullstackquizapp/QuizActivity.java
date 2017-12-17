package edu.udacity.faraonc.fullstackquizapp;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.os.SystemClock;

import java.util.Collections;
import java.util.Vector;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;

/**
 * The activity controller for the Quiz.
 *
 * @author ConardJames
 * @version 121617-01
 */
public class QuizActivity extends AppCompatActivity {

    private static final int RADIO_QUESTIONS_COUNT = 6;
    private static final int CHECKBOX_QUESTIONS_COUNT = 2;
    private static final int TEXT_QUESTIONS_COUNT = 2;
    private static final int ONE_SEC = 1000;
    private static final int HALF_SEC = 500;

    private Vector<QuestionNode> questionNodesVector = new Vector<>();
    private Vector<RadioButton> radioButtonsVector = new Vector<>();
    private Vector<CheckBox> checkBoxesVector = new Vector<>();

    private RadioGroup radioPanel;
    private LinearLayout checkBoxPanel;
    private EditText answerEditText;

    //pointer to the current node
    private int currentQuestionNode = 0;
    private QuizTypeEnum quizType;

    //the keys for the QuizMaker
    private final String CURRENT_QUESTION_NODE = "currentQuestionNode";
    private final String QUESTION_NODES_VECTOR = "questionNodesVector";


    @Override
    /**
     * Set activity and layout.
     *
     * @param savedInstanceState the state of the activity.
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get extra data for main activity
        Intent intent = getIntent();
        this.quizType = (QuizTypeEnum) intent.getSerializableExtra(MainActivity.SESSION);
        setContentView(R.layout.activity_quiz);

        //start generating the questions
        QuizMaker questionGenerator = new QuizMaker(this, this.quizType);
        for (int i = 0; i < RADIO_QUESTIONS_COUNT; i++) {
            this.questionNodesVector.add(questionGenerator.queryQuestion(QuestionTypeEnum.RADIO));
        }

        for (int i = 0; i < CHECKBOX_QUESTIONS_COUNT; i++) {
            this.questionNodesVector.add(questionGenerator.queryQuestion(QuestionTypeEnum.CHECK_BOX));
        }

        for (int i = 0; i < TEXT_QUESTIONS_COUNT; i++) {
            this.questionNodesVector.add(questionGenerator.queryQuestion(QuestionTypeEnum.TEXT));
        }

        //shuffle the questions
        Collections.shuffle(this.questionNodesVector);

        setupPrevButton();
        setupNextButton();
        setupSubmitButton();
        this.radioPanel = (RadioGroup) findViewById(R.id.choices_radio_group);
        this.checkBoxPanel = (LinearLayout) findViewById(R.id.checkboxes_group);
        this.answerEditText = (EditText) findViewById(R.id.answer_edit_text);
        display();
    }

    @Override
    /**
     * Save states of activity and layout.
     *
     * @param outState the state of the activity.
     */
    protected void onSaveInstanceState(Bundle outState) {
        // Make sure to call the super method so that the states of our views are saved
        super.onSaveInstanceState(outState);
        // Save our own state now
        saveQuestionNodeState();
        outState.putInt(CURRENT_QUESTION_NODE, this.currentQuestionNode);
        outState.putSerializable(QUESTION_NODES_VECTOR, this.questionNodesVector);

    }

    @Override
    /**
     * Restore states of activity and layout.
     *
     * @param savedInstanceState the state of the activity.
     */
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            this.currentQuestionNode = savedInstanceState.getInt(CURRENT_QUESTION_NODE, 0);
            this.questionNodesVector = (Vector<QuestionNode>) savedInstanceState.getSerializable(QUESTION_NODES_VECTOR);
            display();
        }
    }

    /**
     * Setup the prev button.
     */
    private void setupPrevButton() {
        Button prevButton = (Button) findViewById(R.id.prev_button);
        prevButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                //check the minimum bound
                if (currentQuestionNode > 0) {
                    saveQuestionNodeState();
                    currentQuestionNode--;
                    display();
                }
            }
        });
    }

    /**
     * Score the user and display the toast.
     */
    private void setupSubmitButton() {
        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new DebouncedOnClickListener(HALF_SEC) {

            @Override
            public void onDebouncedClick(View view) {
                saveQuestionNodeState();
                int correctAnswer = 0;
                for (int i = 0; i < questionNodesVector.size(); i++) {
                    if (questionNodesVector.get(i).compareAnswer()) {
                        correctAnswer++;
                    }
                }
                String message = getText(R.string.score_heading) + "  " + String.valueOf(correctAnswer) + " / " + String.valueOf(questionNodesVector.size());
                final Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL | Gravity.RIGHT, 25, 10);
                toast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        toast.cancel();
                    }
                }, ONE_SEC);
            }
        });
    }

    /**
     * Setup next button.
     */
    private void setupNextButton() {
        Button nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                //check the maximum bound
                if (currentQuestionNode < questionNodesVector.size() - 1) {
                    saveQuestionNodeState();
                    currentQuestionNode++;
                    display();
                }
            }
        });
    }

    /**
     * Save the state of the current question.
     */
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

    /**
     * Save the state of the current question if it uses radio buttons.
     */
    private void saveRadioButtons() {
        boolean states[] = new boolean[this.radioButtonsVector.size()];
        for (int i = 0; i < this.radioButtonsVector.size(); i++) {
            states[i] = this.radioButtonsVector.get(i).isChecked();
        }

        this.questionNodesVector.get(this.currentQuestionNode).setState(states);
        RadioButton rb = ((RadioButton) findViewById(this.radioPanel.getCheckedRadioButtonId()));
        if (rb != null) {
            this.questionNodesVector.get(this.currentQuestionNode).setStateAnswer(rb.getText().toString());
        }
    }

    /**
     * Save the state of the current question if it uses checkboxes.
     */
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
        if (answerIndex != 0) {
            this.questionNodesVector.get(this.currentQuestionNode).setStateAnswer(answers);
        }
    }

    /**
     * Save the state of the current question if it uses a text box.
     */
    private void saveTextBox() {
        this.questionNodesVector.get(this.currentQuestionNode).setStateAnswer(this.answerEditText.getText().toString());
    }

    /**
     * Display the current question node.
     */
    private void display() {
        updateQuestionNumber();
        displayCurrentQuestionNode();
        setupCurrentChoices();
        restoreStates();
        displayChoices();

    }

    /**
     * Update the number on the question header.
     */
    private void updateQuestionNumber() {
        ((TextView) findViewById(R.id.question_number_textview)).setText(this.quizType.toString().replace('_', ' ') + ": " +
                getText(R.string.question_heading) + String.valueOf(this.currentQuestionNode + 1));
    }

    /**
     * Display the question using the textview.
     */
    private void displayCurrentQuestionNode() {
        ((TextView) findViewById(R.id.question_textview)).setText(this.questionNodesVector.get(this.currentQuestionNode).getQuestion());
    }

    /**
     * Restore the view state for the current question.
     */
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

    /**
     * Restore the view state for the current question if it is using radio buttons.
     */
    private void restoreRadioButtons() {
        boolean states[] = this.questionNodesVector
                .get(this.currentQuestionNode)
                .getStates(this.questionNodesVector.get(this.currentQuestionNode).getType());

        this.radioPanel.clearCheck();

        for (int i = 0; i < this.radioButtonsVector.size(); i++) {
            this.radioButtonsVector.get(i).setChecked(states[i]);
        }
    }

    /**
     * Restore the view state for the current question if it is using check boxes.
     */
    private void restoreCheckBoxes() {
        boolean states[] = this.questionNodesVector
                .get(this.currentQuestionNode)
                .getStates(this.questionNodesVector.get(this.currentQuestionNode).getType());

        for (int i = 0; i < this.checkBoxesVector.size(); i++) {
            this.checkBoxesVector.get(i).setChecked(states[i]);
        }
    }

    /**
     * Restore the view state for the current question if it is using a text box.
     */
    private void restoreTextBox() {
        if (!this.questionNodesVector.get(this.currentQuestionNode).getAnswers().isEmpty()) {
            this.answerEditText.setText(this.questionNodesVector.get(this.currentQuestionNode).getAnswers().firstElement());
        } else {
            this.answerEditText.setText("");
        }

    }

    /**
     * Display the views.
     */
    private void displayChoices() {
        switch (this.questionNodesVector.get(this.currentQuestionNode).getType()) {
            case RADIO:
                hideKeyboard();
                this.radioPanel.setVisibility(View.VISIBLE);
                break;

            case CHECK_BOX:
                hideKeyboard();
                this.checkBoxPanel.setVisibility(View.VISIBLE);
                break;

            case TEXT:
                displayTextBox();
        }
    }

    /**
     * Setup the views for the current question.
     */
    private void setupCurrentChoices() {
        switch (this.questionNodesVector.get(this.currentQuestionNode).getType()) {
            case RADIO:
                setupRadioChoices();
                break;

            case CHECK_BOX:
                setupCheckBoxes();
        }
    }

    /**
     * Spawn view children for a question with multiple choices.
     */
    private void setupRadioChoices() {
        this.checkBoxPanel.setVisibility(View.GONE);
        this.answerEditText.setVisibility(View.GONE);
        this.radioPanel.setVisibility(View.GONE);

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

    /**
     * Spawn view children for a question with checkboxes.
     */
    private void setupCheckBoxes() {

        this.checkBoxPanel.setVisibility(View.GONE);
        this.answerEditText.setVisibility(View.GONE);
        this.radioPanel.setVisibility(View.GONE);

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

    /**
     * Shows the text box for a question that needs it.
     */
    private void displayTextBox() {
        this.checkBoxPanel.setVisibility(View.GONE);
        this.radioPanel.setVisibility(View.GONE);
        this.answerEditText.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the keyboard after using Edit Text.
     */
    private void hideKeyboard() {
        try{
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(this.answerEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }catch(Exception e){}
    }

    /********** SOURCE DebouncedOnClickListener: https://gist.github.com/rfreedman/5573388 ************/
    /**
     * A Debounced OnClickListener
     * Rejects clicks that are too close together in time.
     * This class is safe to use as an OnClickListener for multiple views, and will debounce each one separately.
     */
    abstract class DebouncedOnClickListener implements View.OnClickListener {

        private final long minimumInterval;
        private Map<View, Long> lastClickMap;

        /**
         * Implement this in your subclass instead of onClick
         *
         * @param v The view that was clicked
         */
        public abstract void onDebouncedClick(View v);

        /**
         * The one and only constructor
         *
         * @param minimumIntervalMsec The minimum allowed time between clicks - any click sooner than this after a previous click will be rejected
         */
        public DebouncedOnClickListener(long minimumIntervalMsec) {
            this.minimumInterval = minimumIntervalMsec;
            this.lastClickMap = new WeakHashMap<View, Long>();
        }

        @Override
        public void onClick(View clickedView) {
            Long previousClickTimestamp = lastClickMap.get(clickedView);
            long currentTimestamp = SystemClock.uptimeMillis();

            lastClickMap.put(clickedView, currentTimestamp);
            if (previousClickTimestamp == null || Math.abs(currentTimestamp - previousClickTimestamp.longValue()) > minimumInterval) {
                onDebouncedClick(clickedView);
            }
        }
    }
}
