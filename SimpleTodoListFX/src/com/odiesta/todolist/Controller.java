package com.odiesta.todolist;

import com.odiesta.todolist.datamodel.TodoData;
import com.odiesta.todolist.datamodel.TodoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Controller {

    private ObservableList<TodoItem> todoItems;

    @FXML
    private ListView<TodoItem> todoItemListView;

    @FXML
    private TextArea detailsArea;

    @FXML
    private Label deadlineLabel;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ContextMenu todoListContextMenu;

    @FXML
    private ToggleButton filterToggleButton;

    private FilteredList<TodoItem> filteredList;

    private Predicate<TodoItem> wantsTodaysItems;
    private Predicate<TodoItem> wantsAllItems;
    private Predicate<TodoItem> completed;

    public void initialize() {
//       TodoItem item1 = new TodoItem("Cleaning the house" , "Nyapu, mebanten, bersihin burung, masak nasi" ,
//               LocalDate.of(2019, Month.JUNE, 9));
//       TodoItem item2 = new TodoItem("Learn JavaFX" , "Learn about borderpane layout, listView" ,
//                LocalDate.of(2019, Month.JUNE, 10));
//       TodoItem item3 = new TodoItem("Learn Web Development" , "Learn about CSS positioning, desplay" ,
//                LocalDate.of(2019, Month.JUNE, 11));
//       TodoItem item4 = new TodoItem("Exercise" , "Push up and squat 100 times" ,
//                LocalDate.of(2019, Month.JUNE, 10));
//       TodoItem item5 = new TodoItem("Practice piano" , "Continue practice karakuri pierrot song" ,
//                LocalDate.of(2019, Month.JUNE, 12));
//
//       todoItems = FXCollections.observableArrayList();
//       todoItems.add(item1);
//       todoItems.add(item2);
//       todoItems.add(item3);
//       todoItems.add(item4);
//       todoItems.add(item5);
//
//       TodoData.getInstance().setTodoItems(todoItems);
//
        todoListContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem editMenuItem = new MenuItem("Edit");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TodoItem item = todoItemListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TodoItem item = todoItemListView.getSelectionModel().getSelectedItem();
                editTodoItemDialog(item);
            }
        });

        todoListContextMenu.getItems().add(deleteMenuItem);
        todoListContextMenu.getItems().add(editMenuItem);


       todoItemListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {
           @Override
           public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
               if (newValue != null) {
                   TodoItem item = todoItemListView.getSelectionModel().getSelectedItem();
                   detailsArea.setText(item.getDetails());
                   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                   deadlineLabel.setText(formatter.format(item.getDeadline()));
               }
           }
       });

       wantsAllItems = new Predicate<TodoItem>() {
           @Override
           public boolean test(TodoItem todoItem) {
               return true;
           }
       };

       wantsTodaysItems = new Predicate<TodoItem>() {
           @Override
           public boolean test(TodoItem todoItem) {
               return todoItem.getDeadline().equals(LocalDate.now());
           }
       };

       filteredList = new FilteredList<>(TodoData.getInstance().getTodoItems(), wantsAllItems);

        SortedList<TodoItem> sortedList = new SortedList<TodoItem>(filteredList,
                new Comparator<TodoItem>() {
                    @Override
                    public int compare(TodoItem o1, TodoItem o2) {
                        return o1.getDeadline().compareTo(o2.getDeadline());
                    }
                });

       todoItemListView.setItems(sortedList);
       todoItemListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
       todoItemListView.getSelectionModel().selectFirst();

       todoItemListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
           @Override
           public ListCell<TodoItem> call(ListView<TodoItem> param) {
               ListCell<TodoItem> cell = new ListCell<TodoItem>() {
                   @Override
                   protected void updateItem(TodoItem item, boolean empty) {
                       super.updateItem(item, empty);
                       if (empty) {
                           setText(null);
                       } else {
                           setText(item.getShortDescription());
                           if (item.getDeadline().isBefore(LocalDate.now().plusDays(1))) {
                               setTextFill(Color.RED);
                           } else if (item.getDeadline().equals(LocalDate.now().plusDays(1))) {
                               setTextFill(Color.BROWN);
                           } else {
                               setTextFill(Color.BLACK);
                           }
                       }
                   }
               };

               cell.emptyProperty().addListener(
                       (obs, wasEmpty, isNowEmpty) -> {
                           if (isNowEmpty) {
                               cell.setContextMenu(null);
                           } else {
                               cell.setContextMenu(todoListContextMenu);
                           }
                       }
               );

               return cell;
           }
       });

    }

    @FXML
    public void addTodoItemDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Todo Item");
        dialog.setHeaderText("Use this dialog to create a new Todo Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
//            dialog.setDialogPane(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            DialogController controller = fxmlLoader.getController();
            TodoItem item = controller.processResult();
            TodoData.getInstance().addTodoItem(item);
            if (item != null) {
                todoItemListView.getSelectionModel().select(item);
            }
        }

    }

    @FXML
    public void editTodoItemDialog(TodoItem item) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Edit Todo Item");
        dialog.setHeaderText("Use this dialog to edit Todo Item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));

        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        DialogController controller = fxmlLoader.getController();
        controller.setShortDescriptionField(item.getShortDescription());
        controller.setDetailsTextArea(item.getDetails());
        controller.setDeadlineDatePicker(item.getDeadline());

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TodoItem newItem = controller.processResult();
            TodoData.getInstance().editTodoItem(item, newItem);
            if (newItem != null) {
                todoItemListView.getSelectionModel().select(newItem);
            }
        }

    }

    public void deleteItem(TodoItem item) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Deleting item: " + item.getShortDescription());
        alert.setContentText("Are you sure?, press ok to confirm, or cancel to back out");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            TodoData.getInstance().deleteTodoItem(item);
            todoItemListView.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void handleKeyPressed(KeyEvent keyEvent) {
        TodoItem item = todoItemListView.getSelectionModel().getSelectedItem();
        if (keyEvent.getCode() == KeyCode.DELETE) {
            deleteItem(item);
        }
    }

    @FXML
    public void handleFilterButton() {
        TodoItem selectedItem = todoItemListView.getSelectionModel().getSelectedItem();
        if (filterToggleButton.isSelected()) {
            filteredList.setPredicate(wantsTodaysItems);
            if (filteredList.isEmpty()) {
                detailsArea.clear();
                deadlineLabel.setText("");
            } else if (filteredList.contains(selectedItem)) {
                todoItemListView.getSelectionModel().select(selectedItem);
            } else {
                todoItemListView.getSelectionModel().selectFirst();
            }
        } else {
            filteredList.setPredicate(wantsAllItems);
            todoItemListView.getSelectionModel().select(selectedItem);
        }
    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }

}
