package org.ender.updater;

import org.ender.updater.tasks.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import javax.swing.*;

public class MainFrame extends JFrame implements TaskListener {
    private static final int PROGRESS_MAX = 1024;
    private static final String LOG_DIR = "logs";
    private static final long serialVersionUID = 1L;

    private JTextArea logbox;
    private JLabel progressLabel;
    private JProgressBar progress;
    private FileOutputStream log;
    private UpdaterConfig config;
    private TaskExecutor taskExecutor;

    public MainFrame() {
        super("Hafen Launcher");

        openLog();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p;
        add(p = new JPanel());
        p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));

        logbox = new JTextArea();
        logbox.setAlignmentX(LEFT_ALIGNMENT);
        logbox.setEditable(false);
        logbox.setWrapStyleWord(true);
        logbox.setLineWrap(true);
        logbox.setFont(logbox.getFont().deriveFont(10.0f));

        JScrollPane scroll;
        p.add(scroll = new JScrollPane(logbox,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        scroll.setPreferredSize(getSize());
        scroll.setAlignmentX(LEFT_ALIGNMENT);

        p.add(progressLabel = new JLabel());
        progressLabel.setAlignmentX(LEFT_ALIGNMENT);
        progressLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        p.add(progress = new JProgressBar());
        progress.setAlignmentX(LEFT_ALIGNMENT);
        progress.setMinimum(0);
        progress.setMaximum(PROGRESS_MAX);
        progress.setPreferredSize(new Dimension(20, 20));
        pack();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                config = new UpdaterConfig();
                taskExecutor = new TaskExecutor();
                fetchChangelog();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                taskExecutor.stop();
            }
        });
    }

    private void openLog() {
        try {
            File dir = new File(".", LOG_DIR);
            if (!dir.exists())
                dir.mkdirs();
            log = new FileOutputStream(new File(dir, "launcher.log"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void log(String message) {
        message = message.concat("\n");
        try {
            if(log != null){log.write(message.getBytes());}
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finished() {
        try {
            if(log != null){
                log.flush();
                log.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    @Override
    public void step(String text) {
        progressLabel.setText(text);
        log(text);
    }

    @Override
    public void progress(long position, long size) {
        progress.setValue((int) (PROGRESS_MAX * ((float) position / size)));
    }

    private void fetchChangelog() {
        final FetchChangelogTask task = new FetchChangelogTask(config);
        taskExecutor.queue(task, new TaskAdapter(MainFrame.this) {
            @Override
            public void finished() {
                logbox.setText(task.getChangelog());
                MainFrame.this.updateClient();
            }
        });
    }

    private void updateClient() {
        taskExecutor.queue(new UpdateClientTask(config), new TaskAdapter(MainFrame.this) {
            @Override
            public void finished() {
                MainFrame.this.runClient();
            }
        });
    }

    private void runClient() {
        taskExecutor.queue(new RunClientTask(config), MainFrame.this);
    }
}
