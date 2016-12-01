package org.snagajob;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * Watches the incoming application directory for new application files.
 * </p>
 * When a new applciation file arrives, the watcher adds a new {@link org.snagajob.evaluation.EvaluateAppTask}
 * to the {@link org.snagajob.evaluation.EvaluationEngine}
 */
public class FileWatcherQueue implements Runnable {

    /**
     * {@link WatchService} to watch incoming directory.
     */
    private final WatchService service;
    /**
     * Application driver {@link Driver}.
     */
    private final Driver driver;

    public FileWatcherQueue(final Driver driver) throws IOException {
        Path dirToWatch = Paths.get(Driver.getAppDir());
        if (dirToWatch == null) {
            throw new FileNotFoundException(String.format("Directory not found: %s", Driver.getAppDir()));
        }

        this.service = dirToWatch.getFileSystem().newWatchService();
        dirToWatch.register(this.service, ENTRY_CREATE);

        this.driver = driver;
    }

    @Override
    public void run() {
        try {
            // get the first event before looping
            WatchKey key = this.service.take();
            while (key != null) {
                // we have a polled event, now we traverse it and
                // receive all the states from it
                for (WatchEvent event : key.pollEvents()) {
                    final Path path = ((WatchEvent<Path>) event).context();
                    System.out.printf("Received %s event for file: %s\n", event.kind(), event.context());
                    this.driver.processApp(new File(Driver.getAppDir(), path.toString()));
                }
                key.reset();
                key = this.service.take();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping thread");
    }
}
