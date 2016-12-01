package org.snagajob;

import org.snagajob.model.Application;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * Provides persistence layer for evaluation application.
 * </p>
 * It is a dir/file based store.
 * </p>
 * Applications for each questionnaire are stores in directory according to the
 * date of the application and where the application is cleared or gated.
 * <p/>
 * Eg.: Application for q1, received on 2016-11-26 and cleared is stored under:
 *      ${PATH}/q1/2016-11-26/cleared/appfile
 * Eg.: Application for q1, received on 2016-11-26 and gated is stored under:
 *      ${PATH}/q1/2016-11-26/gated/appfile
 */
public class Store {

    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final Pattern APP_FILE_NAME_PATTERN = Pattern.compile(".*_\\d{4}-\\d{2}-\\d{2}.json");
    private static String processedDir = null;
    private static String unprocessedDir = null;

    public static String getProcessedDir() {
        return processedDir;
    }

    public static void setProcessedDir(String processedDir) {
        Store.processedDir = processedDir;
    }

    public static String getUnprocessedDir() {
        return unprocessedDir;
    }

    public static void setUnprocessedDir(String unprocessedDir) {
        Store.unprocessedDir = unprocessedDir;
    }

    public static void markAppProcessed(final Application application) {
        Matcher matcher = DATE_PATTERN.matcher(application.getAppFile().getName());
        matcher.find();

        String outputFileName = String.format("%s/%s/%s/%s/%s", Store.getProcessedDir(), application.getQuestionnaire(),
                matcher.group(0), getSubDir(application.isCleared()), application.getName());
        Path outputPath = Paths.get(outputFileName);

        try {
            if (outputPath.getParent() != null) {
                Files.createDirectories(outputPath.getParent());
            }

            Files.move(application.getAppFile().toPath(), Paths.get(outputFileName), REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void markAppUnprocessed(File appFile) {
        try {
            if (Paths.get(getUnprocessedDir()) != null) {
                Files.createDirectories(Paths.get(getUnprocessedDir()));
            }

            Files.move(appFile.toPath(), Paths.get(unprocessedDir, appFile.getName()), REPLACE_EXISTING);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static File[] getClearedForQuestionnaire(String questionnaire, String date) {
        return new File(
                String.format("%s/%s/%s/%s", Store.getProcessedDir(), questionnaire, date, getSubDir(true)))
                .listFiles();
    }

    public static boolean checkAppFile(String appFileName) {
        return APP_FILE_NAME_PATTERN.matcher(appFileName).find();
    }

    private static String getSubDir(boolean cleared) {
        return cleared ? "cleared" : "gated";
    }
}
