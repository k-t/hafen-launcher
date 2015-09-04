package org.ender.updater.tasks;

import org.ender.updater.UpdaterConfig;

import java.io.*;
import java.net.URL;

public class FetchChangelogTask implements Task {
    private final UpdaterConfig config;
    private String changelog;

    public FetchChangelogTask(UpdaterConfig config) {
        this.config = config;
    }

    public String getChangelog() {
        return changelog;
    }

    @Override
    public void run(TaskListener listener) {
        listener.log("Fetching changelog...");
        StringBuilder sb = new StringBuilder();
        try {
            InputStreamReader in = new InputStreamReader(new URL(config.changelogUrl).openStream());
            try {
                BufferedReader buf = new BufferedReader(in);
                String line;
                while ((line = buf.readLine() ) != null) {
                    sb.append(line).append("\n");
                }
                changelog = sb.toString();
            } finally {
                in.close();
            }
        } catch (Exception ex) {
            listener.log("Couldn't fetch changelog: " + ex.getMessage());
        }
    }
}
