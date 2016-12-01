package org.snagajob.model;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.snagajob.Driver;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by peshave on 11/27/16.
 */
public class Questionnaire {

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    private List<QuestionItem> questions;

    public Questionnaire(final List<QuestionItem> questions) {
        this.questions = questions;
    }

    public static Questionnaire get(final String questionsFile) throws IOException {
        List<QuestionItem> questions = objectMapper.readValue(
                new File(String.format(Driver.getQuesDir() + "/" + questionsFile)),
                objectMapper.getTypeFactory().constructCollectionType(List.class, QuestionItem.class));

        for (QuestionItem q : questions) {
            if (!q.isValid()) {
                throw new IOException(String.format("Questionnaire doesnt conform to the expceted format," +
                        "something is missing: %s", questionsFile));
            }
        }

        return new Questionnaire(questions);
    }

    public void setQuestions(List<QuestionItem> questions) {
        this.questions = questions;
    }

    public int numberOfQuestion() {
        return this.questions.size();
    }

    public Iterator<QuestionItem> getQuesItr() {
        return this.questions.iterator();
    }
}
