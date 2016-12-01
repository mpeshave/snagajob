package org.snagajob.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.snagajob.serde.HashMapDeserializer;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by peshave on 11/27/16.
 */
public class Application {
    private String name;

    @JsonDeserialize(using = HashMapDeserializer.class)
    private HashMap<String, String> questions;

    private String questionnaire;

    private File appFile;

    private boolean cleared = false;

    public Application() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAnsForQuestion(final String quesId) {
        return this.questions.get(quesId);
    }

    public void setQuestions(HashMap<String, String> questions) {
        this.questions = questions;
    }

    public String getQuestionnaire() {
        return questionnaire;
    }

    public void setQuestionnaire(String questionnaire) {
        this.questionnaire = questionnaire;
    }

    public File getAppFile() {
        return appFile;
    }

    public void setAppFile(File appFile) {
        this.appFile = appFile;
    }

    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
    }

    public int getNumOfAnswers() {
        return this.questions.size();
    }

    public boolean isValid() {
        return this.name != null && !this.name.isEmpty() && this.questionnaire != null && !this.questionnaire.isEmpty()
                && this.questions != null;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(String.format("Name: %s, ", this.name));
        strBuilder.append("Questions: [ ");
        for (Map.Entry<String, String> ans : this.questions.entrySet()) {
            strBuilder.append(String.format("%s, ", ans));
        }
        strBuilder.append(" ], ");
        strBuilder.append(String.format("Questionnaire: %s", this.questionnaire));
        return strBuilder.toString();
    }
}
