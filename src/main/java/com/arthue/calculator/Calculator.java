package com.arthue.calculator;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class Calculator extends Application {

    private TextField displayField;

    // Entry point for the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Calculator");

        // Create the display field
        displayField = new TextField();
        displayField.setEditable(false);
        displayField.setAlignment(Pos.CENTER_RIGHT);

        // Create the layout for the buttons
        GridPane buttonPane = new GridPane();
        buttonPane.setPadding(new Insets(10));
        buttonPane.setHgap(5);
        buttonPane.setVgap(5);

        // Button labels in the order they will appear on the GridPane
        String[][] buttonLabels = {
                {"7", "8", "9", "/"},
                {"4", "5", "6", "*"},
                {"1", "2", "3", "-"},
                {"0", "C", "=", "+"}
        };

        // Create buttons and add them to the grid
        for (int row = 0; row < buttonLabels.length; row++) {
            for (int col = 0; col < buttonLabels[row].length; col++) {
                Button button = createButton(buttonLabels[row][col]);
                buttonPane.add(button, col, row);
            }
        }

        // Create the main BorderPane layout
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(displayField);
        borderPane.setCenter(buttonPane);

        // Set the scene and show the stage
        Scene scene = new Scene(borderPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setMinSize(50, 50);
        button.setOnAction(e -> onButtonClick(text));
        return button;
    }

    private void onButtonClick(String text) {
        switch (text) {
            case "C":
                displayField.setText("");
                break;
            case "=":
                calculateResult();
                break;
            default:
                displayField.setText(displayField.getText() + text);
                break;
        }
    }

    private void calculateResult() {
        try {
            // Replace the display text with the result of the evaluation
            double result = evaluate(displayField.getText());
            displayField.setText(String.valueOf(result));
        } catch (Exception e) {
            // If an error occurs, set the text to "Error"
            displayField.setText("Error");
        }
    }

    private double evaluate(String expression) {
        // Remove spaces and replace ÷ and × with / and *
        expression = expression.replaceAll(" ", "").replaceAll("÷", "/").replaceAll("×", "*");

        // Tokenize the expression into numbers and operators
        java.util.List<String> tokens = new java.util.ArrayList<>();
        java.util.Scanner scanner = new java.util.Scanner(expression);
        scanner.useDelimiter("(?<=[-+*/])|(?=[-+*/])");
        while (scanner.hasNext()) {
            tokens.add(scanner.next());
        }
        scanner.close();

        // Stack for operators and operands
        java.util.Stack<Double> values = new java.util.Stack<>();
        java.util.Stack<Character> ops = new java.util.Stack<>();

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            char c = token.charAt(0);
            if (Character.isDigit(c)) {
                values.push(Double.parseDouble(token));
            } else if (token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/")) {
                while (!ops.empty() && hasPrecedence(c, ops.peek())) {
                    values.push(applyOp(ops.pop(), values.pop(), values.pop()));
                }
                ops.push(c);
            }
        }

        while (!ops.empty()) {
            values.push(applyOp(ops.pop(), values.pop(), values.pop()));
        }

        // The 'values' stack will now contain the result
        return values.pop();
    }

    // Returns true if 'op2' has higher or same precedence as 'op1'
    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') return false;
        return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    // Applies an operator 'op' to operands 'a' and 'b'
    private double applyOp(char op, double b, double a) {
        return switch (op) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0) throw new UnsupportedOperationException("Cannot divide by zero");
                yield a / b;
            }
            default -> 0;
        };
    }
}
