package com.nabin.taskmanager.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.nabin.taskmanager.model.Task;
import com.nabin.taskmanager.util.DBUtil;

public class TaskDAO {

    // Add task to DB 
    public boolean addTask(String title, String description, String status, String dueDate, boolean completed) {
        String sql = "INSERT INTO tasks (title, description, status, due_date, completed) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, status);
            stmt.setString(4, dueDate);
            stmt.setBoolean(5, completed);

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Read all tasks from DB 
    public List<Task> getAllTasksFromDB() {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Task task = new Task(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getString("status"),
                    rs.getString("due_date"),
                    rs.getBoolean("completed")  // Read completed
                );
                tasks.add(task);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return tasks;
    }

    // Update task by ID 
    public boolean updateTask(int id, String title, String description, String status, String dueDate, boolean completed) {
        String sql = "UPDATE tasks SET title = ?, description = ?, status = ?, due_date = ?, completed = ? WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setString(3, status);
            stmt.setString(4, dueDate);
            stmt.setBoolean(5, completed);
            stmt.setInt(6, id);

            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Delete task by ID
    public boolean deleteTask(int id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
