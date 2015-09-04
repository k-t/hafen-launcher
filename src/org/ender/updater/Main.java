package org.ender.updater;

public class Main {
    public static void main(String[] args) {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch(Exception e) {}

        MainFrame gui = new MainFrame();
        gui.setSize(600, 400);
        gui.setLocationRelativeTo(null); // place at the screen center
        gui.log(String.format("OS: '%s', arch: '%s'", System.getProperty("os.name"), System.getProperty("os.arch")));
        gui.setVisible(true);
    }
}
