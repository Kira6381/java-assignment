package com.nabin.taskmanager.ui;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.nabin.taskmanager.dao.TaskDAO;
import com.nabin.taskmanager.model.Task;
// import com.nabin.taskmanager.util.DBUtil;

import java.awt.*;
// import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TaskManager extends JFrame {
    private JTextField taskInput;
    private JTextArea descriptionInput;
    private JComboBox<String> priorityBox;
    private JTextField dueDateField;
    private JTextField searchField;
    private JButton addButton, searchButton;
    private JTable taskTable;
    private TaskTableModel tableModel;
    private List<Task> tasks;

    public TaskManager() {
        setTitle("Task Manager");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        TaskDAO taskDAO = new TaskDAO();
        loadAllTasks(taskDAO);

        // Top panel for "Add Task"
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        taskInput = new JTextField(20);
        descriptionInput = new JTextArea(3, 20);
        descriptionInput.setLineWrap(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionInput);
        
        String[] priorities = {"High", "Medium", "Low"};
        priorityBox = new JComboBox<>(priorities);
        dueDateField = new JTextField(10);
        addButton = new JButton("Add Task");

        // Layout for top panel
        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Task Title:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.gridwidth = 2;
        topPanel.add(taskInput, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 1;
        topPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.gridwidth = 2;
        topPanel.add(descScrollPane, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 1;
        topPanel.add(new JLabel("Priority:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        topPanel.add(priorityBox, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        topPanel.add(new JLabel("Due Date:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        topPanel.add(dueDateField, gbc);
        
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.EAST;
        topPanel.add(addButton, gbc);

        // Table for tasks
        tableModel = new TaskTableModel();
        taskTable = new JTable(tableModel);
        
        taskTable.getColumnModel().getColumn(1).setCellRenderer(new TaskNameCellRenderer());

        // Set preferred widths
        TableColumn indexCol = taskTable.getColumnModel().getColumn(0);
        TableColumn taskCol = taskTable.getColumnModel().getColumn(1);
        TableColumn descCol = taskTable.getColumnModel().getColumn(2);
        TableColumn priorityCol = taskTable.getColumnModel().getColumn(3);
        TableColumn dueDateCol = taskTable.getColumnModel().getColumn(4);
        TableColumn completedCol = taskTable.getColumnModel().getColumn(5);
        TableColumn editCol = taskTable.getColumnModel().getColumn(6);
        TableColumn deleteCol = taskTable.getColumnModel().getColumn(7);

        indexCol.setPreferredWidth(30);
        taskCol.setPreferredWidth(150);
        descCol.setPreferredWidth(200);
        priorityCol.setPreferredWidth(80);
        dueDateCol.setPreferredWidth(80);
        completedCol.setPreferredWidth(70);
        editCol.setPreferredWidth(60);
        deleteCol.setPreferredWidth(60);

        // Renderer for colored priority
        priorityCol.setCellRenderer(new PriorityCellRenderer());

        // Renderer for buttons
        taskTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        taskTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(this, "Edit"));

        taskTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        taskTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(this, "Delete"));

        // Checkbox column for completed
        taskTable.getColumnModel().getColumn(5).setCellEditor(taskTable.getDefaultEditor(Boolean.class));
        taskTable.getColumnModel().getColumn(5).setCellRenderer(taskTable.getDefaultRenderer(Boolean.class));

        refreshTaskList(tasks);

        JScrollPane scrollPane = new JScrollPane(taskTable);

        // Bottom panel for "Search"
        JPanel bottomPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        bottomPanel.add(new JLabel("Search:"));
        bottomPanel.add(searchField);
        bottomPanel.add(searchButton);

        // Layout
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String title = taskInput.getText().trim();
            String description = descriptionInput.getText().trim();
            String dueDate = dueDateField.getText().trim();
            
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a task title.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String priority = (String) priorityBox.getSelectedItem();
            boolean success = taskDAO.addTask(title, description, priority, dueDate, false);
            
            if (success) {
                loadAllTasks(taskDAO);
                refreshTaskList(tasks);
                
                // Clear inputs
                taskInput.setText("");
                descriptionInput.setText("");
                dueDateField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add task to database.", "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Search button event
        searchButton.addActionListener(e -> {
            String keyword = searchField.getText().trim().toLowerCase();
            if (keyword.isEmpty()) {
                refreshTaskList(tasks);
                return;
            }
            List<Task> filtered = tasks.stream()
                    .filter(task -> task.getTitle().toLowerCase().contains(keyword) || 
                                   task.getDescription().toLowerCase().contains(keyword))
                    .collect(Collectors.toList());

            if (filtered.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No tasks found matching: " + keyword, "Search Result", JOptionPane.INFORMATION_MESSAGE);
            }
            refreshTaskList(filtered);
        });

        // When checkbox (completed) changes, update task
        taskTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 5) { // Completed column
                int row = e.getFirstRow();
                Boolean completed = (Boolean) taskTable.getValueAt(row, 5);
                Task task = tasks.get(row);

                task.setCompleted(completed);
                boolean success = taskDAO.updateTask(
                    task.getId(), 
                    task.getTitle(),
                    task.getDescription(),
                    task.getStatus(),
                    task.getDueDate(),
                    task.isCompleted()
                );
                
                if (success) {
                    loadAllTasks(taskDAO);
                    refreshTaskList(tasks);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to update task completion status.", 
                        "Database Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }
    
    private void loadAllTasks(TaskDAO taskDAO) {
        // Load all tasks from the database using the TaskDAO's getAllTasksFromDB method
        tasks = taskDAO.getAllTasksFromDB();
        
        // If for some reason the database is empty or there's an error, initialize with an empty list
        if (tasks == null) {
            tasks = new ArrayList<>();
        }
    }
    
    // Task name renderer that strikes through completed tasks
    private class TaskNameCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Task task = tasks.get(row);
            if (task.isCompleted()) {
                label.setText("<html><strike>" + task.getTitle() + "</strike></html>");
            } else {
                label.setText(task.getTitle());
            }
            return label;
        }
    }
        
    private void refreshTaskList(List<Task> list) {
        tasks = list;              // Update the main tasks list here
        sortTasksByPriority();
        tableModel.setTasks(tasks);
        tableModel.fireTableDataChanged();
    }
    
    private void sortTasksByPriority() {
        if (tasks != null) {
            tasks.sort((t1, t2) -> getPriorityValue(t1.getPriority()) - getPriorityValue(t2.getPriority()));
        }
    }
    
    private int getPriorityValue(String priority) {
        return switch (priority.toLowerCase()) {
            case "high" -> 0;
            case "medium" -> 1;
            case "low" -> 2;
            default -> 3;
        };
    }

    // Table model for tasks
    private class TaskTableModel extends AbstractTableModel {
        private final String[] columns = {"#", "Task", "Description", "Priority", "Due Date", "Completed", "Edit", "Delete"};
        private List<Task> taskList;

        public void setTasks(List<Task> tasks) {
            this.taskList = tasks;
        }

        @Override
        public int getRowCount() {
            return taskList == null ? 0 : taskList.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int col) {
            return columns[col];
        }

        @Override
        public Class<?> getColumnClass(int col) {
            if (col == 5) return Boolean.class;
            if (col == 6 || col == 7) return JButton.class;
            return String.class;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 5 || col == 6 || col == 7; // Completed, Edit, Delete editable
        }

        @Override
        public Object getValueAt(int row, int col) {
            Task task = taskList.get(row);
            return switch (col) {
                case 0 -> row + 1;
                case 1 -> task.getTitle();
                case 2 -> task.getDescription();
                case 3 -> task.getStatus();
                case 4 -> task.getDueDate();
                case 5 -> task.isCompleted();
                case 6 -> "Edit";
                case 7 -> "Delete";
                default -> null;
            };
        }

        @Override
        public void setValueAt(Object aValue, int row, int col) {
            if (col == 5) {
                Task task = taskList.get(row);
                task.toggleCompleted();
                fireTableCellUpdated(row, col);
            }
        }
    }

    // Renderer for priority with color
    private class PriorityCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String priority = (String) value;
            if (priority != null) {
                switch (priority.toLowerCase()) {
                    case "high" -> c.setForeground(Color.RED);
                    case "medium" -> c.setForeground(Color.ORANGE.darker());
                    case "low" -> c.setForeground(new Color(0, 128, 0)); // dark green
                    default -> c.setForeground(Color.BLACK);
                }
            }
            return c;
        }
    }

    // Renderer for buttons
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
    
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            setText((value == null) ? "" : value.toString());
            if ("Delete".equals(value)) {
                setForeground(Color.RED);
            } else {
                setForeground(Color.BLACK);
            }
            return this;
        }
    }
    
    // Button editor for Edit and Delete buttons
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String action;
        private boolean clicked;
        private int row;
        private TaskManager parent;

        public ButtonEditor(TaskManager parent, String action) {
            super(new JTextField());
            this.parent = parent;
            this.action = action;
            this.button = new JButton();
            this.button.setOpaque(true);

            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                 boolean isSelected, int row, int column) {
            this.row = row;
            button.setText(action);
            this.clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (clicked) {
                Task selectedTask = tasks.get(row);
                TaskDAO taskDAO = new TaskDAO();

                if ("Delete".equals(action)) {
                    int confirm = JOptionPane.showConfirmDialog(parent,
                            "Are you sure you want to delete this task?",
                            "Confirm Delete",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean success = taskDAO.deleteTask(selectedTask.getId());
                        if (success) {
                            loadAllTasks(taskDAO);
                            refreshTaskList(tasks);
                        } else {
                            JOptionPane.showMessageDialog(parent, 
                                "Failed to delete task from database.", 
                                "Database Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }

                } else if ("Edit".equals(action)) {
                    // Create a custom dialog for editing
                    JDialog editDialog = new JDialog(parent, "Edit Task", true);
                    editDialog.setLayout(new GridBagLayout());
                    GridBagConstraints gbc = new GridBagConstraints();
                    gbc.insets = new Insets(5, 5, 5, 5);
                    gbc.fill = GridBagConstraints.HORIZONTAL;
                    
                    // Fields for editing
                    JTextField titleField = new JTextField(selectedTask.getTitle(), 20);
                    JTextArea descField = new JTextArea(selectedTask.getDescription(), 5, 20);
                    descField.setLineWrap(true);
                    JScrollPane descScrollPane = new JScrollPane(descField);
                    
                    String[] priorities = {"High", "Medium", "Low"};
                    JComboBox<String> priorityCombo = new JComboBox<>(priorities);
                    priorityCombo.setSelectedItem(selectedTask.getStatus());
                    
                    JTextField dueDateField = new JTextField(selectedTask.getDueDate(), 10);
                    JCheckBox completedCheck = new JCheckBox("Completed", selectedTask.isCompleted());
                    
                    JButton saveButton = new JButton("Save");
                    JButton cancelButton = new JButton("Cancel");
                    
                    // Add components to dialog
                    gbc.gridx = 0; gbc.gridy = 0;
                    editDialog.add(new JLabel("Title:"), gbc);
                    
                    gbc.gridx = 1; gbc.gridy = 0;
                    gbc.gridwidth = 2;
                    editDialog.add(titleField, gbc);
                    
                    gbc.gridx = 0; gbc.gridy = 1;
                    gbc.gridwidth = 1;
                    editDialog.add(new JLabel("Description:"), gbc);
                    
                    gbc.gridx = 1; gbc.gridy = 1;
                    gbc.gridwidth = 2;
                    editDialog.add(descScrollPane, gbc);
                    
                    gbc.gridx = 0; gbc.gridy = 2;
                    gbc.gridwidth = 1;
                    editDialog.add(new JLabel("Priority:"), gbc);
                    
                    gbc.gridx = 1; gbc.gridy = 2;
                    editDialog.add(priorityCombo, gbc);
                    
                    gbc.gridx = 0; gbc.gridy = 3;
                    editDialog.add(new JLabel("Due Date:"), gbc);
                    
                    gbc.gridx = 1; gbc.gridy = 3;
                    editDialog.add(dueDateField, gbc);
                    
                    gbc.gridx = 1; gbc.gridy = 4;
                    editDialog.add(completedCheck, gbc);
                    
                    JPanel buttonPanel = new JPanel();
                    buttonPanel.add(saveButton);
                    buttonPanel.add(cancelButton);
                    
                    gbc.gridx = 0; gbc.gridy = 5;
                    gbc.gridwidth = 3;
                    editDialog.add(buttonPanel, gbc);
                    
                    // Button actions
                    saveButton.addActionListener(e -> {
                        String newTitle = titleField.getText().trim();
                        if (newTitle.isEmpty()) {
                            JOptionPane.showMessageDialog(editDialog, 
                                "Title cannot be empty", "Validation Error", 
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        String newDesc = descField.getText().trim();
                        String newPriority = (String) priorityCombo.getSelectedItem();
                        String newDueDate = dueDateField.getText().trim();
                        boolean newCompleted = completedCheck.isSelected();
                        
                        boolean success = taskDAO.updateTask(
                            selectedTask.getId(),
                            newTitle,
                            newDesc,
                            newPriority,
                            newDueDate,
                            newCompleted
                        );
                        
                        if (success) {
                            loadAllTasks(taskDAO);
                            refreshTaskList(tasks);
                            editDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(editDialog, 
                                "Failed to update task in database", 
                                "Database Error", 
                                JOptionPane.ERROR_MESSAGE);
                        }
                    });
                    
                    cancelButton.addActionListener(e -> editDialog.dispose());
                    
                    // Show dialog
                    editDialog.pack();
                    editDialog.setLocationRelativeTo(parent);
                    editDialog.setVisible(true);
                }
            }

            clicked = false;
            return action;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new TaskManager().setVisible(true);
        });
    }
}