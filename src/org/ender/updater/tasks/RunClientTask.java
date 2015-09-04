package org.ender.updater.tasks;

import org.ender.updater.*;

import java.io.File;
import java.io.IOException;

public class RunClientTask implements UpdaterTask {
    private final UpdaterConfig config;

    public RunClientTask(UpdaterConfig config) {
        this.config = config;
    }

    @Override
    public void run(UpdaterListener listener) {
        listener.step("Starting client...");
        String libs = String.format("-Djava.library.path=\"%%PATH%%\"%s.", File.pathSeparator);
        ProcessBuilder pb = new ProcessBuilder("java", "-Xmx"+config.mem, libs, "-jar", config.jar, "-U", config.res, config.server);
        pb.directory(config.dir.getAbsoluteFile());
        try {
            pb.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
