package edu.udacity.faraonc.fullstackquizapp;

import java.util.Vector;
import java.util.Random;

/**
 * Created by faraonc on 12/13/17.
 */
abstract class AbstractQuizMaker {

    private Vector<QuestionNode> checkBoxQuestionsVector;
    private Vector<QuestionNode> textQuestionsVector;
    private Vector<QuestionNode> radioQuestionsVector;

    protected final void init(){
        this.checkBoxQuestionsVector = new Vector<>();
        this.textQuestionsVector = new Vector<>();
        this.radioQuestionsVector = new Vector<>();
    }

    protected final int getNumberCheckBoxQuestions(){
        return this.checkBoxQuestionsVector.size();
    }

    protected final int getNumberTextQuestions(){
        return this.textQuestionsVector.size();
    }

    protected final int getNumberRadioQuestions(){
        return this.radioQuestionsVector.size();
    }

    protected final QuestionNode queryQuestion(QuestionTypeEnum type){

        Random random = new Random();
        switch(type){

            case CHECK_BOX:
                if(!this.checkBoxQuestionsVector.isEmpty()) {
                    random.nextInt(this.checkBoxQuestionsVector.size());
                }
                break;

            case TEXT:
                if(!this.textQuestionsVector.isEmpty()) {
                    random.nextInt(this.textQuestionsVector.size());
                }
                break;

            case RADIO:
                if(!this.radioQuestionsVector.isEmpty()){
                    random.nextInt(this.radioQuestionsVector.size());
                }
                break;
        }

        return null;
    }

    protected final boolean addQuestion(QuestionNode node){

        switch(node.getType()){

            case CHECK_BOX:
                if(this.checkBoxQuestionsVector != null) {
                    this.checkBoxQuestionsVector.add(node);
                }
                return true;

            case TEXT:
                if(this.textQuestionsVector != null) {
                    this.textQuestionsVector.add(node);
                }
                return true;

            case RADIO:
                if(this.radioQuestionsVector != null){
                    this.radioQuestionsVector.add(node);
                }
                return true;

            default:
                return false;
        }
    }



    protected abstract void populateCheckBoxQuestions();
    protected abstract void populateTextQuestions();
    protected abstract void populateRadioQuestions();

}