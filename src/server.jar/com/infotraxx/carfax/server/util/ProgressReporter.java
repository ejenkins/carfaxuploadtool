package com.infotraxx.carfax.server.util;

/**
 * The progress reporter interface.
 * @author Ed Jenkins
 */
public interface ProgressReporter
{

    /**
     * Report progress.
     */
    public void report();

    /**
     * Move the progress bar to 100% to indicate that the task has finished earlier than expected.
     */
    public void finish();

}
