package edu.udacity.faraonc.fullstackquizapp;

import java.util.Vector;
import java.util.Random;
import java.util.HashSet;

import android.util.Log;

/**
 * Created by faraonc on 12/13/17.
 */
abstract class AbstractQuizMaker {

    private Vector<QuestionNode> checkBoxQuestionsVector;
    private Vector<QuestionNode> textQuestionsVector;
    private Vector<QuestionNode> radioQuestionsVector;
    private HashSet<Integer> questionsTracker[];

    private final static int CATEGORY_COUNT = 3;
    private final static int CHECK_BOX_INDEX = 0;
    private final static int TEXT_INDEX = 1;
    private final static int RADIO_INDEX = 2;
    private final static int QUESTION_TYPE_INDEX = 0;
    private final static int NOT_FOUND = -1;

    @SuppressWarnings("unchecked")
    protected final void init() {
        this.checkBoxQuestionsVector = new Vector<>();
        this.textQuestionsVector = new Vector<>();
        this.radioQuestionsVector = new Vector<>();
        this.questionsTracker = new HashSet[CATEGORY_COUNT];
        for(int i = 0; i < CATEGORY_COUNT; i++){
            this.questionsTracker[i] = new HashSet<>();
        }
    }

    protected final int getNumberCheckBoxQuestions() {
        return this.checkBoxQuestionsVector.size();
    }

    protected final int getNumberTextQuestions() {
        return this.textQuestionsVector.size();
    }

    protected final int getNumberRadioQuestions() {
        return this.radioQuestionsVector.size();
    }

    protected final QuestionNode queryQuestion(QuestionTypeEnum type) {

        Random random = new Random();
        int index = NOT_FOUND;
        switch (type) {

            case CHECK_BOX:
                if (!this.checkBoxQuestionsVector.isEmpty()) {
                    do{
                        index = random.nextInt(this.checkBoxQuestionsVector.size());

                    }while(!this.questionsTracker[CHECK_BOX_INDEX].add(index) &&
                            this.questionsTracker[CHECK_BOX_INDEX].size() != this.checkBoxQuestionsVector.size());

                    if(index != NOT_FOUND) {
                        return this.checkBoxQuestionsVector.get(index);
                    }
                }
                break;

            case TEXT:
                if (!this.textQuestionsVector.isEmpty()) {
                    do{
                        index = random.nextInt(this.textQuestionsVector.size());

                    }while(!this.questionsTracker[TEXT_INDEX].add(index) &&
                            this.questionsTracker[TEXT_INDEX].size() != this.textQuestionsVector.size());

                    if(index != NOT_FOUND) {
                        return this.textQuestionsVector.get(index);
                    }
                }
                break;

            case RADIO:
                if (!this.radioQuestionsVector.isEmpty()) {
                    do{
                        index = random.nextInt(this.radioQuestionsVector.size());

                    }while(!this.questionsTracker[RADIO_INDEX].add(index) &&
                            this.questionsTracker[RADIO_INDEX].size() != this.radioQuestionsVector.size());

                    if(index != NOT_FOUND) {
                        return this.radioQuestionsVector.get(index);
                    }
                }

        }

        return null;
    }

    protected final void processStringArrayQuestion(String[] question) {

        QuestionTypeEnum questionType = null;

        if (question[QUESTION_TYPE_INDEX].equals("0")) {
            questionType = QuestionTypeEnum.TEXT;
        } else if (question[QUESTION_TYPE_INDEX].equals("1")) {
            questionType = QuestionTypeEnum.CHECK_BOX;
        } else if (question[QUESTION_TYPE_INDEX].equals("2")) {
            questionType = QuestionTypeEnum.RADIO;
        }

        addQuestion(new QuestionNode(question[1], question[2].split(","), questionType));
    }

    private void addQuestion(QuestionNode node) {

        switch (node.getType()) {

            case CHECK_BOX:
                if (this.checkBoxQuestionsVector != null) {
                    this.checkBoxQuestionsVector.add(node);
                }
                break;

            case TEXT:
                if (this.textQuestionsVector != null) {
                    this.textQuestionsVector.add(node);
                }
                break;

            case RADIO:
                if (this.radioQuestionsVector != null) {
                    this.radioQuestionsVector.add(node);
                }
        }
    }

}