package com.nabin.taskmanager;

import javax.swing.*;

import com.nabin.taskmanager.ui.TaskManager;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskManager app = new TaskManager();
            app.setVisible(true);
        });
    }
}
