package org.ender.updater;

import org.ender.updater.tasks.*;

import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.Border;


public class MainFrame extends JFrame implements UpdaterListener {
    private static final int PROGRESS_MAX = 1024;
    private static final String LOG_DIR = "logs";
    private static final long serialVersionUID = 1L;

    private JTextArea logbox;
    private JLabel progressLabel;
    private JProgressBar progress;
    private FileOutputStream log;

    public MainFrame() {
        super("Hafen Launcher");

        openLog();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p;
        add(p = new JPanel());
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        p.add(logbox = new JTextArea());
        logbox.setAlignmentX(LEFT_ALIGNMENT);
        logbox.setEditable(false);
        logbox.setFont(logbox.getFont().deriveFont(10.0f));

        p.add(progressLabel = new JLabel());
        progressLabel.setAlignmentX(LEFT_ALIGNMENT);
        progressLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        p.add(progress = new JProgressBar());
        progress.setAlignmentX(LEFT_ALIGNMENT);
        progress.setMinimum(0);
        progress.setMaximum(PROGRESS_MAX);
        progress.setPreferredSize(new Dimension(20, 20));
        pack();
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
        logbox.append(message);
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
}
