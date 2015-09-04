package org.ender.updater.tasks;

public interface TaskListener {

    void log(String format);

    void finished();

    void progress(long position, long size);

}
