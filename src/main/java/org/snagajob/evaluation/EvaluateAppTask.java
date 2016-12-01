package org.snagajob.evaluation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.snagajob.Store;
import org.snagajob.model.Application;
import org.snagajob.model.QuestionItem;
import org.snagajob.model.Questionnaire;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Performs the task of evaluating an incoming {@link Application}.
 */
public class EvaluateAppTask implements Runnable {

    private File appFile;

    public EvaluateAppTask(final File appFile) {
        this.appFile = appFile;
    }

    public void run() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);

        try {

            if (!Store.checkAppFile(this.appFile.getName())) {
                System.out.printf("Application file name does not match expected format(app_YYYY-MM-DD.json): %s\n",
                        this.appFile.getName());
                Store.markAppUnprocessed(this.appFile);
                return;
            }

            Application application = objectMapper.readValue(this.appFile, Application.class);
            if (!application.isValid()) {
                System.out.printf("Application json doesnt conform to the format, it is incomplete: %s",
                        this.appFile.getName());
                Store.markAppUnprocessed(this.appFile);
                return;
            }
            application.setAppFile(this.appFile);
            application.setCleared(true);
            String quesFile = application.getQuestionnaire();

            if (!quesFile.isEmpty()) {
                Questionnaire questionnaire = Questionnaire.get(quesFile);

                if (questionnaire.numberOfQuestion() == application.getNumOfAnswers()) {
                    Iterator<QuestionItem> itr = questionnaire.getQuesItr();
                    while (itr.hasNext()) {
                        QuestionItem ques = itr.next();

                        String ans = application.getAnsForQuestion(ques.getId());
                        if (!ques.checkAnswer(ans)) {
                            application.setCleared(false);
                            break;
                        }
                    }
                } else {
                    System.out.println("Number of answers in the application doesnt match the number of questions in " +
                            "the questionnaire.");
                    application.setCleared(false);
                }

                Store.markAppProcessed(application);
                System.out.println(String.format("[%s]: %s cleared: %s", Thread.currentThread().getName(),
                        application.toString(), application.isCleared()));
            } else {
                System.out.println(String.format("Questionnaire[%s] is not correct in app: %s\n",
                        application.getQuestionnaire(), appFile.getName()));
                Store.markAppUnprocessed(this.appFile);
            }
        } catch (JsonParseException e) {
            System.out.printf("Can not parser application: %s\n%s", this.appFile.getName(), e.getMessage());
            Store.markAppUnprocessed(this.appFile);
        } catch (JsonMappingException e) {
            System.out.printf("Can not parser application: %s\n%s", this.appFile.getName(), e.getMessage());
            Store.markAppUnprocessed(this.appFile);
        } catch (IOException e) {
            System.out.printf("Can not parser application: %s\n%s", this.appFile.getName(), e.getMessage());
            Store.markAppUnprocessed(this.appFile);
        }
    }
}