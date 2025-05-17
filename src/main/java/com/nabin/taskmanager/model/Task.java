package com.nabin.taskmanager.model;

public class Task {

    private int id;
    private String title;
    private String description;
    private String status;  
    private String dueDate;
    private boolean completed;  

    // Constructor with id (used when loading from DB)
    public Task(int id, String title, String description, String status, String dueDate, boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    // Constructor without id (used before saving to DB)
    public Task(String title, String description, String status, String dueDate, boolean completed) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.dueDate = dueDate;
        this.completed = completed;
    }

    // Getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return title;
    }

    public void setName(String name) {
        this.title = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPriority() {
        return status;
    }

    public void setPriority(String priority) {
        this.status = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Toggle completed status helper
    public void toggleCompleted() {
        this.completed = !this.completed;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", completed=" + completed +
                '}';
    }
}
