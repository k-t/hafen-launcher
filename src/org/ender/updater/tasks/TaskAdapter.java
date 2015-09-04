package org.ender.updater.tasks;

public class TaskAdapter implements TaskListener  {
    private final TaskListener base;

    public TaskAdapter(TaskListener base){
        this.base = base;
    }

    @Override
    public void log(String format) {
        base.log(format);
    }

    @Override
    public void finished() {
        base.finished();
    }

    @Override
    public void progress(long position, long size) {
        base.progress(position, size);
    }
}
