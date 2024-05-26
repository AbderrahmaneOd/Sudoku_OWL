package org.ensa.sudoku;

import javafx.scene.control.TextField;
import org.apache.jena.rdf.model.*;
import org.apache.jena.reasoner.*;
import org.apache.jena.reasoner.rulesys.GenericRuleReasoner;
import org.apache.jena.reasoner.rulesys.Rule;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class SudokuSolver {
    private TextField[][] cells = new TextField[9][9];
    private Model model;

    public SudokuSolver() {
        model = ModelFactory.createDefaultModel();
        InputStream in = getClass().getClassLoader().getResourceAsStream("sudoku.rdf");
        if (in == null) {
            throw new IllegalArgumentException("sudoku.rdf not found");
        }
        model.read(in, null);
    }

    public void solve() {
        // Add user inputs to the model
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String value = cells[row][col].getText();
                if (!value.isEmpty()) {
                    Resource cell = model.createResource("http://example.org/sudoku#Cell" + row + col);
                    cell.addProperty(model.createProperty("http://example.org/sudoku#rowIndex"), model.createTypedLiteral(row));
                    cell.addProperty(model.createProperty("http://example.org/sudoku#columnIndex"), model.createTypedLiteral(col));
                    cell.addProperty(model.createProperty("http://example.org/sudoku#hasValue"), model.createTypedLiteral(Integer.parseInt(value)));
                }
            }
        }

        // Apply reasoning to deduce the solution
        Reasoner reasoner = new GenericRuleReasoner(Rule.rulesFromURL("sudoku.rules"));
        InfModel infModel = ModelFactory.createInfModel(reasoner, model);

        // Display the deduced solution
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Resource cell = infModel.getResource("http://example.org/sudoku#Cell" + row + col);
                if (cell.hasProperty(model.getProperty("http://example.org/sudoku#hasValue"))) {
                    int value = cell.getProperty(model.getProperty("http://example.org/sudoku#hasValue")).getInt();
                    cells[row][col].setText(Integer.toString(value));
                }
            }
        }
    }
}
