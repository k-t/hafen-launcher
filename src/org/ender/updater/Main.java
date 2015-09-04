package org.ender.updater;

import org.ender.updater.tasks.*;

import java.io.*;
import javax.swing.*;


public class Main extends JFrame implements UpdaterListener {
    private static final int PROGRESS_MAX = 1024;
    private static final String LOG_DIR = "logs";
    private static final long serialVersionUID = 1L;
    private static Updater updater;
    private FileOutputStream log;

    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {}
        Main gui = new Main();
        gui.setVisible(true);
        gui.setSize(350, 450);
        gui.log(String.format("OS: '%s', arch: '%s'", System.getProperty("os.name"), System.getProperty("os.arch")));
        gui.log("Checking for updates...");

        UpdaterConfig cfg = new UpdaterConfig();
        updater = new Updater(gui);
        updater.addTask(new UpdateClientTask(cfg));
        updater.addTask(new RunClientTask(cfg));
        updater.run();
    }

    private JTextArea logbox;
    private JProgressBar progress;

    public Main(){
	super("Hafen Launcher");
	try {
        File logDir = new File(".", LOG_DIR);
        if (!logDir.exists())
            logDir.mkdirs();
	    log = new FileOutputStream(new File(logDir, "launcher.log"));
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JPanel p;
	add(p = new JPanel());
	p.setLayout(new BoxLayout(p, BoxLayout.PAGE_AXIS));
	
	p.add(logbox = new JTextArea());
	logbox.setEditable(false);
	logbox.setFont(logbox.getFont().deriveFont(10.0f));
	
	p.add(progress = new JProgressBar());
	progress.setMinimum(0);
	progress.setMaximum(PROGRESS_MAX);
	pack();
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
    public void progress(long position, long size) {
        progress.setValue((int) (PROGRESS_MAX * ((float) position / size)));
    }
}
