package org.ender.updater;

public interface UpdaterListener {

    void log(String format);

    void finished();

    void progress(long position, long size);

}
