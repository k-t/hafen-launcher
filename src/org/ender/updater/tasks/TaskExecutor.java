package org.ender.updater.tasks;

import org.ender.updater.util.ExceptionUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TaskExecutor {
    private final BlockingQueue<QueueItem> tasks;
    private volatile boolean running;

    public TaskExecutor() {
        tasks = new LinkedBlockingQueue<QueueItem>();
        run();
    }

    public void queue(Task task, TaskListener listener) {
        boolean ret = tasks.add(new QueueItem(task, listener));
    }

    public void stop() {
        running = false;
    }

    private void run() {
        running = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (running) {
                    QueueItem item = null;
                    try {
                        item = tasks.poll(100, TimeUnit.MILLISECONDS);
                        if (item == null)
                            continue;
                        item.task.run(item.listener);
                    } catch (InterruptedException e) {
                    } catch (Exception e) {
                        if (item != null)
                            item.listener.log(ExceptionUtils.getStackTrack(e));
                    } finally {
                        if (item != null)
                            item.listener.finished();
                    }
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }

    private class QueueItem {
        public final Task task;
        public final TaskListener listener;

        public QueueItem(Task task, TaskListener listener) {
            this.task = task;
            this.listener = listener;
        }
    }
}