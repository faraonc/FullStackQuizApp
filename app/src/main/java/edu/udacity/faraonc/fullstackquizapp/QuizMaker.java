package edu.udacity.faraonc.fullstackquizapp;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Vector;
import java.util.Random;
import java.util.HashSet;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Parses XML and generates question nodes.
 *
 * @author ConardJames
 * @version 121617-01
 */
class QuizMaker implements Serializable {

    private Vector<QuestionNode> checkBoxQuestionsVector;
    private Vector<QuestionNode> textQuestionsVector;
    private Vector<QuestionNode> radioQuestionsVector;
    private HashSet<Integer> questionsTracker[];
    private QuizTypeEnum type;

    private final static int CATEGORY_COUNT = 3;
    private final static int CHECK_BOX_INDEX = 0;
    private final static int TEXT_INDEX = 1;
    private final static int RADIO_INDEX = 2;
    private final static int QUESTION_TYPE_INDEX = 0;
    private final static int NOT_FOUND = -1;
    private final static int QUESTION = 1;
    private final static int ANSWER = 2;

    /**
     * Constructs a database of questions based on the given type.
     *
     * @param context used for resource access
     * @param type    quiz category
     */
    QuizMaker(Context context, QuizTypeEnum type) {
        this.checkBoxQuestionsVector = new Vector<>();
        this.textQuestionsVector = new Vector<>();
        this.radioQuestionsVector = new Vector<>();
        this.questionsTracker = new HashSet[CATEGORY_COUNT];
        this.type = type;
        for (int i = 0; i < CATEGORY_COUNT; i++) {
            this.questionsTracker[i] = new HashSet<>();
        }

        Resources resource = context.getResources();
        Field[] fields = R.array.class.getFields();
        for (final Field field : fields) {
            try {
                //find the questions related to the category O(n), optimize into a key/value pair later
                if (field.getName().toLowerCase().contains(this.type.toString().toLowerCase())) {
                    String question[] = resource.getStringArray(field.getInt(R.string.class));
                    processStringArrayQuestion(question);
                }
            } catch (Exception ex) {
                Log.e(context.getString(R.string.quizmaker_constructor_error), ex.getMessage());
            }
        }
    }

    /**
     * Query a question based on the given type.
     * Questions are randomly selected.
     *
     * @param type the type of question
     * @return the node containing the question, choices, and answers.
     */
    QuestionNode queryQuestion(QuestionTypeEnum type) {
        Random random = new Random();
        int index = NOT_FOUND;
        switch (type) {

            case CHECK_BOX:
                if (!this.checkBoxQuestionsVector.isEmpty()) {
                    do {
                        index = random.nextInt(this.checkBoxQuestionsVector.size());

                    } while (!this.questionsTracker[CHECK_BOX_INDEX].add(index) &&
                            this.questionsTracker[CHECK_BOX_INDEX].size() != this.checkBoxQuestionsVector.size());

                    if (index != NOT_FOUND) {
                        return this.checkBoxQuestionsVector.get(index);
                    }
                }
                break;

            case TEXT:
                if (!this.textQuestionsVector.isEmpty()) {
                    do {
                        index = random.nextInt(this.textQuestionsVector.size());

                    } while (!this.questionsTracker[TEXT_INDEX].add(index) &&
                            this.questionsTracker[TEXT_INDEX].size() != this.textQuestionsVector.size());

                    if (index != NOT_FOUND) {
                        return this.textQuestionsVector.get(index);
                    }
                }
                break;

            case RADIO:
                if (!this.radioQuestionsVector.isEmpty()) {
                    do {
                        index = random.nextInt(this.radioQuestionsVector.size());

                    } while (!this.questionsTracker[RADIO_INDEX].add(index) &&
                            this.questionsTracker[RADIO_INDEX].size() != this.radioQuestionsVector.size());

                    if (index != NOT_FOUND) {
                        return this.radioQuestionsVector.get(index);
                    }
                }
        }
        return null;
    }

    /**
     * Process the
     *
     * @param question the array containing the data of the questions.
     */
    private void processStringArrayQuestion(String[] question) {
        QuestionTypeEnum questionType = null;

        if (question[QUESTION_TYPE_INDEX].equals("0")) {
            questionType = QuestionTypeEnum.TEXT;
        } else if (question[QUESTION_TYPE_INDEX].equals("1")) {
            questionType = QuestionTypeEnum.CHECK_BOX;
        } else if (question[QUESTION_TYPE_INDEX].equals("2")) {
            questionType = QuestionTypeEnum.RADIO;
        }

        addQuestion(new QuestionNode(question[QUESTION], question[ANSWER].split(","), questionType));
    }

    /**
     * Add the question to the right vector.
     *
     * @param node the QuestionNode to be added to the corresponding vector.
     */
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