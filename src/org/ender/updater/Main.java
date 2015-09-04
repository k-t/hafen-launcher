package org.ender.updater;

import org.ender.updater.tasks.*;

public class Main {
    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {}

        MainFrame gui = new MainFrame();
        gui.setVisible(true);
        gui.setSize(350, 450);
        gui.log(String.format("OS: '%s', arch: '%s'", System.getProperty("os.name"), System.getProperty("os.arch")));

        UpdaterConfig cfg = new UpdaterConfig();
        Updater updater = new Updater(gui);
        updater.addTask(new UpdateClientTask(cfg));
        updater.addTask(new RunClientTask(cfg));
        updater.run();
    }
}
