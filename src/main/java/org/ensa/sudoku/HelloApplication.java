package org.ensa.sudoku;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;
import org.apache.jena.rdf.model.Model;

import java.io.IOException;
import java.util.function.UnaryOperator;

public class HelloApplication extends Application {

    private TextField[][] cells = new TextField[9][9];

    @Override
    public void start(Stage primaryStage) {
        GridPane mainGridPane = new GridPane();
        mainGridPane.setPadding(new Insets(10));
        mainGridPane.setHgap(5);
        mainGridPane.setVgap(5);

        // Create 3x3 subgrids and add to the main grid
        for (int boxRow = 0; boxRow < 3; boxRow++) {
            for (int boxCol = 0; boxCol < 3; boxCol++) {
                GridPane subGrid = new GridPane();
                subGrid.setHgap(1);
                subGrid.setVgap(1);
                subGrid.setPadding(new Insets(2));
                subGrid.setStyle("-fx-border-color: black; -fx-border-width: 1;");

                for (int row = 0; row < 3; row++) {
                    for (int col = 0; col < 3; col++) {
                        TextField cell = new TextField();
                        cell.setPrefSize(50, 50);
                        cell.setStyle("-fx-font-size: 20; -fx-alignment: center;");
                        setNumericInputOnly(cell);
                        cells[boxRow * 3 + row][boxCol * 3 + col] = cell;
                        subGrid.add(cell, col, row);
                    }
                }

                mainGridPane.add(subGrid, boxCol, boxRow);
            }
        }

        Button submitButton = new Button("Submit");
        submitButton.setOnAction(e -> handleSubmit());

        Button resetButton = new Button("Reset");
        resetButton.setOnAction(e -> handleReset());

        HBox buttonBox = new HBox(10, submitButton, resetButton);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);

        mainGridPane.setAlignment(javafx.geometry.Pos.CENTER);

        VBox root = new VBox(10, mainGridPane, buttonBox);
        root.setAlignment(javafx.geometry.Pos.CENTER);

        Scene scene = new Scene(root, 550, 600);
        primaryStage.setTitle("Sudoku Game");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void setNumericInputOnly(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[1-9]?")) {
                return change;
            } else {
                // Show tooltip message
                Tooltip tooltip = new Tooltip("Only digits 1-9 are allowed");
                tooltip.setAutoHide(true);
                textField.setTooltip(tooltip);
                tooltip.show(textField, textField.getScene().getWindow().getX() + textField.getLayoutX(),
                        textField.getScene().getWindow().getY() + textField.getLayoutY() + 30);
                textField.setStyle("-fx-background-color: lightpink;");
                return null;
            }
        };

        StringConverter<Integer> converter = new IntegerStringConverter();

        TextFormatter<Integer> textFormatter = new TextFormatter<>(converter, null, filter);
        textField.setTextFormatter(textFormatter);

        // Reset style when user starts typing again
        textField.setOnKeyTyped(e -> textField.setStyle("-fx-font-size: 20; -fx-alignment: center;"));
    }

    private void handleSubmit() {
        int[][] board = new int[9][9];

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = cells[row][col].getText();
                board[row][col] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }

        if (solveSudoku(board)) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    cells[row][col].setText(Integer.toString(board[row][col]));
                }
            }
        } else {
            System.out.println("No solution exists");
        }
    }

    private boolean solveSudoku(int[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == 0) {
                    for (int num = 1; num <= 9; num++) {
                        if (isValid(board, row, col, num)) {
                            board[row][col] = num;
                            if (solveSudoku(board)) {
                                return true;
                            }
                            board[row][col] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] board, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
            if (board[row][i] == num || board[i][col] == num ||
                    board[row - row % 3 + i / 3][col - col % 3 + i % 3] == num) {
                return false;
            }
        }
        return true;
    }

    private void handleReset() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                cells[row][col].setText("");
                cells[row][col].setStyle("-fx-font-size: 20; -fx-alignment: center;");
            }
        }
    }


    public static void main(String[] args) {
        launch();
    }
}