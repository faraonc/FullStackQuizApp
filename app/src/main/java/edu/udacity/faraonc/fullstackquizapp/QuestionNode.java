package edu.udacity.faraonc.fullstackquizapp;

/**
 * Created by faraonc on 12/13/17.
 */

class QuestionNode {
    private QuestionTypeEnum type;
    private String choices[];

    private String question[] = new String[2];
    static final int QUESTION = 0;
    static final int ANSWER = 1;

    QuestionNode(String question, String[] choices, String answer, QuestionTypeEnum type){
        this.question[QUESTION] = question;
        this.question[ANSWER] = answer;
        this.type = type;

        if(choices != null) {
            this.choices = new String[choices.length];

            for (int i = 0; i < choices.length; i++) {
                this.choices[i] = choices[i];
            }
        }
    }

    QuestionTypeEnum getType(){
        return this.type;
    }
}
