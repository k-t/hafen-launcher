package org.ender.updater.tasks;

public interface TaskListener {

    void log(String format);

    void finished();

    void step(String text);

    void progress(long position, long size);

}
