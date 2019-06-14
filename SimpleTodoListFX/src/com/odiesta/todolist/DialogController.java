package com.odiesta.todolist;

import com.odiesta.todolist.datamodel.TodoData;
import com.odiesta.todolist.datamodel.TodoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DialogController {

    @FXML
    private TextField shortDescriptionField;

    @FXML
    private TextArea detailsTextArea;

    @FXML
    private DatePicker deadlineDatePicker;

    public TodoItem processResult() {
        if (shortDescriptionField.getText() == null || detailsTextArea.getText() == null || deadlineDatePicker.getValue() == null) {
            return null;
        }

        String shortDescription = shortDescriptionField.getText();
        String details = detailsTextArea.getText();
        LocalDate deadline = deadlineDatePicker.getValue();

        TodoItem item = new TodoItem(shortDescription, details, deadline);
        return item;
    }

    public TextField getShortDescriptionField() {
        return shortDescriptionField;
    }

    public void setShortDescriptionField(String shortDescription) {
        shortDescriptionField.setText(shortDescription);
    }

    public TextArea getDetailsTextArea() {
        return detailsTextArea;
    }

    public void setDetailsTextArea(String details) {
        detailsTextArea.setText(details);
    }

    public DatePicker getDeadlineDatePicker() {
        return deadlineDatePicker;
    }

    public void setDeadlineDatePicker(LocalDate deadline) {
        deadlineDatePicker.setValue(deadline);
    }
}
