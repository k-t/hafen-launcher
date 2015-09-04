package org.ender.updater;

import org.ender.updater.util.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

public class Updater {
    private final UpdaterListener listener;
    private final List<UpdaterTask> tasks;

    public Updater(UpdaterListener listener) {
        this.listener = listener;
        this.tasks = new ArrayList<UpdaterTask>();
    }

    public void addTask(UpdaterTask task) {
        tasks.add(task);
    }

    public void run() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (UpdaterTask task : tasks)
                        task.run(listener);
                } catch (Exception e) {
                    listener.log(ExceptionUtils.getStackTrack(e));
                } finally {
                    listener.finished();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
}