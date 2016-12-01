package org.snagajob.model;

/**
 * Created by peshave on 11/27/16.
 */
public class QuestionItem {

    private String id;

    private String question;

    private String answer;

    public QuestionItem() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getQuestion() {
        return this.question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean checkAnswer(String answered) {
        if (answered == null || answered.isEmpty()) {
            return false;
        }
        return this.answer.equals(answered);
    }

    public boolean isValid() {
        return this.id != null && !this.id.isEmpty() &&
                this.question != null && !this.question.isEmpty() &&
                this.answer != null && !this.answer.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("id: %s, ques: %s, ans: %s", this.id, this.question, this.answer);
    }
}
