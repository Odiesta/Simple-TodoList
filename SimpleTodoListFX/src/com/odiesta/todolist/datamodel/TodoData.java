package com.odiesta.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class TodoData {

    private static TodoData instance = new TodoData();
    private String filename = "todoListItem.txt";
    private ObservableList<TodoItem> todoItems;
    private DateTimeFormatter formatter;

    public TodoData() {
        formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy");
    }

    public static TodoData getInstance() {
        return instance;
    }

    public ObservableList<TodoItem> getTodoItems() {
        return todoItems;
    }

    public void setTodoItems(ObservableList<TodoItem> todoItems) {
        this.todoItems = todoItems;
    }

    public void addTodoItem(TodoItem item) {
        todoItems.add(item);
    }

    public void deleteTodoItem(TodoItem item) {
        todoItems.remove(item);
    }

    public void editTodoItem(TodoItem oldItem, TodoItem newItem) {
        todoItems.set(todoItems.indexOf(oldItem), newItem);
    }

    public void storeTodoItem() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);
        try {
            Iterator<TodoItem> iterator = todoItems.iterator();
            while (iterator.hasNext()) {
                TodoItem item = iterator.next();
                bw.write(String.format("%s\t%s\t%s",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getDeadline().format(formatter)));
                bw.newLine();
            }
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    public void loadTodoItem() throws IOException {

        Path path = Paths.get(filename);
        BufferedReader br = Files.newBufferedReader(path);
        todoItems = FXCollections.observableArrayList();

        String input;

        try {
            while ((input = br.readLine()) != null) {
                String[] itemPieces = input.split("\t");

                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String date = itemPieces[2];

                LocalDate deadline = LocalDate.parse(date, formatter);
                TodoItem item = new TodoItem(shortDescription, details, deadline);
                todoItems.add(item);
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

}
