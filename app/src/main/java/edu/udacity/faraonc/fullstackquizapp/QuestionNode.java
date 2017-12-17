package edu.udacity.faraonc.fullstackquizapp;

import android.util.Log;

import java.util.Arrays;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Vector;
import java.util.Random;

/**
 * QuestionNode contains the questions, answers, choices, and state of a given question in the app.
 *
 * @author ConardJames
 * @version 121617-01
 */
class QuestionNode implements Comparable<QuestionNode>, Serializable {
    private QuestionTypeEnum type;
    private String choices[];
    private String question;
    private Vector<String> answers;
    private QuestionNodeState questionState;

    private final static int FIRST_CHAR = 0;
    private final static int EQUAL = 0;
    private final static int NOT_EQUAL = -1;
    private final static int TRUE_FALSE_LENGTH = 2;

    /**
     * Construct a node based on the given parameters.
     *
     * @param question the question to be displayed.
     * @param choices  the choices for the user.
     * @param type     the type of question.
     */
    QuestionNode(String question, String[] choices, QuestionTypeEnum type) {
        this.question = question;
        this.answers = new Vector<>();
        this.type = type;
        this.questionState = new QuestionNodeState(this.type);
        this.choices = new String[choices.length];

        for (int i = 0; i < choices.length; i++) {

            //initialize the choices
            //answers are marked with the character '$'
            if (choices[i].charAt(FIRST_CHAR) == '$') {
                answers.add(removeCharAt(choices[i], 0));
                this.choices[i] = removeCharAt(choices[i], 0);
            } else {
                this.choices[i] = choices[i];
            }
        }

        //shuffle the choices if it is not TRUE or FALSE
        if (this.choices.length > TRUE_FALSE_LENGTH) {
            shuffleChoices();
        }
    }

    /**
     * Get the question type.
     *
     * @return the type of question.
     */
    QuestionTypeEnum getType() {
        return this.type;
    }

    /**
     * Shuffle the choices
     */
    private void shuffleChoices() {
        Random random = new Random();
        for (int i = this.choices.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            String temp = this.choices[index];
            this.choices[index] = this.choices[i];
            this.choices[i] = temp;
        }
    }

    @Override
    /**
     * String representation of the node.
     *
     * @return the string representation of the node.
     */
    public String toString() {
        return this.type + " " + this.question;
    }

    /**
     * Compare if two nodes are equal or not.
     *
     * @param rhs the right-hand side node.
     * @return 0    if equivalent else -1.
     */
    public int compareTo(QuestionNode rhs) {
        if (this.type == rhs.type && this.question.equals(rhs.question)) {
            return EQUAL;
        }
        return NOT_EQUAL;
    }

    /**
     * Get the question.
     *
     * @return the question.
     */
    String getQuestion() {
        return this.question;
    }

    /**
     * Get the choices for the question.
     *
     * @return the choices for the question
     */
    String[] getChoices() {
        return this.choices;
    }

    /**
     * Set the states for the current question for precise display.
     *
     * @param states the states of choices made by the user.
     */
    void setState(boolean states[]) {
        for (int i = 0; i < states.length; i++) {
            this.questionState.states[i] = states[i];
        }
    }

    /**
     * Saves the answers of the user for the current question.
     *
     * @param answers the users answers for the question.
     */
    void setStateAnswer(String answers[]) {
        this.questionState.answersVector.clear();
        for (int i = 0; i < answers.length; i++) {
            this.questionState.answersVector.add(answers[i]);
        }
    }

    /**
     * Saves the answers of the user for the current question.
     *
     * @param answer the users answers for the question.
     */
    void setStateAnswer(String answer) {
        this.questionState.answersVector.clear();
        this.questionState.answersVector.add(answer);
    }

    /**
     * Get the state of the node.
     *
     * @param type the question type.
     * @return the states of node.
     */
    boolean[] getStates(QuestionTypeEnum type) {
        if (type != QuestionTypeEnum.TEXT) {
            return this.questionState.states;
        }

        return null;
    }

    /**
     * Get the vector of answers.
     *
     * @return the vector of answers.
     */
    Vector<String> getAnswers() {
        return this.questionState.answersVector;
    }

    /**
     * Compare the answers for the current node.
     *
     * @return true if user gets the correct answer/s
     */
    boolean compareAnswer() {
        if (this.questionState.answersVector.size() == 1) {
            if (this.questionState.answersVector.firstElement().equalsIgnoreCase(this.answers.firstElement())) {
                return true;
            }
        } else if (this.questionState.answersVector.size() > 1) {
            for (Iterator<String> iter = this.questionState.answersVector.listIterator(); iter.hasNext(); ) {
                String temp = iter.next();
                if (temp == null) {
                    iter.remove();
                }
            }
            if (this.questionState.answersVector.size() == this.answers.size() && this.questionState.answersVector.containsAll(this.answers)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove a character of a string.
     *
     * @param s   the string to be processed.
     * @param pos the position of the character to be removed.
     * @return the string without the character
     */
    private static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    /**
     * The state of the QuestionNode.
     *
     * @author ConardJames
     * @version 121607-01
     */
    class QuestionNodeState implements Serializable {
        private static final int MAX_CHOICES = 4;
        private boolean states[];
        private Vector<String> answersVector;

        /**
         * Construct the state of the node based on its type.
         *
         * @param type the question type.
         */
        QuestionNodeState(QuestionTypeEnum type) {
            this.answersVector = new Vector<>();
            if (type != QuestionTypeEnum.TEXT) {
                this.states = new boolean[MAX_CHOICES];
            }
        }

        @Override
        /**
         * String representation of QuestionNodeState.
         *
         * @return the string representation of the QuestionNodeState.
         */
        public String toString() {
            return Arrays.toString(this.states) + " & " + this.answersVector.toString();
        }
    }
}
