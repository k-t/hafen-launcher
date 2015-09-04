package org.ender.updater;

import org.ender.updater.UpdaterListener;

public interface UpdaterTask {
    void run(UpdaterListener listener);
}
