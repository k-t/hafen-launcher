package org.ender.updater;

import org.ender.updater.tasks.*;
import org.ender.updater.util.ExceptionUtils;
import org.markdown4j.Markdown4jProcessor;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import javax.swing.*;

public class MainFrame extends JFrame implements TaskListener {
    private static final int PROGRESS_MAX = 1024;
    private static final String LOG_DIR = "logs";
    private static final long serialVersionUID = 1L;

    private JTextPane logbox;
    private JLabel progressLabel;
    private JProgressBar progress;
    private JButton launch;

    private FileOutputStream log;
    private UpdaterConfig config;
    private TaskExecutor taskExecutor;

    public MainFrame() {
        super("Hafen Launcher");

        openLog();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        logbox = new JTextPane();
        logbox.setContentType("text/html");
        logbox.setEditable(false);
        logbox.setFont(logbox.getFont().deriveFont(10.0f));

        Container bottom = new JPanel();
        bottom.setLayout(new BorderLayout());

        bottom.add(progressLabel = new JLabel(), BorderLayout.PAGE_START);
        progressLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        bottom.add(progress = new JProgressBar(), BorderLayout.CENTER);
        progress.setMinimum(0);
        progress.setMaximum(PROGRESS_MAX);
        progress.setPreferredSize(new Dimension(20, 20));

        bottom.add(launch = new JButton(), BorderLayout.PAGE_END);
        launch.setText("Launch");
        launch.setVisible(false);
        launch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runClient();
            }
        });

        Container p = getContentPane();
        p.setLayout(new BorderLayout());
        p.add(new JScrollPane(logbox), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.PAGE_END);

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
                logbox.setText(toHtml(task.getChangelog()));
                MainFrame.this.updateClient();
            }
        });
    }

    private void updateClient() {
        final UpdateClientTask task = new UpdateClientTask(config);
        taskExecutor.queue(task, new TaskAdapter(MainFrame.this) {
            @Override
            public void finished() {
                if (task.getUpdatedFileCount() > 0) {
                    progressLabel.setVisible(false);
                    progress.setVisible(false);
                    launch.setVisible(true);
                } else
                    MainFrame.this.runClient();
            }
        });
    }

    private void runClient() {
        taskExecutor.queue(new RunClientTask(config), MainFrame.this);
    }

    private String toHtml(String markdown) {
        try {
            Markdown4jProcessor processor = new Markdown4jProcessor();
            return processor.process(markdown);
        } catch (Exception e) {
            log(ExceptionUtils.getStackTrack(e));
            return "";
        }
    }
}
