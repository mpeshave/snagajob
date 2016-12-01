package org.snagajob.evaluation;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 * This class holds a cached thread poll to perform evaluations.
 * <p/>
 * Later this could be modified/improved as per requirements.
 */
public class EvaluationEngine {
    /**
     * Cached thread executor that operates on an unbounded queue.
     * All the incoming requests to perform application evaluationare fed to this queue
     * for worker threads to execute them subsequently.
     */
    private final ExecutorService executor;

    public EvaluationEngine() {
        this.executor = Executors.newCachedThreadPool();
    }

    public void addTask(final EvaluateAppTask task) {
        assert task != null;
        if (this.executor.isShutdown()) {
            System.out.println("The executor is shutdown and cannot accept new tasks.");
            return;
        }

        try {
            this.executor.execute(task);
        } catch (RejectedExecutionException ree) {
            System.out.println("Executor rejected executing the task: " + task.toString() + " exception: " + ree.getMessage());
        } catch (NullPointerException npe) {
            System.out.println("Task cant be null.");
        }
    }

    public void destroy() {
        this.executor.shutdown();
    }
}
