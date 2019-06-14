package com.odiesta.todolist;

import com.odiesta.todolist.datamodel.TodoData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));
        primaryStage.setTitle("Todo List");
        primaryStage.setScene(new Scene(root, 900, 500));
        primaryStage.show();
    }

    @Override
    public void init() throws Exception {
        try {
            TodoData.getInstance().loadTodoItem();
        } catch (IOException e) {
            System.out.println("Couldn't load the file");
            e.printStackTrace();
        }
    }

    @Override
    public void stop() throws Exception {
        try {
            TodoData.getInstance().storeTodoItem();
        } catch (IOException e) {
            System.out.println("Couldn't store the item to file");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
