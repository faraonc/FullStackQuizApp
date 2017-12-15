package edu.udacity.faraonc.fullstackquizapp;

import java.util.Vector;
import java.util.Random;

/**
 * Created by faraonc on 12/13/17.
 */

class QuestionNode implements Comparable<QuestionNode> {
    private QuestionTypeEnum type;
    private String choices[];
    private String question;
    private Vector<String> answers;

    private final static int FIRST_CHAR = 0;
    private final static int EQUAL = 0;
    private final static int NOT_EQUAL = -1;
    private final static int TRUE_FALSE_LENGTH = 2;

    QuestionNode(String question, String[] choices, QuestionTypeEnum type) {
        this.question = question;
        this.answers = new Vector<>();
        this.type = type;
        this.choices = new String[choices.length];

        for (int i = 0; i < choices.length; i++) {
            if (choices[i].charAt(FIRST_CHAR) == '$') {
                answers.add(choices[i]);
                this.choices[i] = removeCharAt(choices[i],0);
            } else {
                this.choices[i] = choices[i];
            }
        }

        if (this.choices.length > TRUE_FALSE_LENGTH) {
            shuffleChoices();
        }
    }

    QuestionTypeEnum getType() {
        return this.type;
    }

    private void shuffleChoices() {
        // If running on Java 6 or older, use `new Random()` on RHS here
        Random random = new Random();
        for (int i = this.choices.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            // Simple swap
            String temp = this.choices[index];
            this.choices[index] = this.choices[i];
            this.choices[i] = temp;
        }
    }

    @Override
    public String toString(){
        return this.type + " " + this.question;
    }

    public int compareTo(QuestionNode rhs){
        if(this.type == rhs.type && this.question.equals(rhs.question)){
            return EQUAL;
        }
        return NOT_EQUAL;
    }

    String getQuestion(){
        return this.question;
    }

    String[] getChoices(){
        return this.choices;
    }

    private static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }
}
