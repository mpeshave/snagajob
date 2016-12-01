package org.snagajob;

import org.snagajob.evaluation.EvaluateAppTask;
import org.snagajob.evaluation.EvaluationEngine;

import java.io.File;
import java.io.IOException;


/**
 * Driver for job evaluation app.
 * </p>
 * Instantiates and acts as a link between {@link FileWatcherQueue} and {@link EvaluationEngine}.
 */
public class Driver {

    private static String appDir = null;
    private static String quesDir = null;

    /**
     * Application evaluation engine is a pool of threads.
     */
    final private EvaluationEngine engine;
    /**
     * Directory watcher pools application incoming dir for new applications
     * and feeds it to the evercution engine.
     */
    final private FileWatcherQueue fileWatcherQueue;

    public Driver() throws IOException {
        this.engine = new EvaluationEngine();
        fileWatcherQueue = new FileWatcherQueue(this);
    }

    public static String getAppDir() {
        return appDir;
    }

    public static void setAppDir(String appDir) {
        Driver.appDir = appDir;
    }

    public static String getQuesDir() {
        return quesDir;
    }

    public static void setQuesDir(String quesDir) {
        Driver.quesDir = quesDir;
    }

    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            System.out.printf("Must have 4 inputs: %s\n", args.length);
            System.out.printf("Usage: Driver <apps_dir> <ques_dir> <output_dir> <unprocessed_dir>\n");
            System.out.printf("apps_dir: dir where application json files are stored.\n");
            System.out.printf("ques_dir: dir where questionnaire json files are stored.\n");
            System.out.printf("output_dir: dir to store output.\n");
            System.out.printf("unprocessed_dir: dir to store unprocessed applications.\n");
            System.exit(-1);
        }

        Driver.setAppDir(args[0].trim());
        Driver.setQuesDir(args[1].trim());
        Store.setProcessedDir(args[2].trim());
        Store.setUnprocessedDir(args[3].trim());

        Driver d = new Driver();
        d.process();
    }

    /**
     * Starts the {@link FileWatcherQueue} and scans the incoming dir for already existing application files.
     * If there are existing file in the dir, feeds each one to the evaluation engine. Handles cold starts.
     *
     * @throws IOException
     */
    public void process() throws IOException {
        Thread th = new Thread(this.fileWatcherQueue, "APP_DIR_WATCHER");
        th.start();

        // check for existing files and add to the engine
        File appDir = new File(Driver.getAppDir());
        File[] applicationFiles = appDir.listFiles();
        if (applicationFiles.length != 0) {
            for (File appFile : applicationFiles) {
                if (appFile.isFile()) {
                    this.processApp(appFile);
                }
            }
        }
    }

    /**
     * Adds a new {@link EvaluateAppTask} to the {@link EvaluationEngine} queue.
     * </p>
     * Method is called by {@link FileWatcherQueue} when it receives a new application file.
     *
     * @param newAppFile new applciation json file to process.
     */
    public void processApp(File newAppFile) {
        if (newAppFile.isFile()) {
            EvaluateAppTask appTask = new EvaluateAppTask(newAppFile);
            this.engine.addTask(appTask);
        } else {
            System.out.printf("%s is not a file", newAppFile.toString());
            Store.markAppUnprocessed(newAppFile);
        }
    }
}
