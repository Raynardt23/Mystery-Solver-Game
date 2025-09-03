package com.OblivionNexusDetective.main;

import com.OblivionNexusDetective.gui.MainWindow;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.setVisible(true);
        });
    }
}