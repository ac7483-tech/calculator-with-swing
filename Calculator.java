import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

public class AdvancedCalculator extends JFrame implements ActionListener {

    private JTextField displayField;
    private JTextField expressionField; // To show the full expression
    private String currentInput = "";
    private LinkedList<Double> numbers = new LinkedList<>();
    private LinkedList<Character> operators = new LinkedList<>();

    public AdvancedCalculator() {
        setTitle("Advanced Calculator");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout()); // Using GridBagLayout for more control

        expressionField = new JTextField();
        expressionField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        expressionField.setHorizontalAlignment(SwingConstants.RIGHT);
        expressionField.setEditable(false);
        expressionField.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 2, 5);
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        add(expressionField, gbc);

        displayField = new JTextField("0");
        displayField.setFont(new Font("SansSerif", Font.BOLD, 40));
        displayField.setHorizontalAlignment(SwingConstants.RIGHT);
        displayField.setEditable(false);
        displayField.setBackground(Color.WHITE);
        gbc.gridy = 1;
        gbc.insets = new Insets(2, 5, 5, 5);
        gbc.weighty = 0.15;
        add(displayField, gbc);

        String[] buttonLabels = {
            "%", "√", "CLR", "DEL",
            "7", "8", "9", "/",
            "4", "5", "6", "*",
            "1", "2", "3", "-",
            "+/-", "0", ".", "+"
        };

        int row = 2;
        int col = 0;
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("SansSerif", Font.BOLD, 25));
            button.addActionListener(this);
            button.setFocusable(false);
            
            gbc.gridx = col;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.insets = new Insets(3, 3, 3, 3);
            gbc.weightx = 0.25;
            gbc.weighty = 0.15;

            if (label.equals("CLR") || label.equals("DEL") || label.equals("%") || label.equals("√")) {
                button.setBackground(new Color(255, 180, 180)); // Light red for control buttons
            } else if (label.equals("/") || label.equals("*") || label.equals("-") || label.equals("+")) {
                button.setBackground(new Color(180, 200, 255)); // Light blue for operators
            } else if (label.equals("=")) {
                button.setBackground(new Color(150, 255, 150)); // Green for equals
            } else {
                button.setBackground(new Color(220, 220, 220)); // Grey for numbers and decimal
            }
            button.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

            add(button, gbc);

            col++;
            if (col > 3) {
                col = 0;
                row++;
            }
        }
        
        JButton equalsButton = new JButton("=");
        equalsButton.setFont(new Font("SansSerif", Font.BOLD, 25));
        equalsButton.addActionListener(this);
        equalsButton.setFocusable(false);
        equalsButton.setBackground(new Color(150, 255, 150));
        equalsButton.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        gbc.gridx = 2;
        gbc.gridy = 6; // Last row
        gbc.gridwidth = 2; // Span two columns
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(3, 3, 3, 3);
        gbc.weightx = 0.5;
        gbc.weighty = 0.15;
        add(equalsButton, gbc);


        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if ("0123456789.".contains(command)) {
            if (command.equals(".") && currentInput.contains(".")) {
                return;
            }
            if (displayField.getText().equals("0") && !command.equals(".")) {
                displayField.setText(command);
            } else {
                displayField.setText(displayField.getText() + command);
            }
            currentInput += command;
        } else if ("+-*/".contains(command)) {
            if (!currentInput.isEmpty()) {
                numbers.add(Double.parseDouble(currentInput));
                operators.add(command.charAt(0));
                expressionField.setText(expressionField.getText() + currentInput + " " + command + " ");
                currentInput = "";
                displayField.setText("0"); // Reset display for next number
            } else if (!operators.isEmpty()) { // Allow changing the last operator
                char lastOp = operators.removeLast();
                operators.add(command.charAt(0));
                String expr = expressionField.getText();
                expressionField.setText(expr.substring(0, expr.length() - 3) + command + " ");
            }
        } else if (command.equals("=")) {
            if (!currentInput.isEmpty()) {
                numbers.add(Double.parseDouble(currentInput));
                expressionField.setText(expressionField.getText() + currentInput);
                currentInput = "";
            }
            
            if (numbers.isEmpty()) {
                displayField.setText("0");
                expressionField.setText("");
                return;
            }

            try {
                double result = calculateResult();
                displayField.setText(String.valueOf(result));
                expressionField.setText(String.valueOf(result));
                numbers.clear();
                operators.clear();
                numbers.add(result); // Set result as starting point for next operations
            } catch (ArithmeticException ex) {
                displayField.setText("Error");
                expressionField.setText("");
                numbers.clear();
                operators.clear();
                currentInput = "";
            }
        } else if (command.equals("CLR")) {
            displayField.setText("0");
            expressionField.setText("");
            currentInput = "";
            numbers.clear();
            operators.clear();
        } else if (command.equals("DEL")) {
            if (!currentInput.isEmpty()) {
                currentInput = currentInput.substring(0, currentInput.length() - 1);
                if (currentInput.isEmpty()) {
                    displayField.setText("0");
                } else {
                    displayField.setText(currentInput);
                }
            } else if (!expressionField.getText().isEmpty()) { // If current input is empty, clear last part of expression
                String expr = expressionField.getText().trim();
                int lastSpace = expr.lastIndexOf(' ');
                if (lastSpace != -1) {
                    expressionField.setText(expr.substring(0, lastSpace + 1));
                    // This part would need more complex logic to actually "undo" the last operation/number added to numbers/operators lists
                    // For now, it just visually clears the last part of expressionField
                } else {
                    expressionField.setText("");
                }
            }
        } else if (command.equals("√")) {
            double value = Double.parseDouble(displayField.getText());
            if (value < 0) {
                displayField.setText("Error");
                expressionField.setText("");
                currentInput = "";
                numbers.clear();
                operators.clear();
                return;
            }
            double sqrtResult = Math.sqrt(value);
            displayField.setText(String.valueOf(sqrtResult));
            expressionField.setText("sqrt(" + value + ") = " + sqrtResult);
            currentInput = String.valueOf(sqrtResult);
        } else if (command.equals("%")) {
            double value = Double.parseDouble(displayField.getText());
            double percentResult = value / 100.0;
            displayField.setText(String.valueOf(percentResult));
            expressionField.setText(value + "% = " + percentResult);
            currentInput = String.valueOf(percentResult);
        } else if (command.equals("+/-")) {
            double value = Double.parseDouble(displayField.getText());
            value = -value;
            displayField.setText(String.valueOf(value));
            currentInput = String.valueOf(value);
        }
    }

    private double calculateResult() {
        // This is a simplified calculation, not a full expression parser.
        // It processes multiplication and division immediately, then addition and subtraction.
        
        LinkedList<Double> tempNumbers = new LinkedList<>(numbers);
        LinkedList<Character> tempOperators = new LinkedList<>(operators);
        
        while (tempOperators.contains('*') || tempOperators.contains('/')) {
            int i = 0;
            while (i < tempOperators.size()) {
                char op = tempOperators.get(i);
                if (op == '*' || op == '/') {
                    double n1 = tempNumbers.get(i);
                    double n2 = tempNumbers.get(i + 1);
                    double res = (op == '*') ? n1 * n2 : n1 / n2;
                    tempNumbers.set(i, res);
                    tempNumbers.remove(i + 1);
                    tempOperators.remove(i);
                    break; 
                }
                i++;
            }
        }

        double finalResult = tempNumbers.pollFirst();
        while (!tempOperators.isEmpty()) {
            char op = tempOperators.pollFirst();
            double num = tempNumbers.pollFirst();
            switch (op) {
                case '+': finalResult += num; break;
                case '-': finalResult -= num; break;
            }
        }
        return finalResult;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdvancedCalculator::new);
    }
}